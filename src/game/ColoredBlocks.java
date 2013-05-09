/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Goal: Complete three puzzles.
 *
 * Puzzle 1 : Select the colored blocks in order of their alphatically ordering
 * of color name. Puzzle 2 : Select only & all blue colored blocks. Puzzle 3 :
 * Select the colored blocks in reverse alphabetical order
 *
 *
 * @author Danny Cao
 */
public class ColoredBlocks extends GameplayState
{

    /**
     * Location of puzzler 1.
     */
    private static final int PUZZLER1_X = 23 * TILE_SIZE, PUZZLER1_Y = 17 * TILE_SIZE;
    /**
     * Location of puzzle 1 gate tile.
     */
    private static final int ALPHA_GATE_X = 27, ALPHA_GATE_Y = 24;
    /**
     * List of blocks for puzzle 1 : alphabet.
     */
    private static final ItemSet[] ALPHA_PUZZLE =
    {
        ItemSet.BLU_SQUARE, ItemSet.GRN_SQUARE,
        ItemSet.ORNG_SQUARE, ItemSet.PUR_SQUARE, ItemSet.RED_SQUARE, ItemSet.YLW_SQUARE
    };
    /**
     * List of block locations for puzzle 1 : alphabet.
     */
    private static final int[] ALPHA_X =
    {
        24, 25, 26, 24, 25, 26
    };
    private static final int[] ALPHA_Y =
    {
        20, 20, 20, 21, 21, 21
    };
    /**
     * true if this block has been clicked.
     */
    private static boolean[] isAlphaActive =
    {
        false, false, false, false, false, false
    };
    /**
     * Add the block to this list when player clicks on it.
     */
    private static ArrayList<Item> alphaItems = new ArrayList<Item>();
    private static boolean alphaQuestComplete = false;
    /**
     * Location of puzzler 2 .
     */
    private static final int PUZZLER2_X = 20 * TILE_SIZE, PUZZLER2_Y = 34 * TILE_SIZE;
    /**
     * Location of puzzle gate 2.
     */
    private static final int BLUE_GATE_X = 23, BLUE_GATE_Y = 46;
    /**
     * Location of first block in each row for puzzle 2.
     */
    private static final int BLUE_PUZZLE_X = 16;
    private static final int BLUE_PUZZLE_Y = 37;
    private static final int BLUE_PUZZLE_L = 9, BLUE_PUZZLE_H = 6;
    private static boolean blueQuestComplete = false;
    /**
     * List of active blocks for blue puzzle.
     */
    private static ArrayList<Item> blueItems = new ArrayList<Item>();
    /**
     * Number of blue blocks on the map.
     */
    private static int blueCount = 0;
    /**
     * Selected status of blue puzzle blocks.
     */
    private static boolean[][] isBlueActive = new boolean[BLUE_PUZZLE_L][BLUE_PUZZLE_H];
    /**
     * Location of puzzler 3 .
     */
    private static final int PUZZLER3_X = 40 * TILE_SIZE, PUZZLER3_Y = 38 * TILE_SIZE;
    /**
     * Location of puzzle 1 gate tile.
     */
    private static final int REVERSE_GATE_X = 43, REVERSE_GATE_Y = 36;
    /**
     * List of block locations for puzzle 3 : reverse alphabet.
     */
    private static final int[] REVERSE_ALPHA_X =
    {
        46, 47, 48, 45, 45, 48
    };
    private static final int[] REVERSE_ALPHA_Y =
    {
        41, 43, 41, 42, 45, 44
    };
    private static ArrayList<Item> reverseAlphaItems = new ArrayList<Item>();
    private static boolean[] isReverseActive =
    {
        false, false, false, false, false, false
    };
    private static boolean reverseQuestComplete = false;
    private static final int PORTAL_X = 49 * TILE_SIZE, PORTAL_Y = 33 * TILE_SIZE;

    public ColoredBlocks(int level, Player player)
    {
        super(level, player, "dreamfolds_string.wav");
    }

