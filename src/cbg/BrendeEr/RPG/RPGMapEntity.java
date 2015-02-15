package cbg.BrendeEr.RPG;

import cbg.BrendeEr.GameLib.ImageLoader;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author Erik Brendel
 */
public class RPGMapEntity {
    public static final RPGBufferedGif USELESS = new RPGBufferedGif(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), 1000);

    private RPGBufferedGif img;
    private Point loc = new Point(0, 0);
    private boolean walkable;

    public RPGMapEntity(RPGBufferedGif standard, boolean walkable) {
        img = standard;
        this.walkable = walkable;
    }
    public void setImage(RPGBufferedGif newImage) {
        img = newImage;
    }
    public RPGBufferedGif getImage() {
        return img;
    }
    public void setWalkable(boolean newIsWalkable) {
        walkable = newIsWalkable;
    }
    public boolean isWalkable() {
        return walkable;
    }

    public BufferedImage getImage(Point dimension) {
        return img.getBufferedImage(dimension);
    }

    public Point getLocation() {
        return loc;
    }

    public void setLocation(Point newLocation) {
        loc = newLocation;
    }
    
    /**
     * please overwrite thie method to let the RPGMap.loadMap() method create new instances
     * by default it returns null --> runtime error
     * 
     * @return null, or a new instance
     */
    public RPGMapEntity createNew() {
        return null;
    }
    
    //<editor-fold defaultstate="collapsed" desc="ActionListeners">
    //
    //              F O L D   H E R E 
    //
    
    final ArrayList<RPGActionListener> actionListeners = new ArrayList<>();

    public void addActionListener(RPGActionListener listener) {
        actionListeners.add(listener);
    }

    public void onPlayerEnter(final int direction) {
        new Thread() {
            public void run() {
                for (RPGActionListener l : actionListeners) {
                    l.onPlayerEnter(direction);
                }
            }
        }.start();
    }

    public void onPlayerLeave(final int direction) {
        new Thread() {
            public void run() {
                for (RPGActionListener l : actionListeners) {
                    l.onPlayerLeave(direction);
                }
            }
        }.start();
    }

    public void onInteractTo(final int direction) {
        new Thread() {
            public void run() {
                for (RPGActionListener l : actionListeners) {
                    l.onInteractTo(direction);
                }
            }
        }.start();
    }

    public void onInteractFrom(final int direction) {
        new Thread() {
            public void run() {
                for (RPGActionListener l : actionListeners) {
                    l.onInteractFrom(direction);
                }
            }
        }.start();
    }
//</editor-fold>
}
