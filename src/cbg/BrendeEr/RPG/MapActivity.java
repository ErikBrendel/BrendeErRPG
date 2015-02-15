package cbg.BrendeEr.RPG;

import cbg.BrendeEr.GameLib.ButtonAction;
import cbg.BrendeEr.GameLib.GameActivity;
import cbg.BrendeEr.GameLib.StringMetrics;
import static cbg.BrendeEr.RPG.RPGUtils.drawInnerBounds;
import static cbg.BrendeEr.RPG.RPGUtils.removeLastWord;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author Erik Brendel
 */
public class MapActivity extends GameActivity {
    @Override
    public void onLaunch() {
        //just to update the song :D
        RPG.updateSong();
    }
    

    @Override
    public String getName() {
        return "Map";
    }

    private static RPGWorld world;

    public static void setWorld(RPGWorld world) {
        MapActivity.world = world;
    }
    private static RPGPlayer player;

    public static void setPlayer(RPGPlayer p) {
        player = p;
    }

    public static RPGPlayer getPlayer() {
        return player;
    }

    //<editor-fold defaultstate="collapsed" desc="drawing">
    //make smooth movements here please
    private static final Object moveLock = new Object();
    public static Point oldDrawCoords = null;
    public static Point oldPlayerCoords = null;
    public static int oldPlayerDirection = 0;
    public static long startMovingTick = 0;
    public static boolean movingEnabled = true;
    private static boolean showPauseMenue = false;
    private static final double increaseMovingAnimationSpeed = 1.5d;  //scroll faster then move to prevent laggs

