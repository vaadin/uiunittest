/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.vaadin.annotations.Push;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.server.Constants;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.ServiceException;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.server.VaadinServletResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.server.SpringUIProvider;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.spring.server.SpringVaadinServletService;
import com.vaadin.testbench.uiunittest.mocks.MockDeploymentConfiguration;
import com.vaadin.testbench.uiunittest.mocks.MockHttpSession;
import com.vaadin.testbench.uiunittest.mocks.MockServletConfig;
import com.vaadin.testbench.uiunittest.mocks.MockServletContext;
import com.vaadin.testbench.uiunittest.mocks.MockServletRequest;
import com.vaadin.testbench.uiunittest.mocks.MockServletResponse;
import com.vaadin.testbench.uiunittest.mocks.MockVaadinSession;
import com.vaadin.ui.UI;

/**
 * Base class for unit testing Vaadin Spring applications.
 */
@SuppressWarnings({ "java:S3011", "java:S4274" })
public abstract class SpringUIUnitTest extends AbstractUIUnitTest {

    private static final String UI_CAN_T_BE_NULL = "UI can't be null";

    private AnnotationConfigWebApplicationContext applicationContext;
    private MockServletContext servletContext;
    private MockHttpSession session;
    private VaadinServletRequest vaadinRequest;
    private VaadinServletResponse vaadinResponse;

    @Override
    public UI mockVaadin() throws ServiceException {
        beforeMockVaadin();
        MockVaadinSession vaadinSession = getVaadinSession();
        SpringUIProvider uiProvider = createAndRegisterUiProvider(
                vaadinSession);
        Class<? extends UI> uiClass = getUiClass();
        if (uiClass == null) {
            uiClass = uiProvider
                    .getUIClass(new UIClassSelectionEvent(vaadinRequest));
        }
        if (uiClass == null) {
            throw new ServiceException(
                    "No Spring UI class found. Override getUiClass() or annotate a UI with @SpringUI.");
        }

        UI ui = createSpringUi(uiProvider, uiClass);
        initializeUi(ui, vaadinSession);
        return ui;
    }

    @Override
    public void mockVaadin(UI ui) throws ServiceException {
        assert (ui != null) : UI_CAN_T_BE_NULL;
        beforeMockVaadin();
        initializeUi(ui, getVaadinSession());
    }

    @Override
    public void tearDown() {
        try {
            UI ui = UI.getCurrent();
            if (ui != null) {
                ui.detach();
                ui.close();
            }
            VaadinSession vaadinSession = VaadinSession.getCurrent();
            if (vaadinSession != null) {
                vaadinSession.close();
            }
            VaadinService vaadinService = VaadinService.getCurrent();
            if (vaadinService != null) {
                vaadinService.setCurrentInstances(null, null);
            }
            VaadinService.setCurrent(null);
            VaadinSession.setCurrent(null);
            UI.setCurrent(null);
        } finally {
            closeApplicationContext();
            session = null;
            vaadinRequest = null;
            vaadinResponse = null;
        }
    }

    /**
     * Spring configuration classes to register in the test context.
     *
     * @return configuration classes
     */
    protected abstract Class<?>[] getConfigurationClasses();

    /**
     * The UI class to create. By default, the class is resolved from
     * {@link SpringUIProvider}.
     *
     * @return UI class or {@code null}
     */
    protected Class<? extends UI> getUiClass() {
        return null;
    }

    /**
     * Customize the Spring web application context before it is refreshed.
     *
     * @param context
     *            Spring context
     */
    protected void configureContext(
            AnnotationConfigWebApplicationContext context) {
    }

    /**
     * Hook invoked before the Spring/Vaadin mock environment is created.
     */
    protected void beforeMockVaadin() {
    }

    /**
     * Hook invoked after the Spring context is refreshed.
     *
     * @param context
     *            Spring context
     */
    protected void afterContextRefresh(
            AnnotationConfigWebApplicationContext context) {
    }

    /**
     * Whether Spring Boot startup runners should be executed after refreshing
     * the context.
     *
     * @return {@code true} by default
     */
    protected boolean runSpringBootStartupRunners() {
        return true;
    }

    /**
     * Resolve deployment mode for the mocked Spring Vaadin service.
     * <p>
     * Default is {@link DeploymentMode#AUTO}: it detects production mode from
     * Spring properties and falls back to debug mode when no property is
     * configured.
     *
     * @return deployment mode
     */
    @Override
    protected DeploymentMode getDeploymentMode() {
        return DeploymentMode.AUTO;
    }

