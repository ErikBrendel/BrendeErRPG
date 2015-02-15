package cbg.BrendeEr.RPG;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 *
 * @author Erik Brendel
 */
public class RPGNPC extends RPGMapEntity {

    public RPGNPC(RPGBufferedGif standard) {
        super(standard, false);
    }
    
    
    private final HashMap<String, RPGBufferedGif> textures = new HashMap<>();
    private String simpleMessage = "";
    private Color simpleMessageColor = Color.BLACK;
    public void setSimpleMessage(String msg, Color c) {
        simpleMessage = msg;
        simpleMessageColor = c;
    }
    public String getSimpleMessage() {
        return simpleMessage;
    }
    public Color getSimpleMessageColor() {
        return simpleMessageColor;
    }

    /**
     * the names for the texturesRaw are 0, 1, 2, 3 (as Strings)
     *
     * @param textures the hashMap
     */
    public void setTextures(HashMap<String, RPGBufferedGif> textures) {
        for(String key:textures.keySet()) {
            this.textures.put(key, textures.get(key));
        }
    }

    
    
    @Override
    public RPGMapEntity createNew() {
        RPGNPC create = RPGNPC.createNew(textures, 0, simpleMessage, simpleMessageColor);
        create.setImage(getImage());
        for (RPGActionListener listener: actionListeners) {
            create.addActionListener(listener);
        }
        return create;
    }
    
    public static RPGNPC createNew(HashMap<String, RPGBufferedGif> textures, int startDirection) {
        return createNew(textures, startDirection, "");
    }
    public static RPGNPC createNew(HashMap<String, RPGBufferedGif> textures, int startDirection, String simpleMessage) {
        return createNew(textures, startDirection, simpleMessage, Color.BLACK);
    }
    public static RPGNPC createNew(HashMap<String, RPGBufferedGif> textures, int startDirection, String simpleMessage, Color c) {
        final RPGNPC create = new RPGNPC(textures.get("" + startDirection));
        create.setTextures(textures);
        create.setSimpleMessage(simpleMessage, c);
        create.addActionListener(new RPGActionListener() {
            @Override
            public void onInteractTo(int direction) {
                
                int newDirection = direction + 2;
                if(newDirection >= 4) {
                    newDirection -= 4;
                }
                create.setDirection(newDirection);
                
                if(!create.getSimpleMessage().equals("")) {
                    RPG.showText(create.simpleMessage, create.simpleMessageColor);
                }
            }
        });
        return create;
    }
    public void setDirection(int newDir) {
        setImage(textures.get("" + newDir));
    }
}
