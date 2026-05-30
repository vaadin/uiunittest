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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.server.ServiceException;
import com.vaadin.testbench.uiunittest.SpringSecurityUIUnitTest;
import com.vaadin.testbench.uiunittest.spring.SpringTestConfiguration;
import com.vaadin.testbench.uiunittest.spring.SpringTestUI;
import com.vaadin.ui.UI;

public class SpringSecurityUIUnitTestTest extends SpringSecurityUIUnitTest {

    @After
    public void cleanup() {
        tearDown();
        assertNull(SecurityContextHolder.getContext().getAuthentication());
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
    protected void configureSecurityContext() {
        authenticate("test-user", "ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    public void authenticate_setsSpringSecurityAuthentication()
            throws ServiceException {
        mockVaadin();

        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        assertNotNull(authentication);
        assertEquals("test-user", authentication.getName());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN"
                        .equals(authority.getAuthority())));
    }
}
