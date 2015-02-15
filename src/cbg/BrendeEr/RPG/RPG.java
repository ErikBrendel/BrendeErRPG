package cbg.BrendeEr.RPG;

import cbg.BrendeEr.GameLib.*;
import java.awt.Color;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Erik Brendel
 *
 * just implement RPG and use startUp()
 *
 * Your Classpath-tree:
 *
 * /(root) | > src | > gui | > mainBG.png > intro | > (all intro Pictures -->
 * png)
 *
 */
public abstract class RPG extends Game {

    private static String name;

    public RPG(BufferedImage defaultMenueBG, String name) {
        Game.setTitle(name);
        this.name = name;
        launchGame(this);
        loadActivity(new IntroActivity());
        loadActivity(new MenueActivity());
        loadActivity(new MapActivity());
        loadActivity(new SettingsActivity());
        loadActivity(new LanguageActivity());
        setDefaultBG(defaultMenueBG);
        Game.window.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
                    new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0),
                    "null")); //removing mouse image
    }

    public static String getName() {
        return name;
    }

    /**
     * Launches the RPG and shows the intro, then the main menue
     *
     * @param bgSongUrl the path to thesong, which should be played
     */
    public void startUp(String bgSongUrl) {
        showActivity("Intro");
        showGame();
        updateSong(bgSongUrl);
    }

    /**
     * sets the time each intro-image is shown
     *
     * @param seconds the time in seconds
     */
    public void setIntroTime(float seconds) {
        IntroActivity.setIntroTime(seconds);
    }

    /**
     * adds an Image to the Intro-Images-list
     *
     * @param img the image added
     */
    public void addIntroImage(BufferedImage img) {
        IntroActivity.addImage(img);
    }

    /**
     * sets, how long a single picture fades in and out (default=100ms)
     *
     * @param fadeMS
     */
    public void setIntroFadeMS(int fadeMS) {
        IntroActivity.setFadeMS(fadeMS);
    }

    /**
     * sets the actual world for this game
     *
     * @param world the world, the game is playing in
     * @param mapSongs a collection of MapName --> ResourcePath to all the
     * songs, played by the maps
     */
    public void setWorld(RPGWorld world, HashMap<String, String> mapSongs) {
        RPG.mapSongs = mapSongs;
        MapActivity.setWorld(world);
    }
    private static HashMap<String, String> mapSongs = null;
    private static String backgroundMapActive = "";

    public static void updateSong() {
        updateSong("");
    }

    public static void updateSong(String newSong) {
        System.out.println("updateSong()");
        if (newSong.equals("")) {
            if (mapSongs == null) {
                return;
            }
            String newMap = MapActivity.getPlayer().getMapName();
            newSong = mapSongs.get(newMap);
        }
        if (newSong != null && !newSong.equals(backgroundMapActive) && !newSong.equals("")) {
            System.out.println("next song please :D");
            RPGAudio.playBackground(newSong);
            backgroundMapActive = newSong;
        }

    }

    /**
     * sets the player onject for the game
     *
     * @param player the Player-object
     */
    public void setPlayer(RPGPlayer player) {
        MapActivity.setPlayer(player);
    }

    /**
     * Uses Color.BLACK as default
     *
     * @param text the Text to be shown
     */
    public static void showText(String text) {
        showText(text, Color.BLACK);
    }

    /**
     * Show a text on the screen (like you are talking to somebody)
     *
     * @param text the Text to be shown
     * @param c the color of the text
     */
    public static void showText(String text, Color c) {
        MapActivity.showText(text, c);
    }

    public static int showChoiceMenue(ArrayList<String> choices, String text, Color c) {
        return MapActivity.showSelectMenue(choices, text, c);
    }

    public static String showTextEnterMenue(String inputBGInfo, int maxInputLength, int minResponseLength) {
        return MapActivity.showStringInputMenue(inputBGInfo, maxInputLength, minResponseLength);
    }

    public static void waitForTextClose() {
        while (MapActivity.showSomeText) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
            }
        }
    }

    public static void teleportPlayer(String mapName, Point newLocation, int newDirection) {
        teleportPlayer(mapName, newLocation, newDirection, true);
    }

    public static void teleportPlayer(String mapName, Point newLocation, int newDirection, boolean fade) {
        MapActivity.movingEnabled = false;
        MapActivity.fadeOut();
        MapActivity.getPlayer().setMap(mapName);
        MapActivity.getPlayer().setLocation(newLocation);
        MapActivity.getPlayer().setDirection(newDirection);
        MapActivity.oldPlayerCoords = null;
        MapActivity.oldDrawCoords = null;
        MapActivity.startMovingTick = 0;
        MapActivity.oldPlayerDirection = newDirection;
        updateSong();
        MapActivity.fadeIn();
        MapActivity.movingEnabled = true;
    }

    public static void movePlayer(int direction) {
        MapActivity.moveOneStep(direction);
    }

    public void loadAllMaps() {
        System.out.println("not overwritten!");
    }

//<editor-fold defaultstate="collapsed" desc="Inventory">
    public static void addToInventory(String itemName) {
        addToInventory(itemName, true);
    }
    public static void addToInventory(String itemName, boolean showMessage) {
        if(showMessage) {
            RPG.showText(String.format(RPG.getString("chest01"), RPG.getString(itemName + "_name")));
        }
        String oldInventory = GlobalVariables.getString("playerInventory");
        if (!oldInventory.equals("")) {
            oldInventory += "+";
        }
        oldInventory += itemName;
        GlobalVariables.put("playerInventory", oldInventory);
    }

    public static boolean inventoryContains(String itemName) {
        String inventory[] = GlobalVariables.getString("playerInventory").split("\\+");
        boolean erg = false;
        for (String item : inventory) {
            System.out.println("item = " + item);
            System.out.println("itemName = " + itemName);
            if (item.equals(itemName)) {
                erg = true;
            }
        }
        return erg;
    }

    public static void removeInventory(String itemName) {
        String inventory[] = GlobalVariables.getString("playerInventory").split("\\+");
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i].equals(itemName)) {
                inventory[i] = "REMOVED";
                break;
            }
        }
        String newInventory = "";
        for (String item : inventory) {
            if (!item.equals("REMOVED")) {
                if (!newInventory.equals("")) {
                    newInventory += "+";
                }
                newInventory += item;
            }
        }
        GlobalVariables.put("playerInventory", newInventory);
    }

//</editor-fold>
    public static void showLocationNotification(String msg) {
        RPGLocationNotification.showText(msg);
    }

    //<editor-fold defaultstate="collapsed" desc="Languages">
    private static final HashMap<String, HashMap<String, String>> languages = new HashMap<>();
    private static String languageSelected = "";

    public static void addLanguage(String name, HashMap<String, String> translation) {
        languages.put(name, translation);
        if (languageSelected.equals("")) {
            languageSelected = name;
        }
    }

    public static HashMap<String, HashMap<String, String>> getLanguages() {
        return languages;
    }

    public static void setLanguage(String name) {
        languageSelected = name;
    }

    public static void loadLanguage() {
        String languageFile = GlobalVariables.loadLanguage();
        if (!languageFile.equals("")) {
            setLanguage(languageFile);
        }
    }

    public static String getString(String id) {
        String erg;

        if (languages.isEmpty()) {
            return "[ERROR]Please register a language.";
        }
        HashMap<String, String> language = languages.get(languageSelected);
        String trans = language.get(id);
        if (trans == null) {
            return "[ERROR]NOT FOUND: unknown String.";
        }
        erg = trans;

        return erg;
    }
//</editor-fold>

    public static void wait(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
        }
    }
}
