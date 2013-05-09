package game;

import java.util.ArrayList;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMapPlus;

/**
 * Create the BasicGameState on which all game levels will extend to implement
 * level-specific details.
 *
 * @author Danny Cao
 */
public abstract class GameplayState extends BasicGameState
{

    /**
     * Set to true when quest complete.
     */
    protected static boolean questComplete = false;
    /**
     * Global ID value of a space with no tile in a TiledMap.
     */
    public static final int EMPTY_TILE_GID = 0;
    /**
     * GroupID of GroupObject Layer with the player's starting position.
     */
    public static final int PLAYER_GROUPOBJECT_ID = 0;
    /**
     * ObjectID of Object with the player's starting position.
     */
    public static final int PLAYER_OBJECT_ID = 0;
    /**
     * Layer with collidable objects/tiles.
     */
    public static final String COLLISION_LAYER_PROP = "blocked";
    /**
     * Tile size, assuming tiles are square.
     */
    public static final int TILE_SIZE = 40;
    /**
     * Character tile width.
     */
    public static final int PLAYER_W = 32;
    /**
     * Character tile height.
     */
    public static final int PLAYER_H = 48;
    /**
     * Width of NPC sprites.
     */
    public static final int SPRITE_WIDTH = 40;
    /**
     * Height of NPC sprites.
     */
    public static final int SPRITE_HEIGHT = 40;
    /**
     * Changes how fast characters move.
     */
    public static final float MOVE_SPEED = 0.15f;
    /**
     * Duration of between frames of an animation. Lower number = faster
     * animation.
     */
    public static final int DURATION = 150;
    /**
     * Sidebar variables.
     */
    public static final int SIDEBAR_WIDTH = 150,
            BORDER = 10,
            PAD = 5,
            STATS_Y = Game.APP_HEIGHT / 8,
            ITEMS_Y = Game.APP_HEIGHT / 4,
            ITEMS_BOX_W = SIDEBAR_WIDTH - BORDER * 2,
            BOX_H = 200,
            SKILLS_Y = ITEMS_Y + BOX_H + BORDER * 5,
            MENU_BTN_Y = SKILLS_Y + BOX_H + BORDER * 5;
    /**
     * Tilemap of the current level.
     */
    protected static TiledMapPlus map = null;
    /**
     * A CollisionManager for dealing with collisions.
     */
    protected static CollisionManager collisionManager = null;
    /**
     * Manager for sprites.
     */
    protected static SpritesManager spritesManager = null;
    /**
     * Current level of the game map.
     */
    protected int level = 0;
    /**
     * Player sprite.
     */
    protected Player player = null;
    /**
     * Camera to track the player as he moves.
     */
    protected static Camera camera = null;
    /**
     * An avatar of the player.
     */
    protected static Image avatar = null;
    /**
     * A manager for Skills.
     */
    public static SkillsManager skillsManager = null;
    /**
     * A manager for Items.
     */
    public static ItemsManager itemsManager = null;
    /**
     * Reference to a music file.
     */
    private String musicFilename;
    /**
     * Message to be displayed on screen.
     */
    protected static String message = null;

    /**
     * Create this state.
     *
     * @param level the map level to load.
     * @param player the player.
     * @param musicFilename filename of a music file.
     */
    public GameplayState(int level, Player player, String musicFilename)
    {
        this.level = level;
        this.player = player;
        this.musicFilename = musicFilename;
    }

    /**
     * Returns the id of this level.
     *
     * @return the id of this level.
     */
    @Override
    public int getID()
    {
        return level;
    }

