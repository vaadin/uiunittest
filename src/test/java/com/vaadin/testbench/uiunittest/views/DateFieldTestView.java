package com.vaadin.testbench.uiunittest.views;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.vaadin.data.Result;
import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.testbench.uiunittest.TestView;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

public class DateFieldTestView extends TestView {
    public static final String NAME = "datefield";

    public static final String DATETIMEFORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATEFORMAT = "yyyy-MM-dd";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Component getComponent() {
        VerticalLayout layout = new VerticalLayout();

        MyDateField dateField = new MyDateField();
        dateField.setDateFormat(DATEFORMAT);
        dateField.addValueChangeListener(e -> {
            if (e.isUserOriginated() && e.getValue() != null) {
                Notification.show("Date: " + e.getValue().toString());
            }
        });

        MyDateTimeField dateTimeField = new MyDateTimeField();
        dateTimeField.setResolution(DateTimeResolution.SECOND);
        dateTimeField.setDateFormat(DATETIMEFORMAT);
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern(DATETIMEFORMAT);
        dateTimeField.addValueChangeListener(e -> {
            if (e.isUserOriginated() && e.getValue() != null) {
                Notification.show("Date: " + e.getValue().format(formatter));
            }
        });

        layout.addComponents(dateField, dateTimeField);
        return layout;
    }

    public static class MyDateField extends DateField {

        @Override
        protected Result<LocalDate> handleUnparsableDateString(
                String dateString) {
            return Result.error("Cannot parse: " + dateString);
        }
    }

    public static class MyDateTimeField extends DateTimeField {

        @Override
        protected Result<LocalDateTime> handleUnparsableDateString(
                String dateString) {
            return Result.error("Cannot parse: " + dateString);
        }
    }
}
