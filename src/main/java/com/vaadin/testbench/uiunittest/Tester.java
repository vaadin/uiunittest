/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EventObject;

import com.vaadin.server.AbstractClientConnector;
import com.vaadin.ui.AbstractComponent;

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
        return getComponent().isEnabled() && getComponent().isVisible();
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
        return isInvalid()
                ? getComponent().getComponentError().getFormattedHtmlMessage()
                : null;
    }

    /**
     * Utility method to fire a fabricated event.
     * 
     * @param event
     *            The event to be fired
     */
    protected void fireSimulatedEvent(EventObject event) {
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

    protected T getComponent() {
        return component;
    }
}
