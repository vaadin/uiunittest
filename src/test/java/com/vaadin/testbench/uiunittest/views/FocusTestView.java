package com.vaadin.testbench.uiunittest.views;

import com.vaadin.testbench.uiunittest.TestView;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;

public class FocusTestView extends TestView {
    public static final String NAME = "focus";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Component getComponent() {
        FormLayout layout = new FormLayout();
        TextField firstName = new TextField("First name");
        TextField lastName = new TextField("Last name");
        firstName.focus();
        firstName.addBlurListener(e -> {
            Label label = new Label("First name blurred");
            label.setId("blur");
            addComponent(label);
        });
        lastName.addFocusListener(e -> {
            Label label = new Label("Last name focused");
            label.setId("focus");
            addComponent(label);
        });
        layout.addComponents(firstName, lastName);
        return layout;
    }

}
