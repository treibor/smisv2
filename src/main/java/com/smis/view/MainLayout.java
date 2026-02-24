package com.smis.view;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.vaadin.lineawesome.LineAwesomeIcon;

import com.smis.dbservice.Dbservice;
import com.smis.entity.Users;
import com.smis.entity.UsersRoles;
import com.smis.entity.master.District;
import com.smis.entity.master.State;
import com.smis.util.EmailValidator;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

import jakarta.annotation.security.PermitAll;

@PermitAll
public class MainLayout extends AppLayout {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Anchor anchor = new Anchor("", "MLALADS 1.0");
	Dbservice service;
	Dialog dialog;
	Dialog userdialog;
	Dialog aboutdialog;
	// Notification notify;
	PasswordField oldpwd;
	PasswordField newpwd;
	PasswordField confirmpwd;
	Button cancelButton = new Button("Cancel");
	Button saveButton = new Button("Save");
	TextField userName = new TextField("User Name");
	TextField profileName = new TextField("Profile Name");
	EmailField email = new EmailField("Email");
	String userType;
	ComboBox<State> state = new ComboBox<>("State");
	ComboBox<District> district = new ComboBox<>("District");
	final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	ComboBox<String> usertype = new ComboBox<>("Role");
	boolean isUser;
	boolean isAdmin;
	boolean isSuper;
	private Users loggedUser;

	public MainLayout(Dbservice dbservice) {
		this.service = dbservice;
		this.loggedUser = service.getLoggedUser();
		usertype.setItems("ADMIN", "USER");
		isAdmin = service.hasRole("ADMIN");
		isSuper = service.hasRole("SUPER"); // or SUPER_ADMIN / DIST_ADMIN etc.
		isUser = service.hasRole("USER");
		createHeader();
		createDrawer();
		checkPasswordExpiry();
		// addToDrawer(createHeader(), createDrawer());

		// setPrimarySection(Section.DRAWER);
	}

	private void checkPasswordExpiry() {

		LocalDateTime expiryDate = loggedUser.getPwdChangedDate();
		LocalDateTime expiryDatePlus180Days = expiryDate.plus(180, ChronoUnit.DAYS);
		LocalDateTime today = LocalDateTime.now();
		boolean isExpiryDateValid = expiryDatePlus180Days.isAfter(today);
		if (!isExpiryDateValid) {
			openMandatoryPasswordDialog();
		}
	}

	public void populateDistricts() {
		district.setItems(service.getAllDistricts(state.getValue()));
	}

	private Component createDrawerUserInfo() {

		String username = loggedUser.getProfileName();
		String roles = currentRolesText(); // the method you already fixed
		Span name = new Span(username);
		name.getStyle().set("font-size", "var(--lumo-font-size-s)").set("font-weight", "600");

		Span role = new Span(roles);
		role.getStyle().set("font-size", "var(--lumo-font-size-xs)").set("color", "var(--lumo-secondary-text-color)");

		VerticalLayout text = new VerticalLayout(name, role);
		text.setPadding(false);
		text.setSpacing(false);

		HorizontalLayout userInfo = new HorizontalLayout(text);
		userInfo.setAlignItems(FlexComponent.Alignment.CENTER);
		userInfo.setPadding(true);
		userInfo.setSpacing(true);

		userInfo.getStyle().set("padding", "var(--lumo-space-s)") // not default padding
				.set("border-top", "1px solid var(--lumo-contrast-10pct)");

		return userInfo;
	}

