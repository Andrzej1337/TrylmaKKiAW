package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Klasa obsługująca komunikację z serwerem.
 */
class CommunicationManager
{
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    CommunicationManager( String host, int port ) throws Exception
    {
        try
        {
            socket = new Socket( host, port );
            out = new PrintWriter( socket.getOutputStream(), true );
            in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
        }
        catch( Exception e )
        {
            throw new Exception( "Nie można nawiązać połączenia z serwerem" );
        }
    }

    /**
     * Odebranie komunikatu od serwera
     * @return komunikat w odpowiednim formacie
     * @throws Exception zerwano połączenie
     */
    synchronized String readLine() throws Exception
    {
        try
        {
            return in.readLine();
        }
        catch( Exception e )
        {
            throw new Exception( "Utracono połączenie z serwerem" );
        }
    }

    /**
     * Wysłanie komunikatu do serwera
     * @param line komunikat w odpowiednim formacie
     */
    void writeLine( String line )
    {
        out.println( line );
    }
}
