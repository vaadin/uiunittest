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
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinSession;
import com.vaadin.testbench.uiunittest.TestUI;
import com.vaadin.testbench.uiunittest.UIUnitTest;
import com.vaadin.testbench.uiunittest.views.TreeGridTestView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TreeGrid;

public class DefaultTest extends UIUnitTest {

    private TestUI ui;

    @Before
    public void setup() throws ServiceException {
        ui = new TestUI();
        mockVaadin(ui);
    }

    @After
    public void cleanup() {
        tearDown();
    }

    @Test
    public void defaultTest() {
        assertEquals("Menu", $(Label.class).id("menu").getValue());
        assertTrue($(TreeGrid.class).first() == null);
        test($(Button.class).caption(TreeGridTestView.NAME).first()).click();
        assertTrue($(TreeGrid.class).first() != null);
    }

    @Test
    public void isSerializable() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bs);
        os.writeObject(ui.getSession());
        os.flush();
        os.close();

        byte[] a = bs.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(a);
        ObjectInputStream in = new ObjectInputStream(bis);
        VaadinSession v = (VaadinSession) in.readObject();
    }
}
