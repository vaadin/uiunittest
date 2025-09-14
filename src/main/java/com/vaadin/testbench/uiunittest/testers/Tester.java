/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.testers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.vaadin.event.Action;
import com.vaadin.event.ActionManager;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Focusable;
import com.vaadin.ui.UI;

@SuppressWarnings("java:S3011")
public abstract class Tester<T extends AbstractComponent> {

    private T component;

    protected Tester(T component) {
        this.component = component;
    }

    /**
     * Checks if the component is interactable by user, i.e. it is enabled and
     * it is visible.
     * 
     * @return boolean value
     */
    public boolean isInteractable() {
        try {
            // Temporary unlock allows UI#access tasks to be run
            VaadinSession.getCurrent().unlock();
        } finally {
            VaadinSession.getCurrent().lock();
        }
        return isEnabled() && isVisible();
    }

    // Component#isEnabled() returns components immediate enabled state
    // However if component is in disabled container, it will be disabled also
    // in the Browser DOM. Hence need to check parents.
    private boolean isEnabled() {
        Component currentComponent = getComponent();
        if (!currentComponent.isEnabled()) {
            return false;
        }
        while (currentComponent.getParent() != null) {
            currentComponent = currentComponent.getParent();
            if (!currentComponent.isEnabled()) {
                return false;
            }
        }
        return currentComponent.isEnabled();
    }

    private boolean isVisible() {
        Component currentComponent = getComponent();
        if (!currentComponent.isVisible()) {
            return false;
        }
        while (currentComponent.getParent() != null) {
            currentComponent = currentComponent.getParent();
            if (!currentComponent.isVisible()) {
                return false;
            }
        }
        return currentComponent.isVisible();
    }

    /**
     * Checks if the component has an error, typically this can mean with a
     * field component that Binder has validation error and has set component
     * invalid.
     * 
     * @return boolean value.
     */
    public boolean isInvalid() {
        return getComponent().getComponentError() != null;
    }

    /**
     * Returns the component error message if there is one. Otherwise null.
     * 
     * @return String value.
     */
    public String errorMessage() {
        if (getComponent().getComponentError() == null || getComponent()
                .getComponentError().getFormattedHtmlMessage() == null) {
            return null;
        }
        Document doc = Jsoup.parse(
                getComponent().getComponentError().getFormattedHtmlMessage());
        doc.outputSettings().prettyPrint(false);
        return isInvalid() ? doc.text() : null;
    }

    /**
     * Utility method to fire a fabricated event.
     * 
     * @param event
     *            The event to be fired
     */
    protected void fireSimulatedEvent(EventObject event) {
        fireSimulatedEvent(component, event);
    }

