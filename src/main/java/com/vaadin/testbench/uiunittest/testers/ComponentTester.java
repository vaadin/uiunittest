package com.vaadin.testbench.uiunittest.testers;

import com.vaadin.ui.AbstractComponent;

public class ComponentTester extends Tester<AbstractComponent> {

    /**
     * Create a Tester for a Component
     * 
     * @param component
     *            The Component
     */
    public ComponentTester(AbstractComponent component) {
        super(component);
    }
}