    @Override
    public void paint(BufferedImage img) {

        RPGMap activeMap = world.getMap(player.getMapName());
        BufferedImage mapImage = activeMap.getImage();
        BufferedImage mapImageTop = activeMap.getTopImage();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Point segmentDimension /* in pixels */ = new Point((int) (mapImage.getWidth() / activeMap.getDimension().x), (int) (mapImage.getHeight() / activeMap.getDimension().y));

        //draw map on screen
        Graphics2D g = img.createGraphics();

        int drawX;
        int drawY;

        if (player.getLocation().x <= activeMap.getMaxSegmentsViewable()) {
            //player on the left border
            drawX = 0;
        } else if (player.getLocation().x >= activeMap.getDimension().x - activeMap.getMaxSegmentsViewable()) {
            //player on right border
            drawX = screenSize.width - mapImage.getWidth();
        } else {
            //player is not on a vertical border
            //player in the center
            drawX = (int) Math.ceil(screenSize.width / 2f - ((float) segmentDimension.x * ((float) player.getLocation().x + 0.5f)));
        }

        drawY = (int) Math.ceil(screenSize.height / 2f - ((float) segmentDimension.y * ((float) player.getLocation().y + 0.5f)));


        //center image, if too small
        if (mapImage.getWidth() <= screenSize.width) {
            drawX = (screenSize.width - mapImage.getWidth()) / 2;
        }
        if (mapImage.getHeight() <= screenSize.height) {
            drawY = (screenSize.height - mapImage.getHeight()) / 2;
        }
        
        //no blacvk border
        if (drawY > 0) {
            drawY = 0;
        }
        int maxDrawY = screenSize.height - mapImage.getHeight();
        if (drawY < maxDrawY) {
            drawY = maxDrawY;
        }
        if (drawX > 0) {
            drawX = 0;
        }
        int maxDrawX = screenSize.width - mapImage.getWidth();
        if (drawX < maxDrawX) {
            drawX = maxDrawX;
        }

        Graphics2D gMap = mapImage.createGraphics();

        //draw all the other entities
        for (RPGMapEntity entity : activeMap.entities) {
            int eX = entity.getLocation().x * segmentDimension.x;
            int eY = entity.getLocation().y * segmentDimension.y;
            gMap.drawImage(entity.getImage(segmentDimension), eX, eY, null);
        }
        synchronized (moveLock) {

            int ticksDelay = (int) (RPG.getActivityTicks() - startMovingTick);
            double ticksDelayPercent = (double) ticksDelay / (double) (MOVING_TIME_MS) * increaseMovingAnimationSpeed;
            //now mix them coords with the old ones
            if (oldDrawCoords == null) {
                oldDrawCoords = new Point(drawX, drawY);
            }
            if (ticksDelayPercent >= 1d) {
                //finished scrolling
                oldDrawCoords = new Point(drawX, drawY);
            } else {
                //still scrolling
                drawX = (int) Math.ceil(((1d - ticksDelayPercent) * oldDrawCoords.x) + (ticksDelayPercent * drawX));
                drawY = (int) Math.ceil(((1d - ticksDelayPercent) * oldDrawCoords.y) + (ticksDelayPercent * drawY));
            }

            //and draw the player onto his map
            BufferedImage playerImage;
            Point playerLocationOnMap = new Point(player.getLocation().x * segmentDimension.x, player.getLocation().y * segmentDimension.y);
            //mix playerlocationonmap with oldPlayerCoords
            if (oldPlayerCoords == null) {
                oldPlayerCoords = playerLocationOnMap;
            }
            if (ticksDelayPercent >= 1d) {
                //no scrolling
                oldPlayerCoords = playerLocationOnMap;
                oldPlayerDirection = player.getDirection();
                playerImage = player.getEntity(segmentDimension).getImage(segmentDimension);
            } else {
                //System.out.println("ticksDelayPercent = " + ticksDelayPercent);
                playerLocationOnMap = new Point((int) Math.ceil(((1d - ticksDelayPercent) * oldPlayerCoords.x) + (ticksDelayPercent * playerLocationOnMap.x)),
                        (int) Math.ceil(((1d - ticksDelayPercent) * oldPlayerCoords.y) + (ticksDelayPercent * playerLocationOnMap.y)));
                int newPlayerDirection = player.getDirection();
                player.setDirection(oldPlayerDirection);
                playerImage = player.getEntity(segmentDimension).getImage(segmentDimension);
                player.setDirection(newPlayerDirection);
            } /* */

            gMap.drawImage(playerImage, playerLocationOnMap.x, playerLocationOnMap.y, null);
        }

        g.drawImage(mapImage, drawX, drawY, null);
        g.drawImage(mapImageTop, drawX, drawY, null);

        //make it darker for fading
        int fadeDelta = (int) (RPG.getActivityTicks() - startFadeTick);
        if (!(fadeDelta > FADE_MS)) {
            float fadePercent = (float) (fadeDelta) / (float) (FADE_MS);
            //fade a bit
            int alpha = 0; //255 --> black
            if (fadeInNotOut) {
                //fade in
                alpha = (int) (255f * (1f - fadePercent));
            } else {
                //fade out
                alpha = (int) (255f * fadePercent);
            }
            g.setColor(new Color(0, 0, 0, alpha));
            g.fillRect(0, 0, img.getWidth(), img.getHeight());
        }

        // draw the textbox of textShown
        drawTextBox(img, g);

        //draw the pause menue
        if (showPauseMenue) {
            RPGMenue.render(img, g);
        }

        //draw the questbox
        RPGQuestOverlay.render(img, g, showPauseMenue);
        
        //draw the LocationNotification
        RPGLocationNotification.draw(g, img);
    }

    private static long startFadeTick = 0;
    private static final int FADE_MS = 500;
    private static boolean fadeInNotOut = true;

    public static void fadeOut() {
        startFadeTick = RPG.getActivityTicks();
        fadeInNotOut = false;
        try {
            Thread.sleep(FADE_MS);
        } catch (InterruptedException ex) {
        }
    }

