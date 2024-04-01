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

import com.vaadin.testbench.uiunittest.HasValue;
import com.vaadin.testbench.uiunittest.Tester;

import com.vaadin.ui.AbstractMultiSelect;

public class AbstractMultiSelectTester<T> extends Tester<AbstractMultiSelect<T>>
        implements HasValue<Set<T>> {

    private AbstractMultiSelect<T> field;

    public AbstractMultiSelectTester(AbstractMultiSelect<T> field) {
        super(field);
        this.field = field;
    }

    @Override
    public void setValue(Set<T> value) {
        assert (isInteractable()) : "Can't set value to readOnly or disabled field";
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
