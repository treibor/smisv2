package com.smis.security;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.dom.Element;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;

import java.util.List;
import java.util.Map;


@Route("login")
@AnonymousAllowed
public class Login extends Div implements BeforeEnterObserver {

    private final TextField username = new TextField("Username");
    private final PasswordField password = new PasswordField("Password");
    private final TextField captcha = new TextField("Captcha");

    private final Image captchaImg = new Image();
    private final Button refresh = new Button("Refresh");

    private final Div message = new Div();

    public Login() {
        addClassName("login-page");
        add(buildCard());
        smoothLogin();
    }
    public void smoothLogin() {
    	  getElement().executeJs("""
    	    const root = $0;
    	    const form = root.querySelector('#loginForm');
    	    if (!form || form.__wired) return;
    	    form.__wired = true;

    	    const msg = root.querySelector('.login-message');
    	    const img = root.querySelector('.captcha-img');
    	    const captchaHost = root.querySelector('vaadin-text-field[name="captcha"]');
    	    const captchaInput = root.querySelector('input[name="captcha"]');
    	    const btn = root.querySelector('.login-submit');

    	    // prevent overlapping timers across submits
    	    let retryTimerId = null;
    	    let retryUntilTs = 0;

    	    // 🔧 CAPTCHA RULES (change here if needed)
    	    const CAPTCHA_REGEX = /^[A-Za-z0-9]{6}$/; // exactly 6 alphanumeric

    	    const clearCaptcha = () => {
    	      if (captchaHost) captchaHost.value = '';
    	      if (captchaInput) captchaInput.value = '';
    	    };

    	    const getCaptchaValue = () => {
    	      // prefer vaadin host value if present, fallback to input value
    	      const v = (captchaHost && typeof captchaHost.value === 'string') ? captchaHost.value
    	              : (captchaInput && typeof captchaInput.value === 'string') ? captchaInput.value
    	              : '';
    	      return (v || '').trim();
    	    };

    	    const refreshCaptchaImage = () => {
    	      if (img) img.src = '/captcha-image?ts=' + Date.now();
    	    };

    	    const setButtonEnabled = (enabled) => {
    	      if (btn) btn.disabled = !enabled;
    	    };

    	    const startRetryCountdown = (seconds, baseMessage) => {
    	      seconds = Number(seconds);
    	      if (!Number.isFinite(seconds) || seconds <= 0) seconds = 60;

    	      if (retryTimerId) {
    	        clearInterval(retryTimerId);
    	        retryTimerId = null;
    	      }

    	      retryUntilTs = Date.now() + seconds * 1000;
    	      setButtonEnabled(false);

    	      const tick = () => {
    	        const remainingMs = retryUntilTs - Date.now();
    	        const remaining = Math.max(0, Math.ceil(remainingMs / 1000));

    	        if (msg) msg.textContent = `${baseMessage} Try again in ${remaining}s.`;

    	        if (remaining <= 0) {
    	          if (retryTimerId) clearInterval(retryTimerId);
    	          retryTimerId = null;
    	          retryUntilTs = 0;
    	          setButtonEnabled(true);
    	          if (msg) msg.textContent = '';
    	        }
    	      };

    	      tick();
    	      retryTimerId = setInterval(tick, 1000);
    	    };

    	    form.addEventListener('submit', async (e) => {
    	      e.preventDefault();

    	      // If we're currently in retry countdown, ignore submits
    	      if (retryUntilTs && Date.now() < retryUntilTs) return;

    	      // ✅ CLIENT-SIDE CAPTCHA VALIDATION (NO SUBMIT if invalid)
    	      const cap = getCaptchaValue();
    	      if (!cap) {
    	        if (msg) msg.textContent = 'Please enter captcha.';
    	        clearCaptcha();
    	        // optional: don't refresh image for empty, user just forgot
    	        setButtonEnabled(true);
    	        return;
    	      }
    	      if (!CAPTCHA_REGEX.test(cap)) {
    	        if (msg) msg.textContent = 'Wrong captcha.';
    	        clearCaptcha();
    	        refreshCaptchaImage(); // optional: refresh so user gets a new captcha
    	        setButtonEnabled(true);
    	        return;
    	      }

    	      setButtonEnabled(false);

    	      try {
    	        const fd = new FormData(form);

    	        const res = await fetch(form.action, {
    	          method: 'POST',
    	          body: fd,
    	          credentials: 'same-origin',
    	          redirect: 'follow'
    	        });

    	        // 429 rate limit
    	        if (res.status === 429) {
    	          const text = await res.text().catch(() => '');
    	          const retryAfterHeader = res.headers.get('Retry-After');
    	          const retrySeconds = retryAfterHeader ? parseInt(retryAfterHeader, 10) : 60;

    	          const base = (text && text.trim()) ? text.trim() : 'Too many attempts.';
    	          if (msg) msg.textContent = base;

    	          refreshCaptchaImage();
    	          clearCaptcha();

    	          startRetryCountdown(retrySeconds, base);
    	          return;
    	        }

    	        const url = res.url || '';

    	        // success redirects away from /login
    	        if (res.redirected && !url.includes('/login')) {
    	          window.location.href = url;
    	          return;
    	        }

    	        // failure: redirected back to /login?... (captcha/error)
    	        if (msg) {
    	          if (url.includes('captcha')) msg.textContent = 'Invalid captcha. Please try again.';
    	          else msg.textContent = 'Invalid username or password.';
    	        }

    	        refreshCaptchaImage();
    	        clearCaptcha();
    	        setButtonEnabled(true);

    	      } catch (err) {
    	        if (msg) msg.textContent = 'Unable to contact server. Please try again.';
    	        setButtonEnabled(true);
    	      }
    	    });
    	  """, getElement());
    	}
    