    /**
     * Navigate to a Vaadin Spring view using the view name from
     * {@link SpringView}.
     *
     * @param <T>
     *            view type
     * @param viewClass
     *            target view class
     * @return view instance
     */
    protected <T extends View> T navigate(Class<T> viewClass) {
        SpringView annotation = AnnotatedElementUtils
                .findMergedAnnotation(viewClass, SpringView.class);
        if (annotation == null) {
            throw new IllegalArgumentException(
                    viewClass.getName() + " is not annotated with @SpringView");
        }
        String viewName = deriveViewName(viewClass, annotation);
        if (applicationContext != null) {
            viewName = applicationContext.getEnvironment()
                    .resolvePlaceholders(viewName);
            registerSpringViewFallback(viewName, viewClass);
        }
        return navigate(viewName, viewClass);
    }

    private <T extends View> void registerSpringViewFallback(String viewName,
            Class<T> viewClass) {
        if (UI.getCurrent() == null || UI.getCurrent().getNavigator() == null) {
            return;
        }
        final String targetViewName = viewName;
        UI.getCurrent().getNavigator().addProvider(new ViewProvider() {

            @Override
            public String getViewName(String viewAndParameters) {
                String requestedState = viewAndParameters;
                int slashIndex = viewAndParameters.indexOf('/');
                if (slashIndex >= 0) {
                    requestedState = viewAndParameters.substring(0, slashIndex);
                }
                return targetViewName.equals(requestedState) ? targetViewName
                        : null;
            }

            @Override
            public View getView(String requestedViewName) {
                if (!targetViewName.equals(requestedViewName)) {
                    return null;
                }
                return applicationContext.getAutowireCapableBeanFactory()
                        .createBean(viewClass);
            }
        });
    }

    /**
     * Get the active Spring application context.
     *
     * @return Spring context or {@code null}
     */
    protected AnnotationConfigWebApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * Get the mock servlet context used by the Spring context and Vaadin
     * service.
     *
     * @return mock servlet context
     */
    protected MockServletContext getServletContext() {
        if (servletContext == null) {
            servletContext = new MockServletContext();
        }
        return servletContext;
    }

    /**
     * Get the mock HTTP session or create a new one if it doesn't exist.
     *
     * @return mock HTTP session
     */
    protected MockHttpSession getSession() {
        if (session == null) {
            session = new MockHttpSession(getServletContext());
        }
        return session;
    }

    /**
     * Get the mock Vaadin service or create a new one if it doesn't exist.
     *
     * @return Spring Vaadin service
     * @throws ServiceException
     *             if service initialization fails
     */
    protected SpringVaadinServletService getService() throws ServiceException {
        VaadinService currentService = VaadinService.getCurrent();
        if (currentService instanceof SpringVaadinServletService) {
            return (SpringVaadinServletService) currentService;
        }
        if (currentService != null) {
            clearNonSpringCurrentInstances();
        }

        ensureApplicationContext();
        boolean productionMode = resolveProductionMode();
        MockDeploymentConfiguration deploymentConfiguration =
            createDeploymentConfiguration(productionMode);
        SpringMockVaadinServlet servlet = new SpringMockVaadinServlet();
        servlet.setProductionMode(productionMode);
        SpringMockVaadinService service = new SpringMockVaadinService(
            servlet, deploymentConfiguration, getServletContext());
        VaadinService.setCurrent(service);
        return service;
    }

    /**
     * Get the mock Vaadin session or create a new one if it doesn't exist.
     *
     * @return mock Vaadin session
     * @throws ServiceException
     *             if service initialization fails
     */
    protected MockVaadinSession getVaadinSession() throws ServiceException {
        VaadinSession currentSession = VaadinSession.getCurrent();
        if (currentSession instanceof MockVaadinSession
            && currentSession
                .getService() instanceof SpringVaadinServletService) {
            return (MockVaadinSession) currentSession;
        }
        if (currentSession != null) {
            clearNonSpringCurrentInstances();
        }

        MockVaadinSession vaadinSession = new MockVaadinSession(getService(),
            getSession());
        vaadinSession.lock();
        VaadinSession.setCurrent(vaadinSession);
        vaadinRequest = getVaadinRequest();
        vaadinResponse = new VaadinServletResponse(new MockServletResponse(),
            (VaadinServletService) vaadinSession.getService());
        vaadinSession.getService().setCurrentInstances(vaadinRequest,
            vaadinResponse);
        return vaadinSession;
    }

