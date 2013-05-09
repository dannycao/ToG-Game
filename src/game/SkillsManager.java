package game;

import java.util.HashMap;
import java.util.Map;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.tiled.TiledMapPlus;

/**
 *
 * @author Danny Cao
 */
public class SkillsManager
{

    /**
     * x index of the tiles that are used when doing a skill.
     */
    public static final int SKILL_TILE_X = 0;
    /**
     * Index of tiles on the map to use when using a skill.
     */
    public static final int GRASS_TILE = 0, DIRT_TILE = 1, WOOD_TILE = 2,
            FIRE_TILE = 4, TREE_TILE = 5;
    private TiledMapPlus map = null;
    private Map<SkillSet, Skill> skills = null;
    private CollisionManager cm = null;
    private Player player = null;

    public SkillsManager(TiledMapPlus map, CollisionManager cm)
    {
        skills = new HashMap();
        this.map = map;
        this.cm = cm;
    }

    public void initSkills()
    {

        // SpriteSheet with images for skills icons
        SpriteSheet sSheet = MainMenuState.resourceManager.getSpriteSheet("skills.png");

        // Layer of the map that the player can modify
        final int modLayer = map.getLayerIndex(Layers.MODIFIABLE.toString());

        // A skill that puts a wood tile at the spot on which the player clicked
        Skill build = new Skill(SkillSet.BUILD, sSheet.getSubImage(SkillSet.BUILD.ordinal(), 0));
        build.setAction(new Action()
        {
            @Override
            public void perform(float x, float y)
            {
                int xTile = (int) (x / map.getTileWidth());
                int yTile = (int) (y / map.getTileHeight());

                // only allow to build if player has a wood item
                if (player.hasItem(ItemSet.WOOD))
                {
                    // List of layers to check.
                    // Only allow a player to build if there are no trees, rocks,
                    // or buildings in the way
                    String[] layers =
                    {
                        Layers.TREE.toString(), Layers.ROCK.toString(),
                        Layers.BUILDING.toString()
                    };

                    // If tileBlocks == 0, then there are no tiles that will
                    // disallow the player to use the skill at that tile
                    if (skillableTile(layers, xTile, yTile))
                    {
                        // the id of the tile that represents a build tile
                        int id = map.getTileId(SKILL_TILE_X, WOOD_TILE, 0);
                        map.setTileId(xTile, yTile, modLayer, id);

                        // now the player can walk on this tile
                        cm.setBlocked(x, y, false);

                        // remove one wood item from player
                        player.removeItem(ItemSet.WOOD);
                    }
                }
            }
        });

        skills.put(SkillSet.BUILD, build);


        // A skill that allows a player to "dig" up a tile
        Skill dig = new Skill(SkillSet.DIG, sSheet.getSubImage(SkillSet.DIG.ordinal(), 0));
        dig.setAction(new Action()
        {
            @Override
            public void perform(float x, float y)
            {
                int xTile = (int) (x / map.getTileWidth());
                int yTile = (int) (y / map.getTileHeight());

                // Only allow to dig with no rocks, trees, buildings,
                // water in the way
                String[] layerNames =
                {
                    Layers.TREE.toString(), Layers.ROCK.toString(),
                    Layers.BUILDING.toString(), Layers.CLIFF.toString(),
                    Layers.WATER.toString()
                };

                if (skillableTile(layerNames, xTile, yTile))
                {
                    // id of the tile that is used in the dig skill
                    int id = map.getTileId(SKILL_TILE_X, DIRT_TILE, 0);
                    map.setTileId(xTile, yTile, modLayer, id);

                    // now the player can walk on this tile
                    cm.setBlocked(x, y, false);
                }
            }
        });

        skills.put(SkillSet.DIG, dig);


        // A skill that allows a player to chop trees and collect wood items
        Skill chop = new Skill(SkillSet.CHOP, sSheet.getSubImage(SkillSet.CHOP.ordinal(), 0));
        chop.setAction(new Action()
        {
            @Override
            public void perform(float x, float y)
            {
                int xTile = (int) (x / map.getTileWidth());
                int yTile = (int) (y / map.getTileHeight());

                // Only allow to chop trees
                String[] layerNames =
                {
                    Layers.TREE.toString()
                };

                if (!skillableTile(layerNames, xTile, yTile))
                {
                    // id of the tile that is used in the chop skill
                    int id = map.getTileId(SKILL_TILE_X, GRASS_TILE, 0);
                    map.setTileId(xTile, yTile, modLayer, id);

                    // now the player can walk on this tile
                    cm.setBlocked(x, y, false);

                    // Add a chopped piece of wood to player's list
                    Item wood = GameplayState.itemsManager.getItem(ItemSet.WOOD);
                    player.addItem(wood);
                }
            }
        });

        skills.put(SkillSet.CHOP, chop);
    }

    /**
     * Checks that the player can use a skill to modify a tile.
     *
     * @param name
     * @return
     */
    public boolean skillableTile(String[] layerNames, int xTile, int yTile)
    {
        int tileBlocks = 0;

        for (int i = 0; i < layerNames.length; ++i)
        {
            int layerId = map.getLayerID(layerNames[i]);

            // If layerId < 0, layer with that name doesn't exist
            if (layerId >= 0)
            {
                // layer exists, check if there is a tile on this layer
                // that player can't use skill on.
                tileBlocks += map.getTileId(xTile, yTile, layerId);
            }
        }

        // If tileBlocks > 0, then there are tiles that prevent the player
        // from using this skill on that tile
        return tileBlocks == 0;
    }

    public Skill getSkill(SkillSet name)
    {
        return skills.get(name);
    }

    void setPlayer(Player playerSprite)
    {
        player = playerSprite;
    }
}
