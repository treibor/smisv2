package com.smis.security;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CaptchaController {

    public static final String SESSION_KEY = "LOGIN_CAPTCHA";

    private final CaptchaService captchaService;

    public CaptchaController(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    @GetMapping(value = "/captcha-image", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> captcha(HttpSession session) {
        CaptchaService.Captcha c = captchaService.generate();
        session.setAttribute(SESSION_KEY, c.text());
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .contentType(MediaType.IMAGE_PNG)
                .body(c.pngBytes());
    }
}