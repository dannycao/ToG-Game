package game;

/**
 *
 * @author Danny Cao
 */
public class SpritesManager
{

    private Sprite[][] sprites = null;

    public SpritesManager(int tilesWide, int tilesHigh)
    {
        this.sprites = new Sprite[tilesWide][tilesHigh];
    }

    public int getRows()
    {
        return sprites.length;
    }

    public int getCols()
    {
        return sprites[0].length;
    }

    /**
     *
     * @param sprite
     * @param x in world coordinates
     * @param y in world coordinates
     */
    public void setSprite(Sprite sprite, float x, float y)
    {
        int xIndex = (int) (x / GameplayState.TILE_SIZE);
        int yIndex = (int) (y / GameplayState.TILE_SIZE);

        sprites[xIndex][yIndex] = sprite;
    }

    /**
     *
     * @param x in world coordinates
     * @param y in world coordinates
     * @return
     */
    public Sprite getSprite(float x, float y)
    {
        int xIndex = (int) (x / GameplayState.TILE_SIZE);
        int yIndex = (int) (y / GameplayState.TILE_SIZE);

        return sprites[xIndex][yIndex];
    }

    /**
     * Returns the array of sprites.
     *
     * @return
     */
    public Sprite[][] getSprites()
    {
        return sprites;
    }
}
