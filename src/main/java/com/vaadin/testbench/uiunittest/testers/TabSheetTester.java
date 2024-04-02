/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.testers;

import java.util.Iterator;

import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

public class TabSheetTester extends Tester<TabSheet> {

    public TabSheetTester(TabSheet tabSheet) {
        super(tabSheet);
    }

    public Tab tab(String caption) {
        for (int i = 0; i < getComponent().getComponentCount(); i++) {
            Tab tab = getComponent().getTab(i);
            if (tab.getCaption().contains(caption)) {
                return tab;
            }
        }
        return null;
    }

    /**
     * Simulate user clicking a Tab, the SelectedTabC.hangeEvent fired will have
     * isUserOriginated = true
     * 
     * @param tab
     *            The Tab
     */
    public void click(Tab tab) {
        assert (isInteractable()) : "Can't interact with disabled or invisible TabSheet";
        assert (tab.isEnabled() && tab
                .isVisible()) : "Can't interact with disabled or invisible Tab";
        int index = 0;
        for (int i = 0; i < getComponent().getComponentCount(); i++) {
            Tab t = getComponent().getTab(i);
            if (t.equals(tab)) {
                index = i;
                break;
            }
        }
        click(index);
    }

    /**
     * Simulate user clicking a Tab by its index, the SelectedTabC.hangeEvent
     * fired will have isUserOriginated = true
     * 
     * @param tab
     *            The Tab index
     */
    public void click(int index) {
        Iterator<Component> iter = getComponent().iterator();
        Component comp = null;
        int i = 0;
        while (iter.hasNext()) {
            Component c = iter.next();
            if (i == index) {
                comp = c;
                break;
            }
            i++;
        }
        getComponent().setSelectedTab(comp, true);
    }

    /**
     * Return the currently visible content of the TabSheet.
     *
     * @return A component.
     */
    public Component current() {
        return getComponent().getSelectedTab();
    }

    @Override
    protected TabSheet getComponent() {
        return super.getComponent();
    }
}
