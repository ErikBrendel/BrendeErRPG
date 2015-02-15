package cbg.BrendeEr.RPG;

import cbg.BrendeEr.GameLib.FileHandler;
import static cbg.BrendeEr.RPG.RPGUtils.deleteWholeFolder;
import java.awt.Point;
import java.io.File;
import java.util.HashMap;

/**
 *
 * @author Erik Brendel
 */
public class GlobalVariables {

    private static final HashMap<String, String> strings = new HashMap<>();
    private static final HashMap<String, Boolean> booleans = new HashMap<>();
    private static final HashMap<String, Integer> integers = new HashMap<>();

    public static String loadLanguage() {
        return FileHandler.getFileContent(getSavesPath() + "selectedLanguage");
    }

    public static void saveLanguage(String languageSelected) {
        FileHandler.writeFileContent(getSavesPath() + "selectedLanguage", languageSelected);
    }

    public static void load() {
        //load them from appdata

        try {

            File folder = new File(getSavesPath() + "strings\\");
            for (File f : folder.listFiles()) {
                strings.put(f.getName(), FileHandler.getFileContent(f.getPath()));
            }
            //printMap(strings, "Strings");
            folder = new File(getSavesPath() + "booleans\\");
            for (File f : folder.listFiles()) {
                booleans.put(f.getName(), Boolean.valueOf(FileHandler.getFileContent(f.getPath())));
            }
            //printMap(booleans, "Booleans");
            folder = new File(getSavesPath() + "integers\\");
            for (File f : folder.listFiles()) {
                integers.put(f.getName(), Integer.valueOf(FileHandler.getFileContent(f.getPath())));
            }
            //printMap(integers, "Integers");
            RPGQuestOverlay.showQuest(FileHandler.getFileContent(getSavesPath() + "questName"));
            RPGPlayer player = MapActivity.getPlayer();
            player.setMap(FileHandler.getFileContent(getSavesPath() + "playerMap"));
            int playerX = Integer.valueOf(FileHandler.getFileContent(getSavesPath() + "playerX"));
            int playerY = Integer.valueOf(FileHandler.getFileContent(getSavesPath() + "playerY"));
            player.setLocation(new Point(playerX, playerY));
            player.setDirection(Integer.valueOf(FileHandler.getFileContent(getSavesPath() + "playerDirection")));

        } catch (Exception ex) {
            System.err.println("[Loading]ERROR: " + ex.getMessage());
        }

    }

    public static void save() {
        //delete first save first
        //(new File(getSavesPath())).
        deleteWholeFolder(new File(getSavesPath()), new String[] {"selectedLanguage"});

        //save'em to appdata
        (new File(getSavesPath() + "strings\\")).mkdirs();
        (new File(getSavesPath() + "booleans\\")).mkdirs();
        (new File(getSavesPath() + "integers\\")).mkdirs();

        for (String key : strings.keySet()) {
            FileHandler.writeFileContent(getSavesPath() + "strings\\" + key, strings.get(key));
        }
        for (String key : booleans.keySet()) {
            FileHandler.writeFileContent(getSavesPath() + "booleans\\" + key, booleans.get(key) + "");
        }
        for (String key : integers.keySet()) {
            FileHandler.writeFileContent(getSavesPath() + "integers\\" + key, integers.get(key) + "");
        }
        FileHandler.writeFileContent(getSavesPath() + "questName", RPGQuestOverlay.getQuestName());
        FileHandler.writeFileContent(getSavesPath() + "playerX", MapActivity.getPlayer().getLocation().x + "");
        FileHandler.writeFileContent(getSavesPath() + "playerY", MapActivity.getPlayer().getLocation().y + "");
        FileHandler.writeFileContent(getSavesPath() + "playerMap", MapActivity.getPlayer().getMapName());
        FileHandler.writeFileContent(getSavesPath() + "playerDirection", MapActivity.getPlayer().getDirection() + "");
    }

    public static String getSavesPath() {
        return FileHandler.appDataPath("BrendeErRPG_" + RPG.getName() + "\\save\\");
    }

    public static void put(String name, String value) {
        //put this variable into the hashlist
        strings.put(name.toLowerCase(), value);
    }

    public static void put(String name, int value) {
        //put this variable into the hashlist
        integers.put(name.toLowerCase(), value);
    }

    public static void put(String name, boolean value) {
        //put this variable into the hashlist
        booleans.put(name.toLowerCase(), value);
    }

    public static String getString(String name) {
        //read from the hashlist
        name = name.toLowerCase();
        for (String key : strings.keySet()) {
            if (key.equals(name)) {
                return strings.get(name);
            }
        }
        return "";
    }

    public static boolean getBoolean(String name) {
        //read from the hashlist
        name = name.toLowerCase();
        for (String key : booleans.keySet()) {
            if (key.equals(name)) {
                return booleans.get(name);
            }
        }
        return false;
    }

    public static int getInteger(String name) {
        //read from the hashlist
        name = name.toLowerCase();
        for (String key : integers.keySet()) {
            if (key.equals(name)) {
                return integers.get(name);
            }
        }
        return 0;
    }
}
