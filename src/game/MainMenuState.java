package game;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Main menu.
 *
 * @author Danny Cao
 */
public class MainMenuState extends BasicGameState
{

    /**
     * Keeps track of previous state.
     */
    public static int previousState = -1;
    /**
     * A Manager that has a collection of resources for use in the game.
     */
    protected static ResourceManager resourceManager = null;
    /**
     * Buttons.
     */
    private Image startBtn, loadBtn, saveBtn, quitBtn;
    /**
     * x position of startGameBtn.
     */
    private float btnX;
    /**
     * y position of startGameBtn.
     */
    private float startBtnY, loadBtnY, saveBtnY, quitBtnY;
    /**
     * ID of this state.
     */
    private int id;
    /**
     * GameContainer, can be used to change fullscreen option.
     */
    private GameContainer gc;
    /**
     * GameManager used for saving/ loading game variables.
     */
    protected static GameManager gameManager;
    /**
     * Message .
     */
    private String message = null;
    /**
     * Fullscreen text location.
     */
    private static final int FULLSCREEN_X = 50,
            FULLSCREEN_Y = Game.APP_HEIGHT - 50;
    /**
     * Message Location.
     */
    private static final int MSG_X = Game.APP_WIDTH / 2 - 50,
            MSG_Y = Game.APP_HEIGHT - 50;

    /**
     * Create this state.
     */
    public MainMenuState(int id)
    {
        this.id = id;

        // Create the resource manager with the path to data folder.
        resourceManager = new ResourceManager(Game.DATA_DIR);

        gameManager = new GameManager();
    }

    @Override
    public int getID()
    {
        return id;
    }

    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException
    {
        this.gc = gc;

        //////// Adds resources /////////////
        resourceManager.addImage("start_button.png");
        resourceManager.addImage("load_button.png");
        resourceManager.addImage("save_button.png");
        resourceManager.addImage("quit_button.png");

        resourceManager.addSpriteSheet("devil.png", GameplayState.PLAYER_W, GameplayState.PLAYER_H);
        resourceManager.addSpriteSheet("sprites_npc.png", GameplayState.SPRITE_WIDTH, GameplayState.SPRITE_HEIGHT);
        resourceManager.addSpriteSheet("skills.png", GameplayState.SPRITE_WIDTH, GameplayState.SPRITE_HEIGHT);
        resourceManager.addSpriteSheet("items.png", GameplayState.SPRITE_WIDTH, GameplayState.SPRITE_HEIGHT);


        ///// Buttons /////////
        startBtn = resourceManager.getImage("start_button.png");
        btnX = Game.APP_WIDTH / 2 - startBtn.getWidth() / 2;
        startBtnY = Game.APP_HEIGHT / 12;

        loadBtn = resourceManager.getImage("load_button.png");
        loadBtnY = startBtnY + startBtn.getHeight() + 50;

        saveBtn = resourceManager.getImage("save_button.png");
        saveBtnY = loadBtnY + loadBtn.getHeight() + 20;

        quitBtn = resourceManager.getImage("quit_button.png");
        quitBtnY = saveBtnY + saveBtn.getHeight() + 20;

        // Add all the states in the game
        sbg.addState(new ZeroFloor(States.GAMEPLAY_LVL_0.ordinal(), null));
        sbg.addState(new FloorOfTest(States.GAMEPLAY_LVL_1.ordinal(), null));
        sbg.addState(new ColoredBlocks(States.GAMEPLAY_LVL_2.ordinal(), null));
        sbg.addState(new ZombieRun(States.GAMEPLAY_LVL_3.ordinal(), null));

        // Initialize the first state
        sbg.getState(States.GAMEPLAY_LVL_0.ordinal()).init(gc, sbg);
    }

    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException
    {
        // Render the 'Start Game' button
        startBtn.draw(btnX, startBtnY);
        loadBtn.draw(btnX, loadBtnY);
        saveBtn.draw(btnX, saveBtnY);
        quitBtn.draw(btnX, quitBtnY);

        // Draws 'Start Game' text on top of the startBtn
        g.setColor(Color.magenta);
        String text = "Press 'F' to enter/exit Fullscreen";
        g.drawString(text, FULLSCREEN_X, FULLSCREEN_Y);

        // Draw message if there is one
        if (message != null)
        {
            g.drawString(message, MSG_X, MSG_Y);
        }
    }

    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException
    {

        Input input = gc.getInput();

        // If the mouse is clicked, check if it was clicked within 'Start Game'
        if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON))
        {
            message = null;

            // x, y coordinates of the mouse
            final int mouseX = input.getMouseX();
            final int mouseY = input.getMouseY();

            // check if mouse click occurred within startGameBtn
            if (mouseX >= btnX && mouseX <= btnX + startBtn.getWidth()
                    && mouseY >= startBtnY && mouseY <= startBtnY + startBtn.getHeight())
            {
                // If it's the first time starting up this game, go to first
                // gameplay level, else go back to previous level
                if (MainMenuState.previousState >= 0)
                {
                    sbg.enterState(MainMenuState.previousState);
                } else
                {
                    sbg.enterState(States.GAMEPLAY_LVL_0.ordinal());
                }
            } else if (mouseX >= btnX && mouseX <= btnX + loadBtn.getWidth()
                    && mouseY >= loadBtnY && mouseY <= loadBtnY + loadBtn.getHeight())
            {
                gameManager.loadGame(gc, sbg);

            } else if (mouseX >= btnX && mouseX <= btnX + saveBtn.getWidth()
                    && mouseY >= saveBtnY && mouseY <= saveBtnY + saveBtn.getHeight())
            {
                try
                {
                    gameManager.saveGame();
                    message = "Game Saved";

                } catch (IOException ex)
                {
                    Logger.getLogger(MainMenuState.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else if (mouseX >= btnX && mouseX <= btnX + quitBtn.getWidth()
                    && mouseY >= quitBtnY && mouseY <= quitBtnY + quitBtn.getHeight())
            {
                // quit game
                gc.exit();
            }
        }
    }

    @Override
    public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException
    {
        Game.music = new Music(Game.DATA_DIR + "blue_orchid.wav");
        Game.music.loop();
    }

    @Override
    public void keyPressed(int key, char c)
    {
        // Change fullscreen option through keypress == 'F'
        if (key == Input.KEY_F)
        {
            if (gc.isFullscreen())
            {
                try
                {
                    gc.setFullscreen(false);
                } catch (SlickException ex)
                {
                    Logger.getLogger(MainMenuState.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else
            {
                try
                {
                    gc.setFullscreen(true);
                } catch (SlickException ex)
                {
                    Logger.getLogger(MainMenuState.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
