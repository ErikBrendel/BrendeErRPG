package cbg.BrendeEr.RPG;

import cbg.BrendeEr.GameLib.FileHandler;
import cbg.BrendeEr.GameLib.StringMetrics;
import static cbg.BrendeEr.RPG.RPGUtils.drawInnerBounds;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Erik Brendel
 */
public class RPGMenue {

    public static int selected = 0;
    private static final int maxSelected = 5;
    private static Graphics2D g;
    private static Point boxStart;
    private static Point boxSize;

    public static void render(BufferedImage imgOrig, Graphics2D g) {
        RPGMenue.g = g;
        int imgWidth = imgOrig.getWidth();
        int imgHeight = imgOrig.getHeight();
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(0, 0, imgWidth, imgHeight);

        boxSize = new Point(0, ((maxSelected + 1) * 70) + 25);
        boxSize.x = (int) (imgHeight * 0.4f);

        boxStart = new Point(0, (int) (imgHeight * 0.1f));  //upper-left corner of box
        boxStart.x = imgWidth - boxSize.x - boxStart.y;

        g.setColor(Color.WHITE);
        g.fillRect(boxStart.x, boxStart.y, boxSize.x, boxSize.y);
        g.setColor(Color.BLACK);
        drawInnerBounds(g, boxStart, boxSize, 5);

        g.setFont(MENUE_FONT);

        //inventory
        drawButton(0, RPG.getString("Menue_Inventory"));
        drawButton(1, RPG.getString("Menue_Stats"));
        drawButton(2, RPG.getString("Menue_Map"));
        drawButton(3, RPG.getString("Menue_Save"));
        drawButton(4, RPG.getString("Menue_Quit_Menue"));
        drawButton(5, RPG.getString("Menue_Quit_Desktop"));

        if (subMenueNow != null) {
            subMenueNow.render(g, new Point(50, 150), new Point(boxStart.x - 100, imgHeight - 200));
        }
    }
    private static final int FONT_SIZE = 25;
    private static final Font MENUE_FONT = new Font(Font.MONOSPACED, Font.BOLD, FONT_SIZE);

    private static void drawButton(int id, String text) {
        //subMenues.set(id, submenue);
        int plusY = boxStart.y + 20 + (id * 70);
        int stringX = boxStart.x + (boxSize.x / 2) - StringMetrics.getBounds(g, text).width / 2;
        g.setColor(Color.BLACK);
        drawInnerBounds(g, new Point(boxStart.x + 20, plusY), new Point(boxSize.x - 40, 50), 5);
        if (id == selected) {
            if (!(subMenueNow == null)) {
                g.setColor(Color.GRAY);
            }
            g.fillRect(boxStart.x + 25, plusY + 5, boxSize.x - 50, 40);
            g.setColor(Color.WHITE);

        }
        g.drawString(text, stringX, 30 + plusY);
    }

    public static void buttonPress(int keyID) {
        if (subMenueNow == null) {
            if (keyID == KeyEvent.VK_UP) {
                selected--;
                if (selected < 0) {
                    selected = maxSelected;
                }
            } else if (keyID == KeyEvent.VK_DOWN) {
                selected++;
                if (selected > maxSelected) {
                    selected = 0;
                }
            } else if (keyID == KeyEvent.VK_ENTER) {
                //show sub-menue
                subMenueNow = subMenues.get(selected);
            }
        } else {
            subMenueNow.buttonPress(keyID);
        }
    }

    public static SubMenue subMenueNow = null;
    public static final ArrayList<SubMenue> subMenues = new ArrayList<SubMenue>() {
        {
            add(new Inventory());
            add(new Stats());
            add(new Map());
            add(new WantToSave());
            add(new QuitToMenue());
            add(new QuitToDesktop());
        }
    };

    public static void hideSubMenue() {
        subMenueNow = null;
    }

    //<editor-fold defaultstate="collapsed" desc="All The SubMenue classes">
    public static interface SubMenue {

        public abstract void render(Graphics2D g, Point startPos, Point size);

        public abstract void buttonPress(int keyID);
    }

    public static abstract class YesNoSubMenue implements SubMenue {

        public abstract String getMessage();

        public abstract void interacted(boolean yes);

        public boolean getYesFirstSelected() {
            return false;
        }

        public String getButtonYesMessage() {
            return RPG.getString("YES");
        }

