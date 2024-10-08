/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.mocks;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * Mock servlet configuration for tests.
 *
 * @author Vaadin Ltd
 */
@SuppressWarnings("serial")
public class MockServletConfig implements ServletConfig, Serializable {

    private ServletContext context = new MockServletContext();
    private final Properties initParameters;

    public MockServletConfig() {
        this(new Properties());
    }

    public MockServletConfig(Properties initParameters) {
        this.initParameters = initParameters;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.ServletConfig#getServletName()
     */
    @Override
    public String getServletName() {
        return "Mock Servlet";
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.ServletConfig#getServletContext()
     */
    @Override
    public ServletContext getServletContext() {
        return context;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.ServletConfig#getInitParameter(java.lang.String)
     */
    @Override
    public String getInitParameter(String name) {
        return initParameters.getProperty(name);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.ServletConfig#getInitParameterNames()
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Enumeration getInitParameterNames() {
        return initParameters.propertyNames();
    }

}