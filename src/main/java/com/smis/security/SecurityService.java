package com.smis.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.vaadin.flow.spring.security.AuthenticationContext;

@Component
public class SecurityService {
	private final AuthenticationContext authenticationContext;
	@Autowired UserDetailsServiceImpl userdetails;
	@Autowired BCryptPasswordEncoder passwordEncoder;	
	//@Autowired Audit audit;
	public SecurityService(AuthenticationContext authenticationContext) {
		this.authenticationContext = authenticationContext;
	}
	
	public UserDetails getAuthenticatedUser1() {
		
		return authenticationContext.getAuthenticatedUser(UserDetails.class).get();
	}
	public UserDetails getAuthenticatedUser2() {
		
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return (UserDetails) principal;
		//SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
	
	public boolean canLogin(String user, String password) {
		return true;
	}
	public void logout() {
		//authenticationContext.
		authenticationContext.logout();
	}

	public UserDetails getAuthenticatedUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated()) {
			Object principal = auth.getPrincipal();
			if (principal instanceof UserDetails) {
				//audit.saveLoginAudit("Login Success", "");
				return (UserDetails) principal;
			}
		}
		return null;
	}

	public UserDetails getUser() {
		final SecurityContext securityContext = SecurityContextHolder.getContext();
		final Object principal = securityContext.getAuthentication().getPrincipal();
		if (principal == null) {
				return null;
		}

		return (UserDetails) principal;
	}
	

}
