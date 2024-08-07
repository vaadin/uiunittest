/*
 * Copyright (C) 2000-2024 Vaadin Ltd
*
* This program is available under Vaadin Commercial License and Service Terms.
*
* See <https://vaadin.com/commercial-license-and-service-terms> for the full
* license.
*/
package com.vaadin.testbench.uiunittest.mocks;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.vaadin.server.AbstractDeploymentConfiguration;
import com.vaadin.shared.communication.PushMode;

@SuppressWarnings("serial")
public class MockDeploymentConfiguration
        extends AbstractDeploymentConfiguration {

    private boolean productionMode = false;
    private boolean xsrfProtectionEnabled = true;

    private int resourceCacheTime = 12;
    private int heartbeatInterval = 300;
    private boolean closeIdleSessions = false;
    private PushMode pushMode = PushMode.DISABLED;
    private final Properties initParameters = new Properties();
    private final Map<String, String> applicationOrSystemProperty = new HashMap<>();
    private boolean syncIdCheckEnabled = true;
    private final boolean sendUrlsAsParameters = true;

    @Override
    public boolean isProductionMode() {
        return productionMode;
    }

    public void setProductionMode(boolean productionMode) {
        this.productionMode = productionMode;
    }

    @Override
    public boolean isXsrfProtectionEnabled() {
        return xsrfProtectionEnabled;
    }

    @Override
    public boolean isSyncIdCheckEnabled() {
        return syncIdCheckEnabled;
    }

    public void setSyncIdCheckEnabled(boolean syncIdCheckEnabled) {
        this.syncIdCheckEnabled = syncIdCheckEnabled;
    }

    public void setXsrfProtectionEnabled(boolean xsrfProtectionEnabled) {
        this.xsrfProtectionEnabled = xsrfProtectionEnabled;
    }

    @Override
    public int getResourceCacheTime() {
        return resourceCacheTime;
    }

    public void setResourceCacheTime(int resourceCacheTime) {
        this.resourceCacheTime = resourceCacheTime;
    }

    @Override
    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    @Override
    public boolean isCloseIdleSessions() {
        return closeIdleSessions;
    }

    public void setCloseIdleSessions(boolean closeIdleSessions) {
        this.closeIdleSessions = closeIdleSessions;
    }

    @Override
    public PushMode getPushMode() {
        return pushMode;
    }

    public void setPushMode(PushMode pushMode) {
        this.pushMode = pushMode;
    }

    @Override
    public Properties getInitParameters() {
        return initParameters;
    }

    public void setInitParameter(String key, String value) {
        initParameters.setProperty(key, value);
    }

    public void setApplicationOrSystemProperty(String key, String value) {
        applicationOrSystemProperty.put(key, value);
    }

    @Override
    public String getApplicationOrSystemProperty(String propertyName,
            String defaultValue) {
        if (applicationOrSystemProperty.containsKey(propertyName)) {
            return applicationOrSystemProperty.get(propertyName);
        } else {
            return defaultValue;
        }
    }

    @Override
    public boolean isSendUrlsAsParameters() {
        return sendUrlsAsParameters;
    }

}