/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.views;

import com.vaadin.testbench.uiunittest.TestView;
import com.vaadin.ui.Component;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;

public class MenuBarTestView extends TestView {
    public static final String NAME = "menubar";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Component getComponent() {
        MenuBar menu = new MenuBar();
        menu.addItem("Menu 1", c -> Notification.show("Menu 1"));
        MenuItem item = menu.addItem("Menu 2");
        item.addItem("SubMenu 1", c -> Notification.show("SubMenu 1"));
        item.addItem("SubMenu 2", c -> Notification.show("SubMenu 2"));
        MenuItem subItem = item.addItem("SubMenu 3");
        subItem.addItem("SubSubMenu 1", c -> Notification.show("SubSubMenu 1"));
        return menu;
    }
}
