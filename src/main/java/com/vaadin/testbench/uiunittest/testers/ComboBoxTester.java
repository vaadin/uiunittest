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

public class ComboBoxTester<T> extends AbstractSingleSelectTester<T> {

    public ComboBoxTester(ComboBox<T> field) {
        super(field);
    }

    /**
     * Simulate text input to Filter field. Asserts that filter field is not
     * disabled. If only one item matches filter, it is selected. If no items
     * match and newItemProvider is present, it will be called with given value.
     *
     * @param value
     *            String value
     */
    public void setInput(String value) {
        ComboBox<T> comboBox = getComponent();
        comboBox.focus();
        assert (isInteractable()) : "ComboBox is not enabled";
        assert (comboBox
                .isTextInputAllowed()) : "ComboBox has filter field disabled";
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
            } else if (items.size() == 0
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
