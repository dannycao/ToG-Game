package game;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 *
 * @author Danny Cao
 */
public class ZombieRun extends GameplayState
{

    public ZombieRun(int level, Player player)
    {
        super(level, player, "dreamfolds_string.wav");
    }

    @Override
    public void initSprites(GameContainer gc, StateBasedGame sbg) throws SlickException
    {
        message = "Level not implemented.";
    }
}
