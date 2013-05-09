package game;

import java.util.ArrayList;
import java.util.Iterator;
import org.newdawn.slick.Animation;
import org.newdawn.slick.SpriteSheet;

/**
 * Sprite class for any sprite.
 *
 * @author Danny Cao
 */
public class Player extends Sprite
{

    /**
     * Sprite's level, a stat.
     */
    private int level = 0;
    /**
     * Sprite's amount of money.
     */
    private int money = 0;
    /**
     * Player's health.
     */
    private int hp = 100;
    /**
     * Player's strength.
     */
    private int str = 1;
    /**
     * Sprite sheet from which to load all animations.
     */
    private SpriteSheet spriteSheet;
    private Animation sprite;
    private Animation spriteMoveDown;
    private Animation spriteMoveLeft;
    private Animation spriteMoveRight;
    private Animation spriteMoveUp;
    /**
     * Index of a Sprite's different movement directions in the SpriteSheet.
     */
    private int sDownIndex = 0, sLeftIndex = 1, sRightIndex = 2, sUpIndex = 3;
    /**
     * List of Skills that this Player has.
     */
    private ArrayList<Skill> skills = null;
    /**
     * Currently active Skill.
     */
    private Skill activeSkill = null;
    /**
     * List of Items that this Player has.
     */
    private ArrayList<Item> items = null;
    /**
     * Currently active Item.
     */
    private Item activeItem = null;

    public int getHP()
    {
        return hp;
    }

    public int getSTR()
    {
        return str;
    }

    void setHP(int hp)
    {
        this.hp = hp;
    }

    void setSTR(int str)
    {
        this.str = str;
    }

    /**
     * Possible directions in which the Player can move.
     */
    public enum Movement
    {

        UP, DOWN, LEFT, RIGHT;
    }
    /**
     * Time between frames.
     */
    private int duration;
    /**
     * Speed at which the Player moves. Must be float for movement to work
     * properly and look smooth.
     */
    private float speed;
    private CollisionManager collisionManager;

    /**
     * Creates a Sprite with 4 basic movements with its index in the SpriteSheet
     * in this order: down, left, right, up.
     *
     * @param frames
     * @param duration
     * @param x
     * @param y
     * @param cm
     */
    public Player(SpriteSheet frames, int duration, float speed, float x, float y, CollisionManager cm)
    {
        this(frames, 0, 1, 2, 3, duration, speed, x, y, cm);
    }

    /**
     * Creates a Player.
     *
     * @param frames
     * @param spriteDownIndex index of "down" animation in the SpriteSheet
     * @param spriteLeftIndex
     * @param spriteRightIndex
     * @param spriteUpIndex
     * @param duration
     * @param speed
     * @param x
     * @param y
     * @param cm
     */
    public Player(SpriteSheet frames, int spriteDownIndex, int spriteLeftIndex,
            int spriteRightIndex, int spriteUpIndex, int duration, float speed,
            float x, float y, CollisionManager cm)
    {

        super(null, x, y, frames.getWidth() / frames.getHorizontalCount(), frames.getHeight() / frames.getVerticalCount());

        spriteMoveDown = new Animation(false);
        spriteMoveLeft = new Animation(false);
        spriteMoveRight = new Animation(false);
        spriteMoveUp = new Animation(false);

        sDownIndex = spriteDownIndex;
        sLeftIndex = spriteLeftIndex;
        sRightIndex = spriteRightIndex;
        sUpIndex = spriteUpIndex;

        sprite = spriteMoveDown;


        spriteSheet = frames;

        this.duration = duration;
        this.speed = speed;

        collisionManager = cm;

        skills = new ArrayList();
        items = new ArrayList();

        initFrames();
    }

    /**
     * Initializes the basic movement animations.
     */
    private void initFrames()
    {
        int spriteCountRow = spriteSheet.getHorizontalCount();

        for (int s = 0; s < spriteCountRow; ++s)
        {
            spriteMoveDown.addFrame(spriteSheet.getSprite(s, sDownIndex), duration);
            spriteMoveLeft.addFrame(spriteSheet.getSprite(s, sLeftIndex), duration);
            spriteMoveRight.addFrame(spriteSheet.getSprite(s, sRightIndex), duration);
            spriteMoveUp.addFrame(spriteSheet.getSprite(s, sUpIndex), duration);
        }
    }