    public static void fadeIn() {
        startFadeTick = RPG.getActivityTicks();
        fadeInNotOut = true;
        try {
            Thread.sleep(FADE_MS);
        } catch (InterruptedException ex) {
        }
    }

//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="KeyListener">
    /**
     * When a button was pressed
     *
     * @param keyCode button-code
     * @param action action mode --> see also ButtonCode.java
     * @param location the mouse pointer location
     */
    @Override
    public void keyPressed(int keyCode, int action, Point location) {
        if (action == ButtonAction.BUTTON_PRESS) {
            if (showTextEnterMenue) {  //enter some text
                if (keyCode == KeyEvent.VK_BACK_SPACE) { //remove last one
                    textEnterMenueContent = textEnterMenueContent.substring(0, textEnterMenueContent.length() - 1);
                }
                if (keyCode == KeyEvent.VK_ENTER) { //finished
                    if (textEnterMenueContent.length() >= textEnterMinLength) {
                        showTextEnterMenue = false;
                    }
                }
                String input = "" + (char) keyCode; //somehow just capital letters :o
                if (textEnterMenueContent.length() != 0) { //just first one big
                    input = input.toLowerCase();
                }
                if (input.matches("[a-zA-Z0-9 ]+") && (textEnterMenueContent.length() < textEnterMaxLength)) {
                    textEnterMenueContent = textEnterMenueContent + input;
                }
                return;
            }

            switch (keyCode) {
                case KeyEvent.VK_W:
                case KeyEvent.VK_UP:
                    move(0);
                    break;
                case KeyEvent.VK_D:
                case KeyEvent.VK_RIGHT:
                    move(1);
                    break;
                case KeyEvent.VK_S:
                case KeyEvent.VK_DOWN:
                    move(2);
                    break;
                case KeyEvent.VK_A:
                case KeyEvent.VK_LEFT:
                    move(3);
                    break;
                case KeyEvent.VK_SPACE:
                case KeyEvent.VK_ENTER:
                    interact();
                    break;
                case KeyEvent.VK_ESCAPE:
                    if (showPauseMenue) {
                        RPGMenue.subMenueNow = null;
                        showPauseMenue = false;
                    } else {
                        showPauseMenue = true;
                    }
                    break;
                case KeyEvent.VK_E: //show inventory
                    if (!showPauseMenue) {
                        RPGMenue.subMenueNow = RPGMenue.subMenues.get(0);
                        showPauseMenue = true;
                        RPGMenue.selected = 0;
                    }
                    break;
                case KeyEvent.VK_Q: //show Map
                    if (!showPauseMenue) {
                        RPGMenue.subMenueNow = RPGMenue.subMenues.get(2);
                        showPauseMenue = true;
                        RPGMenue.selected = 2;
                    }
                    break;
            }
        } else if (action == ButtonAction.BUTTON_UP) {
            switch (keyCode) {
                case KeyEvent.VK_W:
                case KeyEvent.VK_UP:
                    stopMoving();
                    break;
                case KeyEvent.VK_D:
                case KeyEvent.VK_RIGHT:
                    stopMoving();
                    break;
                case KeyEvent.VK_S:
                case KeyEvent.VK_DOWN:
                    stopMoving();
                    break;
                case KeyEvent.VK_A:
                case KeyEvent.VK_LEFT:
                    stopMoving();
                    break;
            }
        }
    }

//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Moving">
    public static final int MOVING_TIME_MS = 200; //200

    public static void move(final int direction) {
        if (showPauseMenue) {
            //send to menue, if opened
            switch (direction) {
                case 0:
                    RPGMenue.buttonPress(KeyEvent.VK_UP);
                    break;
                case 1:
                    RPGMenue.buttonPress(KeyEvent.VK_RIGHT);
                    break;
                case 2:
                    RPGMenue.buttonPress(KeyEvent.VK_DOWN);
                    break;
                case 3:
                    RPGMenue.buttonPress(KeyEvent.VK_LEFT);
                    break;
            }
            return;
        }

        if (!showSomeText) {
            synchronized (moveLock) {
                player.setDirection(direction);
                if (!player.isMoving()) {
                    player.setMoving(true);
                    new Thread() {
                        public void run() {
                            //wait a bit to enable standing-rotation
                            try {
                                for (int i = 0; i < MOVING_TIME_MS / 2; i++) {
                                    Thread.sleep(1);
                                    if (!player.isMoving()) {
                                        return;
                                    }
                                }
                            } catch (InterruptedException ex) {
                                return;
                            }
                            while (player.isMoving()) {

                                moveOneStep(direction);

                                try {
                                    for (int i = 0; i < MOVING_TIME_MS; i++) {
                                        Thread.sleep(1);
                                        if (!player.isMoving()) {
                                            return;
                                        }
                                    }
                                } catch (InterruptedException ex) {
                                    return;
                                }
                            }
                        }
                    }.start();
                }
            }
        } else {
            if (showSelectMenue) {
                //switch between the selected choices in choicesMenue
                if (direction == 2) {
                    selectedMenueItemID++;
                    if (selectedMenueItemID >= selectMenueItems.size()) {
                        selectedMenueItemID = 0;
                    }
                } else if (direction == 0) {
                    selectedMenueItemID--;
                    if (selectedMenueItemID < 0) {
                        selectedMenueItemID = selectMenueItems.size() - 1;
                    }
                }
            }
        }
    }

