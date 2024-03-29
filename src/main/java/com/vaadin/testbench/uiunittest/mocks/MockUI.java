/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.mocks;

import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
public class MockUI extends UI {

    public MockUI() throws ServiceException {
        this(findOrcreateSession());
    }

    public MockUI(VaadinSession session) {
        setSession(session);
        setCurrent(this);
    }

    @Override
    protected void init(VaadinRequest request) {
        // Do nothing
    }

    private static VaadinSession findOrcreateSession() throws ServiceException {
        VaadinSession session = VaadinSession.getCurrent();
        if (session == null) {
            MockHttpSession httpSession = new MockHttpSession(
                    new MockServletContext());
            session = new MockVaadinSession(new MockVaadinService(),
                    httpSession);
            VaadinSession.setCurrent(session);
        }
        return session;
    }

    public static class AlwaysLockedVaadinSession extends MockVaadinSession {

        public AlwaysLockedVaadinSession(VaadinService service,
                MockHttpSession session) {
            super(service, session);
            lock();
        }
    }
}
