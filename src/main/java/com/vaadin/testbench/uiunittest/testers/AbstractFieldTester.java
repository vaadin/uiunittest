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

import com.vaadin.testbench.uiunittest.HasValue;
import com.vaadin.testbench.uiunittest.Tester;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Component.Focusable;

public class AbstractFieldTester<T> extends Tester<AbstractField<T>>
        implements HasValue<T> {

    public AbstractFieldTester(AbstractField<T> field) {
        super(field);
    }

    @Override
    public void setValue(T value) {
        assert (isInteractable()) : "Can't set value to readOnly or disabled field";
        if (getComponent() instanceof Focusable) {
            focus();
        }
        Class<?> clazz = getComponent().getClass();
        while (!clazz.equals(AbstractField.class)) {
            clazz = clazz.getSuperclass();
        }
        try {
            Method setValueMethod = clazz.getDeclaredMethod("setValue",
                    Object.class, Boolean.TYPE);
            setValueMethod.setAccessible(true);
            setValueMethod.invoke(getComponent(), value, true);
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
    protected AbstractField<T> getComponent() {
        return super.getComponent();
    }
}
