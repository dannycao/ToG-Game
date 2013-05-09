package game;

import java.util.ArrayList;
import org.newdawn.slick.tiled.TiledMapPlus;

/**
 * Handles the detection of collision between game objects; specifically, the
 * Player and the Map.
 *
 * @author Danny Cao
 */
public class CollisionManager
{

    /**
     * Number of tiles across and down in a map.
     */
    private int tilesWide, tilesHigh;
    /**
     * Dimension of a tile in a map.
     */
    private int tileWidth, tileHeight;
    /**
     * Dimension of the game world.
     */
    private int worldWidth, worldHeight;
    /**
     * Array specifying "blocked" status of each tile.
     */
    private boolean[][] blocked;
    /**
     * Map of the game world.
     */
    private TiledMapPlus gameMap;

    /**
     * Creates a collision map from a map and a layer with collision tiles.
     *
     * @param gameMap the map of the game world.
     */
    public CollisionManager(TiledMapPlus gameMap)
    {

        this.gameMap = gameMap;

        this.tileWidth = gameMap.getTileWidth();
        this.tileHeight = gameMap.getTileHeight();

        tilesWide = gameMap.getWidth();
        tilesHigh = gameMap.getHeight();

        worldWidth = tileWidth * tilesWide;
        worldHeight = tileHeight * tilesHigh;

        // Find indexes of all layers that has the property "blocked" set to true
        int layerCount = gameMap.getLayerCount();
        ArrayList<Integer> collisionLayers = new ArrayList<Integer>();
        for (int l = 0; l < layerCount; ++l)
        {
            // All layers that have tiles that block player's movement has
            // a "blocked" property that is set to "true"
            String layerProp = gameMap.getLayerProperty(l, GameplayState.COLLISION_LAYER_PROP, "false");

            if (layerProp.equals("true"))
            {
                collisionLayers.add(l);
            }
        }

        blocked = new boolean[tilesWide][tilesHigh];

        // Initialize list of tiles that the player can't move over
        for (int y = 0; y < tilesHigh; ++y)
        {
            for (int x = 0; x < tilesWide; ++x)
            {
                // getTileId returns a 0 if there is no tile (i.e empty)
                // at index x, y on tile l
                // Cycle through all tiles with collision tiles. If the final
                // sum of tileID is != 0, then that tile is blocked.
                int tileID = 0;
                for (int l = 0; l < collisionLayers.size(); ++l)
                {
                    tileID += gameMap.getTileId(x, y, collisionLayers.get(l));
                }

                blocked[x][y] = tileID != GameplayState.EMPTY_TILE_GID;
            }
        }
    }

    /**
     * Returns true if x, y tile of the given map is 'blocked'.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public boolean isBlocked(float x, float y)
    {
        // Do not allow x, y to be outside of the game world
        // Game world is map - width of sidebar
        if (x < GameplayState.SIDEBAR_WIDTH + 10 || x > worldWidth
                || y < 0 || y > worldHeight)
        {
            return true;
        }

        // Gets the index into the layer of the x, y coordinate
        int xTile = (int) (x / tileWidth);
        int yTile = (int) (y / tileHeight);


        return blocked[xTile][yTile];
    }

    /**
     * Sets an x, y position to "blocked" status.
     *
     * @param x the x coordinate of the tile.
     * @param y the y coordinate of the tile.
     *
     * @param blocked
     */
    public void setBlocked(float x, float y, boolean blocked)
    {
        int xTile = (int) (x / tileWidth);
        int yTile = (int) (y / tileHeight);

        this.blocked[xTile][yTile] = blocked;
    }
}
