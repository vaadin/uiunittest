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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Focusable;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public abstract class Tester<T extends AbstractComponent> {

    private T component;

    public Tester(T component) {
        this.component = component;
    }

    /**
     * Checks if the component is interactable by user, i.e. it is enabled and
     * it is visible.
     * 
     * @return boolean value
     */
    public boolean isInteractable() {
        VaadinSession.getCurrent().unlock();
        try {
        } finally {
            VaadinSession.getCurrent().lock();
        }
        return isEnabled() && isVisible();
    }

    // Component#isEnabled() returns components immediate enabled state
    // However if component is in disabled container, it will be disabled also
    // in the Browser DOM. Hence need to check parents.
    private boolean isEnabled() {
        Component component = getComponent();
        if (!component.isEnabled()) {
            return false;
        }
        while (component.getParent() != null) {
            component = component.getParent();
            if (!component.isEnabled()) {
                return false;
            }
        }
        return component.isEnabled();
    }

    private boolean isVisible() {
        Component component = getComponent();
        if (!component.isVisible()) {
            return false;
        }
        while (component.getParent() != null) {
            component = component.getParent();
            if (!component.isVisible()) {
                return false;
            }
        }
        return component.isVisible();
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
        return isInvalid() ? doc.text().toString() : null;
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
            UI ui = UI.getCurrent(); // getComponent().getUI();
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
            UI ui = UI.getCurrent(); // getComponent().getUI();
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

    protected T getComponent() {
        return component;
    }
}
