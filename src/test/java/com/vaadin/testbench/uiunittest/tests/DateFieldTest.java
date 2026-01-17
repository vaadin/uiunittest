/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.tests;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.ServiceException;
import com.vaadin.testbench.uiunittest.TestUI;
import com.vaadin.testbench.uiunittest.UIUnitTest;
import com.vaadin.testbench.uiunittest.views.DateFieldTestView;
import com.vaadin.testbench.uiunittest.views.DateFieldTestView.MyDateField;
import com.vaadin.testbench.uiunittest.views.DateFieldTestView.MyDateTimeField;
import com.vaadin.ui.Notification;

public class DateFieldTest extends UIUnitTest {

    private TestUI ui;

    @Before
    public void setup() throws ServiceException {
        ui = new TestUI();
        mockVaadin(ui);
        navigate(DateFieldTestView.NAME, DateFieldTestView.class);
    }

    @After
    public void cleanup() {
        tearDown();
    }

    @Test
    public void textInput_DateField() {
        LocalDate date = LocalDate.of(1971, 12, 10);
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern(DateFieldTestView.DATEFORMAT);
        String input = date.format(formatter);
        test($(MyDateField.class).single()).setInput(input);
        assertEquals("Date: " + input,
                $(Notification.class).last().getCaption());
    }

    @Test
    public void unparsable_DateField() {
        test($(MyDateField.class).single()).setInput("unparsable");
        assertEquals("Cannot parse: unparsable",
                test($(MyDateField.class).single()).errorMessage());
    }

    @Test
    public void textInput_DateTimeField() {
        LocalDateTime date = LocalDateTime.of(1971, 12, 10, 5, 15, 32);
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern(DateFieldTestView.DATETIMEFORMAT);
        String input = date.format(formatter);
        test($(MyDateTimeField.class).single()).setInput(input);
        assertEquals("Date: " + input,
                $(Notification.class).last().getCaption());
    }

    @Test
    public void unparsable_DateTimeField() {
        test($(MyDateTimeField.class).single()).setInput("unparsable");
        assertEquals("Cannot parse: unparsable",
                test($(MyDateTimeField.class).single()).errorMessage());
    }

}
