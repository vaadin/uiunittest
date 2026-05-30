/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;

import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.testbench.uiunittest.UIUnitTest;
import com.vaadin.ui.UI;

public class UIUnitTestDeploymentModeTest extends UIUnitTest {

    private static final String PRODUCTION_MODE_PROPERTY =
            "vaadin.servlet.productionMode";

    private DeploymentMode deploymentMode = DeploymentMode.DEBUG;

    @Override
    protected DeploymentMode getDeploymentMode() {
        return deploymentMode;
    }

    @After
    public void cleanup() {
        System.clearProperty(PRODUCTION_MODE_PROPERTY);
        if (UI.getCurrent() != null || VaadinSession.getCurrent() != null
                || VaadinService.getCurrent() != null) {
            tearDown();
        }
    }

    @Test
    public void mockVaadin_allowsForcingProductionModeViaApi()
            throws ServiceException {
        deploymentMode = DeploymentMode.PRODUCTION;

        mockVaadin();

        assertTrue(VaadinService.getCurrent().getDeploymentConfiguration()
                .isProductionMode());
    }

    @Test
    public void mockVaadin_allowsForcingDebugModeViaApi()
            throws ServiceException {
        deploymentMode = DeploymentMode.DEBUG;
        System.setProperty(PRODUCTION_MODE_PROPERTY, "true");

        mockVaadin();

        assertFalse(VaadinService.getCurrent().getDeploymentConfiguration()
                .isProductionMode());
    }

    @Test
    public void mockVaadin_autoDetectsProductionModeFromSystemProperty()
            throws ServiceException {
        deploymentMode = DeploymentMode.AUTO;
        System.setProperty(PRODUCTION_MODE_PROPERTY, "true");

        mockVaadin();

        assertTrue(VaadinService.getCurrent().getDeploymentConfiguration()
                .isProductionMode());
    }
}
