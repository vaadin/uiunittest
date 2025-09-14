package com.vaadin.testbench.uiunittest.views;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.testbench.uiunittest.TestView;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class ShortcutView extends TestView {
    public static final String NAME = "shortcut";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Component getComponent() {
        VerticalLayout layout = new VerticalLayout();
        Label label = new Label("Press Page Up or Page Down");
        label.setId("label");
        layout.addComponent(label);

        addShortcutListener(
                new ShortcutListener("Next", KeyCode.PAGE_DOWN, null) {
                    @Override
                    public void handleAction(Object sender, Object target) {
                        layout.removeAllComponents();
                        Label label = new Label("Next");
                        label.setId("next");
                        layout.addComponent(label);
                    }
                });
        addShortcutListener(
                new ShortcutListener("Previous", KeyCode.PAGE_UP, null) {
                    @Override
                    public void handleAction(Object sender, Object target) {
                        layout.removeAllComponents();
                        Label label = new Label("Previous");
                        label.setId("previous");
                        layout.addComponent(label);
                    }
                });
        return layout;
    }

}
