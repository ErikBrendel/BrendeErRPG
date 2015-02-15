package cbg.BrendeEr.RPG;

/**
 *
 * @author Erik Brendel
 */
public class RPGActionListener {

    /**
     * Launched, when the player enters this segment. First event launched.
     *
     * @param direction direction of the player
     */
    public void onPlayerEnter(int direction) {
        //Overwrite here!
    }

    /**
     * Launched, when the player leaves this segment. Second event launched.
     *
     * @param direction direction of the player
     */
    public void onPlayerLeave(int direction) {
        //Overwrite also here!
    }

    /**
     * Launched, when the player stands near this segment and interacts towards
     * it. 3rd event launched.
     *
     * @param direction direction of the player
     */
    public void onInteractTo(int direction) {

    }

    /**
     * Launched, when the player stands on that segment and interacts. 4th event
     * launched.
     *
     * @param direction direction of the player
     */
    public void onInteractFrom(int direction) {

    }
}
