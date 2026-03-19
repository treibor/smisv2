package com.smis.security;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

//@Configuration
public class SessionListenerConfig {
	/*@Bean
	public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> sessionTimeoutCustomizer() {
		return factory -> {
			Session session = new Session();
			session.setTimeout(Duration.ofSeconds(63));
			factory.setSession(session);
		};
	}
	*/
}