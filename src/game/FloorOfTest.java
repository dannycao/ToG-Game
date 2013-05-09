package game;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;

/**
 * A GameplayState level.
 *
 * Goal: Player has to perform tasks to use a portal to go to next level.
 *
 * Task1: Herd all cows to their pens. Task2: Buy an ax from a vendor. Task3:
 * Learn how to build from a woodcutter. Task4: Build a bridge to get to the
 * portal.
 *
 * @author Danny Cao
 */
public class FloorOfTest extends GameplayState
{

    /**
     * Locations of cows.
     */
    private static final int[] COWS_X =
    {
        54, 26, 7, 16, 104, 126, 72
    };
    private static final int[] COWS_Y =
    {
        2, 29, 52, 73, 71, 43, 70
    };
    /**
     * Number of cows in total.
     */
    private static int numCows = 2; //TODO: remove after test COWS_X.length;
    /**
     * Locations of cow pens.
     */
    private static final int[] PENS_X =
    {
        9, 18, 26, 73, 79, 87, 95
    };
    private static final int[] PENS_Y =
    {
        66, 66, 66, 6, 6, 6, 6
    };
    /**
     * Location of the farmer.
     */
    private static final float FARMER_X = 102 * TILE_SIZE, FARMER_Y = 10 * TILE_SIZE;
    /**
     * Cow herding quest status.
     */
    private static boolean cowQuestComplete = false;
    /**
     * Money for herding cows.
     */
    private static final int COWS_QUEST_MONEY = 10;
    /**
     * Location of the merchant.
     */
    private static final float MERCHANT_X = 107 * TILE_SIZE, MERCHANT_Y = 65 * TILE_SIZE;
    /**
     * Cost of an ax.
     */
    private static final int AX_COST = 10;
    /**
     * Status of ax quest.
     */
    private static boolean axQuestComplete = false;
    /**
     * Location of the builder.
     */
    private static final float BUILDER_X = 66 * TILE_SIZE, BUILDER_Y = 56 * TILE_SIZE;
    /**
     * Number of wood pieces needed to become a builder.
     */
    private static int woodPiecesNeeded = 3;
    /**
     * Status of quest to gain skill to build.
     */
    private static boolean buildQuestComplete = false;
    /**
     * Location of the portal.
     */
    private static final float PORTAL_X = 7 * TILE_SIZE, PORTAL_Y = 4 * TILE_SIZE;

    public FloorOfTest(int level, Player player)
    {
        super(level, player, "shadow_in_ice_string.wav");
    }

