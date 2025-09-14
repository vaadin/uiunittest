/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.testbench.uiunittest.views.BeanValidationView;
import com.vaadin.testbench.uiunittest.views.BinderTestView;
import com.vaadin.testbench.uiunittest.views.ButtonTestView;
import com.vaadin.testbench.uiunittest.views.CheckBoxGroupTestView;
import com.vaadin.testbench.uiunittest.views.ComboBoxTestView;
import com.vaadin.testbench.uiunittest.views.DateFieldTestView;
import com.vaadin.testbench.uiunittest.views.DoubleIdView;
import com.vaadin.testbench.uiunittest.views.FocusTestView;
import com.vaadin.testbench.uiunittest.views.GridEditorTestView;
import com.vaadin.testbench.uiunittest.views.GridTestView;
import com.vaadin.testbench.uiunittest.views.MenuBarTestView;
import com.vaadin.testbench.uiunittest.views.PanelTestView;
import com.vaadin.testbench.uiunittest.views.PushTestView;
import com.vaadin.testbench.uiunittest.views.ShortcutView;
import com.vaadin.testbench.uiunittest.views.TabSheetTestView;
import com.vaadin.testbench.uiunittest.views.TreeGridTestView;
import com.vaadin.testbench.uiunittest.views.WindowView;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
@Push
@Theme(ValoTheme.THEME_NAME)
public class TestUI extends UI {

    Navigator nav;
    Map<String, Class<? extends View>> views = new HashMap<>();

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout content = new VerticalLayout();
        nav = new Navigator(this, content);
        addView("", DefaultView.class);
        addView(GridTestView.NAME, GridTestView.class);
        addView(MenuBarTestView.NAME, MenuBarTestView.class);
        addView(ComboBoxTestView.NAME, ComboBoxTestView.class);
        addView(TabSheetTestView.NAME, TabSheetTestView.class);
        addView(CheckBoxGroupTestView.NAME, CheckBoxGroupTestView.class);
        addView(TreeGridTestView.NAME, TreeGridTestView.class);
        addView(FocusTestView.NAME, FocusTestView.class);
        addView(BinderTestView.NAME, BinderTestView.class);
        addView(DateFieldTestView.NAME, DateFieldTestView.class);
        addView(ButtonTestView.NAME, ButtonTestView.class);
        addView(GridEditorTestView.NAME, GridEditorTestView.class);
        addView(PushTestView.NAME, PushTestView.class);
        addView(PanelTestView.NAME, PanelTestView.class);
        addView(DoubleIdView.NAME, DoubleIdView.class);
        addView(WindowView.NAME, WindowView.class);
        addView(BeanValidationView.NAME, BeanValidationView.class);
        addView(ShortcutView.NAME, ShortcutView.class);
        setContent(content);
        nav.navigateTo("");
    }

    private void addView(String name, Class<? extends View> view) {
        nav.addView(name, view);
        views.put(name, view);
    }

    public Map<String, Class<? extends View>> getViews() {
        return views;
    }

    public void navigate(String name) {
        nav.navigateTo(name);
    }

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = TestUI.class)
    public static class Servlet extends VaadinServlet {
    }

}
