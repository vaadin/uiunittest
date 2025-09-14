package com.vaadin.testbench.uiunittest.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.uiunittest.TestUI;
import com.vaadin.testbench.uiunittest.UIUnitTest;
import com.vaadin.testbench.uiunittest.views.ShortcutView;
import com.vaadin.ui.Label;

import elemental.events.KeyboardEvent.KeyCode;

public class ShortcutTest extends UIUnitTest {

    private ShortcutView view;

    @Before
    public void setup() throws Exception {
        TestUI ui = new TestUI();
        mockVaadin(ui);
        view = navigate(ShortcutView.NAME, ShortcutView.class);
    }
    
    @Test
    public void shortcut() {
        test(view).shortcut(KeyCode.PAGE_DOWN);
        assertEquals("Next", $(Label.class).id("next").getValue());
        test(view).shortcut(KeyCode.PAGE_UP);
        assertEquals("Previous", $(Label.class).id("previous").getValue());
    }
}