    public static void moveOneStep(int direction) {
        if (movingEnabled) {
            player.setDirection(direction);

            startMovingTick = RPG.getActivityTicks();

            //launch the events for the segment / the entities
            world.getMap(player.getMapName()).getSegment(player.getLocation().x, player.getLocation().y).onPlayerLeave(player.getDirection());
            for (RPGMapEntity e : world.getMap(player.getMapName()).entities) {
                if (e.getLocation().x == player.getLocation().x && e.getLocation().y == player.getLocation().y) {
                    e.onPlayerLeave(player.getDirection());
                }
            }
            Point newLoc;
            switch (player.getDirection()) {
                case 0:
                    newLoc = new Point(player.getLocation().x, player.getLocation().y - 1);
                    break;
                case 1:
                    newLoc = new Point(player.getLocation().x + 1, player.getLocation().y);
                    break;
                case 2:
                    newLoc = new Point(player.getLocation().x, player.getLocation().y + 1);
                    break;
                case 3:
                    newLoc = new Point(player.getLocation().x - 1, player.getLocation().y);
                    break;
                default:
                    return;
            }
            if (isWalkable(newLoc)) {
                player.setLocation(newLoc);
            }

            //launch the entering events
            world.getMap(player.getMapName()).getSegment(player.getLocation().x, player.getLocation().y).onPlayerEnter(player.getDirection());
            for (RPGMapEntity e : world.getMap(player.getMapName()).entities) {
                if (e.getLocation().x == player.getLocation().x && e.getLocation().y == player.getLocation().y) {
                    e.onPlayerEnter(player.getDirection());
                }
            }
        }
    }


    public static void stopMoving() {
        synchronized (moveLock) {
            player.setMoving(false);
        }
    }

