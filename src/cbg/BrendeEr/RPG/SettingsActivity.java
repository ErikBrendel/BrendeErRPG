/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cbg.BrendeEr.RPG;

import cbg.BrendeEr.GameLib.ListMenueActivity;

/**
 *
 * @author Erik Brendel
 */
public class SettingsActivity extends ListMenueActivity{
    private static final int ID_LANGUAGE = 0;
    public SettingsActivity() {
        setTitle("Loading...");
        addButton("...", ID_LANGUAGE);
        addButton("...", IDENTIFIER_BACK);
    }
    @Override
    public void onLaunch() {
        setTitle(RPG.getString("Title_Settings"));
        setButtonName(ID_LANGUAGE, RPG.getString("Button_Language"));
        setButtonName(IDENTIFIER_BACK, RPG.getString("Button_Back"));
    }
    @Override
    public String getName() {
        return "Settings";
    }

    @Override
    public void buttonKlicked(int actionIdentifier) {
        switch (actionIdentifier) {
            case ID_LANGUAGE:
                RPG.showActivity("Language");
                break;
            case IDENTIFIER_BACK:
                RPG.showActivity("Menue");
                return;
        }
    }

    
}
