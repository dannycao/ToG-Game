package game;

import org.newdawn.slick.Graphics;

/**
 * Handles the translating of a game map to follow a Player as he moves around
 * in the game world.
 *
 * Code taken from the following author and blog:
 *
 * @author Sergi Rodríguez's Development Blog
 * http://shockper.com/blog/tutorials/slick2d-introduccion-al-movimiento-de-camara/
 */
public class Camera
{

    /**
     * Amount to translate the map.
     */
    private int transX, transY;
    /**
     * Dimension of the map.
     */
    private int mapWidth, mapHeight;

    /**
     * A Camera that follows a Player as he moves around the game world.
     *
     * @param mapWidth the width of the map.
     * @param mapHeight the height of the map.
     */
    public Camera(int mapWidth, int mapHeight)
    {
        transX = 0;
        transY = 0;

        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
    }

    /**
     * Moves the map around, creating the effect of the Player and Camera moving
     * around the game world.
     *
     * @param g the Graphics object used to "move" the map.
     * @param entity the Player on which to follow using the Camera.
     */
    public void translate(Graphics g, Player entity)
    {
        // Check player's position relative to the screen
        // and "move" camera to center on player as he moves
        if (entity.getX() - Game.APP_WIDTH / 2 + 16 < 0)
        {
            transX = 0;
        } else if (entity.getX() + Game.APP_WIDTH / 2 + 16 > mapWidth)
        {
            transX = -mapWidth + Game.APP_WIDTH;
        } else
        {
            transX = (int) -entity.getX() + Game.APP_WIDTH / 2 - 16;
        }

        if (entity.getY() - Game.APP_HEIGHT / 2 + 16 < 0)
        {
            transY = 0;
        } else if (entity.getY() + Game.APP_HEIGHT / 2 + 16 > mapHeight)
        {
            transY = -mapHeight + Game.APP_HEIGHT;
        } else
        {
            transY = (int) -entity.getY() + Game.APP_HEIGHT / 2 - 16;
        }

        // shift the rendering on objects on screen to simulate camera movement
        g.translate(transX, transY);
    }

    /**
     * Returns the x translation amount.
     *
     * @return the x translation amount.
     */
    public int getTranslateX()
    {
        return -transX;
    }

    /**
     * Returns the y translation amount.
     *
     * @return the y translation amount.
     */
    public int getTranslateY()
    {
        return -transY;
    }
}