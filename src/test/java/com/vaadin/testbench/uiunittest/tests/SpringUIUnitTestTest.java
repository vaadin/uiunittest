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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.After;
import org.junit.Test;
import org.springframework.core.env.MapPropertySource;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.testbench.uiunittest.SpringUIUnitTest;
import com.vaadin.testbench.uiunittest.spring.ApplicationScopedMockService;
import com.vaadin.testbench.uiunittest.spring.AutowiredTestButton;
import com.vaadin.testbench.uiunittest.spring.ConventionMappedView;
import com.vaadin.testbench.uiunittest.spring.SpringTestConfiguration;
import com.vaadin.testbench.uiunittest.spring.SpringTestConfiguration.StartupRunnerState;
import com.vaadin.testbench.uiunittest.spring.SpringTestUI;
import com.vaadin.testbench.uiunittest.spring.TestView;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

public class SpringUIUnitTestTest extends SpringUIUnitTest {

    private DeploymentMode deploymentMode = DeploymentMode.AUTO;
    private Boolean productionModeProperty;

    @After
    public void cleanup() {
        if (UI.getCurrent() != null || VaadinSession.getCurrent() != null
                || VaadinService.getCurrent() != null
                || getApplicationContext() != null) {
            tearDown();
        }
    }

    @Override
    protected Class<?>[] getConfigurationClasses() {
        return new Class<?>[] { SpringTestConfiguration.class };
    }

    @Override
    protected Class<? extends UI> getUiClass() {
        return SpringTestUI.class;
    }

    @Override
    protected void configureContext(
            AnnotationConfigWebApplicationContext context) {
        if (productionModeProperty != null) {
            context.getEnvironment().getPropertySources().addFirst(
                    new MapPropertySource("spring-ui-unit-test",
                            Collections.singletonMap(
                                    "vaadin.servlet.productionMode",
                                    String.valueOf(productionModeProperty))));
        }
    }

    @Override
    protected DeploymentMode getDeploymentMode() {
        return deploymentMode;
    }

    @Test
    public void mockVaadin_createsSpringManagedUI()
            throws ServiceException {
        UI ui = mockVaadin();

        assertTrue(ui instanceof SpringTestUI);
        SpringTestUI springUi = (SpringTestUI) ui;
        assertNotNull(springUi.getService());
        assertSame(getApplicationContext().getBean(
                ApplicationScopedMockService.class), springUi.getService());
        assertEquals(PushMode.AUTOMATIC,
                springUi.getPushConfiguration().getPushMode());
    }

    @Test
    public void mockVaadin_supportsManuallyProvidedUI()
            throws ServiceException {
        ManualTestUI ui = new ManualTestUI();

        mockVaadin(ui);

        assertSame(ui, UI.getCurrent());
        assertEquals("manual", $(Label.class).id("manual-label").getValue());
    }

    @Test
    public void navigate_usesSpringViewAnnotationAndAutowiresViewChain()
            throws ServiceException {
        mockVaadin();

        TestView view = navigate(TestView.class);
        assertNotNull(view);
        assertNotNull(view.getPresenter());
        assertSame(getApplicationContext().getBean(
                ApplicationScopedMockService.class),
                view.getPresenter().getService());
        assertSame(view.getButton(), $(AutowiredTestButton.class).single());

        test($(AutowiredTestButton.class).single()).click();

        assertEquals("service-message-1",
                $(Label.class).id("spring-message").getValue());
    }

    @Test
    public void navigate_supportsSpringViewNamingConventions()
            throws ServiceException {
        mockVaadin();

        ConventionMappedView view = navigate(ConventionMappedView.class);

        assertNotNull(view);
        assertEquals("Convention mapped", view.getValue());
    }

    @Test
    public void startupRunners_areRunByDefault() throws ServiceException {
        mockVaadin();

        StartupRunnerState state = getApplicationContext()
                .getBean(StartupRunnerState.class);

        assertTrue(state.applicationRunnerCalled);
        assertTrue(state.commandLineRunnerCalled);
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
        productionModeProperty = Boolean.TRUE;

        mockVaadin();

        assertFalse(VaadinService.getCurrent().getDeploymentConfiguration()
                .isProductionMode());
    }

    @Test
    public void mockVaadin_autoDetectsProductionModeFromSpringProperties()
            throws ServiceException {
        productionModeProperty = Boolean.TRUE;

        mockVaadin();

        assertTrue(VaadinService.getCurrent().getDeploymentConfiguration()
                .isProductionMode());
    }

    @Test
    public void tearDown_clearsCurrentInstancesAndClosesSpringContext()
            throws ServiceException {
        mockVaadin();
        AnnotationConfigWebApplicationContext context = getApplicationContext();

        tearDown();

        assertFalse(context.isActive());
        assertNull(UI.getCurrent());
        assertNull(VaadinSession.getCurrent());
        assertNull(VaadinRequest.getCurrent());
        assertNull(VaadinResponse.getCurrent());
        assertNull(VaadinService.getCurrent());
    }

    @SuppressWarnings("serial")
    public static class ManualTestUI extends UI {

        @Override
        protected void init(VaadinRequest request) {
            Label label = new Label("manual");
            label.setId("manual-label");
            setContent(label);
        }
    }
}