    private void fireSimulatedEvent(Component component, EventObject event) {
        Class<?> clazz = component.getClass();
        while (!clazz.equals(AbstractClientConnector.class)) {
            clazz = clazz.getSuperclass();
        }
        try {
            Method fireEventMethod = clazz.getDeclaredMethod("fireEvent",
                    EventObject.class);
            fireEventMethod.setAccessible(true);
            fireEventMethod.invoke(component, event);
        } catch (NoSuchMethodException | SecurityException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Attempt to focus the component as a user. If the component is not
     * Focusable this does nothing. If component is the current focused item in
     * the UI, does nothing. If the UI had a previous component focused, it will
     * be blurred.
     * <p>
     * <em>Note</em>: There is a limitation. Programmatic call to focus() in
     * application logic will not fire focus and blur events, as it requires
     * client roundtrip.
     */
    public void focus() {
        assert (isInteractable()) : "Cannot focus non-interactable component";
        if (getComponent() instanceof Focusable) {
            UI ui = UI.getCurrent();
            Focusable focused = null;
            Focusable focusable = (Focusable) getComponent();
            Class<?> clazz = ui.getClass();
            while (!clazz.equals(UI.class)) {
                clazz = clazz.getSuperclass();
            }
            try {
                Field field = clazz.getDeclaredField("pendingFocus");
                field.setAccessible(true);
                focused = (Focusable) field.get(ui);
            } catch (NoSuchFieldException | SecurityException
                    | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
            if (focused != null && focused.equals(focusable)) {
                return;
            }
            if (focused != null) {
                fireSimulatedEvent(focused, new BlurEvent(focused));
            }
            ((Focusable) getComponent()).focus();
            fireSimulatedEvent(new FocusEvent(focusable));
        }
    }

    /**
     * Test if the component is focused according to UI. If the component is not
     * Focusable returns false.
     *
     * @return boolean value.
     */
    public boolean isFocused() {
        if (getComponent() instanceof Focusable) {
            UI ui = UI.getCurrent();
            Focusable focused = null;
            Class<?> clazz = ui.getClass();
            while (!clazz.equals(UI.class)) {
                clazz = clazz.getSuperclass();
            }
            try {
                Field field = clazz.getDeclaredField("pendingFocus");
                field.setAccessible(true);
                focused = (Focusable) field.get(ui);
            } catch (NoSuchFieldException | SecurityException
                    | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return focused == getComponent();
        }
        return false;
    }

    /**
     * Trigger keyshortcut listener hooked to the component.
     *
     * @param key
     *            the key code of the shortcut, Use e.g.
     *            {@link com.vaadin.event.ShortcutAction.KeyCode}
     */
    public void shortcut(int key) {
        assert (isInteractable()) : "Can't send shortcut to non-interactable component";
        UI ui = UI.getCurrent();
        if (ui == null) {
            throw new IllegalStateException(
                    "There is no current UI to send the shortcut event to");
        }
        ActionManager am = getActionManager();
        Action[] actions = am.getActions(getComponent(), getComponent());
        for (Action action : actions) {
            if (action instanceof ShortcutAction) {
                ShortcutAction shortcutAction = (ShortcutAction) action;
                if (shortcutAction.getKeyCode() == key
                        && shortcutAction.getModifiers().length == 0) {
                    am.handleAction(shortcutAction, getComponent(),
                            getComponent());
                }
            }
        }
    }

    /**
     * Trigger keyshortcut listener hooked to the component.
     *
     * @param key
     *            the key code of the shortcut, Use e.g.
     *            {@link com.vaadin.event.ShortcutAction.KeyCode}
     * @param modifierKeys
     *            the modifier keys required for the shortcut, Use e.g.
     *            {@link com.vaadin.event.ShortcutAction.ModifierKey}
     */
    public void shortcut(int key, int... modifierKeys) {
        assert (isInteractable()) : "Can't send shortcut to non-interactable component";
        UI ui = UI.getCurrent();
        if (ui == null) {
            throw new IllegalStateException(
                    "There is no current UI to send the shortcut event to");
        }
        ActionManager am = getActionManager();
        Action[] actions = am.getActions(getComponent(), getComponent());
        for (Action action : actions) {
            if (action instanceof ShortcutAction) {
                ShortcutAction shortcutAction = (ShortcutAction) action;
                if (shortcutAction.getKeyCode() == key) {
                    int[] required = shortcutAction.getModifiers();
                    Set<Integer> requiredSet = new HashSet<>();
                    for (int mod : required) {
                        requiredSet.add(mod);
                    }
                    Set<Integer> modifierKeysSet = new HashSet<>();
                    for (int mod : modifierKeys) {
                        modifierKeysSet.add(mod);
                    }
                    if (modifierKeysSet.containsAll(requiredSet)) {
                        am.handleAction(shortcutAction, getComponent(),
                                getComponent());
                    }
                }
            }
        }
    }

    private ActionManager getActionManager() {
        // Get action manager using reflection
        ActionManager am = null;
        Class<?> clazz = getComponent().getClass();
        while (!clazz.equals(AbstractComponent.class)) {
            clazz = clazz.getSuperclass();
        }
        try {
            Method getActionManagerMethod = clazz
                    .getDeclaredMethod("getActionManager");
            getActionManagerMethod.setAccessible(true);
            am = (ActionManager) getActionManagerMethod.invoke(getComponent());
        } catch (NoSuchMethodException | SecurityException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new RuntimeException(
                    "Failed to get ActionManager from component", e);
        }
        return am;
    }

    protected T getComponent() {
        return component;
    }
}
