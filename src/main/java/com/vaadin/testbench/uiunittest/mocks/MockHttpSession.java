/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.mocks;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

public class MockHttpSession implements HttpSession {

    private Map<String, Object> attributes = new ConcurrentHashMap<>();
    private String sessionId = UUID.randomUUID().toString();
    private AtomicBoolean valid = new AtomicBoolean(true);
    private long creationTime = System.currentTimeMillis();
    private int maxInactiveInterval = 1800;
    private ServletContext servletConect;

    public MockHttpSession(ServletContext servletContext) {
        this.servletConect = servletContext;
    }

    @Override
    public long getCreationTime() {
        checkValid();
        return creationTime;
    }

    @Override
    public String getId() {
        return sessionId;
    }

    @Override
    public long getLastAccessedTime() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return servletConect;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        checkValid();
        maxInactiveInterval = interval;
    }

    @Override
    public int getMaxInactiveInterval() {
        checkValid();
        return maxInactiveInterval;
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        checkValid();
        return attributes.get(name);
    }

    @Override
    public Object getValue(String name) {
        return getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        checkValid();
        return Collections.enumeration(attributes.keySet());
    }

    @Override
    public String[] getValueNames() {
        checkValid();
        Set<String> keySet = attributes.keySet();
        String[] keys = new String[keySet.size()];
        return keySet.toArray(keys);
    }

    @Override
    public void setAttribute(String name, Object value) {
        checkValid();
        attributes.put(name, value);
    }

    @Override
    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        checkValid();
        attributes.remove(name);
    }

    @Override
    public void removeValue(String name) {
        removeAttribute(name);
    }

    @Override
    public void invalidate() {
        valid.set(false);
    }

    @Override
    public boolean isNew() {
        checkValid();
        return false;
    }

    private void checkValid() {
        if (!valid.get()) {
            throw new IllegalStateException("Invalidated");
        }
    }

    void setId(String id) {
        sessionId = id;
    }
}
