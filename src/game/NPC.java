package game;

import org.newdawn.slick.Image;

/**
 * A Non-Playable-Character.
 *
 * @author Danny Cao
 */
public class NPC extends Sprite
{

    /**
     * An Action that the NPC can take.
     */
    private Action action = null;

    /**
     * Creates an NPC.
     *
     * @param image
     * @param x coordinate in the game world.
     * @param y coordinate in the game world.
     */
    public NPC(Image image, float x, float y)
    {
        super(image, x, y, image.getWidth(), image.getHeight());
    }

    /**
     * Sets the action that this NPC can take.
     *
     * @param action
     */
    public void setAction(Action action)
    {

        this.action = action;
    }

    /**
     * Calls this method to have the NPC perform his Action.
     *
     * @param x coordinate on which to perform the action.
     * @param y coordinate on which to perform the action.
     */
    @Override
    public void update(float x, float y)
    {
        // Only perform Action if this NPC can do it
        if (action != null)
        {
            action.perform(x, y);
        }
    }
}
