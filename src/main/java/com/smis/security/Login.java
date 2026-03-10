package com.smis.security;

import java.util.List;
import java.util.Map;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@AnonymousAllowed
public class Login extends Div implements BeforeEnterObserver {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final TextField username = new TextField("Username");
    private final PasswordField password = new PasswordField("Password");
    private final TextField captcha = new TextField("Captcha");
    private final boolean captchaEnabled;
    private final Image captchaImg = new Image();
    private final Button refresh = new Button("Refresh");
    private final Div message = new Div();

    public Login(Environment env) {
        addClassName("login-page");
        this.captchaEnabled = env.getProperty("app.login.captcha-enabled", Boolean.class, true);
        add(buildCard());
        smoothLogin();
    }

    private Component buildCard() {
        Div card = new Div();
        card.addClassName("login-card");

        Div hero = new Div();
        hero.addClassName("login-hero");

        H4 app = new H4("Scheme MIS 2.0");
        app.addClassName("login-app");

        Paragraph tagline = new Paragraph("Government of Meghalaya");
        tagline.addClassName("login-tagline");

        hero.add(app, tagline);

        Div body = new Div();

        username.getElement().setAttribute("name", "username");
        username.getElement().setAttribute("autocomplete", "username");

        password.getElement().setAttribute("name", "password");
        password.getElement().setAttribute("autocomplete", "current-password");
        password.setRevealButtonVisible(false);

        username.setWidthFull();
        password.setWidthFull();

        body.add(username, password);

        if (captchaEnabled) {
            captcha.getElement().setAttribute("name", "captcha");
            captcha.setWidthFull();

            captchaImg.setAlt("Captcha");
            captchaImg.setSrc("/captcha-image?ts=" + System.currentTimeMillis());
            captchaImg.addClassName("captcha-img");

            refresh.addClassName("captcha-refresh");
            refresh.addClickListener(e ->
                    captchaImg.setSrc("/captcha-image?ts=" + System.currentTimeMillis())
            );

            HorizontalLayout captchaRow = new HorizontalLayout(captchaImg, refresh);
            captchaRow.addClassName("captcha-row");
            captchaRow.setWidthFull();

            body.add(captchaRow, captcha);
        }

        message.addClassName("login-message");
        body.add(message);

        Element submit = new Element("button");
        submit.setText("Log in");
        submit.setAttribute("type", "submit");
        submit.getClassList().add("login-submit");

        Element form = new Element("form");
        form.setAttribute("method", "post");
        form.setAttribute("action", "/login");
        form.getStyle().set("width", "100%");
        form.setAttribute("id", "loginForm");

        CsrfToken csrf = (CsrfToken) VaadinServletRequest.getCurrent()
                .getHttpServletRequest()
                .getAttribute(CsrfToken.class.getName());

        if (csrf != null) {
            Element csrfInput = new Element("input");
            csrfInput.setAttribute("type", "hidden");
            csrfInput.setAttribute("name", csrf.getParameterName());
            csrfInput.setAttribute("value", csrf.getToken());
            form.appendChild(csrfInput);
        }

        form.appendChild(body.getElement());
        form.appendChild(submit);

        Div formWrapper = new Div();
        formWrapper.addClassName("login-body");
        formWrapper.getElement().appendChild(form);

        card.add(hero, formWrapper);
        return card;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean loggedIn = auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);

        if (loggedIn) {
            event.forwardTo("");
            return;
        }

        Map<String, List<String>> params =
                event.getLocation().getQueryParameters().getParameters();

        if (captchaEnabled && params.containsKey("captcha")) {
            message.setText("Invalid captcha. Please try again.");
            captchaImg.setSrc("/captcha-image?ts=" + System.currentTimeMillis());

        } else if (params.containsKey("error")) {
            message.setText("Invalid username or password.");
            if (captchaEnabled) {
                captchaImg.setSrc("/captcha-image?ts=" + System.currentTimeMillis());
            }

        } else if (params.containsKey("expired")) {
            message.setText("Session expired. Please log in again.");

        } else if (params.containsKey("logout")) {
            message.setText("You have been logged out.");

        } else {
            message.setText("");
        }
    }

    public void smoothLogin() {
        getElement().executeJs("""
            const root = $0;
            const captchaEnabled = $1;

            const form = root.querySelector('#loginForm');
            if (!form || form.__wired) return;
            form.__wired = true;

            const msg = root.querySelector('.login-message');
            const img = root.querySelector('.captcha-img');
            const captchaHost = root.querySelector('vaadin-text-field[name="captcha"]');
            const captchaInput = root.querySelector('input[name="captcha"]');
            const btn = root.querySelector('.login-submit');

            const CAPTCHA_REGEX = /^[A-Za-z0-9]{6}$/;

            const clearCaptcha = () => {
              if (captchaHost) captchaHost.value = '';
              if (captchaInput) captchaInput.value = '';
            };

            const getCaptchaValue = () => {
              const v =
                  (captchaHost && typeof captchaHost.value === 'string') ? captchaHost.value :
                  (captchaInput && typeof captchaInput.value === 'string') ? captchaInput.value :
                  '';
              return (v || '').trim();
            };

            const refreshCaptchaImage = () => {
              if (img) img.src = '/captcha-image?ts=' + Date.now();
            };

            form.addEventListener('submit', (e) => {
              if (msg) msg.textContent = '';

              // captcha OFF -> normal Spring Security submit
              if (!captchaEnabled) {
                return;
              }

              const cap = getCaptchaValue();

              if (!cap) {
                e.preventDefault();
                if (msg) msg.textContent = 'Please enter captcha.';
                clearCaptcha();
                if (btn) btn.disabled = false;
                return;
              }

              if (!CAPTCHA_REGEX.test(cap)) {
                e.preventDefault();
                if (msg) msg.textContent = 'Wrong captcha.';
                clearCaptcha();
                refreshCaptchaImage();
                if (btn) btn.disabled = false;
                return;
              }

              if (btn) btn.disabled = true;
            });
        """, getElement(), captchaEnabled);
    }
}