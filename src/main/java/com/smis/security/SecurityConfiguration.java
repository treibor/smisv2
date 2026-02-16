package com.smis.security;


import java.util.List;

import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.vaadin.flow.spring.security.VaadinWebSecurity;

import jakarta.servlet.http.HttpServletRequest;

//@EnableWebSecurity

@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {
	
    

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(),
                new HttpSessionSecurityContextRepository()
        );
    }
    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy(SessionRegistry sessionRegistry) {

        // 1) Concurrency control
        ConcurrentSessionControlAuthenticationStrategy concurrent =
                new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry);
        concurrent.setMaximumSessions(1);

        // Choose ONE behavior:
        concurrent.setExceptionIfMaximumExceeded(false); // false = kick old session, allow new login
        // concurrent.setExceptionIfMaximumExceeded(true); // true = block new login if already logged in

        // 2) Session fixation protection (like migrateSession)
        SessionFixationProtectionStrategy fixation = new SessionFixationProtectionStrategy();

        // 3) Register session in SessionRegistry
        RegisterSessionAuthenticationStrategy register =
                new RegisterSessionAuthenticationStrategy(sessionRegistry);

        return new CompositeSessionAuthenticationStrategy(List.of(concurrent, fixation, register));
    }
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().requestMatchers(new AntPathRequestMatcher("/images/*.png"));
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	 super.configure(http);
        http
          .headers(headers -> headers
              .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000))
              .contentTypeOptions(Customizer.withDefaults())
              .frameOptions(frame -> frame.deny())
              .referrerPolicy(ref -> ref.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN))
              .addHeaderWriter(new StaticHeadersWriter("Permissions-Policy", "geolocation=(self), microphone=()"))
          )
          .sessionManagement(session -> session
              .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
              .sessionFixation(sf -> sf.migrateSession())
              .invalidSessionUrl("/login")
              .maximumSessions(1)
                  .sessionRegistry(sessionRegistry())
                  .expiredUrl("/login?expired")
                  .maxSessionsPreventsLogin(false) // kick old, allow new
          )
          .securityContext(sc -> sc.securityContextRepository(securityContextRepository()))
          .logout(logout -> logout
              .logoutUrl("/logout")
              .logoutSuccessUrl("/login?logout")
              .invalidateHttpSession(true)
              .clearAuthentication(true)
              .deleteCookies("JSESSIONID")
          );

        http.exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
            if (isVaadinInternalRequest(request)) {
                response.setStatus(401);
                response.setContentType(MediaType.TEXT_PLAIN_VALUE);
                response.getWriter().write("SESSION_EXPIRED");
            } else {
                // normal browser navigation → go to login page
                response.sendRedirect("/login?expired");
            }
        }));
       
        
        setLoginView(http, Login.class);
    }
    private boolean isVaadinInternalRequest(HttpServletRequest request) {
        // Vaadin sends this header on internal requests (UIDL, heartbeat, push, etc.)
        String vaadinHeader = request.getHeader("X-Vaadin-Request");
        if (vaadinHeader != null) {
            return true;
        }

        // Also match the default Vaadin servlet path
        String uri = request.getRequestURI();
        return uri != null && uri.contains("/vaadinServlet/");
    }
}