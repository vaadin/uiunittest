package com.vaadin.testbench.uiunittest.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.server.ServiceException;
import com.vaadin.testbench.uiunittest.TestUI;
import com.vaadin.testbench.uiunittest.UIUnitTest;
import com.vaadin.testbench.uiunittest.views.ButtonTestView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;

public class ButtonTest extends UIUnitTest {

    private TestUI ui;
    private ButtonTestView view;

    @Before
    public void setup() throws ServiceException {
        ui = new TestUI();
        mockVaadin(ui);
        view = navigate(ButtonTestView.NAME, ButtonTestView.class);
    }

    @After
    public void cleanup() {
        tearDown();
    }

    @Test
    public void buttonClick() {
        test($(Button.class).id("normal")).click();
        assertEquals("Clicked", $(Notification.class).last().getCaption());
    }

    @Test
    public void buttonShortcut() {
        test($(Button.class).id("normal")).shortcut(KeyCode.ENTER);
        assertEquals("Clicked", $(Notification.class).last().getCaption());
    }

    @Test
    public void buttonShortcutNotTriggeredWithWrongKey() {
        test($(Button.class).id("normal")).shortcut(KeyCode.ESCAPE);
        assertEquals(null, $(Notification.class).last());
    }

    @Test
    public void buttonShortcutWithModifier() {
        test($(Button.class).id("save"))
                .shortcut(KeyCode.S, ModifierKey.CTRL);
        assertEquals("Saved", $(Notification.class).last().getCaption());
    }

    @Test
    public void buttonShortcutWithoutModifierDoesNotFire() {
        test($(Button.class).id("save"))
                .shortcut(KeyCode.S);
        assertEquals(null, $(Notification.class).last());
    }
    @Test
    public void buttonShortcutWithWrongModifierDoesNotFire() {
        test($(Button.class).id("save"))
                .shortcut(KeyCode.S, ModifierKey.ALT);
        assertEquals(null, $(Notification.class).last());
    }

    @Test
    public void buttonClickFocuses() {
        test($(Button.class).id("focus")).click();
        assertEquals("Focused", $(Notification.class).last().getCaption());
    }

    @Test
    public void hiddenCannotFocus() {
        Button button = $(Button.class).id("hidden");
        int errors = 0;
        try {
            test(button).focus();
        } catch (AssertionError e) {
            errors++;
        }
        assertEquals(1, errors);
        assertEquals(null, $(Notification.class).last());
    }

    @Test
    public void hiddenCannotClick() {
        Button button = $(Button.class).id("hidden");
        int errors = 0;
        try {
            test(button).click();
        } catch (AssertionError e) {
            errors++;
        }
        assertEquals(1, errors);
        assertEquals(null, $(Notification.class).last());
    }

    @Test
    public void disableOnClick() {
        test($(Button.class).id("disableOnClick")).click();
        assertEquals("Clicked", $(Notification.class).last().getCaption());
        assertFalse(
                test($(Button.class).id("disableOnClick")).isInteractable());
    }

}
