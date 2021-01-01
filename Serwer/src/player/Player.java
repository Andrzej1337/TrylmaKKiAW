package player;

import shared.PlayerColor;

/**
 * Reprezentuje postać gracza na serwerze
 */
public abstract class Player
{
    PlayerColor color;
    private boolean finished;

    Player()
    {

    }

    /**
     * Wysyła komendę do gracza
     */
    public abstract void sendCommand(String command);

    /**
     * Odczytuje komendę wysłaną przez gracza
     */
    public abstract String readResponse() throws PlayerLeftException;

    public PlayerColor getColor()
    {
        return color;
    }

    public void setFinished(boolean status)
    {
        this.finished = status;
    }

    public boolean isFinished()
    {
        return finished;
    }
}
