/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.spring;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.EnableVaadinNavigation;

@Configuration
@EnableVaadin
@EnableVaadinNavigation
@ComponentScan(basePackageClasses = SpringTestConfiguration.class)
public class SpringTestConfiguration {

    @Bean
    public StartupRunnerState startupRunnerState() {
        return new StartupRunnerState();
    }

    @Bean
    public ApplicationRunner applicationRunner(StartupRunnerState state) {
        return args -> state.applicationRunnerCalled = true;
    }

    @Bean
    public CommandLineRunner commandLineRunner(StartupRunnerState state) {
        return args -> state.commandLineRunnerCalled = true;
    }

    public static class StartupRunnerState {

        public boolean applicationRunnerCalled;
        public boolean commandLineRunnerCalled;
    }
}
