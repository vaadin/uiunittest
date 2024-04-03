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
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.ui.AbstractMultiSelect;
import com.vaadin.ui.Component.Focusable;

public class AbstractMultiSelectTester<T> extends Tester<AbstractMultiSelect<T>>
        implements HasValue<Set<T>> {

    public AbstractMultiSelectTester(AbstractMultiSelect<T> field) {
        super(field);
    }

    /**
     * Simulate clicking item in multiselect component to toggle its selection.
     *
     * @param item
     *            Item to click
     */
    public void clickItem(T item) {
        Set<T> value = getComponent().getValue().stream()
                .map(Objects::requireNonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (value.contains(item)) {
            value.remove(item);
            setValue(value);
        } else {
            value.add(item);
            setValue(value);
        }
    }

    @Override
    public void setValue(Set<T> value) {
        assert (isInteractable()) : "Can't set value to readOnly or disabled field";
        AbstractMultiSelect<T> field = getComponent();
        if (field instanceof Focusable) {
            focus();
        }
        Set<T> copy = value.stream().map(Objects::requireNonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Class<?> clazz = field.getClass();
        while (!clazz.equals(AbstractMultiSelect.class)) {
            clazz = clazz.getSuperclass();
        }
        try {
            Method updateSelectionMethod = clazz.getDeclaredMethod(
                    "updateSelection", Set.class, Set.class, Boolean.TYPE);
            updateSelectionMethod.setAccessible(true);
            updateSelectionMethod.invoke(field, copy,
                    new LinkedHashSet<>(field.getSelectedItems()), true);
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
}
