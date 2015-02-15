package cbg.BrendeEr.RPG;

import cbg.BrendeEr.GameLib.ImageLoader;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 *
 * @author Erik Brendel
 */
public class RPGPlayer {

    public RPGPlayer() {
        //make the default skin:
        setTextures(generateDefaultTexture());

    }

    private static HashMap<String, RPGBufferedGif> generateDefaultTexture() {
        HashMap<String, RPGBufferedGif> defaultTexture = new HashMap<>();
        Point dimension = new Point(256, 256);
        for (int i = 0; i < 4; i++) {
            BufferedImage playerImage = new BufferedImage(dimension.x, dimension.y, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = playerImage.createGraphics();

            g.setColor(Color.RED);
            g.fillOval(0, 0, dimension.x, dimension.y);

            g.setColor(Color.BLACK);
            int p1X = 0;
            int p1Y = 0;
            switch (i) {
                case 0:
                    p1X = dimension.x / 2;
                    p1Y = 0;
                    break;
                case 1:
                    p1X = dimension.x;
                    p1Y = dimension.y / 2;
                    break;
                case 2:
                    p1X = dimension.x / 2;
                    p1Y = dimension.y;
                    break;
                case 3:
                    p1X = 0;
                    p1Y = dimension.y / 2;
                    break;
            }
            g.drawLine(dimension.x / 2, dimension.y / 2, p1X, p1Y);/* */

            defaultTexture.put(i + "", new RPGBufferedGif(playerImage, 100));
        }
        return defaultTexture;
    }

    private String mapName = "";

    public void setMap(String mapName) {
        this.mapName = mapName;
    }

    public String getMapName() {
        return mapName;
    }

    private Point mapLocation = new Point(0, 0);

    public void setLocation(Point newLocation) {
        mapLocation = newLocation;
    }

    public Point getLocation() {
        return mapLocation;
    }

    private int direction = 0; //0-->up 1-->right 2-->down 3--> left

    public void setDirection(int newDirection) {
        direction = newDirection;
    }

    public int getDirection() {
        return direction;
    }
    private boolean moving = false;

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean newMoving) {
        if(newMoving == true) { //start all moving animations
            for(int i = 0; i <= 3; i++) {
                textures.get("w" + i).restart(true);
            }
        }
        moving = newMoving;
    }

    private final HashMap<String, RPGBufferedGif> textures = new HashMap<>();

    /**
     * the names for the textures are 0, 1, 2, 3 (as Strings) the walking gifs
     * are w0, w1, w2, w3
     *
     * @param textures the hashMap
     */
    public void setTextures(HashMap<String, RPGBufferedGif> textures) {
        for (String key : textures.keySet()) {
            this.textures.put(key, textures.get(key));
        }
    }

    private RPGMapEntity playerEntity = null;

    public RPGMapEntity getEntity(Point dimension) {
        RPGBufferedGif playerGif;
        if (moving) {
            playerGif = textures.get("w" + String.valueOf(getDirection()));
        } else {
            playerGif = textures.get(String.valueOf(getDirection()));
        }
        if (playerEntity == null) {
            playerEntity = new RPGMapEntity(playerGif, true);
        } else {
            playerEntity.setImage(playerGif);
        }
        playerEntity.setLocation(mapLocation);
        return playerEntity;
    }
}