    @Override
    public void initSprites(GameContainer gc, StateBasedGame sbg) throws SlickException
    {

        // SpriteSheet containing all NPC images
        SpriteSheet spritesSheet = MainMenuState.resourceManager.getSpriteSheet("sprites_npc.png");


        ///////////// cows /////////////////
        // place cows on the map

        // get cow image
        Image cowImg = spritesSheet.getSubImage(2, 1);

        for (int i = 0; i < COWS_X.length; ++i)
        {
            // Convert map x, y index to world coordinates
            final float cowWorldX = COWS_X[i] * TILE_SIZE;
            final float cowWorldY = COWS_Y[i] * TILE_SIZE;

            final float penWorldX = PENS_X[i] * TILE_SIZE;
            final float penWorldY = PENS_Y[i] * TILE_SIZE;

            final NPC penCow = new NPC(cowImg, penWorldX, penWorldY);

            // create a new NPC with x, y stored in cows_x/y
            NPC cow = new NPC(cowImg, cowWorldX, cowWorldY);
            cow.setAction(new Action()
            {
                @Override
                public void perform(float x, float y)
                {
                    message = "Mooooo!!!!";
                    --numCows;

                    // cow has been clicked on, remove from its current location
                    // and move to the pen
                    spritesManager.setSprite(null, cowWorldX, cowWorldY);
                    collisionManager.setBlocked(cowWorldX, cowWorldY, false);

                    spritesManager.setSprite(penCow, penWorldX, penWorldY);
                    collisionManager.setBlocked(penWorldX, penWorldY, true);
                }
            });

            spritesManager.setSprite(cow, cowWorldX, cowWorldY);
            collisionManager.setBlocked(cowWorldX, cowWorldY, true);
        }


        /////////// Farmer ///////////////
        // Place a farmer that asks player to herd all his cows back for him
        Image farmerImg = spritesSheet.getSubImage(3, 0);
        NPC farmer = new NPC(farmerImg, FARMER_X, FARMER_Y);
        farmer.setAction(new Action()
        {
            @Override
            public void perform(float x, float y)
            {
                // If player has herded all the cows & this is the first time
                // talking to the farmer again
                if (numCows <= 0 && !cowQuestComplete)
                {
                    message = "Thanks for herding all the cows for me.\n"
                            + "Here's some money for your trouble.";

                    player.addMoney(COWS_QUEST_MONEY);

                    cowQuestComplete = true;

                } else if (cowQuestComplete)
                {
                    // Already gave money to player, don't give again.
                    message = "Stop pestering me, jackass.";
                } else
                {
                    // Ask player to herd cows for him
                    message = "Please bring back my " + numCows + " cows to me.";
                }
            }
        });
        spritesManager.setSprite(farmer, FARMER_X, FARMER_Y);
        collisionManager.setBlocked(FARMER_X, FARMER_Y, true);


        ///// Merchant //////////////////
        Image merchantImg = spritesSheet.getSubImage(4, 0);
        NPC merchant = new NPC(merchantImg, MERCHANT_X, MERCHANT_Y);
        merchant.setAction(new Action()
        {
            @Override
            public void perform(float x, float y)
            {
                // Sells the player an ax for money
                if (player.getMoney() >= AX_COST && !axQuestComplete)
                {
                    message = "Here's an ax. Thanks for the business.";

                    player.removeMoney(AX_COST);

                    // With an ax, player can now chop wood.
                    Skill chop = skillsManager.getSkill(SkillSet.CHOP);
                    player.addSkill(chop);

                    axQuestComplete = true;

                } else if (axQuestComplete)
                {
                    // Player already gained chop skill
                    message = "I don't have anything else to sell.\n"
                            + "Unless you want my body. ;)";
                } else
                {
                    // player doesn't have enough money
                    message = "An ax will cost you: $ " + AX_COST + ".\n"
                            + "Show me the money!";
                }
            }
        });
        spritesManager.setSprite(merchant, MERCHANT_X, MERCHANT_Y);
        collisionManager.setBlocked(MERCHANT_X, MERCHANT_Y, true);


        ///////////////// Builder ///////////
        Image builderImg = spritesSheet.getSubImage(2, 0);
        NPC builder = new NPC(builderImg, BUILDER_X, BUILDER_Y);
        builder.setAction(new Action()
        {
            @Override
            public void perform(float x, float y)
            {
                int playerWoodCount = player.getItemCount(ItemSet.WOOD);

                // Player gains ability to build if he has collected enough wood
                // pieces. Can only gain skill once
                if (playerWoodCount >= woodPiecesNeeded && !buildQuestComplete)
                {
                    message = "I'm proud of you apprentice.\n"
                            + "You are now a legit Builder.\n"
                            + "Go forth and build.";

                    Skill build = skillsManager.getSkill(SkillSet.BUILD);
                    player.addSkill(build);

                    player.removeItems(ItemSet.WOOD, woodPiecesNeeded);

                    buildQuestComplete = true;

                } else if (buildQuestComplete)
                {
                    // Already gained build skill
                    message = "Stop screwing around and go build shit.";

                } else
                {
                    // Has not gained build skill and don't have enough wood
                    message = "Building is hard work.\n"
                            + "Chop " + woodPiecesNeeded + " pieces of wood\n"
                            + "and I'll teach you how to build.";
                }
            }
        });
        spritesManager.setSprite(builder, BUILDER_X, BUILDER_Y);
        collisionManager.setBlocked(BUILDER_X, BUILDER_Y, true);


        //// Portal
        final StateBasedGame sb = sbg;
        final GameContainer g = gc;

        Image portalImg = spritesSheet.getSubImage(1, 2);
        NPC portal = new NPC(portalImg, PORTAL_X, PORTAL_Y);
        portal.setAction(new Action()
        {
            @Override
            public void perform(float x, float y)
            {
                questComplete = true;
                player.setLevel(level + 1);

                sb.addState(new ColoredBlocks(States.GAMEPLAY_LVL_2.ordinal(), player));
                try
                {
                    sb.getState(States.GAMEPLAY_LVL_2.ordinal()).init(g, sb);
                } catch (SlickException ex)
                {
                    Logger.getLogger(FloorOfTest.class.getName()).log(Level.SEVERE, null, ex);
                }

                sb.enterState(States.GAMEPLAY_LVL_2.ordinal());
            }
        });
        spritesManager.setSprite(portal, PORTAL_X, PORTAL_Y);

    }

    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException
    {
        super.update(gc, sbg, delta);
    }
}