	private void createDrawer() {

		VerticalLayout drawerContent = new VerticalLayout();
		drawerContent.setSizeFull();
		drawerContent.setPadding(false); // important: root no padding
		drawerContent.setSpacing(false);

		// Add navigation items with helper text
		SideNavItemWithHelperText home = new SideNavItemWithHelperText("Home", "", HomeView.class,
				LineAwesomeIcon.HOME_SOLID.create());

		SideNavItemWithHelperText mla = new SideNavItemWithHelperText("Inbox", "", WorkView.class,
				LineAwesomeIcon.PEOPLE_CARRY_SOLID.create());

		SideNavItemWithHelperText history = new SideNavItemWithHelperText("History", "", WorkViewHistory.class,
				LineAwesomeIcon.HISTORY_SOLID.create());

		SideNavItemWithHelperText releaseorder = new SideNavItemWithHelperText("Bulk Release Order", "", PrintView.class,
				LineAwesomeIcon.WOLF_PACK_BATTALION.create());

		SideNavItemWithHelperText master = new SideNavItemWithHelperText("Master", "", MasterView.class,
				LineAwesomeIcon.BALANCE_SCALE_LEFT_SOLID.create());

		SideNavItemWithHelperText distmaster = new SideNavItemWithHelperText("District Master", "", DistView.class,
				LineAwesomeIcon.BALANCE_SCALE_RIGHT_SOLID.create());

		SideNavItemWithHelperText report = new SideNavItemWithHelperText("Reports", "", ReportView.class,
				LineAwesomeIcon.CALCULATOR_SOLID.create());

		SideNavItemWithHelperText audit = new SideNavItemWithHelperText("Audit Trail", "", AuditView.class,
				LineAwesomeIcon.CALENDAR.create());

		SideNavItemWithHelperText users = new SideNavItemWithHelperText("Users", "", UsersView.class,
				LineAwesomeIcon.USER.create());
		SideNavItemWithHelperText old_works = new SideNavItemWithHelperText("Old Works", "", com.smis.view.old.WorkView.class,
				LineAwesomeIcon.OLD_REPUBLIC.create());
		SideNavItemWithHelperText old_ro = new SideNavItemWithHelperText("Release Order", "", com.smis.view.old.PrintView.class,
				LineAwesomeIcon.DONATE_SOLID.create());

		master.setVisible(isAdmin);
		distmaster.setVisible(isSuper);
		// releaseorder.setVisible(checkAuthority(service.getProcessFlowByOrder(3)));
		releaseorder.setVisible(
		        service.hasAuthorityForStep(loggedUser, "GENERATE_RELEASE_ORDER")
		        ||
		        service.hasAuthorityForStep(loggedUser, "UPLOAD_RELEASE_ORDER")
		);
		audit.setVisible(isAdmin);
		users.setVisible(isAdmin);
		old_works.setVisible(service.hasRole("OLD"));
		old_ro.setVisible(service.hasRole("OLD"));
		VerticalLayout navItems = new VerticalLayout(home, mla, history, releaseorder, old_works,old_ro,master, distmaster, report,
				audit, users);
		navItems.setPadding(true);
		navItems.setSpacing(true);
		navItems.setWidthFull();
		navItems.addClassName("drawer-nav");

		Div spacer = new Div();
		drawerContent.expand(spacer);

		Component userInfo = createDrawerUserInfo();

		drawerContent.add(navItems, spacer, userInfo);

		addToDrawer(drawerContent);
	}

	private Component menuItem(VaadinIcon icon, String text) {
		Icon i = icon.create();
		i.setSize("12px");
		i.getStyle().set("margin-right", "var(--lumo-space-s)").set("color", "var(--lumo-secondary-text-color)");

		Span label = new Span(text);
		label.getStyle().set("font-size", "var(--lumo-font-size-s)") //
				.set("line-height", "1.2");

		HorizontalLayout layout = new HorizontalLayout(i, label);
		layout.setAlignItems(FlexComponent.Alignment.CENTER);
		layout.setPadding(false);
		layout.setSpacing(false);

		return layout;
	}

