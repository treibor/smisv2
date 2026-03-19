package com.smis.security;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@Component
public class SessionCreationDebugListener implements HttpSessionListener {

	@Component
	public class SessionListener implements HttpSessionListener {

	    private static final DateTimeFormatter FORMATTER =
	            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
	                             .withZone(ZoneId.systemDefault());

	    private String format(long epochMillis) {
	        return FORMATTER.format(Instant.ofEpochMilli(epochMillis));
	    }

	    @Override
	    public void sessionCreated(HttpSessionEvent se) {
	        System.out.println(
	            "SESSION CREATED | SID=" + se.getSession().getId()
	            + " | TIMEOUT=" + se.getSession().getMaxInactiveInterval()
	            + " | CREATED=" + format(se.getSession().getCreationTime())
	        );
	    }

	    @Override
	    public void sessionDestroyed(HttpSessionEvent se) {
	        System.out.println(
	            "SESSION DESTROYED | SID=" + se.getSession().getId()
	            + " | DESTROYED_AT=" + format(System.currentTimeMillis())
	            + " | LAST_ACCESS=" + format(se.getSession().getLastAccessedTime())
	            + " | TIMEOUT=" + se.getSession().getMaxInactiveInterval()
	        );

	        //new Exception("WHO DESTROYED SESSION?").printStackTrace();
	    }
	}
}
