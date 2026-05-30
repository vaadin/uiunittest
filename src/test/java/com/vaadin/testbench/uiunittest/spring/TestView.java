/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.spring;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SpringView(name = TestView.NAME)
public class TestView extends CustomComponent implements View {

    public static final String NAME = "test";

    private final TestPresenter presenter;
    private final AutowiredTestButton button;
    private final Label message = new Label("initial");

    public TestView(TestPresenter presenter, AutowiredTestButton button) {
        this.presenter = presenter;
        this.button = button;
        message.setId("spring-message");
        button.addClickListener(
                event -> message.setValue(presenter.getMessage()));
        setCompositionRoot(new VerticalLayout(message, button));
    }

    @Override
    public void enter(ViewChangeEvent event) {
    }

    public TestPresenter getPresenter() {
        return presenter;
    }

    public AutowiredTestButton getButton() {
        return button;
    }

    public Label getMessage() {
        return message;
    }
}
