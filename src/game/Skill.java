package game;

import org.newdawn.slick.Image;

/**
 *
 * @author Danny Cao
 */
public class Skill extends NPC
{

    private SkillSet skillName = null;

    public Skill(SkillSet skillName)
    {
        this(skillName, null, 0, 0);
    }

    public Skill(SkillSet skillName, Image image)
    {
        this(skillName, image, 0, 0);
    }

    public Skill(SkillSet skillName, Image image, float x, float y)
    {
        super(image, x, y);

        this.skillName = skillName;
    }

    public SkillSet getName()
    {
        return skillName;
    }
}