    /**
     * Must call super.int(..) from any method that overrides this. Then call
     * initSprites to initialize any level-specific sprites.
     *
     * @param gc
     * @param sbg
     * @throws SlickException
     */
    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException
    {
        // Load the devil sprite sheet
        SpriteSheet playerSpriteSheet = MainMenuState.resourceManager.getSpriteSheet("devil.png");

        avatar = playerSpriteSheet.getSubImage(0, 0).getScaledCopy(1.5f);

        // Load the specified game level
        String gameLevel = "level_" + level + ".tmx";
        map = new TiledMapPlus(Game.DATA_DIR + gameLevel);

        collisionManager = new CollisionManager(map);

        skillsManager = new SkillsManager(map, collisionManager);
        skillsManager.initSkills();

        itemsManager = new ItemsManager();

        spritesManager = new SpritesManager(map.getWidth(), map.getHeight());

        // Gets the starting x, y coordinate of the player sprite from the Map file
        // getObject() method does not work properly because Object name not set properly somewhere
        // int spriteX = map.getObjectGroup("sprites").getObject("player").x;
        int spriteX = map.getObjectX(PLAYER_GROUPOBJECT_ID, PLAYER_OBJECT_ID);
        int spriteY = map.getObjectY(PLAYER_GROUPOBJECT_ID, PLAYER_OBJECT_ID);

        // Only create a new player if there isn't already a player (from a previous level)
        // This way, player keeps all his Skills & Items
        if (player == null)
        {
            player = new Player(playerSpriteSheet, DURATION, MOVE_SPEED, spriteX, spriteY, collisionManager);

            // If player is null, one of two things happened.
            // either, this is the 0'th level or user loaded this level from file
            // If user loaded level from file, need to re-initialize player
            if (level > 0)
            {
                MainMenuState.gameManager.loadPlayer(player);
            }

        } else
        {
            // Player already exists, just need to update position and collision manager
            player.setX(spriteX);
            player.setY(spriteY);

            player.setCollisionManager(collisionManager);

            // Re-initialzes Player's skills to use the new map & collisionManager
            ArrayList<Skill> oldSkills = player.getAllSkills();
            ArrayList<Skill> newSkills = new ArrayList<Skill>(oldSkills.size());
            for (int i = 0; i < oldSkills.size(); ++i)
            {
                Skill s = skillsManager.getSkill(oldSkills.get(i).getName());
                newSkills.add(s);
            }

            player.setAllSkills(newSkills);
            player.setActiveSkill(null);

            //////////// TODO: do I need to re-initialize for Items also?
            player.setActiveItem(null);
        }


        // Set player's level to match game level.
        player.setLevel(level);

        skillsManager.setPlayer(player);

        int mapWidth = map.getWidth() * map.getTileWidth();
        int mapHeight = map.getHeight() * map.getTileHeight();
        camera = new Camera(mapWidth, mapHeight);

        initSprites(gc, sbg);
    }

    public Player getPlayer()
    {
        return player;
    }

    /**
     * This method is called when entering this state.
     *
     * @param gc
     * @param sbg
     * @throws SlickException
     */
    @Override
    public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException
    {
        Game.music = new Music(Game.DATA_DIR + musicFilename);
        Game.music.loop();
    }

    /**
     * Initializes any sprites for this level.
     *
     * @param gc
     * @param sbg
     * @throws SlickException
     */
    public abstract void initSprites(GameContainer gc, StateBasedGame sbg) throws SlickException;

