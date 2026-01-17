package com.vaadin.testbench.uiunittest.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.ServiceException;
import com.vaadin.testbench.uiunittest.TestUI;
import com.vaadin.testbench.uiunittest.UIUnitTest;
import com.vaadin.testbench.uiunittest.views.BeanValidationView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;

public class BeanValidationTest extends UIUnitTest {

    private TestUI ui;

    @Before
    public void setup() throws ServiceException {
        ui = new TestUI();
        mockVaadin(ui);
        navigate(BeanValidationView.NAME, BeanValidationView.class);
    }

    @After
    public void cleanup() {
        tearDown();
    }

    @Test
    public void formDisabled() {
        test($(Button.class).caption("Disable").first()).click();
        assertFalse(test($(TextField.class).caption("First name").first())
                .isInteractable());
        assertFalse(test($(TextField.class).caption("Last name").first())
                .isInteractable());

        int err = 0;
        try {
            test($(TextField.class).caption("First name").first())
                    .setValue("Test");
        } catch (AssertionError e) {
            err++;
        }
        assertEquals(1, err);
    }

    @Test
    public void formHidden() {
        test($(Button.class).caption("Hide").first()).click();
        assertFalse(test($(TextField.class).caption("First name").first())
                .isInteractable());
        assertFalse(test($(TextField.class).caption("Last name").first())
                .isInteractable());

        int err = 0;
        try {
            test($(TextField.class).caption("First name").first())
                    .setValue("Test");
        } catch (AssertionError e) {
            err++;
        }
        assertEquals(1, err);
    }

    @Test
    public void binderReadOnly() {
        test($(Button.class).caption("Read only").first()).click();
        assertFalse(test($(TextField.class).caption("First name").first())
                .isInteractable());
        assertFalse(test($(TextField.class).caption("Last name").first())
                .isInteractable());

        int err = 0;
        try {
            test($(TextField.class).caption("First name").first())
                    .setValue("Test");
        } catch (AssertionError e) {
            err++;
        }
        assertEquals(1, err);
    }

    @Test
    public void validationError() {
        test($(TextField.class).caption("First name").first()).setValue("T");
        assertTrue(test($(TextField.class).caption("First name").first())
                .isInvalid());
        assertEquals("Length: 2 - 20!",
                test($(TextField.class).caption("First name").first())
                        .errorMessage());
    }

    @Test
    public void valueChange() {
        test($(TextField.class).caption("First name").first())
                .setValue("Theodore");
        assertEquals("Value: Theodore",
                $(Notification.class).last().getCaption());
    }

}
