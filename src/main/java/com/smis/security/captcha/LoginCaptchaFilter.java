package com.smis.security.captcha;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;



@Component
public class LoginCaptchaFilter extends OncePerRequestFilter {

    private final boolean captchaEnabled;

    public LoginCaptchaFilter(Environment env) {
        this.captchaEnabled = env.getProperty(
                "app.login.captcha-enabled",
                Boolean.class,
                true
        );
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!captchaEnabled) {
            return true;
        }

        return !("POST".equalsIgnoreCase(request.getMethod())
                && "/login".equals(request.getServletPath()));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String expected = (session == null) ? null
                : (String) session.getAttribute(CaptchaController.SESSION_KEY);
        String provided = request.getParameter("captcha");

        boolean ok = expected != null
                && provided != null
                && expected.equalsIgnoreCase(provided.trim());

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