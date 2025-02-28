package com.smis.security.captcha;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;

import javax.imageio.ImageIO;
import javax.xml.transform.stream.StreamSource;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;
public class CapthaImpl implements Captcha {
    private static final int COUNT_NUM = 6;
    private static final int WIDTH_IMG = 150;
    private static final int HEIGHT_IMG = 40;
    private String genStr;
    private String charsInImg = "QWERTYUIOPASDFGHJKLZXCVBNM1234567890";
    private Random random = new Random(System.nanoTime());

    private void getRandomString() {
        char[] chars = charsInImg.toCharArray();
        StringBuilder tmpStr = new StringBuilder();
        for (int i = 0; i < COUNT_NUM; i++) {
            tmpStr.append(chars[random.nextInt(chars.length - 1)]);
        }
        genStr = tmpStr.toString();
    }

    @Override
    public BufferedImage getCaptchaBufferedImage() {
        getRandomString();
        BufferedImage bufferedImage = new BufferedImage(WIDTH_IMG, HEIGHT_IMG, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bufferedImage.createGraphics();
        Font font = new Font("Charlemagne Std", Font.BOLD, 25);
        graphics.setFont(font);
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.addRenderingHints(hints);
        GradientPaint blackToGray = new GradientPaint(50, 50, Color.BLACK,300, 100, Color.LIGHT_GRAY);
        graphics.setPaint(blackToGray);
        graphics.fillRect(0,0, WIDTH_IMG, HEIGHT_IMG);
        //graphics.setColor(new Color(25, 159, 110));
        graphics.setColor(new Color(101,168, 215));
        int x = 0;
        int y;
        for (int i = 0; i < genStr.length(); i++) {
            x += 10 + (Math.abs(random.nextInt()) % 15);
            y = 20 + Math.abs(random.nextInt()) % 20;
            graphics.drawChars(genStr.toCharArray(), i, 1, x, y);
        }
        graphics.dispose();
        return bufferedImage;
    }

    @Override
    public boolean checkUserAnswer(String userAnswer) {
        return genStr.equals(userAnswer);
    }

    @Override
    public Image getCaptchaImg(){
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(getCaptchaBufferedImage(), "png", bos);
            StreamResource streamResource = new StreamResource("img.png", () -> new ByteArrayInputStream(bos.toByteArray()));
            return new Image(streamResource, "img.png");
        }catch (IOException e){
            return null;
        }
    }
}