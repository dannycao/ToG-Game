package game;

/**
 * Names of layers.
 *
 * @author Danny Cao
 */
public enum Layers
{

    GROUND, CLIFF, WATER, TREE, BUILDING, ROCK, PLAYER, MODIFIABLE;

    /**
     * Returns an all-lowercase version of the enums.
     *
     * @return an all-lowercase version of the enums.
     */
    @Override
    public String toString()
    {
        return this.name().toLowerCase();
    }
}