	private String currentRolesText() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null)
			return "";

		return auth.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.map(r -> r.startsWith("ROLE_") ? r.substring(5) : r) // remove ROLE_
				.sorted().collect(Collectors.joining(", "));
	}

	private void createHeader() {

		Avatar avatarImage = new Avatar(loggedUser.getProfileName());
		avatarImage.setColorIndex(2);

		MenuBar menuBar = new MenuBar();
		menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);

		MenuItem item = menuBar.addItem(avatarImage);
		SubMenu subMenu = item.getSubMenu();

		// --- User info (non-clickable) ---
		String username = loggedUser.getProfileName();
		String role = currentRolesText();
		Span userNameSpan = new Span(username);
		userNameSpan.getStyle().set("font-weight", "600");

		Span roleSpan = new Span(role);
		roleSpan.getStyle().set("font-size", "var(--lumo-font-size-xs)").set("color",
				"var(--lumo-secondary-text-color)");

		VerticalLayout userInfo = new VerticalLayout(userNameSpan, roleSpan);
		userInfo.setPadding(false);
		userInfo.setSpacing(false);
		userInfo.getStyle().set("pointer-events", "none");

		subMenu.addItem(userInfo);
		subMenu.add(new Hr());

		// --- Actions ---
		subMenu.addItem(menuItem(VaadinIcon.INFO_CIRCLE, "About"), e -> openAboutDialog());

		subMenu.addItem(menuItem(VaadinIcon.KEY, "Change Password"), e -> openPasswordDialog());

		subMenu.addItem(menuItem(VaadinIcon.USER_CHECK, "Create User"), e -> createUser()).setVisible(isAdmin);

		//	subMenu.addItem(menuItem(VaadinIcon.SIGN_OUT, "Logout"), e -> securityService.logout());
		subMenu.addItem(menuItem(VaadinIcon.SIGN_OUT, "Logout"), e -> {
		        logout();
		});
		H3 logo = new H3("MLALADS  || " +
		// service.getDistrict().getDistrictName().toUpperCase());
				loggedUser.getDistrict().getDistrictName());
		HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, menuBar);

		header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
		header.expand(logo);
		header.setWidthFull();
		header.addClassNames("py-0", "px-m");

		addToNavbar(header);
	}
	public void logout() {
		UI.getCurrent().getPage().setLocation("/logout");
	}
	private void openAboutDialog() {
		if (aboutdialog != null) {
			aboutdialog = null;
		}
		aboutdialog = new Dialog();
		VerticalLayout dialogLayout1 = createAboutDialog(aboutdialog);
		aboutdialog.add(dialogLayout1);
		aboutdialog.open();
	}

	private VerticalLayout createAboutDialog(Dialog dialog2) {
		H2 headline = new H2("About");
		headline.getStyle().set("margin", "var(--lumo-space-m) 0 0 0").set("font-size", "1.5em").set("font-weight",
				"bold");
		// Label text1=new Label("Designed and Developed By NIC, Meghalaya");
		// Label text2=new Label("Contact: aiban.m@nic.in");
		H3 text1 = new H3("Designed and Developed By NIC, Meghalaya");
		H3 text2 = new H3("Contact: aiban.m@nic.in");
		Button close = new Button("Close");
		close.addClickListener(e -> dialog2.close());
		VerticalLayout dialogLayout1 = new VerticalLayout(headline, text1, text2, close);
		dialogLayout1.setPadding(false);
		dialogLayout1.setAlignItems(FlexComponent.Alignment.STRETCH);
		dialogLayout1.getStyle().set("width", "300px").set("max-width", "100%");
		// clearDialog();
		return dialogLayout1;
	}

	private void openPasswordDialog() {
		if (dialog != null) {
			dialog = null;
		}
		dialog = new Dialog();
		dialog.setModal(true);
		dialog.setCloseOnEsc(false);
		dialog.setCloseOnOutsideClick(false);
		VerticalLayout dialogLayout = createDialogLayout(dialog);
		dialog.add(dialogLayout);
		dialog.open();
	}

	private void openMandatoryPasswordDialog() {
		if (dialog != null) {
			dialog = null;
		}
		dialog = new Dialog();
		dialog.setModal(true);
		dialog.setCloseOnEsc(false);
		dialog.setCloseOnOutsideClick(false);
		// VerticalLayout dialogLayout = createDialogLayout(dialog);
		H2 headline = new H2("Password Expired - Change Password");
		headline.getStyle().set("margin", "var(--lumo-space-m) 0 0 0").set("font-size", "1.5em").set("font-weight",
				"bold");

		oldpwd = new PasswordField("Old Password");
		newpwd = new PasswordField("New Password");
		confirmpwd = new PasswordField("Confirm New Password");
		oldpwd.setRevealButtonVisible(false);
		newpwd.setRevealButtonVisible(false);
		confirmpwd.setRevealButtonVisible(false);
		// oldpwd.setValue("");
		// cancelButton.setText(//userType);
		cancelButton.addClickListener(e -> logout());
		Button saveButton = new Button("Save", e ->
        changePassword(oldpwd, newpwd, confirmpwd, dialog));
		VerticalLayout fieldLayout = new VerticalLayout(oldpwd, newpwd, confirmpwd);
		fieldLayout.setSpacing(false);
		fieldLayout.setPadding(false);
		fieldLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, saveButton);
		buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
		VerticalLayout dialogLayout = new VerticalLayout(headline, fieldLayout, buttonLayout);
		dialogLayout.setPadding(false);
		dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
		dialogLayout.getStyle().set("width", "300px").set("max-width", "100%");
		clearDialog();
		dialog.add(dialogLayout);
		dialog.open();
	}

	private VerticalLayout createDialogLayout(Dialog dialog) {
		H2 headline = new H2("Change Password");
		headline.getStyle().set("margin", "var(--lumo-space-m) 0 0 0").set("font-size", "1.5em").set("font-weight",
				"bold");

		oldpwd = new PasswordField("Old Password");
		newpwd = new PasswordField("New Password");
		confirmpwd = new PasswordField("Confirm New Password");
		oldpwd.setRevealButtonVisible(false);
		newpwd.setRevealButtonVisible(false);
		confirmpwd.setRevealButtonVisible(false);
		// oldpwd.setValue("");
		cancelButton.addClickListener(e -> dialog.close());
		Button saveButton = new Button("Save", e ->
        changePassword(oldpwd, newpwd, confirmpwd, dialog));
		VerticalLayout fieldLayout = new VerticalLayout(oldpwd, newpwd, confirmpwd);
		fieldLayout.setSpacing(false);
		fieldLayout.setPadding(false);
		fieldLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, saveButton);
		buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
		VerticalLayout dialogLayout = new VerticalLayout(headline, fieldLayout, buttonLayout);
		dialogLayout.setPadding(false);
		dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
		dialogLayout.getStyle().set("width", "300px").set("max-width", "100%");
		clearDialog();

		return dialogLayout;
	}

	public void clearDialog() {
		oldpwd.setValue("");
		confirmpwd.setValue("");
		newpwd.setValue("");
	}

	private void changePassword(
	        PasswordField oldpwd,
	        PasswordField newpwd,
	        PasswordField confirmpwd,
	        Dialog dialog
	) {

	    if (oldpwd.getValue().trim().isEmpty()
	            || newpwd.getValue().trim().isEmpty()
	            || confirmpwd.getValue().trim().isEmpty()) {

	        Notification.show("Error: Enter All Values, Please", 3000, Position.TOP_CENTER)
	                .addThemeVariants(NotificationVariant.LUMO_ERROR);
	        return;
	    }

	    if (!checkPasswordStrength(newpwd.getValue())) {
	        Notification.show(
	                "Password is too weak. Please use Lower case, Upper case, Number and Special Characters")
	                .addThemeVariants(NotificationVariant.LUMO_ERROR);
	        return;
	    }

	    if (!newpwd.getValue().equals(confirmpwd.getValue())) {
	        Notification.show("Please check and confirm your passwords", 3000, Position.TOP_CENTER)
	                .addThemeVariants(NotificationVariant.LUMO_ERROR);
	        return;
	    }

	    String pwd = oldpwd.getValue();

	    if (!passwordEncoder.matches(pwd, loggedUser.getPassword())) {
	        Notification.show("Incorrect Old Password", 3000, Position.TOP_CENTER)
	                .addThemeVariants(NotificationVariant.LUMO_ERROR);
	        return;
	    }

	    // Update password
	    loggedUser.setPassword(passwordEncoder.encode(newpwd.getValue().trim()));
	    loggedUser.setPwdChangedDate(LocalDateTime.now());

	    service.saveUser(loggedUser);

	    dialog.close();  // close dialog safely

	    showConfirmationDialog();
	}
	private void showConfirmationDialog() {
		ConfirmDialog dialog = new ConfirmDialog();
		dialog.setHeader("Password Changed");
		dialog.setText(
				"Password has been successfully changed. You will be now be logged out. Please Login again with your new Password");

		dialog.setConfirmText("OK");
		dialog.addConfirmListener(event -> {
			logout(); // Call the logout method
			// getUI().ifPresent(ui -> ui.navigate("login")); // Redirect to the login page
		});

		dialog.open(); // Open the dialog
	}
	
	private void createUser() {
		// TODO Auto-generated method stub

		if (isAdmin == true || isSuper == true) {
			userdialog = new Dialog();
			VerticalLayout dialogLayout1 = createUserDialog(userdialog);
			userdialog.add(dialogLayout1);
			userdialog.open();

		} else {
			Notification.show("Please Contact Your Administrator", 3000, Position.TOP_CENTER);
		}
	}

	private VerticalLayout createUserDialog(Dialog userdialog) {
		H2 headline = new H2("Create User");
		headline.getStyle().set("margin", "var(--lumo-space-m) 0 0 0").set("font-size", "1.5em").set("font-weight",
				"bold");
		state.setItems(service.getAllStates());
		state.addValueChangeListener(e -> populateDistricts());

		if (isSuper) {
			usertype.setEnabled(true);
			usertype.setVisible(true);
			// state.setEnabled(true);
		} else if (isAdmin) {
			state.setValue(loggedUser.getDistrict().getState());
			district.setValue(loggedUser.getDistrict());
			usertype.setValue("USER");
			usertype.setEnabled(false);
			usertype.setVisible(false);
		}
		state.setEnabled(isSuper);
		state.setVisible(isSuper);
		district.setEnabled(isSuper);
		district.setVisible(isSuper);
		state.setItemLabelGenerator(State::getStateName);
		district.setItemLabelGenerator(District::getDistrictName);
		cancelButton.addClickListener(e -> userdialog.close());
		saveButton.addClickListener(e -> saveNewUser());
		newpwd = new PasswordField("Password");
		confirmpwd = new PasswordField("Confirm Password");
		profileName.setHelperText("This will be used as a display name. Other Users will see this name.");
		userName.setHelperText("Your Login Name. Only the Admin and the user should know this name.");
		VerticalLayout fieldLayout1 = new VerticalLayout(state, district, profileName, userName, email, newpwd,
				confirmpwd, usertype);
		fieldLayout1.setSpacing(false);
		fieldLayout1.setPadding(false);
		fieldLayout1.setAlignItems(FlexComponent.Alignment.STRETCH);

		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		HorizontalLayout buttonLayout1 = new HorizontalLayout(cancelButton, saveButton);
		buttonLayout1.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
		VerticalLayout dialogLayout1 = new VerticalLayout(headline, fieldLayout1, buttonLayout1);
		dialogLayout1.setPadding(false);
		dialogLayout1.setAlignItems(FlexComponent.Alignment.STRETCH);
		dialogLayout1.getStyle().set("width", "300px").set("max-width", "100%");
		// clearDialog();
		return dialogLayout1;
	}

	private boolean checkPasswordStrength(String password) {
		boolean containsLowerChar = false, containsUpperChar = false;
		boolean containsDigit = false, containsSpecialChar = false;
		char[] ch = password.toCharArray();
		// System.out.println(password);
		String special_chars = "!(){}[]:;<>?,@#$%^&*+=_-~`|./'";
		for (int i = 0; i < password.length(); i++) {
			if (Character.isLowerCase(ch[i])) {
				containsLowerChar = true;
			}
			if (Character.isUpperCase(ch[i])) {
				containsUpperChar = true;
			}
			if (Character.isDigit(ch[i])) {
				containsDigit = true;
			}
			if (special_chars.contains(String.valueOf(ch[i]))) {
				containsSpecialChar = true;
			}
		}
		if (containsDigit && containsUpperChar && containsSpecialChar && containsLowerChar) {
			return true;
		}
		return false;
	}

	private void saveNewUser() {
		// TODO Auto-generated method stub
		if (district.isEmpty() || state.isEmpty() || usertype.isEmpty() || profileName.getValue().trim().isEmpty()
				|| email.getValue().trim().isEmpty() || userName.getValue().trim().isEmpty()
				|| newpwd.getValue().trim().isEmpty() || confirmpwd.getValue().trim().isEmpty()) {
			Notification.show("Please Enter All Values", 3000, Position.TOP_CENTER)
					.addThemeVariants(NotificationVariant.LUMO_ERROR);
			return;
		}
		if (userName.getValue().trim().length() < 6) {
			Notification.show("User name must be at leat 6 Characters long", 3000, Position.TOP_CENTER)
					.addThemeVariants(NotificationVariant.LUMO_ERROR);
			return;
		}
		if (!checkPasswordStrength(newpwd.getValue())) {
			Notification.show(
					"Weak Password Detected. Please use a combination of Lower case, Upper case, Number and Special Charaters")
					.addThemeVariants(NotificationVariant.LUMO_WARNING);
			return;
		}
		if (!newpwd.getValue().equals(confirmpwd.getValue())) {
			Notification.show("Check Your Passwords, Please", 3000, Position.TOP_CENTER)
					.addThemeVariants(NotificationVariant.LUMO_ERROR);
			return;
		}
		if (!EmailValidator.isValidEmail(email.getValue())) {
			Notification.show("Please Enter a Valid Email", 3000, Position.TOP_CENTER)
					.addThemeVariants(NotificationVariant.LUMO_ERROR);
			return;
		}
		try {
			if (service.findByUserName(userName.getValue()) == null) {
				Users users = new Users();
				UsersRoles role = new UsersRoles();
				users.setDistrict(district.getValue());
				users.setUserName(userName.getValue());
				users.setProfileName(profileName.getValue());
				users.setPassword(passwordEncoder.encode(newpwd.getValue().trim()));
				users.setEnteredBy(loggedUser);
				users.setEnteredOn(LocalDateTime.now());
				users.setPwdChangedDate(LocalDateTime.now());
				users.setEnabled(true);
				users.setEmail(email.getValue());
				service.saveUser(users);
				role.setUser(users);
				role.setRoleName(usertype.getValue().toString());
				role.setAssignedBy(loggedUser);
				service.saveRole(role);
				clearUserFields();
				Notification.show("User Created Successfully", 3000, Position.TOP_CENTER)
						.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
				userdialog.close();
			} else {
				Notification.show("Username Already Taken. Enter Another Username", 3000, Position.TOP_CENTER)
						.addThemeVariants(NotificationVariant.LUMO_ERROR);
				userName.setValue("");
				userName.focus();
			}

		} catch (Exception e) {
			e.printStackTrace();
			Notification.show("Error Encountered. Please Contact The Adminisrator. Error:" + e)
					.addThemeVariants(NotificationVariant.LUMO_ERROR);
		}
	}

	public void clearUserFields() {
		district.setValue(null);
		state.setValue(null);
		userName.setValue("");
		newpwd.setValue("");
		confirmpwd.setValue("");
	}
	
	@Override
	protected void onAttach(AttachEvent attachEvent) {
	    super.onAttach(attachEvent);

	    attachEvent.getUI().getPage().executeJs("""
	      (function () {
	        if (window.__smis_flow_error_patch) return;
	        window.__smis_flow_error_patch = true;

	        function redirectExpired() {
	          window.location.replace('/login?expired');
	        }

	        function looksLikeExpiredSession(msg) {
	          msg = String(msg || '');
	          return msg.includes('Invalid JSON response') ||
	                 msg.includes('<!doctype html') ||
	                 msg.includes('<html') ||
	                 msg.includes('/login');
	        }

	        function patchFlow() {
	          try {
	            const flow = window.Vaadin && window.Vaadin.Flow;
	            if (!flow) return false;

	            const candidates = ['showSystemError', 'showCriticalNotification', 'showCommunicationError'];
	            let patched = false;

	            candidates.forEach(fn => {
	              if (typeof flow[fn] === 'function' && !flow[fn].__smisPatched) {
	                const orig = flow[fn];
	                const wrapped = function () {
	                  try {
	                    const firstArg = arguments && arguments.length ? arguments[0] : '';
	                    if (looksLikeExpiredSession(firstArg)) {
	                      redirectExpired();
	                      return;
	                    }
	                  } catch (e) {}
	                  return orig.apply(this, arguments);
	                };
	                wrapped.__smisPatched = true;
	                flow[fn] = wrapped;
	                patched = true;
	              }
	            });

	            return patched;
	          } catch (e) {
	            return false;
	          }
	        }

	        if (!patchFlow()) {
	          let tries = 0;
	          const t = setInterval(() => {
	            tries++;
	            if (patchFlow() || tries > 20) clearInterval(t);
	          }, 250);
	        }

	        const observer = new MutationObserver(() => {
	          const body = document.body;
	          if (!body) return;

	          const text = body.innerText || '';
	          if (text.includes('Invalid JSON response from server') || text.includes('Invalid UIDL')) {

	            document.querySelectorAll('vaadin-dialog-overlay, vaadin-confirm-dialog-overlay')
	              .forEach(el => {
	                const t = (el.innerText || '');
	                if (t.includes('Invalid JSON response') || t.includes('Invalid UIDL')) el.remove();
	              });

	            Promise.resolve().then(() => redirectExpired());
	          }
	        });

	        if (document.body) observer.observe(document.body, { childList: true, subtree: true });
	      })();
	    """);
	}
	
	
}
