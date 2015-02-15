package cbg.BrendeEr.RPG;

import cbg.BrendeEr.GameLib.ImageLoader;
import cbg.BrendeEr.GameLib.StringMetrics;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author Erik Brendel
 */
public class RPGQuestOverlay {

    public static String getQuestName() {
        return questMessage;
    }
    private static String questMessage = "";
    private static String oldQuestMessage = "";
    private static final int FONT_SIZE = 20;
    private static long startShowing = 0;
    private static final int OLDQUEST_SHOW_FULL_MS = 2000;
    private static final int OLDQUEST_FADE_OUT_MS = 1000;
    private static final int NEWQUEST_SHOW_FULL_MS = 2500;
    private static final int NEWQUEST_FADE_OUT_MS = 1000;
    private static boolean newNotOld = true;
    private static int oldQuestFadeOutMode = 0;
    private static boolean showBox = false;
    private static final BufferedImage unchecked = ImageLoader.get().image("/gui/questUnchecked.png");
    private static final BufferedImage checked = ImageLoader.get().image("/gui/questChecked.png");

    public static void render(BufferedImage imgOrig, Graphics2D gOrig, boolean forceShow) {
        if (showBox || forceShow) {
            int msPassed = (int) (RPG.getActivityTicks() - startShowing);
            float boxAlpha;
            boxAlpha = 1f;
            if (!forceShow) {
                if (newNotOld) {
                    //new challenge 
                    if (msPassed > NEWQUEST_SHOW_FULL_MS + NEWQUEST_FADE_OUT_MS) {
                        //finished, fade out is over
                        showBox = false;
                        return;
                    } else if (msPassed > NEWQUEST_SHOW_FULL_MS) {
                        //still fading
                        boxAlpha = 1f - ((float) (msPassed - NEWQUEST_SHOW_FULL_MS) / (float) NEWQUEST_FADE_OUT_MS);
                    }
                } else {
                    //you did your cahallenge!
                    if (msPassed > OLDQUEST_SHOW_FULL_MS) {
                        //still fading
                        boxAlpha = 1f - ((float) (msPassed - OLDQUEST_SHOW_FULL_MS) / (float) OLDQUEST_FADE_OUT_MS);
                    }
                }
            }

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            int imgWidth = (int) (screenSize.width / 3.5f);

            gOrig.setFont(new Font(Font.MONOSPACED, Font.BOLD, FONT_SIZE));
            ArrayList<String> lines = new ArrayList<>();
            String restText;
            if (newNotOld) {
                restText = questMessage;
            } else {
                restText = oldQuestMessage;
            }

            do {
                String lineNow = restText;
                while (StringMetrics.getBounds(gOrig, lineNow).width > (imgWidth - 20 - 35)) { //margin of 20 near text + 35 for checkbox
                    lineNow = RPGUtils.removeLastWord(lineNow);
                }
                lines.add(lineNow);

                if (restText.length() == lineNow.length()) { //reached end of text
                    restText = "";
                } else {
                    restText = restText.substring(lineNow.length() + 1); //+1 for the next space symbol.
                }
            } while (restText.length() > 0);
            int imgHeight = 20; //margin of 10 near text
            for (String line : lines) {
                imgHeight += StringMetrics.getBounds(gOrig, line).height;
            }

            //background of window
            BufferedImage window = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = window.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, imgWidth, imgHeight);

            //draw text
            g.setFont(gOrig.getFont());
            g.setColor(Color.BLACK);
            int drawHeight = 0; //margin - half of fontsize
            for (String line : lines) {
                drawHeight += StringMetrics.getBounds(g, line).height;
                g.drawString(line, 45, drawHeight);
            }

            //strike text out
            if ((!newNotOld) && oldQuestFadeOutMode == FADE_MODE_STRIKE) {
                drawHeight = 10;
                for (String line : lines) {
                    Dimension lineBounds = StringMetrics.getBounds(g, line);
                    int halfLine = (int) (lineBounds.height / 2f);
                    drawHeight += halfLine;
                    g.drawLine(45, drawHeight, 45 + lineBounds.width, drawHeight);
                    g.drawLine(45, drawHeight - 1, 45 + lineBounds.width, drawHeight - 1);
                    drawHeight += lineBounds.height - halfLine;
                }
            }

            //draw checkbox
            int checkBoxY = ((imgHeight - 40 /*10 borders and 30 checkbox*/) / 2) + 5;
            if (newNotOld) {
                g.drawImage(unchecked, 10, checkBoxY, null);
            } else {
                if (oldQuestFadeOutMode == FADE_MODE_CHECK) {
                    g.drawImage(checked, 10, checkBoxY, null);
                } else {
                    g.drawImage(unchecked, 10, checkBoxY, null);
                }
            }

            //black lines
            g.fillRect(0, 0, imgWidth, 5);
            g.fillRect(0, 0, 5, imgHeight);
            g.fillRect(0, imgHeight - 5, imgWidth, 5);
            g.fillRect(imgWidth - 5, 0, 5, imgHeight);

            //draw (BufferedImage) window on imgOrig using gOrig
            if (boxAlpha != 1f) {
                AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, boxAlpha);
                gOrig.setComposite(ac);
            }
            gOrig.drawImage(window, 10, 10, null);
            gOrig.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP));
        }
    }

    //<editor-fold defaultstate="collapsed" desc="OUTTER ACCESS">
    /**
     * wont show anything of the old quest (like there was nothing)
     */
    public static final int FADE_MODE_NOTHING = 0;

    /**
     * checks the old quest (you did it!)
     */
    public static final int FADE_MODE_CHECK = 1;

    /**
     * strikes out the last quest to start the new one (like aborting or
     * replacing)
     */
    public static final int FADE_MODE_STRIKE = 2;

    public static void replaceQuest(final String questString) {
        replaceQuest(questString, FADE_MODE_CHECK);
    }

    public static void replaceQuest(final String questString, int fadeMode) {
        if (fadeMode != FADE_MODE_NOTHING) {
            removeQuest(fadeMode);
            questMessage = questString;
            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(OLDQUEST_SHOW_FULL_MS);
                    } catch (InterruptedException ex) {
                    }
                    showQuest(questString);
                }
            }.start();
        } else {
            showQuest(questString);
        }
    }
//</editor-fold>

    public static void removeQuest(int fadeMode) {
        oldQuestMessage = questMessage;
        newNotOld = false;
        oldQuestFadeOutMode = fadeMode;
        startShowing = RPG.getActivityTicks();
        showBox = true;
    }

    public static void showQuest(String questName) {
        newNotOld = true;
        startShowing = RPG.getActivityTicks();
        showBox = true;
        questMessage = questName;
    }

}