        public String getButtonNoMessage() {
            return RPG.getString("NO");
        }

        @Override
        public void render(Graphics2D g, Point startPos, Point size) {
            g.setColor(Color.WHITE);
            g.fillRect(startPos.x, startPos.y, size.x, size.y);
            g.setColor(Color.BLACK);
            drawInnerBounds(g, startPos, size, 5);
            int messageX = startPos.x + (size.x - StringMetrics.getBounds(g, getMessage()).width) / 2;
            g.drawString(getMessage(), messageX, startPos.y + (size.y - StringMetrics.getBounds(g, getMessage()).height) / 2);

            int smallBoxWidth = (int) Math.ceil((size.x - 50) / 2f);
            int smallBoxX = startPos.x + 15;
            int smallBoxY = startPos.y + size.y - 65;
            int stringX = smallBoxX + (smallBoxWidth - StringMetrics.getBounds(g, getButtonYesMessage()).width) / 2;
            drawInnerBounds(g, new Point(smallBoxX, smallBoxY), new Point(smallBoxWidth, 50), 5);
            if (yesSelected) {
                g.fillRect(smallBoxX + 5, smallBoxY + 5, smallBoxWidth - 10, 40);
                g.setColor(Color.WHITE);
            }
            g.drawString(getButtonYesMessage(), stringX, smallBoxY + FONT_SIZE + 8);

            g.setColor(Color.BLACK);
            smallBoxX += smallBoxWidth + 20;
            stringX = smallBoxX + (smallBoxWidth - StringMetrics.getBounds(g, getButtonNoMessage()).width) / 2;
            drawInnerBounds(g, new Point(smallBoxX, smallBoxY), new Point(smallBoxWidth, 50), 5);
            if (!yesSelected) {
                g.fillRect(smallBoxX + 5, smallBoxY + 5, smallBoxWidth - 10, 40);
                g.setColor(Color.WHITE);
            }
            g.drawString(getButtonNoMessage(), stringX, smallBoxY + FONT_SIZE + 8);
        }
        private boolean yesSelected = getYesFirstSelected();

        @Override
        public void buttonPress(int keyID) {
            if (keyID == KeyEvent.VK_LEFT || keyID == KeyEvent.VK_RIGHT) {
                yesSelected = !yesSelected;
            } else if (keyID == KeyEvent.VK_ENTER) {
                interacted(yesSelected);
            }
        }
    }

    public static abstract class WhiteBoxSubMenue implements SubMenue {

        @Override
        public void render(Graphics2D g, Point startPos, Point size) {
            g.setColor(Color.WHITE);
            g.fillRect(startPos.x, startPos.y, size.x, size.y);
            g.setColor(Color.BLACK);
            drawInnerBounds(g, startPos, size, 5);
            continueRender(g, startPos, size);
        }

        public abstract void continueRender(Graphics2D g, Point startPos, Point size);
    }

    public static class QuitToDesktop extends YesNoSubMenue {

        @Override
        public String getMessage() {
            return RPG.getString("Menue_ConfirmLeave");

        }

        @Override
        public void interacted(boolean yes) {
            if (yes) {
                RPG.endGame();
            } else {
                super.yesSelected = false;
                RPGMenue.hideSubMenue();
            }
        }

    }

    public static class QuitToMenue extends YesNoSubMenue {

        @Override
        public String getMessage() {
            return RPG.getString("Menue_ConfirmMenue");

        }

        @Override
        public void interacted(boolean yes) {
            if (yes) {
                RPG.showActivity("Menue");
            } else {
                super.yesSelected = false;
                RPGMenue.hideSubMenue();
            }
        }

    }

    public static class WantToSave extends YesNoSubMenue {

        @Override
        public boolean getYesFirstSelected() {
            return true;
        }

        @Override
        public String getMessage() {
            return RPG.getString("Menue_WantToSave");

        }

        @Override
        public void interacted(boolean yes) {
            if (yes) {
                //check here is there is a slot
                boolean saveFree = false;
                if (FileHandler.getFileContent(GlobalVariables.getSavesPath() + "playerDirection", "[Check for existing save]").equals("")) {
                    saveFree = true;
                }
                if (saveFree) {
                    subMenueNow = new SaveAnimation();
                } else {
                    subMenueNow = new ConfirmToSave();
                }
            } else {
                super.yesSelected = false;
                RPGMenue.hideSubMenue();
            }
        }

    }

