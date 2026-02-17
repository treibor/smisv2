package com.smis.security;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;

@Service
public class CaptchaService {

    private static final SecureRandom RND = new SecureRandom();
    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // avoid confusing chars
    private static final int W = 160, H = 50;

    public Captcha generate() {
        String text = randomText(6);
        byte[] png = renderPng(text);
        return new Captcha(text, png);
    }

    private String randomText(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(CHARS.charAt(RND.nextInt(CHARS.length())));
        return sb.toString();
    }

    private byte[] renderPng(String text) {
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // background
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, W, H);

            // noise lines
            g.setColor(new Color(200, 200, 200));
            for (int i = 0; i < 10; i++) {
                int x1 = RND.nextInt(W), y1 = RND.nextInt(H);
                int x2 = RND.nextInt(W), y2 = RND.nextInt(H);
                g.drawLine(x1, y1, x2, y2);
            }

            // text
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.setColor(new Color(20, 20, 20));
            FontMetrics fm = g.getFontMetrics();
            int x = (W - fm.stringWidth(text)) / 2;
            int y = (H + fm.getAscent()) / 2 - 4;
            g.drawString(text, x, y);

            // noise dots
            for (int i = 0; i < 200; i++) {
                int xDot = RND.nextInt(W), yDot = RND.nextInt(H);
                img.setRGB(xDot, yDot, new Color(RND.nextInt(255), RND.nextInt(255), RND.nextInt(255)).getRGB());
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate captcha", e);
        } finally {
            g.dispose();
        }
    }

    public record Captcha(String text, byte[] pngBytes) {}
}
