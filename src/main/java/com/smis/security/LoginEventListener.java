package com.smis.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.smis.dbservice.AuditService;

@Component
public class LoginEventListener {

    @Autowired AuditService auditService;

    @EventListener
    public void onSuccess(org.springframework.security.authentication.event.AuthenticationSuccessEvent e) {
        var auth = e.getAuthentication();
        String username = auth.getName();
        String roles = auth.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .distinct().sorted()
                .collect(java.util.stream.Collectors.joining(", "));

        // IP is not in the event, get it from current request:
        var reqAttr = (org.springframework.web.context.request.ServletRequestAttributes)
                org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
        if (reqAttr == null) return;

        var request = reqAttr.getRequest();
        String ip = auditService.getRealClientIp(request);
        //System.out.println("AAAAAAAAAAAAAAAAAA");
        auditService.saveAuthAudit("Login", "Login Success", username, "Roles:" + roles, ip);
    }

    @EventListener
    public void onFailure(org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent e) {
        String username = String.valueOf(e.getAuthentication().getPrincipal());
        var reqAttr = (org.springframework.web.context.request.ServletRequestAttributes)
                org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
        if (reqAttr == null) return;

        var request = reqAttr.getRequest();
        String ip = auditService.getRealClientIp(request);

        auditService.saveAuthAudit("Login", "Login Fail", username,
                "Reason=" + e.getException().getClass().getSimpleName(), ip);
    }
}