    public static class ConfirmToSave extends YesNoSubMenue {

        @Override
        public String getMessage() {
            return RPG.getString("Menue_ConfirmOverwrite");

        }

        @Override
        public void interacted(boolean yes) {
            if (yes) {
                //start saving
                subMenueNow = new SaveAnimation();
            } else {
                super.yesSelected = false;
                RPGMenue.hideSubMenue();
            }
        }

    }

    public static class SaveAnimation extends WhiteBoxSubMenue {

        public SaveAnimation() {

            //saving
            GlobalVariables.save();

            startTick = RPG.getActivityTicks();
        }
        private final long startTick;
        private static final int TICKS_DURATION = 1000;
        private boolean finished = false;

        @Override
        public void continueRender(Graphics2D g, Point startPos, Point size) {

            int ticksDelta = (int) (RPG.getActivityTicks() - startTick);
            if (ticksDelta < TICKS_DURATION) {
                int boxX = startPos.x + 15;
                int boxY = startPos.y + (size.y / 2) - 30;
                int boxSizeX = size.x - 30;
                int boxSizeY = 60;
                g.setColor(Color.BLACK);
                drawInnerBounds(g, new Point(boxX, boxY), new Point(boxSizeX, boxSizeY), 5);
                int barLength = (int) Math.ceil(((double) ticksDelta / (double) TICKS_DURATION) * (boxSizeX - 20));
                g.fillRect(boxX + 10, boxY + 10, barLength, 40);
            } else {
                String msg = RPG.getString("Menue_FinishedSaving");
                Dimension bounds = StringMetrics.getBounds(g, msg);
                g.drawString(msg, startPos.x + (size.x - bounds.width) / 2, startPos.y + (size.y - bounds.height) / 2 + 20);
                finished = true;
            }
        }

        @Override
        public void buttonPress(int keyID) {
            if (finished && keyID == KeyEvent.VK_ENTER) {
                RPGMenue.hideSubMenue();
            }
        }
    }

    public static class Inventory extends WhiteBoxSubMenue {

        public Inventory() {
            itemNames = new ArrayList<>();
            itemNames.add("Back");
            metrics = new ArrayList<>();
            metrics.add(new Dimension(100, 25));
        }

        int selected = 0;
        ArrayList<String> itemNames = null;
        String[] itemIDs = null;
        ArrayList<Dimension> metrics = null;