    public static boolean isWalkable(Point newLoc) { //entities overwrite isWalkable() of segments
        boolean segmentErg = true;
        Point max = world.getMap(player.getMapName()).getDimension();
        if (newLoc.x < 0 || newLoc.x >= max.x) {
            segmentErg = false;
        }
        if (newLoc.y < 0 || newLoc.y >= max.y) {
            segmentErg = false;
        }

        if (segmentErg && !world.getMap(player.getMapName()).getSegment(newLoc.x, newLoc.y).isWalkable()) {
            segmentErg = false;
        }

        for (RPGMapEntity e : world.getMap(player.getMapName()).entities) {
            if (e.getLocation().x == newLoc.x && e.getLocation().y == newLoc.y) {
                if (e.isWalkable()) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        return segmentErg;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Interacting">
    public void interact() {
        if (!showPauseMenue) {
            if (showSomeText) {
                if (showSelectMenue) {
                    //pick this one in the active selectMenue
                    showSelectMenue = false;

                } else {
                    //just normal text shown
                    if (animateFirstLine || animateLastLine) {
                        //just cancle the animation
                        animateFirstLine = false;
                        animateLastLine = false;
                    } else {
                        //remove the first line from textShown
                        //remove two lines, if there were only two
                        textShown.remove(0);
                        if (textShown.size() == 1) {
                            textShown.remove(0);
                        }
                        if (!(textShown.size() > 0)) {
                            showSomeText = false;
                        }
                        //animate the new shown line
                        animateLastLine = true;
                        startAnimationTick = RPG.getActivityTicks();
                    }
                }
            } else {
                //launch the interact-events
                world.getMap(player.getMapName()).getSegment(player.getLocation().x, player.getLocation().y).onInteractFrom(player.getDirection());
                for (RPGMapEntity e : world.getMap(player.getMapName()).entities) {
                    if (e.getLocation().x == player.getLocation().x && e.getLocation().y == player.getLocation().y) {
                        e.onInteractFrom(player.getDirection());
                    }
                }

                try {
                    Point interactToLocation = new Point(player.getLocation().x, player.getLocation().y);
                    switch (player.getDirection()) {
                        case 0:
                            interactToLocation.y -= 1;
                            break;
                        case 1:
                            interactToLocation.x += 1;
                            break;
                        case 2:
                            interactToLocation.y += 1;
                            break;
                        case 3:
                            interactToLocation.x -= 1;
                            break;
                    }
                    world.getMap(player.getMapName()).getSegment(interactToLocation.x, interactToLocation.y).onInteractTo(player.getDirection());
                    for (RPGMapEntity e : world.getMap(player.getMapName()).entities) {
                        if (e.getLocation().x == interactToLocation.x && e.getLocation().y == interactToLocation.y) {
                            e.onInteractTo(player.getDirection());
                        }
                    }
                } catch (Exception ex) {
                }
            }
        } else {
            //send keys to menue
            RPGMenue.buttonPress(KeyEvent.VK_ENTER);
        }
    }

//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Text-showing logic">
    //
    //
    //
    //             S H O W   T H E   T E X T B O X
    //
    static boolean showSomeText = false;
    static ArrayList<String> textShown = new ArrayList<>();
    private static final int TEXTBOX_FONT_SIZE = 60;
    private static final Font TEXTBOX_FONT = new Font(Font.MONOSPACED, Font.BOLD, TEXTBOX_FONT_SIZE);
    static Color textColor = Color.BLACK;

    //animation
    private static final int SYMBOL_ANIMATION_MS = 20; //30
    private static boolean animateFirstLine = false;
    private static boolean animateLastLine = false;
    private static long startAnimationTick = 0;

    //seelectMenue
    private static boolean showSelectMenue = false;
    private static final ArrayList<String> selectMenueItems = new ArrayList<>();
    private static int selectedMenueItemID = 0;

    //TextEnterMenue
    private static String textEnterBGString = "Please enter sonme text.";
    private static boolean showTextEnterMenue = false;
    private static String textEnterMenueContent = "";
    private static long startTextEnterMenueCursorBlinkingTick = 0;
    private static int textEnterMaxLength = 10;
    private static int textEnterMinLength = 0;
    private static final Font TEXT_ENTER_FONT = TEXTBOX_FONT.deriveFont(TEXTBOX_FONT.getSize() / 2f);

    static void showText(String text, Color c) {
        Graphics2D g = (new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)).createGraphics();
        g.setFont(TEXTBOX_FONT);
        stopMoving();
        textColor = c;
        int maxTextWidth = Toolkit.getDefaultToolkit().getScreenSize().width - 20 - 20 - 35;
        do {
            String lineNow = text;
            while (StringMetrics.getBounds(g, lineNow).width > maxTextWidth) {
                lineNow = removeLastWord(lineNow);
            }
            textShown.add(lineNow);
            //System.out.println("added as line: " + lineNow);
            if (text.length() == lineNow.length()) { //reached end of text
                text = "";
            } else {
                text = text.substring(lineNow.length() + 1); //+1 for the next space symbol.
            }
        } while (text.length() > 0);

        showSomeText = true;
        animateFirstLine = true;
        startAnimationTick = RPG.getActivityTicks();
    }

    static int showSelectMenue(ArrayList<String> choices, String text, Color c) {
        for (String s : choices) {
            selectMenueItems.add(s);
        }
        showSelectMenue = true;
        showText(text, c);

        //wait for user to select and return id (clear everything)
        while (showSelectMenue) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
            }
        }
        int erg = selectedMenueItemID;
        selectedMenueItemID = 0;
        selectMenueItems.clear();
        textShown.clear();
        System.out.println("selectedMenueItemID = " + selectedMenueItemID);
        return erg;
    }

