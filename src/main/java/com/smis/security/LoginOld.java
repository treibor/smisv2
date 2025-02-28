///*
// * package com.smis.security;
// * 
// * import java.util.List; import java.util.Random;
// * 
// * import org.springframework.beans.factory.annotation.Autowired; import
// * org.springframework.security.authentication.AuthenticationManager; import
// * org.springframework.security.authentication.
// * UsernamePasswordAuthenticationToken; import
// * org.springframework.security.core.Authentication; import
// * org.springframework.security.core.context.SecurityContext; import
// * org.springframework.security.core.context.SecurityContextHolder; import
// * org.springframework.security.core.context.SecurityContextHolderStrategy;
// * import org.springframework.security.core.session.SessionInformation; import
// * org.springframework.security.core.session.SessionRegistry; import
// * org.springframework.security.core.userdetails.UserDetails; import
// * org.springframework.security.web.context.SecurityContextRepository;
// * 
// * import com.smis.audit.Audit; import com.smis.security.captcha.Captcha; import
// * com.smis.security.captcha.CapthaImpl; import com.smis.view.HomeView; import
// * com.vaadin.flow.component.Component; import com.vaadin.flow.component.UI;
// * import com.vaadin.flow.component.button.Button; import
// * com.vaadin.flow.component.button.ButtonVariant; import
// * com.vaadin.flow.component.dialog.Dialog; import
// * com.vaadin.flow.component.formlayout.FormLayout; import
// * com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep; import
// * com.vaadin.flow.component.html.Anchor; import
// * com.vaadin.flow.component.html.H1; import com.vaadin.flow.component.html.H2;
// * import com.vaadin.flow.component.html.H3; import
// * com.vaadin.flow.component.html.H5; import
// * com.vaadin.flow.component.html.Image; import
// * com.vaadin.flow.component.html.Span; import
// * com.vaadin.flow.component.icon.Icon; import
// * com.vaadin.flow.component.icon.VaadinIcon; import
// * com.vaadin.flow.component.notification.Notification; import
// * com.vaadin.flow.component.notification.NotificationVariant; import
// * com.vaadin.flow.component.orderedlayout.FlexComponent; import
// * com.vaadin.flow.component.orderedlayout.HorizontalLayout; import
// * com.vaadin.flow.component.orderedlayout.VerticalLayout; import
// * com.vaadin.flow.component.textfield.EmailField; import
// * com.vaadin.flow.component.textfield.PasswordField; import
// * com.vaadin.flow.component.textfield.TextField; import
// * com.vaadin.flow.router.BeforeEnterEvent; import
// * com.vaadin.flow.router.BeforeEnterObserver; import
// * com.vaadin.flow.router.Route; import com.vaadin.flow.server.VaadinService;
// * import com.vaadin.flow.server.VaadinServletRequest; import
// * com.vaadin.flow.server.VaadinServletResponse; import
// * com.vaadin.flow.server.WrappedSession; import
// * com.vaadin.flow.server.auth.AnonymousAllowed;
// * 
// * import elemental.json.JsonObject;
// * 
// * @Route("login")
// * 
// * @AnonymousAllowed public class LoginOld extends VerticalLayout implements
// * BeforeEnterObserver {
// * 
// * @Autowired private Audit audit;
// * 
// * @Autowired SessionRegistry sr;
// * 
// * @Autowired private AuthenticationManager authenticationManager;
// * 
// * @Autowired SecurityContextRepository securityRepo; private final
// * AuthenticatedUser authenticatedUser; HorizontalLayout captchacontainer = new
// * HorizontalLayout(); Button refreshButton = new Button(new
// * Icon(VaadinIcon.REFRESH)); Captcha captcha = new CapthaImpl(); Image image;
// * public TextField captchatext = new TextField(); TextField usernameField = new
// * TextField("User Name"); PasswordField passwordField = new
// * PasswordField("Password"); Button button = new Button("Login"); H3 maintitle
// * = new H3("Government of Meghalaya"); H2 title = new H2("Scheme MIS 2.0 "); H3
// * description = new H3("Version 2.0"); Anchor anchor = new Anchor(); private
// * final SecurityContextHolderStrategy securityContextHolderStrategy =
// * SecurityContextHolder .getContextHolderStrategy(); String dynamicKey = "";
// * 
// * public LoginOld(AuthenticatedUser authenticatedUser) { this.authenticatedUser
// * = authenticatedUser; dynamicKey = generateDynamicKey();
// * button.addClickListener(e -> { String username = usernameField.getValue();
// * String password = passwordField.getValue();
// * UI.getCurrent().getPage().executeJs("var key = $2;" + // Use $2 to represent
// * the dynamic key "var encryptedUsername = '';" +
// * "for (var i = 0; i < $0.length; i++) {" +
// * "    encryptedUsername += String.fromCharCode($0.charCodeAt(i) ^ key.charCodeAt(i % key.length));"
// * + "}" + "var encryptedPassword = '';" +
// * "for (var j = 0; j < $1.length; j++) {" +
// * "    encryptedPassword += String.fromCharCode($1.charCodeAt(j) ^ key.charCodeAt(j % key.length));"
// * + "}" +
// * "return { username: btoa(encryptedUsername), password: btoa(encryptedPassword) };"
// * , username, password, dynamicKey // Pass dynamicKey as the third argument
// * ).then(response -> { JsonObject data = (JsonObject) response; String
// * encryptedUsername = data.getString("username"); String encryptedPassword =
// * data.getString("password"); doLogin(encryptedUsername, encryptedPassword);
// * }); }); // setAlignItems(FlexComponent.Alignment.STRETCH);
// * setAlignItems(Alignment.CENTER);
// * setJustifyContentMode(JustifyContentMode.CENTER); setSizeFull();
// * add(createPasswordForm()); getStyle().set("background-color",
// * "hsla(0, 0%, 95%, 0.69)"); // add(createLoginForm()); }
// * 
// * private String generateDynamicKey() { String characters =
// * "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
// * StringBuilder key = new StringBuilder(); Random rnd = new Random(); while
// * (key.length() < 5) { // length of the key int index = (int) (rnd.nextFloat()
// * characters.length()); key.append(characters.charAt(index)); } return
// * key.toString(); }
// * 
// * private Component createPasswordForm() {
// * 
// * // captchacontainer.add(getCaptcha(), refreshButton);
// * usernameField.setRequired(true);
// * usernameField.setAllowedCharPattern("[0-9A-Za-z@]");
// * usernameField.setMinLength(5); usernameField.setMaxLength(20);
// * passwordField.setRequired(true); passwordField.setMinLength(5);
// * passwordField.setMaxLength(20); captchatext.setPlaceholder("ENTER CAPTCHA");
// * captchatext.setMaxLength(6); captchatext.setMinLength(6);
// * button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
// * button.setAutofocus(true); anchor.setText("Forgot Password?");
// * anchor.getElement().addEventListener("click", e -> ForgotPassword());
// * anchor.getStyle().set("color",
// * "hsla(211, 100%, 50%, 0.90)").set("padding-bottom", "20px"); var form = new
// * FormLayout(); // form.add(title, 1); // form.add(description, 1);
// * form.add(usernameField, 1); form.add(passwordField, 1);
// * 
// * // form.add(getCaptcha(), 1); form.add(new Span(), 1); //
// * form.add(captchatext, 1); // form.add(, 1); form.add(button, 1); //
// * form.add(anchor, 1); form.setResponsiveSteps(new ResponsiveStep("0", 1), new
// * ResponsiveStep("300px", 1)); form.setWidth("320px");
// * form.getStyle().set("padding", "20px");
// * 
// * var header = new VerticalLayout(); maintitle.getStyle().set("color",
// * "white"); title.getStyle().set("color", "white");
// * description.getStyle().set("color", "white"); header.add(maintitle, title);
// * header.getStyle().set("background-color", "hsla(211, 100%, 50%, 0.90)");
// * header.setAlignItems(Alignment.START);
// * header.setJustifyContentMode(JustifyContentMode.END);
// * header.setHeight("150px"); header.getStyle().set("border-radius",
// * "10px 10px 0 0"); var container = new VerticalLayout();
// * container.setSizeUndefined(); container.getStyle().set("background-color",
// * "white"); // container.getStyle().set("border", "2px solid black");
// * container.getStyle().set("border-radius", "10px");
// * container.getStyle().set("padding", "0px");
// * container.setAlignItems(Alignment.CENTER);
// * container.setJustifyContentMode(JustifyContentMode.CENTER);
// * container.add(header, form, anchor);
// * 
// * var wrapper = new VerticalLayout(); wrapper.setSizeFull();
// * wrapper.setAlignItems(Alignment.CENTER);
// * wrapper.setJustifyContentMode(JustifyContentMode.CENTER); wrapper.add(title,
// * description, container);
// * 
// * return container; }
// * 
// * Captcha
// * 
// * private void doLogin(String encryptedUsername, String encryptedPassword) {
// * button.setEnabled(false); String username=""; String password=""; if
// * (captcha.checkUserAnswer(captchatext.getValue())) { try { username =
// * CryptUtils.decryptUsername(encryptedUsername, dynamicKey); password =
// * CryptUtils.decryptPassword(encryptedPassword, dynamicKey); int
// * activeSessionCount = getActiveSessionCountForUser(username); if
// * (activeSessionCount == 0) { // invalidatePreviousSessionsForUser(username);
// * UsernamePasswordAuthenticationToken token = new
// * UsernamePasswordAuthenticationToken(username, password); Authentication
// * authentication = this.authenticationManager.authenticate(token);
// * 
// * SecurityContext context =
// * this.securityContextHolderStrategy.createEmptyContext();
// * context.setAuthentication(authentication);
// * this.securityContextHolderStrategy.setContext(context);
// * securityRepo.saveContext(context, VaadinServletRequest.getCurrent(),
// * VaadinServletResponse.getCurrent()); // registerSession(ServletContext,
// * (UserDetails) authentication.getPrincipal());
// * registerSession(VaadinService.getCurrentRequest().getWrappedSession(),
// * (UserDetails) authentication.getPrincipal());
// * //audit.saveLoginAudit("Login Successfully", username);
// * UI.getCurrent().navigate(HomeView.class); }else { Notification.
// * show("User Is Already Logged In. Please login with a different User").
// * addThemeVariants(NotificationVariant.LUMO_ERROR); } } catch (Exception e) {
// * 
// * //Notification.show("Authentication failed: " + e.getMessage()) //
// * .addThemeVariants(NotificationVariant.LUMO_ERROR);
// * Notification.show("Authentication failed: Wrong User Name and Password" )
// * .addThemeVariants(NotificationVariant.LUMO_ERROR);
// * 
// * clearFields();
// * 
// * //audit.saveLoginAudit("Login Failure- Authentication", username); } } else {
// * Notification.show("Invalid captcha").addThemeVariants(NotificationVariant.
// * LUMO_ERROR); clearFields(); //audit.saveLoginAudit("Login Failure- Captcha",
// * username);
// * 
// * } }
// * 
// * private void doLogin(String encryptedUsername, String encryptedPassword) {
// * button.setEnabled(false); String username = ""; String password = "";
// * 
// * try { username = CryptUtils.decryptUsername(encryptedUsername, dynamicKey);
// * password = CryptUtils.decryptPassword(encryptedPassword, dynamicKey); int
// * activeSessionCount = getActiveSessionCountForUser(username);
// * //System.out.println(activeSessionCount);
// * 
// * invalidatePreviousSessionsForUser(username);
// * UsernamePasswordAuthenticationToken token = new
// * UsernamePasswordAuthenticationToken(username, password); Authentication
// * authentication = this.authenticationManager.authenticate(token);
// * 
// * SecurityContext context =
// * this.securityContextHolderStrategy.createEmptyContext();
// * context.setAuthentication(authentication);
// * this.securityContextHolderStrategy.setContext(context);
// * securityRepo.saveContext(context, VaadinServletRequest.getCurrent(),
// * VaadinServletResponse.getCurrent()); // registerSession(ServletContext,
// * (UserDetails) authentication.getPrincipal());
// * registerSession(VaadinService.getCurrentRequest().getWrappedSession(),
// * (UserDetails) authentication.getPrincipal());
// * audit.saveLoginAudit("Login Successfully", username);
// * UI.getCurrent().navigate(HomeView.class);
// * 
// * } catch (Exception e) {
// * 
// * // Notification.show("Authentication failed: " + e.getMessage()) //
// * .addThemeVariants(NotificationVariant.LUMO_ERROR);
// * Notification.show("Authentication failed: Wrong User Name and Password")
// * .addThemeVariants(NotificationVariant.LUMO_ERROR);
// * 
// * clearFields();
// * 
// * audit.saveLoginAudit("Login Failure- Authentication", username); }
// * 
// * }
// * 
// * private void registerSession(WrappedSession session, UserDetails userDetails)
// * { sr.registerNewSession(session.getId(), userDetails); }
// * 
// * public int getActiveSessionCountForUser(String username) { int count = 0;
// * List<Object> principals = sr.getAllPrincipals(); for (Object principal :
// * principals) { if (principal instanceof UserDetails) { UserDetails userDetails
// * = (UserDetails) principal; if (userDetails.getUsername().equals(username)) {
// * List<SessionInformation> sessionInfoList = sr.getAllSessions(userDetails,
// * true); count += sessionInfoList.size(); } } } return count; }
// * 
// * public void ForgotPassword() { Dialog aboutdialog = new Dialog(); Button
// * cancelButton = new Button("Cancel"); H2 headline = new
// * H2("Forgot Password?"); // H3 header=new H3("Meghalaya Biodiversity Board");
// * // H3 header2=new H3("People's Biodiversity Register (PBR): Version 2.0"); H5
// * body = new H5("Please Enter Your Email Id"); EmailField email = new
// * EmailField(); email.setPlaceholder("Email"); email.setMaxLength(20);
// * email.setMinLength(5); Button submitbutton = new Button("Submit");
// * submitbutton.addClickListener(e ->
// * Notification.show("To Be Implemented Using Email API. Public IP Required"));
// * headline.getStyle().set("margin",
// * "var(--lumo-space-m) 0 0 0").set("font-size", "1.5em") .set("font-weight",
// * "bold").set("text-decoration", "underline"); cancelButton.addClickListener(e
// * -> aboutdialog.close()); HorizontalLayout buttonLayout1 = new
// * HorizontalLayout(submitbutton, cancelButton);
// * buttonLayout1.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
// * VerticalLayout dialogLayout1 = new VerticalLayout(headline, body, email,
// * buttonLayout1); dialogLayout1.setPadding(false);
// * dialogLayout1.setAlignItems(FlexComponent.Alignment.STRETCH);
// * dialogLayout1.getStyle().set("width", "300px").set("max-width", "100%");
// * aboutdialog.add(dialogLayout1); aboutdialog.open(); }
// * 
// * public void invalidatePreviousSessionsForUser(String username) { // Get
// * active session count for the user int activeSessionCount =
// * getActiveSessionCountForUser(username);
// * 
// * // If there are active sessions for the user, invalidate them if
// * (activeSessionCount > 0) { List<Object> principals = sr.getAllPrincipals();
// * for (Object principal : principals) { if (principal instanceof UserDetails) {
// * UserDetails userDetails = (UserDetails) principal; if
// * (userDetails.getUsername().equals(username)) { List<SessionInformation>
// * sessionInfoList = sr.getAllSessions(userDetails, true); // Invalidate //
// * previous // sessions for (SessionInformation sessionInfo : sessionInfoList) {
// * sessionInfo.expireNow(); // Expire session information
// * 
// * } } } } }
// * 
// * }
// * 
// * private void clearFields() { regenerateCaptcha(); button.setEnabled(true);
// * passwordField.setValue(""); usernameField.setValue("");
// * captchatext.setValue(""); }
// * 
// * public Component getCaptcha() { image = captcha.getCaptchaImg();
// * captchacontainer.add(image, refreshButton); refreshButton.addClickListener(e
// * -> regenerateCaptcha());
// * refreshButton.setTooltipText("Generate Another Captcha");
// * captchacontainer.setWidthFull();
// * captchacontainer.setJustifyContentMode(JustifyContentMode.CENTER);
// * captchacontainer.getStyle().set("padding", "20px"); return captchacontainer;
// * }
// * 
// * private void regenerateCaptcha() { captchacontainer.remove(image);
// * captchacontainer.remove(refreshButton); image = captcha.getCaptchaImg();
// * captchacontainer.add(image, refreshButton); }
// * 
// * @Override public void beforeEnter(BeforeEnterEvent event) { if
// * (authenticatedUser.get().isPresent()) { // Already logged in //
// * loginOverlay.setOpened(false); event.forwardTo(""); }
// * 
// * //
// * loginOverlay.setError(event.getLocation().getQueryParameters().getParameters(
// * ).containsKey("error")); } }
// */