        private void clearNonSpringCurrentInstances() {
        VaadinService currentService = VaadinService.getCurrent();
        if (currentService != null) {
            currentService.setCurrentInstances(null, null);
        }
        UI.setCurrent(null);
        VaadinSession.setCurrent(null);
        VaadinService.setCurrent(null);
        }

        private boolean resolveProductionMode() {
            DeploymentMode mode = getDeploymentMode();
            if (mode == DeploymentMode.PRODUCTION) {
                return true;
            }
            if (mode == DeploymentMode.DEBUG) {
                return false;
            }
            if (applicationContext == null) {
                return false;
            }
            String value = applicationContext.getEnvironment().getProperty(
                    "vaadin.servlet.productionMode");
            if (value == null) {
                value = applicationContext.getEnvironment().getProperty(
                        "vaadin.servlet.production-mode");
            }
            if (value == null) {
                value = applicationContext.getEnvironment().getProperty(
                        Constants.SERVLET_PARAMETER_PRODUCTION_MODE);
            }
            return Boolean.parseBoolean(value);
        }

        private MockDeploymentConfiguration createDeploymentConfiguration(
                boolean productionMode) {
            MockDeploymentConfiguration configuration = new MockDeploymentConfiguration();
            configuration.setProductionMode(productionMode);
            configuration.setInitParameter(Constants.SERVLET_PARAMETER_PRODUCTION_MODE,
                    String.valueOf(productionMode));
            return configuration;
        }

    /**
     * Create a Vaadin servlet request for the mocked HTTP session and Spring
     * Vaadin service.
     *
     * @return Vaadin servlet request
     * @throws ServiceException
     *             if service initialization fails
     */
    protected VaadinServletRequest getVaadinRequest() throws ServiceException {
        MockServletRequest request = new MockServletRequest(getSession());
        if (applicationContext != null) {
            request.setAttribute(
                    WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
                    applicationContext);
        }
        vaadinRequest = new VaadinServletRequest(request,
                (VaadinServletService) getVaadinSession().getService());
        return vaadinRequest;
    }

    private void ensureApplicationContext() throws ServiceException {
        if (applicationContext != null) {
            return;
        }
        applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.setServletContext(getServletContext());
        configureContext(applicationContext);
        applicationContext.register(getConfigurationClasses());
        getServletContext().setAttribute(
                WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
                applicationContext);
        applicationContext.refresh();
        afterContextRefresh(applicationContext);
        if (runSpringBootStartupRunners()) {
            runStartupRunners(applicationContext);
        }
    }

    private void runStartupRunners(ConfigurableApplicationContext context)
            throws ServiceException {
        ApplicationArguments arguments = new DefaultApplicationArguments(
                new String[0]);
        List<Object> runners = new ArrayList<>();
        runners.addAll(context.getBeansOfType(ApplicationRunner.class).values());
        runners.addAll(
                context.getBeansOfType(CommandLineRunner.class).values());
        AnnotationAwareOrderComparator.sort(runners);
        for (Object runner : runners) {
            try {
                if (runner instanceof ApplicationRunner) {
                    ((ApplicationRunner) runner).run(arguments);
                } else {
                    ((CommandLineRunner) runner)
                            .run(arguments.getSourceArgs());
                }
            } catch (Exception e) {
                throw new ServiceException(
                        "Failed to run Spring Boot startup runner", e);
            }
        }
    }

    private SpringUIProvider createAndRegisterUiProvider(
            MockVaadinSession vaadinSession) {
        SpringUIProvider uiProvider = new SpringUIProvider(vaadinSession);
        vaadinSession.addUIProvider(uiProvider);
        return uiProvider;
    }

    private UI createSpringUi(SpringUIProvider uiProvider,
            Class<? extends UI> uiClass) throws ServiceException {
        if (applicationContext.getBeanNamesForType(uiClass).length == 0) {
            return createPlainUi(uiClass);
        }
        return uiProvider
                .createInstance(new UICreateEvent(vaadinRequest, uiClass,
                        UIUnitTest.mockId.get()));
    }

    private UI createPlainUi(Class<? extends UI> uiClass)
            throws ServiceException {
        try {
            return uiClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ServiceException("Failed to create UI " + uiClass, e);
        }
    }

    private void initializeUi(UI ui, MockVaadinSession vaadinSession)
            throws ServiceException {
        ui.setSession(vaadinSession);
        setUiToSession(vaadinSession, ui);
        applyPushConfiguration(ui);
        ui.getPage().init(vaadinRequest);
        invokeUiInit(ui);
    }