    /**
     * Levels must override this method to render level-specific objects.
     *
     * First, must call super.render(...), then GameplayState.cam.translate(...)
     * to set the camera.
     *
     * @param gc
     * @param sbg
     * @param g
     * @throws SlickException
     */
    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException
    {
        // Keeps the camera focused on the player as he moves.
        camera.translate(g, player);
        map.render(0, 0);

        // Render each sprite
        // For better efficiciency, instead of going through the whole 2d array
        // of sprites, can just check the sprites that are within the current
        // camera view
        Sprite[][] s = spritesManager.getSprites();
        for (int i = 0; i < s.length; ++i)
        {
            for (int j = 0; j < s[0].length; ++j)
            {
                Sprite sp = s[i][j];
                if (sp != null)
                {
                    sp.draw();
                }
            }
        }

        player.draw();


        ////////////// sidebar //////////
        // Render the sidebar using absolute screen coordinates
        g.resetTransform();

        Font font = g.getFont();
        int fontH = font.getHeight("Font");

        // Creates a sidebar background
        g.setColor(Color.black);
        g.fillRect(0, 0, SIDEBAR_WIDTH, Game.APP_HEIGHT);

        // Color of all sidebar Strings
        g.setColor(Color.magenta);

        // avatar
        g.fillRect(BORDER, BORDER, avatar.getWidth(), avatar.getHeight());
        g.drawImage(avatar, BORDER, BORDER);

        // level
        g.drawString("Level", BORDER + avatar.getWidth() + PAD, BORDER);
        g.drawString("" + player.getLevel(), BORDER + avatar.getWidth() + PAD, BORDER + fontH + PAD);

        // Stats
        g.drawString("Stats", BORDER, STATS_Y);
        g.drawString("HP: " + player.getHP(), BORDER, STATS_Y + fontH + PAD);
        g.drawString("STR: " + player.getSTR(), BORDER, STATS_Y + fontH * 2 + PAD);
        g.drawString("$: " + player.getMoney(), BORDER, STATS_Y + fontH * 3 + PAD);

        // items
        g.drawString("Items", BORDER, ITEMS_Y);

        // Get all of the player's items
        // and draw an icon for each one
        int itemIconX = BORDER;
        int itemIconY = ITEMS_Y + fontH + PAD;

        // player's current active item, could be null
        Item curItem = player.getActiveItem();
        ItemSet activeItem = null;
        if (curItem != null)
        {
            // If the player's current item is not null,
            // get the item's name
            activeItem = curItem.getName();
        }

        ArrayList<Item> items = player.getAllItems();
        for (int i = 0; i < items.size(); ++i)
        {
            Item item = items.get(i);
            Image itemIcon = item.getImage();

            // Set the x, y position at which the skillIcon is draw on screen
            // This lets me check if the user clicked on an icon in the update()
            // method.
            item.setX(itemIconX);
            item.setY(itemIconY);

            // if it's the currently active item
            // draw it with a magenta overlayS
            if (item.getName() == activeItem)
            {
                g.drawImage(itemIcon, itemIconX, itemIconY, Color.magenta);
            } else
            {
                g.drawImage(itemIcon, itemIconX, itemIconY);
            }

            itemIconX += itemIcon.getWidth() + PAD;

            // draw on the next row if itemIconX + itemIconWidth > sidebarwidth
            if (itemIconX >= SIDEBAR_WIDTH - itemIcon.getWidth() + BORDER)
            {
                itemIconY += itemIcon.getHeight() + PAD;
                itemIconX = BORDER;
            }
        }

        // Skills
        g.drawString("Skills", BORDER, SKILLS_Y);

        // Get all of the player's skills
        // and draw an icon for each one
        int skillIconX = BORDER;
        int skillIconY = SKILLS_Y + fontH + PAD;

        // player's current active skill, could be null
        Skill curSkill = player.getActiveSkill();
        SkillSet activeSkill = null;
        if (curSkill != null)
        {
            // If the player's current skill is not null,
            // get the skill's name
            activeSkill = curSkill.getName();
        }

        ArrayList<Skill> skills = player.getAllSkills();
        for (int i = 0; i < skills.size(); ++i)
        {
            Skill skill = skills.get(i);
            Image skillIcon = skill.getImage();

            // Set the x, y position at which the skillIcon is draw on screen
            // This lets me check if the user clicked on an icon in the update()
            // method.
            skill.setX(skillIconX);
            skill.setY(skillIconY);

            // if it's the currently active skill
            // draw it with a magenta overlay
            if (skill.getName() == activeSkill)
            {
                g.drawImage(skillIcon, skillIconX, skillIconY, Color.magenta);
            } else
            {
                g.drawImage(skillIcon, skillIconX, skillIconY);
            }

            skillIconX += skillIcon.getWidth() + PAD;

            // draw on the next row if skillIconX + skillIconWidth > sidebarwidth
            // draw on the next row if itemIconX + itemIconWidth > sidebarwidth
            if (skillIconX >= SIDEBAR_WIDTH - skillIcon.getWidth() + BORDER)
            {
                skillIconY += skillIcon.getHeight() + PAD;
                skillIconX = BORDER;
            }
        }

        // Buttons
        g.drawString("Press ESC for", BORDER, MENU_BTN_Y);
        g.drawString("  Main Menu", BORDER, MENU_BTN_Y + fontH + PAD);

        ////////////// end sidebar /////////

        ////// displays messages ////
        if (message != null)
        {
            int msgH = font.getHeight(message);
            int msgRectX = SIDEBAR_WIDTH + BORDER;
            int msgRectY = Game.APP_HEIGHT - msgH - BORDER * 2;
            int msgRectW = Game.APP_WIDTH - (SIDEBAR_WIDTH + BORDER * 2);
            int msgRectH = msgH + BORDER;


            g.setColor(Color.black);
            g.fillRect(msgRectX, msgRectY, msgRectW, msgRectH);

            g.setColor(Color.magenta);
            g.drawString(message, msgRectX + PAD, msgRectY + PAD);
        }
    }

    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException
    {
        Input input = gc.getInput();

        // Devil walking when user presses any of the arrow keys
        if (input.isKeyDown(Input.KEY_DOWN))
        {

            // Move the devil along the +y axis
            // The render method is called automatically to re-draw the sprite
            // in it's new position
            //devilY += delta * Global.WALK_SPEED;
            player.move(Player.Movement.DOWN, delta);


        } else if (input.isKeyDown(Input.KEY_UP))
        {
            player.move(Player.Movement.UP, delta);


        } else if (input.isKeyDown(Input.KEY_LEFT))
        {

            player.move(Player.Movement.LEFT, delta);


        } else if (input.isKeyDown(Input.KEY_RIGHT))
        {

            player.move(Player.Movement.RIGHT, delta);


        } else if (input.isKeyDown(Input.KEY_ESCAPE))
        {
            // return to main menu state
            // Before leaving, set the value of previous state in main menu state
            // to be able to return to it
            MainMenuState.previousState = getID();

            // Also save current state of game
            MainMenuState.gameManager.savePlayer(player);

            sbg.enterState(States.MAIN_MENU.ordinal());
        }

        /// Test if mousePressed is within sidebar boundaries
        // if it is, find which area (Skills, Items, or Spells area?)
        // within the specified area, cycle through the list of ClickableImages
        // and check if the x, y of the mouseClick falls within the bounds
        // of that ClickableImage. If it is, set alpha of image to 1.0f
        // ClickableImage extends Image and contains x, y, width, height, name
        if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON))
        {
            // turn off message as soon as player does something else
            message = null;

            float x = input.getMouseX();
            float y = input.getMouseY();

            // first check if the click occurred in the sidebar
            if (x <= SIDEBAR_WIDTH)
            {
                // Check if the click occurred in the skills area
                if (y >= SKILLS_Y && y <= SKILLS_Y + BOX_H)
                {
                    // Switch the active skill to whichever skillIcon was clicked
                    // if that skill is already active, deactivate it
                    ArrayList<Skill> skills = player.getAllSkills();

                    // exit for loop as soon as the skill is found so it doesn't
                    // have to loop through the whole list
                    boolean notFound = true;
                    for (int i = 0; i < skills.size() && notFound; ++i)
                    {
                        Skill skill = skills.get(i);
                        Image skillIcon = skill.getImage();

                        float skillIconX = skill.getX();
                        float skillIconY = skill.getY();
                        int skillIconW = skillIcon.getWidth();
                        int skillIconH = skillIcon.getHeight();

                        if (x >= skillIconX && x <= skillIconX + skillIconW
                                && y >= skillIconY && y <= skillIconY + skillIconH)
                        {
                            notFound = false; // exit loop

                            // if the selected skill is already the active skill
                            // deactivate it, else set it as the active skill
                            if (player.getActiveSkill() == skill)
                            {
                                player.setActiveSkill(null);
                            } else
                            {
                                player.setActiveSkill(skill);
                            }
                        }
                    }

                } else if (y >= ITEMS_Y && y <= ITEMS_Y + BOX_H)
                {
                    // Click occurred in the items area

                    // Switch the active item to whichever itemIcon was clicked
                    // if that item is already active, deactivate it
                    ArrayList<Item> items = player.getAllItems();

                    // exit for loop as soon as the item is found so it doesn't
                    // have to loop through the whole list
                    boolean notFound = true;
                    for (int i = 0; i < items.size() && notFound; ++i)
                    {
                        Item item = items.get(i);
                        Image itemIcon = item.getImage();

                        float itemIconX = item.getX();
                        float itemIconY = item.getY();
                        int itemIconW = itemIcon.getWidth();
                        int itemIconH = itemIcon.getHeight();

                        if (x >= itemIconX && x <= itemIconX + itemIconW
                                && y >= itemIconY && y <= itemIconY + itemIconH)
                        {
                            notFound = false; // exit loop

                            // if the selected item is already the active item
                            // deactivate it, else set it as the active item
                            if (player.getActiveItem() == item)
                            {
                                player.setActiveItem(null);
                            } else
                            {
                                player.setActiveItem(item);
                            }
                        }
                    }
                }

            } else // mouse clicked in the game world
            {
                // Mouse x, y is the screen coordinate (NOT world/map coordinate)

                // convert to world coordinates
                x = x + camera.getTranslateX();
                y = y + camera.getTranslateY();

                // Only allow player to click on sprites or use a skill if
                // it's within a certain distance from the player
                int playerX = (int) player.getX() + player.getWidth() / 2;
                int playerY = (int) player.getY() + player.getHeight();
                int dist = TILE_SIZE * 2;

                if (x >= playerX - dist && x <= playerX + dist
                        && y >= playerY - dist && y <= playerY + dist)
                {

                    // First check if clicked on a sprite
                    // if not, check if the player has a skill active
                    Sprite s = spritesManager.getSprite(x, y);

                    if (s != null)
                    {
                        s.update(x, y); // call's the NPC's perform method

                    } else
                    {
                        Skill activeSkill = player.getActiveSkill();

                        // If player selected a skill to use,
                        // use the skill
                        if (activeSkill != null)
                        {
                            activeSkill.update(x, y);
                        }

                    }
                }
            }

        }
    }
}
