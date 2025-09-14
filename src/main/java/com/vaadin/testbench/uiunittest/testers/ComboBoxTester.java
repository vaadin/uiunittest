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
import java.util.List;

import com.vaadin.shared.ui.combobox.ComboBoxServerRpc;
import com.vaadin.ui.ComboBox;

@SuppressWarnings("java:S3011")
public class ComboBoxTester<T> extends AbstractSingleSelectTester<T> {

    public ComboBoxTester(ComboBox<T> field) {
        super(field);
    }

    /**
     * Simulate text input to Filter field. Asserts that filter field is not
     * disabled. If only one item matches filter, it is selected. If no items
     * match and newItemProvider is present, it will be called with given value.
     * ComboBox will gain focus and fire focus event as a side effect.
     *
     * @param value
     *            String value
     */
    public void setInput(String value) {
        ComboBox<T> comboBox = getComponent();
        assert (isInteractable()) : "Cannot set input to readonly, disabled or hidden ComboBox";
        assert (comboBox
                .isTextInputAllowed()) : "ComboBox has filter field disabled";
        comboBox.focus();
        Class<?> clazz = getComponent().getClass();
        while (!clazz.equals(ComboBox.class)) {
            clazz = clazz.getSuperclass();
        }
        try {
            Field rpcField = clazz.getDeclaredField("rpc");
            rpcField.setAccessible(true);
            ComboBoxServerRpc rpc = (ComboBoxServerRpc) rpcField.get(comboBox);
            rpc.setFilter(value);
            List<T> items = comboBox.getDataCommunicator()
                    .fetchItemsWithRange(0, 2);
            if (items.size() == 1) {
                setValue(items.get(0));
            } else if (items.isEmpty()
                    && comboBox.getNewItemProvider() != null) {
                rpc.createNewItem(value);
            }
        } catch (NoSuchFieldException | SecurityException
                | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    protected ComboBox<T> getComponent() {
        return (ComboBox<T>) super.getComponent();
    }
}
