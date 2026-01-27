package com.smis.security;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//@Component
public class CSPNonceFilter extends OncePerRequestFilter {

    private static final SecureRandom secureRandom = new SecureRandom();

    private String generateNonce() {
        byte[] nonce = new byte[16];
        secureRandom.nextBytes(nonce);
        return Base64.getEncoder().encodeToString(nonce);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException {

        String nonce = generateNonce();
        request.setAttribute("cspNonce", nonce);

        String csp = "default-src 'self'; " +
                     "script-src 'self' 'nonce-" + nonce + "'; " +
                     "style-src 'self' 'nonce-" + nonce + "'; " +
                     "object-src 'none'; " +
                     "img-src 'self' data:; " +
                     "font-src 'self' data:; " +
                     "connect-src 'self'; " +
                     "frame-ancestors 'self'; " +
                     "base-uri 'self';";

        response.setHeader("Content-Security-Policy", csp);

        filterChain.doFilter(request, response);
    }
}