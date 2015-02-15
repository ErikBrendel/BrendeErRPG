package cbg.BrendeEr.RPG;

import cbg.BrendeEr.GameLib.FileHandler;
import cbg.BrendeEr.GameLib.Game;
import cbg.BrendeEr.GameLib.ListMenueActivity;

/**
 *
 * @author Erik Brendel
 */
public class MenueActivity extends ListMenueActivity {

    private static final int ID_NEW = 0;
    private static final int ID_LOAD = 1;
    private static final int ID_SETTINGS = 2;

    public MenueActivity() {
        setTitle("Loading...");
        addButton("...", ID_LOAD);
        addButton("...", ID_NEW);
        addButton("...", ID_SETTINGS);
        addButton("...", IDENTIFIER_BACK);
    }

    @Override
    public void onLaunch() {
        setButtonName(ID_LOAD, RPG.getString("Button_Load"));
        setButtonName(ID_NEW, RPG.getString("Button_New"));
        setButtonName(ID_SETTINGS, RPG.getString("Button_Settings"));
        setButtonName(IDENTIFIER_BACK, RPG.getString("Button_Exit"));
        setTitle(RPG.getString("Title_Menue"));
    }

    @Override
    public String getName() {
        return "Menue";
    }

    @Override
    public void buttonKlicked(int actionIdentifier) {
        switch (actionIdentifier) {
            case ID_LOAD:

                //check here is there is a slot
                boolean saveFree = false;
                if (FileHandler.getFileContent(GlobalVariables.getSavesPath() + "playerDirection", "[Check for existing save]").equals("")) {
                    saveFree = true;
                }
                if (!saveFree) {
                    GlobalVariables.load();
                    ((RPG) Game.singleton).loadAllMaps();
                    RPG.showActivity("Map");
                }
                break;
            case ID_NEW:
                ((RPG) Game.singleton).loadAllMaps();
                RPG.showActivity("Map");
                break;
            case ID_SETTINGS:
                RPG.showActivity("Settings");
                break;
            case IDENTIFIER_BACK:
                RPG.endGame();
                break;
        }
    }
}
