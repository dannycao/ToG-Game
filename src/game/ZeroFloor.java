package game;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 *
 * @author Danny Cao
 */
public class ZeroFloor extends GameplayState
{

    public ZeroFloor(int level, Player player)
    {
        super(level, player, "shadow_in_ice_piano.wav");
    }

    @Override
    public void initSprites(GameContainer gc, StateBasedGame sbg) throws SlickException
    {
        Image questerImg = MainMenuState.resourceManager.getSpriteSheet("sprites_npc.png").getSubImage(0, 0);
        Image portalImg = MainMenuState.resourceManager.getSpriteSheet("sprites_npc.png").getSubImage(1, 1);

        final float portalX = 280;
        final float portalY = 80;

        final StateBasedGame sb = sbg;
        final GameContainer g = gc;

        NPC portal = new NPC(portalImg, portalX, portalY);
        portal.setAction(new Action()
        {
            @Override
            public void perform(float x, float y)
            {
                if (questComplete)
                {
                    player.setLevel(level + 1);

                    sb.addState(new FloorOfTest(States.GAMEPLAY_LVL_1.ordinal(), player));
                    try
                    {
                        sb.getState(States.GAMEPLAY_LVL_1.ordinal()).init(g, sb);
                    } catch (SlickException ex)
                    {
                        Logger.getLogger(ZeroFloor.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    sb.enterState(States.GAMEPLAY_LVL_1.ordinal());
                }
            }
        });
        spritesManager.setSprite(portal, portalX, portalY);



        //// 
        final float questerX = 1640;
        final float questerY = 880;
        NPC quester = new NPC(questerImg, questerX, questerY);
        quester.setAction(new Action()
        {
            @Override
            public void perform(float x, float y)
            {
                message = "Good job. Now move on.";

                questComplete = true;
            }
        });
        spritesManager.setSprite(quester, questerX, questerY);
        collisionManager.setBlocked(questerX, questerY, true);
    }
}
