/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Spring UI unit test base with Spring Security authentication helpers.
 */
public abstract class SpringSecurityUIUnitTest extends SpringUIUnitTest {

    @Override
    protected void beforeMockVaadin() {
        super.beforeMockVaadin();
        configureSecurityContext();
    }

    @Override
    public void tearDown() {
        try {
            super.tearDown();
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * Configure the default security context for a test. The default
     * implementation does nothing.
     */
    protected void configureSecurityContext() {
    }

    /**
     * Authenticate with the given username and authorities.
     *
     * @param username
     *            principal name
     * @param authorities
     *            granted authority names
     */
    protected void authenticate(String username, String... authorities) {
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .createAuthorityList(authorities);
        UserDetails principal = new User(username, "n/a", grantedAuthorities);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                principal, null, grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
