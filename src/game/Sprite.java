package game;

import org.newdawn.slick.Image;

/**
 *
 * @author Danny Cao
 */
public abstract class Sprite
{

    private float x, y;
    private int width, height;
    private Image image;

    public Sprite(Image image, float x, float y, int width, int height)
    {
        this.image = image;

        this.x = x;
        this.y = y;

        this.width = width;
        this.height = height;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setX(float x)
    {
        this.x = x;
    }

    public void setY(float y)
    {
        this.y = y;
    }

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }

    public void setImage(Image image)
    {
        this.image = image;
    }

    public void setImageAlpha(float alpha)
    {
        image.setAlpha(alpha);
    }

    public Image getImage()
    {
        return image;
    }

    public abstract void update(float x, float y);

    public void draw()
    {
        image.draw(this.getX(), this.getY());
    }
}
