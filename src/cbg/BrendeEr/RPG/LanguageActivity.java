/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cbg.BrendeEr.RPG;

import cbg.BrendeEr.GameLib.ListMenueActivity;
import java.util.HashMap;

/**
 *
 * @author Erik Brendel
 */
public class LanguageActivity extends ListMenueActivity {

    public LanguageActivity() {
        setTitle("Loading...");
    }

    private static boolean finishedLoading = false;

    private static final HashMap<Integer, String> languageOnButton = new HashMap<>();
    @Override
    public void onLaunch() {
        if (!finishedLoading) {
            HashMap<String, HashMap<String, String>> languages = RPG.getLanguages();
            int i = 0;
            for (String key : languages.keySet()) {
                addButton("...", i);
                languageOnButton.put(i, key);
                i++;
            }
            addButton("...", IDENTIFIER_BACK);
            setLanguageDependentThings();
            finishedLoading = true;
        }
    }

    @Override
    public String getName() {
        return "Language";
    }

    @Override
    public void buttonKlicked(int actionIdentifier) {
        if (actionIdentifier == IDENTIFIER_BACK) {
            RPG.showActivity("Settings");
            return;
        }
        String languageCode = languageOnButton.get(actionIdentifier);
        RPG.setLanguage(languageCode);
        GlobalVariables.saveLanguage(languageCode);
        setLanguageDependentThings();
    }

    private void setLanguageDependentThings() {
        setTitle(RPG.getString("Title_Languages"));
        for(int i = 0; i < 1000; i++) {
            try {
                String l = languageOnButton.get(i);
                setButtonName(i, RPG.getString("Language_" + l));
            } catch (Exception e) {
                break;
            }
        }
        setButtonName(IDENTIFIER_BACK, RPG.getString("Button_Back"));
    }
}
