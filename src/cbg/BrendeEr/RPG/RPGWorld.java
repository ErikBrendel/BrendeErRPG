package cbg.BrendeEr.RPG;

import java.util.ArrayList;

/**
 *
 * @author Erik Brendel
 */
public class RPGWorld {

    private ArrayList<RPGMap> maps;

    public RPGWorld() {
        maps = new ArrayList<>();
    }

    public void addMap(RPGMap m) {
        maps.add(m);
    }
    
    public RPGMap getMap(String name) {
        name = name.toLowerCase();
        for(int i = 0; i < maps.size(); i++) {
            if(maps.get(i).getName().toLowerCase().equals(name)) {
                return maps.get(i);
            }
        }
        
        System.out.println("NO MAP FOUND WITH NAME \"" + name + "\".");
        return null;
    }
}
