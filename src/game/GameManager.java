/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.io.BufferedReader;
import java.util.logging.Logger;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * In charge of managing game states, resources for saving/loading.
 *
 * @author Danny Cao
 */
public class GameManager
{

    private static final String SAVE_FILE = "src/game/data/save_file.txt";
    private Player player = null;

    public GameManager()
    {
    }

    /**
     * Save the player's current status.
     *
     * Only saved to file when the "Save" button is pressed in Main Menu
     *
     * @param player
     */
    public void savePlayer(Player player)
    {
        this.player = player;
    }

    /**
     * Saves to file.
     *
     * @throws IOException
     */
    public void saveGame() throws IOException
    {
        if (player != null)
        {
            BufferedWriter bw = null;
            try
            {
                File file = new File(SAVE_FILE);
                // Overwrite file
                bw = new BufferedWriter(new FileWriter(file, false));

                ///// Save stats, one on each line
                bw.write("" + player.getLevel() + "\n");
                bw.write("" + player.getHP() + "\n");
                bw.write("" + player.getSTR() + "\n");
                bw.write("" + player.getMoney() + "\n");

                ////// Save items, all on one line
                ArrayList<Item> items = player.getAllItems();
                for (int i = 0; i < items.size(); ++i)
                {
                    bw.write(items.get(i).getName() + " ");
                }
                bw.newLine();

                //////// Save skills, all on one line
                ArrayList<Skill> skills = player.getAllSkills();
                for (int i = 0; i < skills.size(); ++i)
                {
                    bw.write(skills.get(i).getName() + " ");
                }
                bw.newLine();


            } catch (IOException ex)
            {
                Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, null, ex);
            } finally
            {
                if (bw != null)
                {
                    bw.close();
                }
            }
        }
    }

    /**
     * Load a player's stats, items, skills from file.
     *
     * @param player
     */
    public void loadPlayer(Player player)
    {
        BufferedReader br = null;

        try
        {
            br = new BufferedReader(new FileReader(new File(SAVE_FILE)));

            if (br.ready())
            {
                ///////// File first lists all the player's stats, one
                // on each line.
                int level = Integer.parseInt(br.readLine().trim());
                player.setLevel(level);

                int hp = Integer.parseInt(br.readLine().trim());
                player.setHP(hp);

                int str = Integer.parseInt(br.readLine().trim());
                player.setSTR(str);

                int money = Integer.parseInt(br.readLine().trim());
                player.setMoney(money);


                ////// Player might not have any items or skills saved
                /// to file, so must check to see if there are any more lines
                if (br.ready())
                {
                    String itemsString = br.readLine().trim();
                    if (itemsString.length() > 0)
                    {
                        String[] items = itemsString.split(" ");
                        ArrayList<Item> itemsList = new ArrayList<Item>();
                        for (int i = 0; i < items.length; ++i)
                        {
                            ItemSet itemName = ItemSet.valueOf(items[i]);
                            Item item = GameplayState.itemsManager.getItem(itemName);
                            itemsList.add(item);
                        }
                        player.setAllItems(itemsList);
                    }
                }

                if (br.ready())
                {
                    String skillsString = br.readLine().trim();
                    if (skillsString.length() > 0)
                    {
                        String[] skills = skillsString.split(" ");
                        ArrayList<Skill> skillsList = new ArrayList<Skill>();
                        for (int i = 0; i < skills.length; ++i)
                        {
                            SkillSet skillName = SkillSet.valueOf(skills[i]);
                            Skill skill = GameplayState.skillsManager.getSkill(skillName);
                            skillsList.add(skill);
                        }
                        player.setAllSkills(skillsList);
                    }
                }
            }
        } catch (IOException ex)
        {
            Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally
        {
            if (br != null)
            {
                try
                {
                    br.close();
                } catch (IOException ex)
                {
                    Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Loads the game from a file.
     *
     * This actually only loads the correct gameplayState level. Inside the
     * gameplayState, the init function will call loadPlayer() to load the
     * player's stats, items, skills.
     *
     * @param gc
     * @param sbg
     */
    public void loadGame(GameContainer gc, StateBasedGame sbg)
    {

        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new FileReader(new File(SAVE_FILE)));


            // Make sure there is a file with game data to load
            if (br.ready())
            {
                String s = br.readLine();

                // First line contains the level to load
                // Get the level to load
                int level = Integer.parseInt(s.trim());

                try
                {
                    // load the level, which will initialize all variables
                    // including player to default values
                    sbg.getState(level).init(gc, sbg);

                    sbg.enterState(level);

                } catch (SlickException ex)
                {
                    Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }



        } catch (IOException ex)
        {
            Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally
        {
            if (br != null)
            {
                try
                {
                    br.close();
                } catch (IOException ex)
                {
                    Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
