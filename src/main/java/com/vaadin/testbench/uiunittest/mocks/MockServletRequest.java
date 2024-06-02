/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.mocks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import com.vaadin.testbench.uiunittest.Utils;

public class MockServletRequest implements HttpServletRequest {

    private List<Part> partsInt;
    private Cookie[] cookiesInt;
    private MockHttpSession session;
    private Map<String, List<String>> headers = new ConcurrentHashMap<>();
    private Map<String, Object> attributes = new ConcurrentHashMap<>();
    private Map<String, String[]> parameters = new ConcurrentHashMap<>();
    private String characterEncodingInt;
    private Set<Locale> locales = Utils.setOfItems(Locale.UK);

    public MockServletRequest(MockHttpSession session) {
        this.session = session;
        String[] loc = { "" };
        parameters.put("v-loc", loc);
        String[] cw = { "1280" };
        parameters.put("v-cw", cw);
        String[] ch = { "800" };
        parameters.put("v-ch", ch);
        String[] wn = { "window" };
        parameters.put("v-wn", wn);
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    @Override
    public String getCharacterEncoding() {
        return characterEncodingInt;
    }

    @Override
    public void setCharacterEncoding(String env)
            throws UnsupportedEncodingException {
        characterEncodingInt = env;
    }

    @Override
    public int getContentLength() {
        return -1;
    }

    @Override
    public long getContentLengthLong() {
        return -1;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public String getParameter(String name) {
        return parameters.get(name) != null ? parameters.get(name)[0] : null;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        return parameters.get(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return parameters;
    }

    @Override
    public String getProtocol() {
        return "HTTP/1.1";
    }

    @Override
    public String getScheme() {
        return "http";
    }

    @Override
    public String getServerName() {
        return "127.0.0.1";
    }

    @Override
    public int getServerPort() {
        return 8080;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public String getRemoteAddr() {
        return "127.0.0.1";
    }

    @Override
    public String getRemoteHost() {
        return "127.0.0.1";
    }

    @Override
    public void setAttribute(String name, Object o) {
        attributes.put(name, o);
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    @Override
    public Locale getLocale() {
        return locales.iterator().next();
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return Collections.enumeration(locales);
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public String getRealPath(String path) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public int getRemotePort() {
        return 8080;
    }

    @Override
    public String getLocalName() {
        return "localhost";
    }

    @Override
    public String getLocalAddr() {
        return "127.0.0.1";
    }

    @Override
    public int getLocalPort() {
        return 8080;
    }

    @Override
    public ServletContext getServletContext() {
        return getSession().getServletContext();
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest,
            ServletResponse servletResponse) throws IllegalStateException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public DispatcherType getDispatcherType() {
        return DispatcherType.REQUEST;
    }

    @Override
    public String getAuthType() {
        return null;
    }

    @Override
    public Cookie[] getCookies() {
        return cookiesInt;
    }

    @Override
    public long getDateHeader(String name) {
        return -1;
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name) != null && headers.get(name).size() == 1
                ? headers.get(name).get(0)
                : null;
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return headers.get(name) != null
                ? Collections.enumeration(headers.get(name))
                : null;
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(headers.keySet());
    }

    @Override
    public int getIntHeader(String name) {
        return Integer.valueOf(getHeader(name));
    }

    @Override
    public String getMethod() {
        return "GET";
    }

    @Override
    public String getPathInfo() {
        return "";
    }

    @Override
    public String getPathTranslated() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public String getContextPath() {
        return "";
    }

    @Override
    public String getQueryString() {
        return null;
    }

    @Override
    public String getRemoteUser() {
        return null;
    }

    @Override
    public boolean isUserInRole(String role) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Principal getUserPrincipal() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public String getRequestedSessionId() {
        return session.getId();
    }

    @Override
    public String getRequestURI() {
        return "/";
    }

    @Override
    public StringBuffer getRequestURL() {
        return new StringBuffer("http://localhost:8080/");
    }

    @Override
    public String getServletPath() {
        return "";
    }

    @Override
    public HttpSession getSession(boolean create) {
        return session;
    }

    @Override
    public HttpSession getSession() {
        return getSession(true);
    }

    @Override
    public String changeSessionId() {
        String id = UUID.randomUUID().toString();
        session.setId(id);
        return id;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    @Override
    public boolean authenticate(HttpServletResponse response)
            throws IOException, ServletException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void login(String username, String password)
            throws ServletException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void logout() throws ServletException {
        throw new UnsupportedOperationException("not implemented");

    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        if (partsInt != null)
            return partsInt;
        throw new IllegalStateException(
                "Unable to process parts as no multi-part configuration has been provided");
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        if (partsInt == null)
            throw new IllegalStateException(
                    "Unable to process parts as no multi-part configuration has been provided");
        return partsInt.get(0);
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass)
            throws IOException, ServletException {
        throw new UnsupportedOperationException("not implemented");
    }

}
