/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.testers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.vaadin.ui.AbstractSingleSelect;
import com.vaadin.ui.Component.Focusable;

public class AbstractSingleSelectTester<T>
        extends Tester<AbstractSingleSelect<T>> {

    public AbstractSingleSelectTester(AbstractSingleSelect<T> field) {
        super(field);
    }

    /**
     * Simulate clicking item in single select component to get it selected.
     *
     * @param item
     *            Item to click
     */
    public void clickItem(T item) {
        assert (isInteractable()) : "Can't set value to readonly, hidden or disabled field";
        setValue(item);
    }

    protected void setValue(T value) {
        if (getComponent() instanceof Focusable) {
            focus();
        }
        Class<?> clazz = getComponent().getClass();
        while (!clazz.equals(AbstractSingleSelect.class)) {
            clazz = clazz.getSuperclass();
        }
        try {
            Method setSelectedItemMethod = clazz.getDeclaredMethod(
                    "setSelectedItem", Object.class, Boolean.TYPE);
            setSelectedItemMethod.setAccessible(true);
            setSelectedItemMethod.invoke(getComponent(), value, true);
        } catch (NoSuchMethodException | SecurityException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the component is interactable by user, i.e. it is enabled and
     * it is visible and not in readonly state.
     * 
     * @return boolean value
     */
    @Override
    public boolean isInteractable() {
        return super.isInteractable() && !getComponent().isReadOnly();
    }

    @Override
    protected AbstractSingleSelect<T> getComponent() {
        return super.getComponent();
    }
}