    private void setUiToSession(MockVaadinSession vaadinSession, UI ui)
            throws ServiceException {
        UI.setCurrent(ui);
        Class<?> clazz = ui.getClass();
        while (!clazz.equals(UI.class)) {
            clazz = clazz.getSuperclass();
        }
        try {
            Field uiIdField = clazz.getDeclaredField("uiId");
            uiIdField.setAccessible(true);
            uiIdField.set(ui, UIUnitTest.mockId.getAndIncrement());
        } catch (NoSuchFieldException | SecurityException
                | IllegalArgumentException | IllegalAccessException e) {
            throw new ServiceException("Failed to set uiId field", e);
        }
        vaadinSession.addUI(ui);
    }

    private void applyPushConfiguration(UI ui) {
        if (ui.getClass().isAnnotationPresent(Push.class)) {
            Push push = ui.getClass().getAnnotation(Push.class);
            ui.getPushConfiguration().setPushMode(push.value());
            ui.getPushConfiguration().setTransport(push.transport());
        }
    }

    private void invokeUiInit(UI ui) throws ServiceException {
        try {
            Method initMethod = findUiInitMethod(ui.getClass());
            initMethod.setAccessible(true);
            initMethod.invoke(ui, vaadinRequest);
        } catch (SecurityException | IllegalAccessException
                | IllegalArgumentException e) {
            throw new ServiceException("Failed to initialize UI", e);
        } catch (InvocationTargetException e) {
            throw new ServiceException("Failed to initialize UI",
                    e.getTargetException());
        }
    }

    private Method findUiInitMethod(Class<?> uiClass) throws ServiceException {
        Class<?> clazz = uiClass;
        while (clazz != null) {
            try {
                return clazz.getDeclaredMethod("init", VaadinRequest.class);
            } catch (NoSuchMethodException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new ServiceException("Failed to find UI init method");
    }

    private String deriveViewName(Class<?> viewClass, SpringView annotation) {
        if (!SpringView.USE_CONVENTIONS.equals(annotation.name())) {
            return annotation.name();
        }
        String simpleName = viewClass.getSimpleName().replaceFirst("View$", "");
        return upperCamelToLowerHyphen(simpleName);
    }

    private String upperCamelToLowerHyphen(String string) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (Character.isUpperCase(c)) {
                c = Character.toLowerCase(c);
                if (shouldPrependHyphen(string, i)) {
                    sb.append('-');
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private boolean shouldPrependHyphen(String string, int index) {
        if (index == 0) {
            return false;
        }
        if (!Character.isUpperCase(string.charAt(index - 1))) {
            return true;
        }
        return index + 1 < string.length()
                && !Character.isUpperCase(string.charAt(index + 1));
    }

    private void closeApplicationContext() {
        if (servletContext != null) {
            servletContext.removeAttribute(
                    WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        }
        if (applicationContext != null) {
            applicationContext.close();
            applicationContext = null;
        }
        servletContext = null;
    }

    @SuppressWarnings("serial")
    private static class SpringMockVaadinService
            extends SpringVaadinServletService {

        SpringMockVaadinService(SpringMockVaadinServlet servlet,
                DeploymentConfiguration deploymentConfiguration,
                MockServletContext servletContext) throws ServiceException {
            super(servlet, deploymentConfiguration, null);
            try {
                servlet.setServletService(this);
                servlet.init(new MockServletConfig(servletContext));
                init();
            } catch (ServletException e) {
                throw new ServiceException(
                        "Failed to initialize Spring Vaadin servlet", e);
            }
        }

        @Override
        protected List<RequestHandler> createRequestHandlers()
                throws ServiceException {
            return Collections.emptyList();
        }

        @Override
        protected boolean isAtmosphereAvailable() {
            return true;
        }
    }

    @SuppressWarnings("serial")
    private static class SpringMockVaadinServlet extends SpringVaadinServlet {

        private SpringMockVaadinService service;
        private boolean productionMode = false;

        @Override
        protected VaadinServletService createServletService(
                DeploymentConfiguration deploymentConfiguration)
                throws ServiceException {
            return service;
        }

        @Override
        protected DeploymentConfiguration createDeploymentConfiguration(
                Properties initParameters) {
            initParameters.setProperty(
                    Constants.SERVLET_PARAMETER_PRODUCTION_MODE,
                    String.valueOf(productionMode));
            return super.createDeploymentConfiguration(initParameters);
        }

        void setServletService(SpringMockVaadinService service) {
            this.service = service;
        }

        void setProductionMode(boolean productionMode) {
            this.productionMode = productionMode;
        }
    }
}
