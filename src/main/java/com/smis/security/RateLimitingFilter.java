package com.smis.security;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket loginIpBucket() {
        Bandwidth limit = Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    private Bucket loginUserBucket() {
        Bandwidth limit = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    private Bucket captchaBucket() {
        Bandwidth limit = Bandwidth.classic(30, Refill.intervally(30, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();   // ✅ safer than getRequestURI()
        String method = request.getMethod();

        if ("POST".equalsIgnoreCase(method) && "/login".equals(path)) return false;
        if ("GET".equalsIgnoreCase(method) && "/captcha-image".equals(path)) return false;

        return true;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getServletPath();
        String ip = clientIp(request);

        if ("/captcha-image".equals(path)) {
            Bucket b = buckets.computeIfAbsent("captcha:" + ip, k -> captchaBucket());

            var probe = b.tryConsumeAndReturnRemaining(1);
            if (!probe.isConsumed()) {
                long retrySeconds = nanosToSecondsCeil(probe.getNanosToWaitForRefill());
                reject(response, "Too many captcha refreshes. Please slow down.", retrySeconds);
                return;
            }

            filterChain.doFilter(request, response);
            return;
        }

        if ("/login".equals(path)) {
            // 1) IP limiter
            Bucket ipBucket = buckets.computeIfAbsent("login-ip:" + ip, k -> loginIpBucket());
            var ipProbe = ipBucket.tryConsumeAndReturnRemaining(1);
            if (!ipProbe.isConsumed()) {
                long retrySeconds = nanosToSecondsCeil(ipProbe.getNanosToWaitForRefill());
                reject(response, "Too many login attempts from your network. Please wait and try again.", retrySeconds);
                return;
            }

            // 2) Username limiter
            String username = request.getParameter("username");
            if (username != null && !username.isBlank()) {
                String u = username.trim().toLowerCase();
                Bucket userBucket = buckets.computeIfAbsent("login-user:" + u, k -> loginUserBucket());
                var userProbe = userBucket.tryConsumeAndReturnRemaining(1);
                if (!userProbe.isConsumed()) {
                    long retrySeconds = nanosToSecondsCeil(userProbe.getNanosToWaitForRefill());
                    reject(response, "Too many login attempts for this username. Please wait and try again.", retrySeconds);
                    return;
                }
            }

            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void reject(HttpServletResponse response, String msg, long retryAfterSeconds) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("text/plain;charset=UTF-8");
        response.setHeader("Retry-After", String.valueOf(Math.max(1, retryAfterSeconds)));
        response.setHeader("Cache-Control", "no-store");
        response.getWriter().write(msg);
    }

    private long nanosToSecondsCeil(long nanos) {
        if (nanos <= 0) return 1;
        return (nanos + 999_999_999L) / 1_000_000_000L; // ceil
    }

    private String clientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            String first = xff.split(",")[0].trim();
            if (!first.isBlank()) return first;
        }
        return request.getRemoteAddr();
    }
}