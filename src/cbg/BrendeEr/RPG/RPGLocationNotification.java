/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cbg.BrendeEr.RPG;

import cbg.BrendeEr.GameLib.StringMetrics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 *
 * @author Erik Brendel
 */
public class RPGLocationNotification {

    private static String msg = "";
    private static Dimension msgDimension = null;
    private static long startTick = 0;
    private static boolean draw = false;
    private static final Font DEFAULT_FONT = new Font(Font.MONOSPACED, Font.BOLD, 30);

    public static void draw(Graphics2D gOrig, BufferedImage img) {
        if (draw) {
            int delta = (int) (RPG.getActivityTicks() - startTick);
            if (delta > 3000) {
                draw = false;
                startTick = 0;
                msg = "";
                msgDimension = null;
                return;
            }
            if (msgDimension == null) {
                gOrig.setFont(DEFAULT_FONT);
                msgDimension = StringMetrics.getBounds(gOrig, msg);
            }
            int imgHeight = msgDimension.height + 20;
            int imgWidth = msgDimension.width + 20;
            BufferedImage boxImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = boxImage.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, imgWidth, imgHeight);
            g.setColor(Color.BLACK);
            RPGUtils.drawInnerBounds(g, new Point(0, 0), new Point(imgWidth, imgHeight), 5);
            g.setFont(DEFAULT_FONT);
            g.drawString(msg, 10, imgHeight - 20);

            int drawX = img.getWidth() - imgWidth - 10;
            int drawY = 10;
            if(delta < 500) {
                //scrolling in
                drawY = 10 - (int)Math.ceil(((double)(500 - delta)/500d) * (imgHeight + 10d));
            } else if (delta > 2500) {
                //scrolling out
                drawY = 10 - (int)Math.ceil(((double)(delta - 2500)/500d) * (imgHeight + 10d));
            }
            gOrig.drawImage(boxImage, drawX, drawY, null);
        }
    }

    public static void showText(String msg) {
        RPGLocationNotification.msg = msg;
        startTick = RPG.getActivityTicks();
        draw = true;
    }
}