    /**
     * asks user to input some String
     *
     * @param inputBGInfo the gray background of the textbox
     * @param maxLength the maximum length of string the user can enter
     * @param minLength the minimum response length
     * @return
     */
    public static String showStringInputMenue(String inputBGInfo, int maxLength, int minLength) {
        textEnterBGString = inputBGInfo;
        textEnterMaxLength = maxLength;
        textEnterMinLength = minLength;
        textEnterMenueContent = "";
        startTextEnterMenueCursorBlinkingTick = RPG.getActivityTicks();
        showTextEnterMenue = true;

        while (showTextEnterMenue) { //wait for input
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
            }
        }
        return textEnterMenueContent;
    }

    private void drawTextBox(BufferedImage imgOrig, Graphics2D gOrig) {
        Graphics2D g;
        if (showSomeText) {
            //<editor-fold defaultstate="collapsed" desc="Draw the textbox">
//get the lines as string
            String line1 = textShown.get(0);
            String line2 = "";
            if (textShown.size() > 1) {
                line2 = textShown.get(1);
            }

            //get the width of the textbox(easy)
            int imgWidth = imgOrig.getWidth() - 20;

            int imgHeight = TEXTBOX_FONT_SIZE * 2 + (2 * 10);//2*10 for upper/lower text bouds border

            BufferedImage img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
            g = img.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, imgWidth, imgHeight);
            g.setColor(textColor);
            g.setFont(TEXTBOX_FONT);

            //draw the text
            String line1Shortened;
            if (animateFirstLine) {
                int endPos = (int) ((float) (RPG.getActivityTicks() - startAnimationTick) / (float) SYMBOL_ANIMATION_MS);
                if (endPos >= line1.length()) {
                    //finished animating first line, now the second one
                    line1Shortened = line1;
                    animateFirstLine = false;
                    animateLastLine = true;
                    startAnimationTick = RPG.getActivityTicks();
                } else {
                    line1Shortened = line1.substring(0, endPos);
                }
            } else {
                line1Shortened = line1;

                //only second line if !animateFirstLine
                String line2Shortened;
                if (animateLastLine) {

                    int endPos = (int) ((float) (RPG.getActivityTicks() - startAnimationTick) / (float) SYMBOL_ANIMATION_MS);
                    if (endPos >= line2.length()) {
                        //finished animating
                        line2Shortened = line2;
                        animateLastLine = false;
                    } else {
                        line2Shortened = line2.substring(0, endPos);
                    }
                } else {
                    line2Shortened = line2;
                }
                g.drawString(line2Shortened, 10, imgHeight - 20);

            }
            g.drawString(line1Shortened, 10, (int) Math.ceil(imgHeight / 2f) - 10);

            //draw arrow if needed
            if (textShown.size() > 2) {
                g.setColor(new Color(textColor.getRed(), textColor.getGreen(), textColor.getBlue(), 200)); //a bit lighter
                Polygon p = new Polygon();
                int arrowShiftUp = 0;
                if ((int) (RPG.getActivityTicks()) % 1000 > 500) {
                    arrowShiftUp = 10;
                }
                p.addPoint(imgWidth - 35, imgHeight - 35 - arrowShiftUp);
                p.addPoint(imgWidth - 15, imgHeight - 35 - arrowShiftUp);
                p.addPoint(imgWidth - 25, imgHeight - 15 - arrowShiftUp);
                g.fillPolygon(p);
            }
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, imgWidth, 5);
            g.fillRect(0, 0, 5, imgHeight);
            g.fillRect(0, imgHeight - 5, imgWidth, 5);
            g.fillRect(imgWidth - 5, 0, 5, imgHeight);

            //draw the box on image
            int textBoxDrawHeight = imgOrig.getHeight() - imgHeight - 10;
