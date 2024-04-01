/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.testers;

import java.util.List;

import com.vaadin.testbench.uiunittest.Tester;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;

public class MenuBarTester extends Tester<MenuBar> {

    public MenuBarTester(MenuBar component) {
        super(component);
    }

    /**
     * Find MenuItem by its text.
     *
     * @param text
     *            String value
     * @return The first menu item matching the text, null if none found.
     */
    public MenuItem item(String text) {
        List<MenuItem> items = getComponent().getItems();
        return item(items, text);
    }

    private MenuItem item(List<MenuItem> items, String text) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getText().equals(text)) {
                return items.get(i);
            } else if (items.get(i).getChildren() != null) {
                return item(items.get(i).getChildren(), text);
            }
        }
        return null;
    }

    /**
     * Find MenuItem with its sequential id
     *
     * @param id
     *            int value
     * @return The MenuItem matching the id, null if not found
     */
    public MenuItem item(int id) {
        List<MenuItem> items = getComponent().getItems();
        return item(items, id);
    }

    private MenuItem item(List<MenuItem> items, int id) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() == id) {
                return items.get(i);
            } else if (items.get(i).getChildren() != null) {
                return item(items.get(i).getChildren(), id);
            }
        }
        return null;
    }

    /**
     * Simulate click on MenuItem, i.e. trigger its command.
     *
     * @param item
     */
    public void click(MenuItem item) {
        assert (isInteractable()) : "Can't interact with disabled or invisible MenuBar";
        assert (item
                .getMenuBar() == getComponent()) : "Can't click foreign item";
        assert (item.isEnabled()
                && item.isVisible()) : "MenuItem is disabled or invisible";
        assert (item
                .getCommand() != null) : "The MenuItem has no Command associated with it";
        focus();
        item.getCommand().menuSelected(item);
    }
}
