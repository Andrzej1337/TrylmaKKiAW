package serverboard;

import shared.PlayerColor;

/**
 * Reprezentuje pojedyncze pole i pionka na nim stojącego
 */
public class Field
{
    /**
     * kolor pionka na polu
     */
    private PlayerColor currentColor;
    /**
     * kolor bazy startowej
     */
    private PlayerColor nativeColor;
    /**
     * kolor mety
     */
    private PlayerColor targetColor;
    private boolean playable;

    public Field(){}

    public Field(PlayerColor currentColor, PlayerColor nativeColor, PlayerColor targetColor, boolean playable)
    {
        this.currentColor = currentColor;
        this.nativeColor = nativeColor;
        this.targetColor = targetColor;
        this.playable = playable;
    }

    public PlayerColor getCurrentColor()
    {
        return currentColor;
    }

    void setCurrentColor(PlayerColor currentColor)
    {
        this.currentColor = currentColor;
    }

    PlayerColor getNativeColor()
    {
        return nativeColor;
    }

    public PlayerColor getTargetColor()
    {
        return targetColor;
    }

    public boolean isPlayable()
    {
        return playable;
    }

}
