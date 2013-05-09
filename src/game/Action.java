package game;

/**
 * An interface for any object that can perform an Action.
 *
 * @author Danny Cao
 */
public interface Action
{

    /**
     * Perform some kind of action.
     *
     * @param x the x coordinate on which to perform the action.
     * @param y the y coordinate on which to perform the action.
     */
    public abstract void perform(float x, float y);
}
