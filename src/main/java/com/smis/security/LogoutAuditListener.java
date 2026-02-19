package com.smis.security;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.smis.dbservice.AuditService;

@Component
public class LogoutAuditListener {

    private final AuditService auditService;

    public LogoutAuditListener(AuditService auditService) {
        this.auditService = auditService;
    }

    @EventListener
    public void onLogoutSuccess(LogoutSuccessEvent event) {
        Authentication auth = event.getAuthentication();

        String username = (auth != null) ? auth.getName() : "UNKNOWN";
        String roles = (auth != null)
                ? auth.getAuthorities().stream()
                    .map(a -> a.getAuthority())
                    .distinct()
                    .sorted()
                    .collect(java.util.stream.Collectors.joining(", "))
                : "—";

        // No HttpServletRequest here, so IP/UA is not available.
        // (We can solve that by storing IP in session during login if you really need it.)
        auditService.saveAuthAudit("Logout","Logout Success", username, roles, "");
    }
}