package com.smis.security;
import java.io.Serializable;

import org.springframework.stereotype.Component;

import com.vaadin.flow.server.CustomizedSystemMessages;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.SystemMessages;
import com.vaadin.flow.server.SystemMessagesInfo;
import com.vaadin.flow.server.VaadinServiceInitListener;
@Component
public class MyServiceInitListener implements VaadinServiceInitListener, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().setSystemMessagesProvider(systemMessagesInfo -> {
            CustomizedSystemMessages messages = new CustomizedSystemMessages();

            // 1. Handle Session Expiry (Prevents the Red Banner)
            // Setting caption and message to null triggers an immediate redirect 
            // instead of showing the notification banner.
            messages.setSessionExpiredCaption(null);
            messages.setSessionExpiredMessage(null);
            messages.setSessionExpiredURL("login"); 
            messages.setSessionExpiredNotificationEnabled(false);

            // 2. Handle Communication Errors (The "Invalid JSON" case)
            // If the server sends back the Login HTML instead of JSON, 
            // this tells Vaadin to just go to the login page.
           
            return messages;
        });
    }
}