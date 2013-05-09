package game;

import java.util.HashMap;
import java.util.Map;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

/**
 * A Resource Manager.
 *
 * @author Danny Cao
 */
public class ResourceManager
{

    private Map<String, Object> resources = null;
    private String dir = null;

    /**
     * Create a ResourceManager that looks for files located in the resourceDir.
     *
     * @param resourceDir
     */
    public ResourceManager(String resourceDir)
    {
        resources = new HashMap();
        dir = resourceDir;
    }

    /**
     * Adds an image resource to the resource manager.
     *
     * @param filename in the form of "name.ext"
     */
    public void addImage(String filename) throws SlickException
    {

        Image img = new Image(dir + filename);

        resources.put(filename, img);
    }

    /**
     * Retrieves an image resource.
     *
     * @param filename in the form of "name.ext"
     */
    public Image getImage(String filename)
    {
        // Needs to do error checking
        // What does an invalid cast return? null?
        // if it returns null, then check for null at caller level?
        // or throw exception?
        return (Image) resources.get(filename);
    }

    public void addSpriteSheet(String filename, int tileW, int tileH) throws SlickException
    {
        SpriteSheet ss = new SpriteSheet(new Image(dir + filename), tileW, tileH);
        resources.put(filename, ss);
    }

    public SpriteSheet getSpriteSheet(String filename)
    {
        // error checking? refer to getImage method comments.
        return (SpriteSheet) resources.get(filename);
    }
}
