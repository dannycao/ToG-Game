package game;

import org.newdawn.slick.Image;

/**
 * Items for use in game.
 *
 * @author Danny Cao
 */
public class Item extends NPC
{

    /**
     * Name of the item.
     */
    private ItemSet itemName = null;

    /**
     * Creates an item.
     *
     * @param itemName
     */
    public Item(ItemSet itemName)
    {
        this(itemName, null, 0, 0);
    }

    /**
     * Creates an item with an image associated with it.
     *
     * @param itemName
     * @param image
     */
    public Item(ItemSet itemName, Image image)
    {
        this(itemName, image, 0, 0);
    }

    /**
     * Creates an item.
     *
     * @param itemName
     * @param image the image to associate with an Item.
     * @param x coordinate to place the item image on screen.
     * @param y coordinate to place the item image on screen.
     */
    public Item(ItemSet itemName, Image image, float x, float y)
    {
        super(image, x, y);

        this.itemName = itemName;
    }

    public ItemSet getName()
    {
        return itemName;
    }
}
