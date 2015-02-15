/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cbg.BrendeEr.RPG;

import cbg.BrendeEr.GameLib.ButtonAction;
import cbg.BrendeEr.GameLib.GameActivity;
import cbg.BrendeEr.GameLib.ImageLoader;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author Erik Brendel
 */
public class IntroActivity extends GameActivity {

    @Override
    public String getName() {
        return "Intro";
    }

    @Override
    public void onLaunch() {

        //scale all the images
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        while (imagesRaw.size() > 0) {
            images.add(ImageLoader.getScaledImage(imagesRaw.remove(0), screenSize.width, screenSize.height, ImageLoader.MODE_MEDIUM));
        }

    }

    private static final ArrayList<BufferedImage> imagesRaw = new ArrayList<>();
    private static final ArrayList<BufferedImage> images = new ArrayList<>();
    private static int showPos = 0;
    private static int showTime = 1000;
    private static int fadeMS = 100;

    static void setIntroTime(float seconds) {
        showTime = (int) Math.ceil(seconds * 1000f);
    }

    static void setFadeMS(int fadeMS) {
        IntroActivity.fadeMS = fadeMS;
    }

    public static void addImage(BufferedImage img) {
        imagesRaw.add(img);
    }

    @Override
    public void paint(BufferedImage img) {
        if (showPos >= images.size()) {
            goToMenue();
        } else {
            Graphics2D g = img.createGraphics();
            g.drawImage(images.get(showPos), 0, 0, null);

            //claculate fade
            int cAlpha = (int) Math.ceil(((double) (RPG.getActivityTicks() % showTime) / (double) fadeMS) * 255d);
            if (cAlpha > 255) {
                cAlpha = (int) Math.ceil(((double) (showTime - (RPG.getActivityTicks() % showTime)) / (double) fadeMS) * 255d);
                if (cAlpha > 255) {
                    cAlpha = 255;
                }
            }
            cAlpha = 255 - cAlpha;
            Color fadeColor = new Color(0, 0, 0, cAlpha);
            g.setColor(fadeColor);
            g.fillRect(0, 0, images.get(0).getWidth(), images.get(0).getHeight());

            showPos = (int) RPG.getActivityTicks() / showTime;
        }
    }

    @Override
    public void keyPressed(int keyCode, int action, Point location) {
        if (action == ButtonAction.BUTTON_PRESS) {
            goToMenue();
        }
    }

    private void goToMenue() {
        System.out.println("Show the Menue!");
        RPG.showActivity("Menue");
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
        }
        images.clear();
    }
}
