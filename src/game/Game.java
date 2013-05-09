package game;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Creates a StateBasedGame.
 *
 * Initial container to add and start different game states.
 *
 * @author Danny Cao
 */
public class Game extends StateBasedGame
{

    /**
     * Width of the app.
     */
    public static final int APP_WIDTH = 1280;
    /**
     * Height of the app.
     */
    public static final int APP_HEIGHT = 800;
    /**
     * Data directory of all resources.
     */
    public static final String DATA_DIR = "game/data/";
    /**
     * Music stream.
     */
    public static Music music;

    /**
     * Add and initializes the states of the game.
     *
     * @param gc the GameContainer for the game.
     * @throws SlickException
     */
    @Override
    public void initStatesList(GameContainer gc) throws SlickException
    {
        // Add the first state
        this.addState(new MainMenuState(States.MAIN_MENU.ordinal()));

        // Enter the first state
        this.enterState(States.MAIN_MENU.ordinal());
    }

    /**
     * Creates a game.
     */
    public Game() throws SlickException
    {
        super("Tower of God");
    }

    /**
     * Starting point of the program. Creates a game.
     */
    public static void main(String[] args) throws SlickException
    {

        AppGameContainer app = new AppGameContainer(new Game());

        // Start in fullscreen mode
        app.setDisplayMode(Game.APP_WIDTH, Game.APP_HEIGHT, false);
        app.setShowFPS(false);
        app.start();
    }
}