//</editor-fold>
            gOrig.drawImage(img, 10, textBoxDrawHeight, null);

            //
            //
            //
            //now draw select-menue if needed
            if (showSelectMenue) {
                //<editor-fold defaultstate="collapsed" desc="Code">
//find out minimum bounds
                gOrig.setFont(TEXTBOX_FONT);
                Point bounds = new Point(0, 0);
                for (String item : selectMenueItems) {
                    Dimension itemStringBounds = StringMetrics.getBounds(gOrig, item);
                    bounds.y += itemStringBounds.height + 15; // 2*15 upper+lower inner bounds
                    if (itemStringBounds.width > bounds.x) {
                        bounds.x = itemStringBounds.width;
                    }
                }
                bounds.x += 2 * 15 + 2 * 15; //5 border + 10 margin (outter)
                bounds.y += 15; //for outter AND inner bounds

                //create extra bufferedImage
                BufferedImage choiceImg = new BufferedImage(bounds.x, bounds.y, BufferedImage.TYPE_INT_RGB);
                g = choiceImg.createGraphics();
                g.setFont(TEXTBOX_FONT);
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, bounds.x, bounds.y);
                g.setColor(Color.BLACK);
                drawInnerBounds(g, new Point(0, 0), bounds, 5);

                //draw Strings on it
                int drawHeight = 15;
                for (int i = 0; i < selectMenueItems.size(); i++) {
                    g.setColor(Color.BLACK);
                    Dimension lineBounds = StringMetrics.getBounds(g, selectMenueItems.get(i));
                    drawInnerBounds(g, new Point(15, drawHeight), new Point(bounds.x - 30, lineBounds.height), 5);

                    drawHeight += lineBounds.height;
                    if (selectedMenueItemID == i) {
                        g.fillRect(15, drawHeight - lineBounds.height, bounds.x - 30, lineBounds.height);
                        g.setColor(Color.WHITE);
                    }
                    g.drawString(selectMenueItems.get(i), (bounds.x - lineBounds.width) / 2, drawHeight - 10);
                    drawHeight += 15;
                }
                //</editor-fold>
                //draw it on screen
                gOrig.drawImage(choiceImg, 10, textBoxDrawHeight - 10 - choiceImg.getHeight(), null);
            }
        }

        //
        //
        //
        //
        //now draw the TextEnterMeune (String input) if needed
        if (showTextEnterMenue) {
            //find out minimum bounds
            //<editor-fold defaultstate="collapsed" desc="code">
            gOrig.setFont(TEXT_ENTER_FONT);
            Point bounds = new Point(0, 0);
            Dimension bgStringBounds = StringMetrics.getBounds(gOrig, textEnterBGString);
            Dimension inputStringBounds = StringMetrics.getBounds(gOrig, textEnterMenueContent);
            bounds.x = bgStringBounds.width + 2 * 15; // upper/lower bounds
            bounds.y = bgStringBounds.height + 2 * 15;
            if (inputStringBounds.width > bgStringBounds.width) { //if inputtted text is longer
                bounds.x = inputStringBounds.width + 2 * 15;
            }
            BufferedImage textEnterImg = new BufferedImage(bounds.x, bounds.y, BufferedImage.TYPE_INT_RGB);
            g = textEnterImg.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, bounds.x, bounds.y);
            g.setColor(Color.BLACK);
            drawInnerBounds(g, new Point(0, 0), bounds, 5);
            g.setFont(TEXT_ENTER_FONT);
            if (textEnterMenueContent.equals("")) {
                //draw the bg-info text
                g.setColor(Color.GRAY);
                g.drawString(textEnterBGString, 15, bounds.y - 25);

            } else {
                g.setColor(Color.BLACK);
                g.drawString(textEnterMenueContent, 15, bounds.y - 25);

            }
            //now blibki
            if (((int) (RPG.getActivityTicks() - startTextEnterMenueCursorBlinkingTick)) % 500 > 200) {
                //draw the cursor
                int cursorX = 15 + inputStringBounds.width;
                g.setColor(Color.BLACK);
                g.drawLine(cursorX, 15, cursorX, bounds.y - 15);
                g.drawLine(cursorX + 1, 15, cursorX + 1, bounds.y - 15);
            }

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int textEnterImgX = (screenSize.width - bounds.x) / 2;
            int textEnterImgY = (screenSize.height - bounds.y) / 2;
//</editor-fold>
            gOrig.drawImage(textEnterImg, textEnterImgX, textEnterImgY, null);
        }
    }
    //</editor-fold>
}
