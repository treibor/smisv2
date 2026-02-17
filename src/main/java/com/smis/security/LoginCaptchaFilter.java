package com.smis.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class LoginCaptchaFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !("POST".equalsIgnoreCase(request.getMethod()) &&
                 "/login".equals(request.getServletPath()));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String expected = (session == null) ? null : (String) session.getAttribute(CaptchaController.SESSION_KEY);
        String provided = request.getParameter("captcha");

        boolean ok = expected != null
                && provided != null
                && expected.equalsIgnoreCase(provided.trim());

        // One-time use: remove in both success and failure (if session exists)
        if (session != null) {
            session.removeAttribute(CaptchaController.SESSION_KEY);
        }

        if (!ok) {
            response.sendRedirect("/login?captcha");
            return;
        }

        chain.doFilter(request, response);
    }
}