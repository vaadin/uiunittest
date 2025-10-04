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
import java.util.concurrent.atomic.AtomicInteger;

import com.vaadin.annotations.Push;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.server.VaadinServletResponse;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;
import com.vaadin.testbench.uiunittest.mocks.MockHttpSession;
import com.vaadin.testbench.uiunittest.mocks.MockServletContext;
import com.vaadin.testbench.uiunittest.mocks.MockServletRequest;
import com.vaadin.testbench.uiunittest.mocks.MockServletResponse;
import com.vaadin.testbench.uiunittest.mocks.MockUI;
import com.vaadin.testbench.uiunittest.mocks.MockVaadinService;
import com.vaadin.testbench.uiunittest.mocks.MockVaadinSession;
import com.vaadin.ui.UI;

/**
 * Base class for unit testing complex Vaadin Components and UIs with vanilla
 * Vaadin applications without specific support for CDI or Spring servlet.
 */
@SuppressWarnings({ "java:S3011", "java:S4274" })
public abstract class UIUnitTest extends AbstractUIUnitTest {

    private static final String UI_CAN_T_BE_NULL = "UI can't be null";

    private VaadinServletRequest vaadinRequest;
    private VaadinServletResponse vaadinResponse;
    public static final AtomicInteger mockId = new AtomicInteger(1);
    private MockHttpSession session;

    @Override
    public UI mockVaadin() throws ServiceException {
        MockVaadinSession vaadinSession = getVaadinSession();
        MockUI ui = new MockUI(vaadinSession);
        setUiToSession(vaadinSession, ui);
        MockVaadinService service = (MockVaadinService) vaadinSession
                .getService();
        vaadinRequest = getVaadinRequest();
        MockServletResponse response = new MockServletResponse();
        service.setCurrentInstances(vaadinRequest,
                new VaadinServletResponse(response, service));
        ui.getPage().init(vaadinRequest);
        return ui;
    }

    private void setUiToSession(MockVaadinSession vaadinSession, UI ui) {
        UI.setCurrent(ui);
        Class<?> clazz = ui.getClass();
        while (!clazz.equals(UI.class)) {
            clazz = clazz.getSuperclass();
        }
        try {
            Field uiIdField = clazz.getDeclaredField("uiId");
            uiIdField.setAccessible(true);
            uiIdField.set(ui, mockId.getAndIncrement());
        } catch (NoSuchFieldException | SecurityException
                | IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set uiId field", e);
        }
        vaadinSession.addUI(ui);
    }

    @Override
    public void mockVaadin(UI ui) throws ServiceException {
        assert (ui != null) : UI_CAN_T_BE_NULL;
        MockVaadinSession vaadinSession = getVaadinSession();
        ui.setSession(vaadinSession);
        setUiToSession(vaadinSession, ui);
        MockVaadinService service = (MockVaadinService) vaadinSession
                .getService();
        vaadinRequest = getVaadinRequest();
        MockServletResponse response = new MockServletResponse();
        vaadinResponse = new VaadinServletResponse(response, service);
        service.setCurrentInstances(vaadinRequest, vaadinResponse);
        if (ui.getClass().isAnnotationPresent(Push.class)) {
            Push push = ui.getClass().getAnnotation(Push.class);
            ui.getPushConfiguration().setPushMode(push.value());
            ui.getPushConfiguration().setTransport(push.transport());
        }
        ui.getPage().init(vaadinRequest);
        Class<?> clazz = ui.getClass();
        try {
            Method initMethod = clazz.getDeclaredMethod("init",
                    VaadinRequest.class);
            initMethod.setAccessible(true);
            initMethod.invoke(ui, vaadinRequest);
        } catch (NoSuchMethodException | SecurityException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void tearDown() {
        VaadinSession vaadinSession = VaadinSession.getCurrent();
        UI ui = UI.getCurrent();
        ui.detach();
        ui.close();
        vaadinSession.close();
        VaadinService.getCurrent().setCurrentInstances(null, null);
        VaadinService.setCurrent(null);
        VaadinSession.setCurrent(null);
        UI.setCurrent(null);
        this.session = null;
    }

    /**
     * Get the mock VaadinService or create a new one if it doesn't exist.
     *
     * @return the MockVaadinService
     * @throws ServiceException
     */
    protected MockVaadinService getService() throws ServiceException {
        if (VaadinService.getCurrent() == null) {
            MockVaadinService service = new MockVaadinService();
            VaadinService.setCurrent(service);
            return service;

        }
        return (MockVaadinService) VaadinService.getCurrent();
    }

    /**
     * Get the mock HTTP session or create a new one if it doesn't exist.
     * <p>
     * This can be called before calling mockVaadin() to customize the session,
     * for example to set attributes.
     *
     * @return the MockHttpSession
     */
    protected MockHttpSession getSession() {
        if (session == null) {
            session = new MockHttpSession(new MockServletContext());
        }
        return session;
    }

    /**
     * Get the mock VaadinSession or create a new one if it doesn't exist.
     * <p>
     * This can be called before calling mockVaadin() to customize the session,
     * for example to set attributes.
     *
     * @return the MockVaadinSession
     * @throws ServiceException
     */
    protected MockVaadinSession getVaadinSession() throws ServiceException {
        if (VaadinSession.getCurrent() == null) {
            MockVaadinSession vaadinSession = new MockVaadinSession(
                    getService(), getSession());
            vaadinSession.lock();
            VaadinSession.setCurrent(vaadinSession);
            return vaadinSession;
        }
        return (MockVaadinSession) VaadinSession.getCurrent();
    }

    /**
     * Create a VaadinServletRequest for the mocked HTTP session and service.
     * <p>
     * This can be used as utility to create a VaadinRequest for testing methods
     * that need it.
     *
     * @throws ServiceException
     */
    protected VaadinServletRequest getVaadinRequest() throws ServiceException {
        MockServletRequest request = new MockServletRequest(getSession());
        vaadinRequest = new VaadinServletRequest(request,
                (VaadinServletService) getVaadinSession().getService());
        return vaadinRequest;
    }
}
