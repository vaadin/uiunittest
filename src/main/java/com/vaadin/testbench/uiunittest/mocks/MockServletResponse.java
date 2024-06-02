/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.mocks;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.vaadin.testbench.uiunittest.Utils;

public class MockServletResponse implements HttpServletResponse {

    private String contentType;
    private int bufferSize;
    private int status = 200;
    private Map<String, List<String>> headers = new ConcurrentHashMap<String, List<String>>();
    private String characterEncoding = "ISO-8859-1";
    private Locale locale;

    @Override
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void setCharacterEncoding(String charset) {
        this.characterEncoding = charset;
    }

    @Override
    public void setContentLength(int len) {
        // NOP
    }

    @Override
    public void setContentLengthLong(long len) {
        // NOP
    }

    @Override
    public void setContentType(String type) {
        this.contentType = type;

    }

    @Override
    public void setBufferSize(int size) {
        this.bufferSize = size;
    }

    @Override
    public int getBufferSize() {
        return bufferSize;
    }

    @Override
    public void flushBuffer() throws IOException {
    }

    @Override
    public void resetBuffer() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean isCommitted() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void setLocale(Locale loc) {
        this.locale = loc;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public void addCookie(Cookie cookie) {
        // NOP
    }

    @Override
    public boolean containsHeader(String name) {
        return headers.containsKey(name);
    }

    @Override
    public String encodeURL(String url) {
        return encodeUrl(url);
    }

    @Override
    public String encodeRedirectURL(String url) {
        return encodeUrl(url);
    }

    @Override
    public String encodeUrl(String url) {
        return url;
    }

    @Override
    public String encodeRedirectUrl(String url) {
        return encodeUrl(url);
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        throw new IOException("The app requests a failure: " + sc + " " + msg);
    }

    @Override
    public void sendError(int sc) throws IOException {
        throw new IOException("The app requests a failure: " + sc);
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void setDateHeader(String name, long date) {
        setHeader(name, "" + date);
    }

    @Override
    public void addDateHeader(String name, long date) {
        addHeader(name, "" + date);
    }

    @Override
    public void setHeader(String name, String value) {
        headers.put(value, Utils.listOfItems(value));
    }

    @Override
    public void addHeader(String name, String value) {
        Collection<String> h = getHeaders(name);
        if (h != null) {
            h.add(value);
        } else {
            setHeader(name, value);
        }
    }

    @Override
    public void setIntHeader(String name, int value) {
        setHeader(name, "" + value);
    }

    @Override
    public void addIntHeader(String name, int value) {
        addHeader(name, "" + value);
    }

    @Override
    public void setStatus(int sc) {
        this.status = sc;
    }

    @Override
    public void setStatus(int sc, String sm) {
        this.status = sc;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name) != null ? headers.get(name).get(0) : null;
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return headers.get(name);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return headers.keySet();
    }

}
