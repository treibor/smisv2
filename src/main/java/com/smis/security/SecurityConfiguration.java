package com.smis.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;
import com.vaadin.flow.spring.security.VaadinWebSecurity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//@EnableWebSecurity

@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {
	@Autowired
	private RateLimitingFilter rateLimitingFilter;
	@Autowired
	private LoginCaptchaFilter loginCaptchaFilter;
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
	    return new HttpSessionSecurityContextRepository();
	}

	

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().requestMatchers(new AntPathRequestMatcher("/images/*.png"));
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class);
	    http.addFilterBefore(loginCaptchaFilter,
	            org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

	    http.headers(headers -> headers
	            .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000))
	            .contentTypeOptions(Customizer.withDefaults())
	            .frameOptions(frame -> frame.deny())
	            .referrerPolicy(ref -> ref.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN))
	            .addHeaderWriter(new StaticHeadersWriter("Permissions-Policy", "geolocation=(self), microphone=()"))
	            .addHeaderWriter(new StaticHeadersWriter("Content-Security-Policy",
	                    "default-src 'self'; " +
	                    "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
	                    "style-src 'self' 'unsafe-inline'; " +
	                    "img-src 'self' data:; " +
	                    "font-src 'self' data:; " +
	                    "connect-src 'self' ws: wss:; " +
	                    "object-src 'none'; " +
	                    "frame-ancestors 'none'; " +
	                    "base-uri 'self'; " +
	                    "form-action 'self'"))
	    );

	    http
	      .securityContext(sc -> sc.securityContextRepository(securityContextRepository()))
	      .sessionManagement(session -> session
	            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
	            .sessionFixation(sf -> sf.migrateSession())
	            .maximumSessions(1)
	                .sessionRegistry(sessionRegistry())
	                .expiredUrl("/login?expired")
	                .maxSessionsPreventsLogin(false)
	      )
	      .logout(logout -> logout
	            .logoutUrl("/logout")
	            .logoutSuccessUrl("/login?logout")
	            .invalidateHttpSession(true)
	            .clearAuthentication(true)
	            .deleteCookies("JSESSIONID")
	      );

	    http.exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
	        if (isVaadinInternalRequest(request)) {
	            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	        } else {
	            response.sendRedirect("/login"); // <-- key change
	        }
	    }));

	    http.authorizeHttpRequests(auth -> auth
	    	    .requestMatchers(new AntPathRequestMatcher("/captcha-image")).permitAll()
	    	    .requestMatchers(new AntPathRequestMatcher("/images/*.png")).permitAll()
	    	    .requestMatchers(new AntPathRequestMatcher("/login", "POST")).permitAll()
	    	
	    	    .requestMatchers(new AntPathRequestMatcher("/**", "PUT")).denyAll()
	    	    .requestMatchers(new AntPathRequestMatcher("/**", "DELETE")).denyAll()
	    	    .requestMatchers(new AntPathRequestMatcher("/**", "PATCH")).denyAll()
	    	    .requestMatchers(new AntPathRequestMatcher("/**", "TRACE")).denyAll()
	    	);

	    super.configure(http);
	   
	    setLoginView(http, Login.class);
	}

	private boolean isVaadinInternalRequest(HttpServletRequest request) {
	    return request.getHeader("X-Vaadin-Request") != null;
	}
}