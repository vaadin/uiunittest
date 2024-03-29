/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest;

public interface HasValue<T> {

    /**
     * Set value as user. This will mean that accompanying event will have
     * isUserOriginated = true.
     * 
     * @param value
     *            The value
     */
    public void setValue(T value);

}
