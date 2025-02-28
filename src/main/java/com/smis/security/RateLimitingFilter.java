package com.smis.security;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitingFilter implements Filter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();

        // Remove context path to normalize the URI
        String normalizedURI = requestURI.substring(contextPath.length());

        // Exclude specific URLs from rate limiting
        if (!shouldRateLimit(normalizedURI)) {
            chain.doFilter(request, response);
            return;
        }

        Bucket bucket = buckets.computeIfAbsent(normalizedURI, this::createNewBucket);

        if (bucket.tryConsume(1)) {
            // Log the request count
            long availableTokens = bucket.getAvailableTokens();
           
            //Notification.show("Too Many Requests. All requests are now Limited",5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_WARNING);
            chain.doFilter(request, response);
        } else {
            httpResponse.setStatus(429); // Too Many Requests
            httpResponse.getWriter().write("Too many requests");
        }
    }

    @Override
    public void destroy() {
    }

    private boolean shouldRateLimit(String requestURI) {
        // Rate limit only specific URLs
        return requestURI.equals("/login") || requestURI.equals("/")|| requestURI.equals("/dashboard")|| requestURI.endsWith("/")|| requestURI.endsWith("/dashboard");
    }

    private Bucket createNewBucket(String requestURI) {
        int threshold = getThreshold(requestURI);
        Refill refill = Refill.greedy(threshold, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(threshold, refill);
        return Bucket4j.builder().addLimit(limit).build();
    }

    private int getThreshold(String requestURI) {
        // Define different thresholds for different URLs
        switch (requestURI) {
            case "/login":
                return 50; // Max 5 requests per minute for /login
            case "/dashboard":
                return 10; // Max 10 requests per minute for /home
            case "/mlaschemes":
                return 200; // Max 10 requests per minute for /home
            case "/":
                return 100; // Max 10 requests per minute for /home
            default:
                return 100; // Default threshold
        }
    }
}