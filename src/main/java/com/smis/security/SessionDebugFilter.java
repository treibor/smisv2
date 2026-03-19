package com.smis.security;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

//@Component
public class SessionDebugFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String qs = request.getQueryString();
        String full = qs == null ? uri : uri + "?" + qs;

        if (session != null) {
            System.out.println(
                "METHOD=" + method +
                " | URI=" + full +
                " | SID=" + session.getId() +
                " | TIMEOUT=" + session.getMaxInactiveInterval() +
                " | LAST_ACCESS=" + session.getLastAccessedTime() +
                " | NOW=" + System.currentTimeMillis()
            );
        } else {
            System.out.println(
                "METHOD=" + method +
                " | URI=" + full +
                " | NO SESSION"
            );
        }

        filterChain.doFilter(request, response);
    }
}
