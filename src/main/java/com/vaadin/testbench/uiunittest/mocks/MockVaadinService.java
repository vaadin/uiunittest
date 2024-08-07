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
import java.util.List;

import javax.servlet.ServletException;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;

/**
 *
 * @author Vaadin Ltd
 */
@SuppressWarnings("serial")
public class MockVaadinService extends VaadinServletService {

    public MockVaadinService() throws ServiceException {
        this(new MockDeploymentConfiguration());
    }

    public MockVaadinService(DeploymentConfiguration deploymentConfiguration)
            throws ServiceException {
        this(new MockVaadinServlet(), deploymentConfiguration);
    }

    public MockVaadinService(MockVaadinServlet servlet,
            DeploymentConfiguration deploymentConfiguration)
            throws ServiceException {
        super(servlet, deploymentConfiguration);

        try {
            servlet.setServletService(this);
            servlet.init(new MockServletConfig());
            this.init();
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected List<RequestHandler> createRequestHandlers()
            throws ServiceException {
        return Collections.emptyList();
    }

    @Override
    public void init() {
        try {
            super.init();
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected boolean isAtmosphereAvailable() {
        return true;
    }

    
    public static class MockVaadinServlet extends VaadinServlet {

        private MockVaadinService service;

        @Override
        protected VaadinServletService createServletService(
                DeploymentConfiguration deploymentConfiguration)
                throws ServiceException {
            VaadinServletService service = this.service;
            return service;
        }

        public void setServletService(MockVaadinService mockVaadinService) {
            this.service = mockVaadinService;
        }
    }
}