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

    @Override
    public UI mockVaadin() throws ServiceException {
        MockHttpSession session = new MockHttpSession(new MockServletContext());
        VaadinServletService service = getService();
        MockVaadinSession vaadinSession = new MockVaadinSession(service,
                session);
        vaadinSession.lock();
        VaadinSession.setCurrent(vaadinSession);
        MockUI ui = new MockUI(vaadinSession);
        setUiToSession(vaadinSession, ui);
        MockServletRequest request = new MockServletRequest(session);
        VaadinServletRequest vaadinRequest = new VaadinServletRequest(request,
                (VaadinServletService) vaadinSession.getService());
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
        MockHttpSession session = new MockHttpSession(new MockServletContext());
        VaadinServletService service = getService();
        MockVaadinSession vaadinSession = new MockVaadinSession(service,
                session);
        vaadinSession.lock();
        VaadinSession.setCurrent(vaadinSession);
        ui.setSession(vaadinSession);
        setUiToSession(vaadinSession, ui);
        MockServletRequest request = new MockServletRequest(session);
        vaadinRequest = new VaadinServletRequest(request,
                (VaadinServletService) vaadinSession.getService());
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

    protected VaadinServletService getService() throws ServiceException {
        return new MockVaadinService();
    }

    @Override
    public void tearDown() {
        VaadinSession session = VaadinSession.getCurrent();
        UI ui = UI.getCurrent();
        ui.detach();
        ui.close();
        session.close();
        VaadinService.getCurrent().setCurrentInstances(null, null);
        VaadinService.setCurrent(null);
        VaadinSession.setCurrent(null);
        UI.setCurrent(null);
    }

}