    private Component buildCard() {
        Div card = new Div();
        card.addClassName("login-card");

        // ---------- HERO HEADER ----------
        Div hero = new Div();
        hero.addClassName("login-hero");

        H4 app = new H4("Scheme MIS 2.0");
        app.addClassName("login-app");

        Paragraph tagline = new Paragraph("Government of Meghalaya");
        tagline.addClassName("login-tagline");

        hero.add(app, tagline);

        // ---------- BODY ----------
        Div body = new Div();
        //body.addClassName("login-body");

        //H3 title = new H3("Log in");
        //title.addClassName("login-title");

        // Spring Security parameter names
        username.getElement().setAttribute("name", "username");
        password.getElement().setAttribute("name", "password");
        captcha.getElement().setAttribute("name", "captcha");

        username.setWidthFull();
        password.setWidthFull();
        captcha.setWidthFull();

        // Captcha setup
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

        message.addClassName("login-message");

        Element submit = new Element("button");
        submit.setText("Log in");
        submit.setAttribute("type", "submit");
        submit.getClassList().add("login-submit");

        // ---- Real HTML form posting to Spring Security ----
        Element form = new Element("form");
        form.setAttribute("method", "post");
        form.setAttribute("action", "/login");
        form.getStyle().set("width", "100%");
        form.setAttribute("id", "loginForm");
        form.setAttribute("action", "/login");
        // CSRF
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

        body.add( username, password, captchaRow, captcha, message);
        form.appendChild(body.getElement());      // Vaadin components inside form

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
            event.forwardTo("");  // home route
            return;               // IMPORTANT
        }

        Map<String, List<String>> params =
                event.getLocation().getQueryParameters().getParameters();

        if (params.containsKey("captcha")) {
            message.setText("Invalid captcha. Please try again.");
            captchaImg.setSrc("/captcha-image?ts=" + System.currentTimeMillis());
        } else if (params.containsKey("error")) {
            message.setText("Invalid username or password.");
            captchaImg.setSrc("/captcha-image?ts=" + System.currentTimeMillis());
        } else if (params.containsKey("expired")) {
            message.setText("Session expired. Please log in again.");
        } else if (params.containsKey("logout")) {
            message.setText("You have been logged out.");
        } else {
            message.setText("");
        }
    }
}