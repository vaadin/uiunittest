/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.testers;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.vaadin.shared.ui.datefield.AbstractDateFieldServerRpc;
import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.ui.AbstractDateField;
import com.vaadin.ui.UI;

public class AbstractDateFieldTester<T extends Temporal & TemporalAdjuster & Serializable & Comparable<? super T>, R extends Enum<R>>
        extends AbstractFieldTester<T> {

    public AbstractDateFieldTester(AbstractDateField<T, R> field) {
        super(field);
    }

    /**
     * Simulate text input to date field. Asserts that filter field is not
     * disabled. If only one item matches filter, it is selected. If no items
     * match and newItemProvider is present, it will be called with given value.
     * The DateField will gain focus and fire focus event as a side effect.
     *
     * @param value
     *            String value
     */
    public void setInput(String value) {
        AbstractDateField<T, R> dateField = getComponent();
        assert (isInteractable()) : "Can't set value to readonly, hidden or disabled field";
        dateField.focus();

        Map<String, Integer> resolutions = new HashMap<>();
        String format = dateField.getDateFormat();
        DateTimeFormatter formatter;
        if (format == null) {
            formatter = DateTimeFormatter
                    .ofLocalizedDateTime(FormatStyle.MEDIUM)
                    .withLocale(UI.getCurrent().getLocale());
        } else {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd[ HH:mm:ss]",
                    Locale.ENGLISH);
        }
        try {
            TemporalAccessor temporal = formatter.parse(value);
            try {
                LocalDateTime date = LocalDateTime.from(temporal);
                resolutions.put(DateTimeResolution.HOUR.name(), date.getHour());
                resolutions.put(DateTimeResolution.MINUTE.name(),
                        date.getMinute());
                resolutions.put(DateTimeResolution.SECOND.name(),
                        date.getSecond());
                resolutions.put(DateTimeResolution.MONTH.name(),
                        date.getMonthValue());
                resolutions.put(DateTimeResolution.DAY.name(),
                        date.getDayOfMonth());
                resolutions.put(DateTimeResolution.YEAR.name(), date.getYear());
            } catch (DateTimeException e) {
                LocalDate date = LocalDate.from(temporal);
                resolutions.put(DateTimeResolution.MONTH.name(),
                        date.getMonthValue());
                resolutions.put(DateTimeResolution.DAY.name(),
                        date.getDayOfMonth());
                resolutions.put(DateTimeResolution.YEAR.name(), date.getYear());
            }
        } catch (DateTimeParseException e) {
        }

        Class<?> clazz = getComponent().getClass();
        while (!clazz.equals(AbstractDateField.class)) {
            clazz = clazz.getSuperclass();
        }
        try {
            Field rpcField = clazz.getDeclaredField("rpc");
            rpcField.setAccessible(true);
            AbstractDateFieldServerRpc rpc = (AbstractDateFieldServerRpc) rpcField
                    .get(dateField);
            rpc.update(value, resolutions);
        } catch (NoSuchFieldException | SecurityException
                | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    protected AbstractDateField<T, R> getComponent() {
        return (AbstractDateField<T, R>) super.getComponent();
    }
}
