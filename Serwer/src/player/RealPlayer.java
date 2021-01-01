package player;

import shared.PlayerColor;

import java.net.Socket;

/**
 * Reprezentuje realnego gracza (nie bota) na serwerze
 */
public class RealPlayer extends Player
{
    private CommunicationManager communicationManager;

    public RealPlayer(Socket socket, PlayerColor color) throws Exception
    {
        this.color = color;
        communicationManager = new CommunicationManager(socket);
    }

    @Override
    public void sendCommand(String command)
    {
        communicationManager.writeLine( command );
    }

    @Override
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
