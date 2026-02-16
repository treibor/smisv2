package com.smis.security;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;

import com.smis.audit.Audit;
import com.smis.security.captcha.Captcha;
import com.smis.security.captcha.CapthaImpl;
import com.smis.view.HomeView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinServletResponse;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Route("login")
@AnonymousAllowed
public class Login extends VerticalLayout implements BeforeEnterObserver {
	@Autowired
	private SessionAuthenticationStrategy sessionAuthenticationStrategy;
	@Autowired
	Audit audit;
	@Autowired
	private AuthenticationConfiguration authenticationConfiguration;
	@Autowired
	SecurityContextRepository securityRepo;
	private final AuthenticatedUser authenticatedUser;
	HorizontalLayout captchacontainer = new HorizontalLayout();
	Button refreshButton = new Button(new Icon(VaadinIcon.REFRESH));
	Captcha captcha = new CapthaImpl();
	Image image;
	public TextField captchatext = new TextField();
	TextField usernameField = new TextField("User Name");

	PasswordField passwordField = new PasswordField("Password");
	Button button = new Button("Login");
	H3 title = new H3("Scheme MIS 2.0");
	H2 description = new H2("Government of Meghalaya");
	Anchor anchor = new Anchor();
	private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
			.getContextHolderStrategy();
	String dynamicKey = "";