    /**
     * Moves the Player.
     *
     * @param dir the direction to move the player
     * @param delta the amount to move the player
     */
    public void move(Movement dir, int delta)
    {
        // New position of the player if the movement is successful.
        float newX = getX();
        float newY = getY();

        // Distance to move the player
        float distance = delta * speed;

        // x, y is the coordinates for the top-left corner of the sprite
        // so need to add the sprite's width or height when checking for collision
        // against the game map's max x and max y boundaries.
        // Also, since the resulting x, y will be converted into coordinates
        // of a TileMap Tiles array, we need to check for collision in all corners
        // of the sprite. Actually only want to check for collision on the bottom
        // half of the sprite because a sprite can walk "in front" of an object
        // on the z-axis. So check for collision against the corners with '*'
        /*
         * --------------------------------------------
         * ' (0,0)   ' (0, 1) '
         * '         '        '
         * '    ====='=====   '
         * '    ====='=====   '
         * '    ====='=====   '
         * '----====='=====---'------------------------
         * '    ====='=====   '
         * '    ====='=====   '
         * '    ==*=='==*==   '
         * ' (1,0)   ' (1,1)  '
         * '--------------------------------------------
         */
        // Even though a sprite has a given width, it doesn't actually occupy
        // that whole space. For more precise collision detection, ignore 
        // extra space surrounding sprite.
        float padX = getWidth();
        float padY = getHeight();
        float offset = padX / 8.0f;

        boolean collision = false;

        switch (dir)
        {
            case UP:
                sprite = spriteMoveUp;
                newY -= distance;
                // Check collision for bottom-left & bottom-right corners
                if (collisionManager.isBlocked(newX + offset * 3, newY + padY)
                        || collisionManager.isBlocked(newX + offset * 5, newY + padY))
                {
                    collision = true;
                } // Stops the top of the sprite from going beyond the top edge
                // of the map.
                else if (newY < 0)
                {
                    collision = true;
                }
                break;
            case DOWN:
                sprite = spriteMoveDown;
                newY += distance;
                // Check collision for bottom-left & bottom-right
                if (collisionManager.isBlocked(newX + offset * 3, newY + padY)
                        || collisionManager.isBlocked(newX + offset * 5, newY + padY))
                {
                    collision = true;
                }
                break;
            case LEFT:
                sprite = spriteMoveLeft;
                newX -= distance;
                // Check collision for bottom-left
                if (collisionManager.isBlocked(newX + offset * 3, newY + padY))
                {
                    collision = true;
                }
                break;
            case RIGHT:
                sprite = spriteMoveRight;
                newX += distance;
                // Check collision for bottom-right
                if (collisionManager.isBlocked(newX + offset * 5, newY + padY))
                {
                    collision = true;
                }
                break;
            default:
                break;
        }

        // Moves the player if it won't collide with anything else
        if (!collision)
        {
            setX(newX);
            setY(newY);

            sprite.update(delta);
        }
    }

    /**
     * Draws the sprite at its x,y position.
     */
    @Override
    public void draw()
    {
        sprite.draw(getX(), getY());
    }

    /**
     * Unimplemented.
     *
     * @param x
     * @param y
     */
    @Override
    public void update(float x, float y)
    {
    }

    /**
     * Add a Skill to this Player.
     *
     * @param skill
     */
    public void addSkill(Skill skill)
    {
        boolean noDuplicates = true;

        // check for duplicate skills
        for (Skill s : skills)
        {
            if (s.getName() == skill.getName())
            {
                noDuplicates = false;
            }
        }

        if (noDuplicates)
        {
            skills.add(skill);
        }
    }

    /**
     * Gets Player's currently active Skill.
     *
     * @return
     */
    public Skill getActiveSkill()
    {
        return activeSkill;
    }

    /**
     * Returns the list of this Player's Skills.
     *
     * @return
     */
    public ArrayList<Skill> getAllSkills()
    {
        return skills;
    }

    /**
     * Sets all of this Player's Skills.
     *
     * @param newSkills
     */
    public void setAllSkills(ArrayList<Skill> newSkills)
    {
        skills = newSkills;
    }

    /**
     * Sets the Player's currently active Skill.
     *
     * @param skill
     */
    public void setActiveSkill(Skill skill)
    {
        activeSkill = skill;
    }

    /**
     * Checks if player has a skill.
     */
    public boolean hasSkill(SkillSet skillName)
    {
        for (Skill s : skills)
        {
            if (s.getName() == skillName)
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Adds an Item to this Player's list of items.
     *
     * @param item
     */
    public void addItem(Item item)
    {
        items.add(item);
    }

    /**
     * Removes an Item from this Player's list of items.
     *
     * @return the Item if found, else return null.
     */
    public Item removeItem(ItemSet itemName)
    {
        Iterator<Item> iter = items.iterator();

        while (iter.hasNext())
        {
            Item item = iter.next();

            if (item.getName() == itemName)
            {
                iter.remove();
                return item;
            }
        }

        return null;
    }

    /**
     * Removes a certain number of items from the player.
     */
    public boolean removeItems(ItemSet itemName, int amount)
    {
        // Only remove if player has enough items
        if (getItemCount(itemName) >= amount)
        {
            while (amount > 0)
            {
                removeItem(itemName);
                --amount;
            }

            return true;
        }

        return false;
    }

    /**
     * Gets the Player's currently active item.
     *
     * @return
     */
    public Item getActiveItem()
    {
        return activeItem;
    }

    /**
     * Checks if player has an item.
     *
     * @return
     */
    public boolean hasItem(ItemSet itemName)
    {
        for (Item e : items)
        {
            if (e.getName() == itemName)
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks the number of an item a player has.
     *
     * @return
     */
    public int getItemCount(ItemSet itemName)
    {
        int count = 0;
        for (Item e : items)
        {
            if (e.getName() == itemName)
            {
                ++count;
            }
        }

        return count;
    }

    public ArrayList<Item> getAllItems()
    {
        return items;
    }

    public void setAllItems(ArrayList<Item> newItems)
    {
        items = newItems;
    }

    public void setActiveItem(Item item)
    {
        activeItem = item;
    }

    /**
     * Gets the Player's current level.
     *
     * @return
     */
    public int getLevel()
    {
        return level;
    }

    /**
     * Increase the player's level by 1.
     */
    public void levelUp()
    {
        ++level;
    }

    /**
     * Sets the player's level.
     *
     * @param level
     */
    public void setLevel(int level)
    {
        this.level = level;
    }

    public void setDuration(int dur)
    {
        this.duration = dur;
    }

    public int getDuration()
    {
        return duration;
    }

    public void setSpeed(float speed)
    {
        this.speed = speed;
    }

    public float getSpeed()
    {
        return speed;
    }

    public void setCollisionManager(CollisionManager cm)
    {
        this.collisionManager = cm;
    }

    public CollisionManager getCollisionManager()
    {
        return collisionManager;
    }

    public void setMoney(int amount)
    {
        money = amount;
    }

    public void addMoney(int amount)
    {
        money += amount;
    }

    public int getMoney()
    {
        return money;
    }

    public void removeMoney(int amount)
    {
        money -= money;
    }
}
