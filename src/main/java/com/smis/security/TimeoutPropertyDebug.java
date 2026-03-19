package com.smis.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class TimeoutPropertyDebug {

    @Autowired
    private ConfigurableEnvironment env;
    
    @PostConstruct
    public void printTimeoutSources() {
        System.out.println("ACTIVE PROFILES:");
        for (String profile : env.getActiveProfiles()) {
            System.out.println(" - " + profile);
        }

        System.out.println("RESOLVED server.servlet.session.timeout = "
                + env.getProperty("server.servlet.session.timeout"));
        System.out.println("RESOLVED spring.session.timeout = "
                + env.getProperty("spring.session.timeout"));

        for (PropertySource<?> ps : env.getPropertySources()) {
            Object v1 = ps.getProperty("server.servlet.session.timeout");
            Object v2 = ps.getProperty("spring.session.timeout");

            if (v1 != null || v2 != null) {
                System.out.println("PROPERTY SOURCE: " + ps.getName()
                        + " | server.servlet.session.timeout=" + v1
                        + " | spring.session.timeout=" + v2);
            }
        }
    }
}