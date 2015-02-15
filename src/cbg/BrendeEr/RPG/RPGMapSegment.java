package cbg.BrendeEr.RPG;

import cbg.BrendeEr.GameLib.ImageLoader;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author Erik Brendel
 */
public class RPGMapSegment {

    private final Color defaultBGColor;
    private Point dimensions = new Point(10, 10);
    private boolean walkable = true;
    private RPGBufferedGif layer0 = null;
    private RPGBufferedGif layer1 = null;
    private RPGBufferedGif layer2 = null;

    public RPGMapSegment(Color defaultBGColor, boolean walkable) {
        this.defaultBGColor = defaultBGColor;
        this.walkable = walkable;
    }

    public boolean isWalkable() {
        return walkable;
    }

    public void setDimensions(Point newDimensions) {
        if (dimensions != newDimensions) {
            dimensions = newDimensions;
            render();
            renderOverPlayer();
        }
    }

    public void setLayer0(RPGBufferedGif newLayer0) {
        layer0 = newLayer0;
    }

    public void setLayer1(RPGBufferedGif newLayer1) {
        layer1 = newLayer1;
    }

    public void setLayer2(RPGBufferedGif newLayer2) {
        layer2 = newLayer2;
    }

    //Render-section
    private BufferedImage render;
    private BufferedImage renderTop;

    public void render() {
        render = new BufferedImage(dimensions.x, dimensions.y, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = render.createGraphics();
        g.setColor(defaultBGColor);
        g.fillRect(0, 0, dimensions.x, dimensions.y);
        if (layer0 != null) {
            g.drawImage(layer0.getBufferedImage(dimensions), 0, 0, null);
        }
        if (layer1 != null) {
            g.drawImage(layer1.getBufferedImage(dimensions), 0, 0, null);
        }
    }
    
    public void renderOverPlayer() {
        renderTop = new BufferedImage(dimensions.x, dimensions.y, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = renderTop.createGraphics();
        if (layer2 != null) {
            g.drawImage(layer2.getBufferedImage(dimensions), 0, 0, null);
        }
    }

    public BufferedImage getImage() {
        return render;
    }
    public BufferedImage getTopImage() {
        return renderTop;
    }

    //<editor-fold defaultstate="collapsed" desc="launch the events in the actionlistener">
    //
    //
    //
    //                        F O L D     H E R E  !
    //
    private ArrayList<RPGActionListener> actionListeners = new ArrayList<>();

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
