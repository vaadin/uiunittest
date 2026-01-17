package com.vaadin.testbench.uiunittest.views;

import com.vaadin.testbench.uiunittest.TestView;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class ExtendedComponentView extends TestView {
    public static final String NAME = "extended-component";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Component getComponent() {
        VerticalLayout layout = new VerticalLayout();
        MyLabel label = new MyLabel();
        label.setId("my-label");
        MyField myField = new MyField();
        myField.setId("my-field");
        myField.addValueChangeListener(e -> {
            label.setText(e.getValue());
        });
        layout.addComponents(myField, label);
        return layout;
    }

    public static class MyField extends TextField {

        public MyField() {
            super("My Field");
        }
    }

    public static class MyLabel extends Label {

        public MyLabel() {
            super();
            setText("none");
        }

        public void setText(String text) {
            setValue("Value: " + text);
        }
    }

}
