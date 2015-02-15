package cbg.BrendeEr.RPG;

import cbg.BrendeEr.GameLib.Game;
import org.newdawn.easyogg.OggClip;

/**
 *
 * @author Erik Brendel
 */
public class RPGAudio {

    private static final boolean PLAY_AUDIO = true;

    private static OggClip background = null;

    public static void playBackground(final String name) {
        if (PLAY_AUDIO) {
            new Thread() {
                public void run() {
                    if (background != null) {
                        for (float i = 0.9f; i > 0.7f; i -= 0.05f) {
                            background.setGain(i);
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException ex) {
                            }
                        }/* */

                        background.stop();
                        background.close();
                    }
                    try {
                        //background = new OggClip(name);
                        background = new OggClip(Game.singleton.getClass().getResourceAsStream(name));
                        background.loop();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }.start();
        } else {
            System.err.println("AUDIO DISABLED! PLEASE ENABLE AUDIO BEFORE PUBLISHING!");
        }
    }

    public static void playSingleSound(final String name) {
        new Thread() {
            public void run() {
                try {
                    OggClip c = new OggClip(Game.singleton.getClass().getResourceAsStream(name));
                    c.play();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.start();
    }
}