        @Override
        public void continueRender(Graphics2D g, Point startPos, Point size) {
            /*String msg = "Your Inventory is empty";
             Dimension bounds = StringMetrics.getBounds(g, msg);
             g.drawString(msg, startPos.x + (size.x - bounds.width) / 2, startPos.y + (size.y - bounds.height) / 2 + 10); /* */
            g.setColor(Color.BLACK);
            Point box1Start = new Point(startPos.x + 15, startPos.y + 15);
            Point box1Size = new Point((size.x - 40) / 2, size.y - 30);
            Point box2Start = new Point(startPos.x + 25 + box1Size.x, startPos.y + 15);
            Point box2Size = box1Size;
            drawInnerBounds(g, box1Start, box1Size, 5);
            drawInnerBounds(g, box2Start, box2Size, 5);
            g.setFont(MENUE_FONT);

            //
            //draw left box: itemList
            //
            //update itemlist sometimes
            if (true) { //check if needed to be updated
                itemNames.clear();
                metrics.clear();
                itemIDs = GlobalVariables.getString("playerInventory").split("\\+");
                for (String id : itemIDs) {
                    if (!id.equals("")) {
                        String translate = RPG.getString(id + "_name");
                        itemNames.add(translate);
                        metrics.add(StringMetrics.getBounds(g, translate));
                    }
                }
                itemNames.add("Back");
                metrics.add(StringMetrics.getBounds(g, "Back"));
            }

            for (int i = 0; i < itemNames.size(); i++) {
                Point startItemBox = new Point(box1Start.x + 10, box1Start.y + 10 + (i * (45 + 5)));
                Point sizeItemBox = new Point(box1Size.x - 20, 45);
                drawInnerBounds(g, startItemBox, sizeItemBox, 5);
                if (i == selected) {
                    if (drawDetails) {
                        g.setColor(Color.GRAY);
                    } else {
                        g.setColor(Color.BLACK);
                    }
                    g.fillRect(startItemBox.x + 5, startItemBox.y + 5, sizeItemBox.x - 10, sizeItemBox.y - 10);
                    g.setColor(Color.WHITE);
                }
                g.drawString(itemNames.get(i), startItemBox.x + (sizeItemBox.x - metrics.get(i).width) / 2, startItemBox.y + 35);
                g.setColor(Color.BLACK);
            }

            //
            //draw right box: armor
            //
            g.drawString("Armor: Coming soon.", box2Start.x + 10, box2Start.y + (box2Size.y / 2));

            //
            //draw details
            //
            if (drawDetails) {
                String itemName = RPG.getString(detailsItemId + "_name");
                String itemDescription = RPG.getString(detailsItemId + "_description");
                int maxLineLength = (int) (size.x * 0.8f); //px
                ArrayList<String> lines = new ArrayList<>();
                lines.add(itemName);
                lines.add("");
                //lines.add(itemDescription); //insert line breaks here, please
                ArrayList<String> descLines = RPGUtils.breakIntoLines(itemDescription, maxLineLength, g);
                for (String l : descLines) {
                    lines.add(l);
                }

                ArrayList<Dimension> lineMetrics = new ArrayList<>();
                for (String line : lines) {
                    lineMetrics.add(StringMetrics.getBounds(g, line));
                }
                int longest = 0;
                int totalHeight = 0;
                for (Dimension lineD : lineMetrics) {
                    if (lineD.width > longest) {
                        longest = lineD.width;
                    }
                    totalHeight += lineD.height;
                }
                Point detailsSize = new Point(longest + 20, totalHeight + 20);
                Point detailsStart = new Point(startPos.x + (size.x - detailsSize.x) / 2, startPos.y + (size.y - detailsSize.y) / 2);
                g.setColor(Color.WHITE);
                g.fillRect(detailsStart.x, detailsStart.y, detailsSize.x, detailsSize.y);
                g.setColor(Color.BLACK);
                drawInnerBounds(g, detailsStart, detailsSize, 5);

                int drawHeight = detailsStart.y;
                for (int i = 0; i < lines.size(); i++) {
                    drawHeight += lineMetrics.get(i).height;
                    int drawX = detailsStart.x + (detailsSize.x - lineMetrics.get(i).width) / 2;
                    g.drawString(lines.get(i), drawX, drawHeight);
                }
            }
        }

        private boolean drawDetails = false;
        private String detailsItemId = "";

        @Override
        public void buttonPress(int keyID) {
            if (drawDetails) {
                drawDetails = false;
                return;
            }
            if (keyID == KeyEvent.VK_ENTER) {
                if (selected == itemNames.size() - 1) {
                    RPGMenue.hideSubMenue();
                } else {
                    //show details
                    drawDetails = true;
                    detailsItemId = itemIDs[selected];
                }
            } else if (keyID == KeyEvent.VK_DOWN) {
                selected++;
                if (selected >= itemNames.size()) {
                    selected = 0;
                }
            } else if (keyID == KeyEvent.VK_UP) {
                selected--;
                if (selected < 0) {
                    selected = itemNames.size() - 1;
                }
            }
        }
    }

    public static class Stats extends WhiteBoxSubMenue {

        @Override
        public void continueRender(Graphics2D g, Point startPos, Point size) {
            String msg = "You have no stats yet. Go kill something!";
            Dimension bounds = StringMetrics.getBounds(g, msg);
            g.drawString(msg, startPos.x + (size.x - bounds.width) / 2, startPos.y + (size.y - bounds.height) / 2 + 10);
        }

        @Override
        public void buttonPress(int keyID) {
            if (keyID == KeyEvent.VK_ENTER) {
                RPGMenue.hideSubMenue();
            }
        }
    }

    public static class Map extends WhiteBoxSubMenue {

        @Override
        public void continueRender(Graphics2D g, Point startPos, Point size) {
            String msg = "You own no map.";
            Dimension bounds = StringMetrics.getBounds(g, msg);
            g.drawString(msg, startPos.x + (size.x - bounds.width) / 2, startPos.y + (size.y - bounds.height) / 2 + 10);
        }

        @Override
        public void buttonPress(int keyID) {
            if (keyID == KeyEvent.VK_ENTER) {
                RPGMenue.hideSubMenue();
            }
        }
    }

//</editor-fold>
}
