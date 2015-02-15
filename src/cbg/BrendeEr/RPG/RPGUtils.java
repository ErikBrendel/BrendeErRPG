package cbg.BrendeEr.RPG;

import cbg.BrendeEr.GameLib.StringMetrics;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Erik Brendel
 */
public class RPGUtils {

    public static String removeLastWord(String input) {
        String[] words = input.split(" ");
        String output = "";
        for (int i = 0; i < words.length - 1; i++) {
            output = output + " " + words[i];
        }
        output = output.substring(1);
        return output;
    }

    public static ArrayList<String> breakIntoLines(String longLine, int maxPixelLength, Font f) {
        BufferedImage useless = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = useless.createGraphics();
        g.setFont(f);
        return breakIntoLines(longLine, maxPixelLength, g);
    }

    public static ArrayList<String> breakIntoLines(String longLine, int maxPixelLength, Graphics2D g) {
        ArrayList<String> lines = new ArrayList<>();
        do {
            String lineNow = longLine;
            while (StringMetrics.getBounds(g, lineNow).width >= maxPixelLength) {
                lineNow = removeLastWord(lineNow);
            }
            lines.add(lineNow);
            if (longLine.length() == lineNow.length()) {
                longLine = "";
            } else {
                longLine = longLine.substring(lineNow.length() + 1); //plus1 for space
            }
        } while (longLine.length() > 1);

        return lines;
    }

    /**
     *
     * @param g the graphics object
     * @param p1 upper left corner
     * @param dimension size of rectangle
     * @param size thickness of border
     */
    public static void drawInnerBounds(Graphics2D g, Point p1, Point dimension, int size) {
        g.fillRect(p1.x, p1.y, dimension.x, size);
        g.fillRect(p1.x, p1.y, size, dimension.y);
        g.fillRect(p1.x, p1.y + dimension.y - size, dimension.x, size);
        g.fillRect(p1.x + dimension.x - size, p1.y, size, dimension.y);
    }

    public static void printMap(HashMap m, String name) {
        System.out.println("Map: " + name);
        for (Object k : m.keySet()) {
            System.out.println("Map[" + k + "] = " + m.get(k));
        }
    } /* */


    public static boolean isPureAscii(String v) {
        return StandardCharsets.US_ASCII.newEncoder().canEncode(v);
    }

    public static void deleteWholeFolder(File f, String[] exceptFileNames) {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                deleteWholeFolder(c, exceptFileNames);
            }
        } else {
            if (!contains(f.getName(), exceptFileNames)) {
                //System.out.println("Deleting: " + f.getName());
                f.delete();
            }
        }
    }

    public static <T> boolean contains(T search, T[] array) {
        if (search instanceof String) {
            String string = (String) search;
            for (T array1 : array) {
                String compare = (String) array1;
                if (string.equals(compare)) {
                    return true;
                }
            }
        } else {
            for (T t : array) {
                if (t == search) {
                    return true;
                }
            }
        }
        return false;
    }

}
