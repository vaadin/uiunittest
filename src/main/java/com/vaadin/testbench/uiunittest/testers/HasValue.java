/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.testers;

public interface HasValue<T> {

    /**
     * Set value as user. This will mean that accompanying event will have
     * isUserOriginated = true. The field will gain focus and fire focus event
     * as a side effect.
     * 
     * @param value
     *            The value
     */
    public void setValue(T value);

}
