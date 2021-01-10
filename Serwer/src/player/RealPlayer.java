package player;

import shared.PlayerColor;

import java.net.Socket;

/**
 * Reprezentuje realnego gracza na serwerze
 */
public class RealPlayer
{
    private boolean finished;
    PlayerColor color;
    private CommunicationManager communicationManager;

    public RealPlayer(Socket socket, PlayerColor color) throws Exception
    {
        this.color = color;
        communicationManager = new CommunicationManager(socket);
    }

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


    public void sendCommand(String command)
    {
        communicationManager.writeLine( command );
    }


    public String readResponse() throws PlayerLeftException
    {
        try
        {
            return communicationManager.readLine();
        }
        catch( Exception ignored )
        {
            throw new PlayerLeftException( color.toString() );
        }
    }
}