    @Override
    public void initSprites(GameContainer gc, StateBasedGame sbg) throws SlickException
    {
        final StateBasedGame sb = sbg;
        final GameContainer g = gc;


        final SpriteSheet spritesSS = MainMenuState.resourceManager.getSpriteSheet("sprites_npc.png");

        // Portal
        Image portalImg = spritesSS.getSubImage(1, 2);
        NPC portal = new NPC(portalImg, PORTAL_X, PORTAL_Y);
        portal.setAction(new Action()
        {
            @Override
            public void perform(float x, float y)
            {
                questComplete = true;
                player.setLevel(level + 1);

                sb.addState(new ZombieRun(States.GAMEPLAY_LVL_3.ordinal(), player));
                try
                {
                    sb.getState(States.GAMEPLAY_LVL_3.ordinal()).init(g, sb);
                } catch (SlickException ex)
                {
                    Logger.getLogger(ColoredBlocks.class.getName()).log(Level.SEVERE, null, ex);
                }

                sb.enterState(States.GAMEPLAY_LVL_3.ordinal());
            }
        });
        spritesManager.setSprite(portal, PORTAL_X, PORTAL_Y);


        Image puzzlerImg = spritesSS.getSubImage(0, 1);

        /// First puzzle: alpha: player has to click on blocks in alphabetical
        // order of their color
        NPC alphaPuzzler = new NPC(puzzlerImg, PUZZLER1_X, PUZZLER1_Y);
        alphaPuzzler.setAction(new Action()
        {
            @Override
            public void perform(float x, float y)
            {
                message = "Everything has its order.";

                // Reset the status of each block to un-selected/not active
                for (int i = 0; i < isAlphaActive.length; ++i)
                {
                    isAlphaActive[i] = false;
                }

                for (int i = 0; i < alphaItems.size(); ++i)
                {
                    Item item = alphaItems.get(i);
                    item.setImageAlpha(1.0f);
                }

                alphaItems.clear();
            }
        });
        spritesManager.setSprite(alphaPuzzler, PUZZLER1_X, PUZZLER1_Y);
        collisionManager.setBlocked(PUZZLER1_X, PUZZLER1_Y, true);

        // Adds the alpha puzzle blocks to the game
        int index = 0;
        for (ItemSet e : ALPHA_PUZZLE)
        {
            final int finalIndex = index;
            final Item i = itemsManager.getItem(e);
            i.setAction(new Action()
            {
                @Override
                public void perform(float x, float y)
                {
                    // If the block is already selected, don't do anything else to it
                    // If the block hasn't been selected, change its alpha to show
                    // it as being selected and add it to the list of active items
                    if (!isAlphaActive[finalIndex])
                    {
                        i.setImageAlpha(.5f);
                        alphaItems.add(i);

                        isAlphaActive[finalIndex] = true;
                    }
                }
            });

            i.setX(ALPHA_X[index] * TILE_SIZE);
            i.setY(ALPHA_Y[index] * TILE_SIZE);

            spritesManager.setSprite(i, ALPHA_X[index] * TILE_SIZE, ALPHA_Y[index] * TILE_SIZE);
            ++index;
        }


        //////////// 2nd puzzle: Select all & only blue colored blocks

        // set all blocks to de-selected
        for (int i = 0; i < isBlueActive.length; ++i)
        {
            for (int j = 0; j < isBlueActive[i].length; ++j)
            {
                isBlueActive[i][j] = false;
            }
        }

        NPC bluePuzzler = new NPC(puzzlerImg, PUZZLER2_X, PUZZLER2_Y);
        bluePuzzler.setAction(new Action()
        {
            @Override
            public void perform(float x, float y)
            {
                message = "You should listen to what I have to say.\n"
                        + "There is only one thing you need to know:\n"
                        + "Look to the sky for inspiration.";

                // reset all blocks to de-selected
                for (int i = 0; i < isBlueActive.length; ++i)
                {
                    for (int j = 0; j < isBlueActive[i].length; ++j)
                    {
                        isBlueActive[i][j] = false;
                    }
                }

                for (int i = 0; i < blueItems.size(); ++i)
                {
                    blueItems.get(i).setImageAlpha(1.0f);
                }

                blueItems.clear();
            }
        });
        spritesManager.setSprite(bluePuzzler, PUZZLER2_X, PUZZLER2_Y);
        collisionManager.setBlocked(PUZZLER2_X, PUZZLER2_Y, true);

        // Adds the colored blocks randomly
        // Make sure there is at least 1 blue block
        Random rgen = new Random();

        int blueI = rgen.nextInt(BLUE_PUZZLE_L);
        int blueJ = rgen.nextInt(BLUE_PUZZLE_H);

        for (int i = 0; i < BLUE_PUZZLE_L; ++i)
        {
            for (int j = 0; j < BLUE_PUZZLE_H; ++j)
            {
                final int finalIndexI = i;
                final int finalIndexJ = j;

                ItemSet itemName = ItemSet.BLU_SQUARE;

                // if the i, j == blueI, blueJ then assign a blue block to
                // that tile, else randomly select 
                if (i != blueI || j != blueJ)
                {
                    // Randomly select a block from the alpha list
                    int random = rgen.nextInt(ALPHA_PUZZLE.length);

                    itemName = ALPHA_PUZZLE[random];
                }

                // Keep track of the number of blue squares
                if (itemName == ItemSet.BLU_SQUARE)
                {
                    ++blueCount;
                }

                final Item item = itemsManager.getItem(itemName);
                item.setAction(new Action()
                {
                    @Override
                    public void perform(float x, float y)
                    {
                        if (!isBlueActive[finalIndexI][finalIndexJ])
                        {
                            item.setImageAlpha(.5f);
                            blueItems.add(item);

                            isBlueActive[finalIndexI][finalIndexJ] = true;
                        }
                    }
                });

                item.setX((BLUE_PUZZLE_X + i) * TILE_SIZE);
                item.setY((BLUE_PUZZLE_Y + j) * TILE_SIZE);

                spritesManager.setSprite(item, (BLUE_PUZZLE_X + i) * TILE_SIZE, (BLUE_PUZZLE_Y + j) * TILE_SIZE);

            }

        }

        //////////// Reverse alpha quest
        NPC reversePuzzler = new NPC(puzzlerImg, PUZZLER3_X, PUZZLER3_Y);
        reversePuzzler.setAction(new Action()
        {
            @Override
            public void perform(float x, float y)
            {
                message = "If stuck, retrace your steps to the beginning.";

                // reset all blocks to de-selected
                for (int i = 0; i < isReverseActive.length; ++i)
                {

                    isReverseActive[i] = false;
                }

                for (int i = 0; i < reverseAlphaItems.size(); ++i)
                {
                    reverseAlphaItems.get(i).setImageAlpha(1.0f);
                }

                reverseAlphaItems.clear();
            }
        });
        spritesManager.setSprite(reversePuzzler, PUZZLER3_X, PUZZLER3_Y);
        collisionManager.setBlocked(PUZZLER3_X, PUZZLER3_Y, true);

        // Adds blocks to the game
        index = 0;
        for (ItemSet e : ALPHA_PUZZLE)
        {
            final int finalIndex = index;
            final Item i = itemsManager.getItem(e);
            i.setAction(new Action()
            {
                @Override
                public void perform(float x, float y)
                {
                    // If the block is already selected, don't do anything else to it
                    // If the block hasn't been selected, change its alpha to show
                    // it as being selected and add it to the list of active items
                    if (!isReverseActive[finalIndex])
                    {
                        i.setImageAlpha(.5f);
                        reverseAlphaItems.add(i);

                        isReverseActive[finalIndex] = true;
                    }
                }
            });

            i.setX(REVERSE_ALPHA_X[index] * TILE_SIZE);
            i.setY(REVERSE_ALPHA_Y[index] * TILE_SIZE);

            spritesManager.setSprite(i, REVERSE_ALPHA_X[index] * TILE_SIZE, REVERSE_ALPHA_Y[index] * TILE_SIZE);
            ++index;
        }

    }

    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException
    {
        super.update(gc, sbg, delta);

        // Check if the player selected each block in alphabetical order
        if (alphaItems.size() == ALPHA_PUZZLE.length)
        {
            alphaQuestComplete = true;

            for (int i = 0; i < alphaItems.size() && alphaQuestComplete; ++i)
            {
                if (alphaItems.get(i).getName() != ALPHA_PUZZLE[i])
                {
                    alphaQuestComplete = false;
                }
            }

            if (alphaQuestComplete)
            {
                // solved the puzzle, open the gate
                openGate(ALPHA_GATE_X, ALPHA_GATE_Y);
            }
        }

        // Check if player selected only & all blue squares
        if (blueItems.size() == blueCount)
        {
            blueQuestComplete = true;

            // needs to check that all items in blueItems are actually blue
            for (int i = 0; i < blueItems.size() && blueQuestComplete; ++i)
            {
                if (blueItems.get(i).getName() != ItemSet.BLU_SQUARE)
                {
                    blueQuestComplete = false;
                }
            }

            if (blueQuestComplete)
            {
                // solved the puzzle, open the gate
                openGate(BLUE_GATE_X, BLUE_GATE_Y);
            }
        }

        // Check if player selected the squares in reverse order
        if (reverseAlphaItems.size() == ALPHA_PUZZLE.length)
        {
            reverseQuestComplete = true;

            int j = 0;
            for (int i = reverseAlphaItems.size() - 1; i >= 0 && reverseQuestComplete; --i)
            {
                if (reverseAlphaItems.get(i).getName() != ALPHA_PUZZLE[j++])
                {
                    reverseQuestComplete = false;
                }
            }

            if (reverseQuestComplete)
            {
                openGate(REVERSE_GATE_X, REVERSE_GATE_Y);
            }
        }
    }

    /**
     * Opens a gate after solving the puzzle.
     *
     * @param x in map index coordinates
     * @param y in map index coordinates
     */
    private void openGate(int gateX, int gateY)
    {
        int modLevel = map.getLayerIndex(Layers.MODIFIABLE.toString());
        int id = map.getTileId(0, 0, 0);

        map.setTileId(gateX, gateY, modLevel, id);
        collisionManager.setBlocked(gateX * TILE_SIZE, gateY * TILE_SIZE, false);
    }
}
