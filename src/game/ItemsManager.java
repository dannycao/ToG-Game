package game;

import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

/**
 * A Manager that initializes and stores a set of Items.
 *
 * This allows Items to be shared between levels and loaded/saved to file.
 *
 * @author Danny Cao
 */
public class ItemsManager
{
    // SpriteSheet with images for items icons

    private static SpriteSheet sSheet = null;

    public ItemsManager()
    {
        sSheet = MainMenuState.resourceManager.getSpriteSheet("items.png");
    }

    public Item getItem(ItemSet name)
    {
        Image itemImg = null;

        int index = name.ordinal();

        // Items starting with purple square are on row 2 of the items.png
        if (index < ItemSet.PUR_SQUARE.ordinal())
        {
            itemImg = sSheet.getSubImage(name.ordinal(), 0);
        } else
        {
            itemImg = sSheet.getSubImage(name.ordinal() % sSheet.getHorizontalCount(), 1);
        }

        itemImg = itemImg.copy();

        return new Item(name, itemImg);
    }
}