	public Login(AuthenticatedUser authenticatedUser) {
		this.authenticatedUser = authenticatedUser;
		this.dynamicKey = generateDynamicKey();
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);
		setSizeFull();
		add(createPasswordForm());
		getStyle().set("background-color", "hsla(0, 0%, 95%, 0.69)");
	}

	private String generateDynamicKey() {
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder key = new StringBuilder();
		Random rnd = new Random();
		while (key.length() < 5) { // length of the key
			int index = (int) (rnd.nextFloat() * characters.length());
			key.append(characters.charAt(index));
		}
		return key.toString();
	}

	private Component createPasswordForm() {
		captchatext.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);
		// captchacontainer.add(getCaptcha(), refreshButton);
		usernameField.setRequired(true);
		usernameField.setAllowedCharPattern("[0-9A-Za-z@]");
		usernameField.setMinLength(5);
		usernameField.setMaxLength(40);
		passwordField.setRequired(true);
		passwordField.setMinLength(5);
		passwordField.setMaxLength(40);
		captchatext.setPlaceholder("CAPTCHA");
		captchatext.setMaxLength(6);
		captchatext.setMinLength(6);
		captchatext.setMaxWidth("100px");
		captchatext.setHeightFull();
		button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		button.setAutofocus(true);
		anchor.setText("Forgot Password?");
		anchor.getStyle().set("cursor", "pointer");
		anchor.getElement().addEventListener("click",e-> ForgotPassword());
		usernameField.getElement().setAttribute("autocomplete", "off");
		passwordField.getElement().setAttribute("autocomplete", "off");
		button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		button.setAutofocus(true);

		// Press Enter to login
		passwordField.addKeyDownListener(Key.ENTER, e -> button.click());
		usernameField.addKeyDownListener(Key.ENTER, e -> button.click());
		button.addClickListener(e -> {
		    button.setEnabled(false);
		    try {
		        String encryptedUsername = encryptClientSide(usernameField.getValue(), dynamicKey);
		        String encryptedPassword = encryptClientSide(passwordField.getValue(), dynamicKey);
		        doLogin(encryptedUsername, encryptedPassword);
		    } finally {
		        // doLogin() will navigate on success; on failure we re-enable below
		        button.setEnabled(true);
		    }
		});

		anchor.getStyle().set("color", "hsla(211, 100%, 50%, 0.90)").set("padding-bottom", "20px");
		var form = new FormLayout();
		// form.add(title, 1);
		// form.add(description, 1);
		form.add(usernameField, 1);
		form.add(passwordField, 1);

		// form.add(getCaptcha(), 1);
		form.add(new Span(), 1);
		// form.add(captchatext, 1);
		// form.add(, 1);
		form.add(button, 1);
		// form.add(anchor, 1);
		form.setResponsiveSteps(new ResponsiveStep("0", 1), new ResponsiveStep("300px", 1));
		form.setWidth("320px");
		form.getStyle().set("padding", "20px");

		var header = new VerticalLayout();
		title.getStyle().set("color", "white");
		description.getStyle().set("color", "white");
		header.add(title, description);
		header.getStyle().set("background-color", "hsla(211, 100%, 50%, 0.90)");
		header.setAlignItems(Alignment.START);
		header.setJustifyContentMode(JustifyContentMode.END);
		header.setHeight("150px");
		header.getStyle().set("border-radius", "10px 10px 0 0");
		var container = new VerticalLayout();
		container.setSizeUndefined();
		container.getStyle().set("background-color", "white");
		// container.getStyle().set("border", "2px solid black");
		container.getStyle().set("border-radius", "10px");
		container.getStyle().set("padding", "0px");
		container.setAlignItems(Alignment.CENTER);
		container.setJustifyContentMode(JustifyContentMode.CENTER);
		container.add(header, form, anchor);
		/*
		 * var wrapper = new VerticalLayout(); wrapper.setSizeFull();
		 * wrapper.setAlignItems(Alignment.CENTER);
		 * wrapper.setJustifyContentMode(JustifyContentMode.CENTER);
		 * wrapper.add(title,description, container);
		 */
		return container;
	}

	private void doLogin(String encryptedUsername, String encryptedPassword) {

	    String usernameRaw = decryptUsername(encryptedUsername, dynamicKey);
	    String password = decryptPassword(encryptedPassword, dynamicKey);

	    String username = usernameRaw == null ? "" : usernameRaw.trim();
	    if (username.isEmpty() || password == null || password.isEmpty()) {
	        Notification.show("Please enter username and password")
	                .addThemeVariants(NotificationVariant.LUMO_ERROR);
	        button.setEnabled(true);
	        return;
	    }

	    HttpServletRequest req = VaadinServletRequest.getCurrent().getHttpServletRequest();
	    String ip = getClientIp(req);
	    String userAgent = req.getHeader("User-Agent");

	    try {
	        UsernamePasswordAuthenticationToken token =
	                new UsernamePasswordAuthenticationToken(username, password);

	        AuthenticationManager authenticationManager =
	                authenticationConfiguration.getAuthenticationManager();

	        Authentication authentication = authenticationManager.authenticate(token);

	        HttpServletResponse res = VaadinServletResponse.getCurrent().getHttpServletResponse();
	        sessionAuthenticationStrategy.onAuthentication(authentication, req, res);

	        SecurityContext context = SecurityContextHolder.createEmptyContext();
	        context.setAuthentication(authentication);
	        securityContextHolderStrategy.setContext(context);
	        securityRepo.saveContext(context,
	                VaadinServletRequest.getCurrent(),
	                VaadinServletResponse.getCurrent());

	        audit.saveLoginAudit("Login Successfully | ip=" + ip + " | ua=" + safeUa(userAgent), username,"","");
	        UI.getCurrent().navigate(HomeView.class);

	    } catch (SessionAuthenticationException e) {
	        audit.saveLoginAudit("Login Failure - Already logged in | ip=" + ip + " | ua=" + safeUa(userAgent), username,"","");

	        Notification.show("This user is already logged in on another device.")
	                .addThemeVariants(NotificationVariant.LUMO_ERROR);

	        clearFields();
	        button.setEnabled(true);

	    } catch (Exception e) {
	        audit.saveLoginAudit("Login Failure - Invalid credentials | ip=" + ip + " | ua=" + safeUa(userAgent), username,"","");

	        Notification.show("Invalid credentials")
	                .addThemeVariants(NotificationVariant.LUMO_ERROR);

	        clearFields();
	        button.setEnabled(true);
	    }
	}

	private String encryptClientSide(String value, String key) {
		// Implement client-side encryption logic here
		return Base64.getEncoder().encodeToString(value.getBytes());
	}

	private String decryptUsername(String encryptedUsername, String key) {
		// Implement server-side decryption logic here
		return new String(Base64.getDecoder().decode(encryptedUsername));
	}

	private String decryptPassword(String encryptedPassword, String key) {
		// Implement server-side decryption logic here
		return new String(Base64.getDecoder().decode(encryptedPassword));
	}

	

	

	public void ForgotPassword() {
		Dialog aboutdialog = new Dialog();
		Button cancelButton = new Button("Cancel");
		H2 headline = new H2("Forgot Password?");
		// H3 header=new H3("Meghalaya Biodiversity Board");
		// H3 header2=new H3("People's Biodiversity Register (PBR): Version 2.0");
		H5 body = new H5("Please Enter Your Email Id");
		EmailField email = new EmailField();
		email.setPlaceholder("Email");
		email.setMaxLength(20);
		email.setMinLength(5);
		Button submitbutton = new Button("Submit");
		submitbutton.addClickListener(e -> Notification.show("To Be Implemented Using Email API. Public IP Required"));
		headline.getStyle().set("margin", "var(--lumo-space-m) 0 0 0").set("font-size", "1.5em")
				.set("font-weight", "bold").set("text-decoration", "underline");
		cancelButton.addClickListener(e -> aboutdialog.close());
		HorizontalLayout buttonLayout1 = new HorizontalLayout(submitbutton, cancelButton);
		buttonLayout1.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
		VerticalLayout dialogLayout1 = new VerticalLayout(headline, body, email, buttonLayout1);
		dialogLayout1.setPadding(false);
		dialogLayout1.setAlignItems(FlexComponent.Alignment.STRETCH);
		dialogLayout1.getStyle().set("width", "300px").set("max-width", "100%");
		aboutdialog.add(dialogLayout1);
		aboutdialog.open();
	}

	private String getClientIp(HttpServletRequest req) {
	    // If you are behind reverse proxy / load balancer, these headers may be set.
	    // Only trust them if YOU control the proxy.
	    String xff = req.getHeader("X-Forwarded-For");
	    if (xff != null && !xff.isBlank()) {
	        // first IP in list is the client
	        return xff.split(",")[0].trim();
	    }
	    String xri = req.getHeader("X-Real-IP");
	    if (xri != null && !xri.isBlank()) return xri.trim();

	    return req.getRemoteAddr();
	}

	private String safeUa(String ua) {
	    if (ua == null) return "";
	    // avoid huge log entries / DB overflow
	    ua = ua.trim();
	    return ua.length() > 180 ? ua.substring(0, 180) : ua;
	}
	private void clearFields() {
		regenerateCaptcha();
		button.setEnabled(true);
		passwordField.setValue("");
		usernameField.setValue("");
		captchatext.setValue("");
	}

	public Component getCaptcha() {
		image = captcha.getCaptchaImg();
		captchacontainer.add(image, refreshButton, captchatext);
		refreshButton.addClickListener(e -> regenerateCaptcha());
		refreshButton.setTooltipText("Generate Another Captcha");
		captchacontainer.setWidthFull();
		captchacontainer.setJustifyContentMode(JustifyContentMode.CENTER);
		captchacontainer.getStyle().set("padding", "20px");
		return captchacontainer;
	}

	private void regenerateCaptcha() {
		captchacontainer.remove(image);
		captchacontainer.remove(refreshButton);
		captchacontainer.remove(captchatext);
		image = captcha.getCaptchaImg();
		captchacontainer.add(image, refreshButton, captchatext);
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {

	    if (authenticatedUser.get().isPresent()) {
	        event.forwardTo("");
	        return;
	    }

	    Map<String, List<String>> params = event.getLocation()
	            .getQueryParameters().getParameters();

	    if (params.containsKey("kicked")) {
	        Notification.show("You were logged out because this account was used on another device.")
	                .addThemeVariants(NotificationVariant.LUMO_CONTRAST);
	    } else if (params.containsKey("timeout")) {
	        Notification.show("Your session timed out due to inactivity. Please log in again.")
	                .addThemeVariants(NotificationVariant.LUMO_CONTRAST);
	    } else if (params.containsKey("logout")) {
	        Notification.show("You have been logged out.")
	                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
	    }
	}
}