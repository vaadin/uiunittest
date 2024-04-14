/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.views;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.vaadin.testbench.uiunittest.TestView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class PushTestView extends TestView {
    public static final String NAME = "push";

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private CompletableFuture<Void> future;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Component getComponent() {
        VerticalLayout layout = new VerticalLayout();
        Label label = new Label("");
        label.setId("push-label");

        Button button = new Button("Spin");
        button.setId("spin-button");
        button.setDisableOnClick(true);
        button.addClickListener(e -> {
            label.addStyleName(ValoTheme.LABEL_SPINNER);
            future = getStringWithDelay().thenAccept(text -> {
                getUI().access(() -> {
                    label.removeStyleName(ValoTheme.LABEL_SPINNER);
                    label.setValue(text);
                    button.setEnabled(true);
                });
            });
        });

        layout.addComponents(label, button);
        return layout;
    }

    private CompletableFuture<String> getStringWithDelay() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Hello";
        }, executor);
    }
}
