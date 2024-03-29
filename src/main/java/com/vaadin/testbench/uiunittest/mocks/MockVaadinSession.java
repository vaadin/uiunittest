/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.mocks;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedHttpSession;
import com.vaadin.server.WrappedSession;

@SuppressWarnings("serial")
public class MockVaadinSession extends VaadinSession {
    /*
     * Used to make sure there's at least one reference to the mock session
     * while it's locked. This is used to prevent the session from being eaten
     * by GC in tests where @Before creates a session and sets it as the current
     * instance without keeping any direct reference to it. This pattern has a
     * chance of leaking memory if the session is not unlocked in the right way,
     * but it should be acceptable for testing use.
     */
    private static final ThreadLocal<MockVaadinSession> referenceKeeper = new ThreadLocal<>();
    private WrappedSession wrappedSession;

    public MockVaadinSession(VaadinService service,
            MockHttpSession httpSession) {
        super(service);
        wrappedSession = new WrappedHttpSession(httpSession);
    }

    @Override
    public WrappedSession getSession() {
        return wrappedSession;
    }

    @Override
    public void close() {
        super.close();
        closeCount++;
    }

    public int getCloseCount() {
        return closeCount;
    }

    @Override
    public Lock getLockInstance() {
        return lock;
    }

    @Override
    public void lock() {
        super.lock();
        referenceKeeper.set(this);
    }

    @Override
    public void unlock() {
        super.unlock();
        referenceKeeper.remove();
    }

    private int closeCount;

    private ReentrantLock lock = new ReentrantLock();
}
