package com.vaadin.testbench.uiunittest.views;

import com.vaadin.testbench.uiunittest.TestView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

public class ButtonTestView extends TestView {
    public static final String NAME = "button";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Component getComponent() {
        VerticalLayout layout = new VerticalLayout();
        Button normal = new Button("Normal");
        normal.addClickListener(e -> Notification.show("Clicked"));
        normal.setId("normal");
        Button focus = new Button("Focus");
        focus.addFocusListener(e -> Notification.show("Focused"));
        focus.setId("focus");
        Button hidden = new Button("Hidden");
        hidden.addFocusListener(e -> Notification.show("Focused"));
        hidden.setVisible(false);
        hidden.setId("hidden");
        Button disableOnClick = new Button("Disable on click");
        disableOnClick.addClickListener(e -> Notification.show("Clicked"));
        disableOnClick.setDisableOnClick(true);
        disableOnClick.setId("disableOnClick");
        layout.addComponents(normal, focus, hidden, disableOnClick);
        return layout;
    }

}
