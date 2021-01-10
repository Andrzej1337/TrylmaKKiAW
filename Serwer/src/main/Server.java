package main;

import shared.PlayerColor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class
Server
{
    private ServerSocket serverSocket;
    private List<Socket> playerSockets;
    private Match match;

    private int numberOfRealPlayers = 1;


    Server( int port ) throws Exception
    {
        System.out.println("Uruchamianie serwera...");
        try
        {
            serverSocket = new ServerSocket(port);
        } catch (Exception e)
        {
            throw new Exception("Nie można uzyskać dostępu to portu " + port);
        }
    }

    void run()
    {
        //noinspection InfiniteLoopStatement
        while( true )
        {
            System.out.println("Domyślnie mecz uruchomi się za 10 sekund z ustawieniami:");
            System.out.println( "Liczba graczy . . . " + numberOfRealPlayers );

            System.out.println();
            System.out.println( "Wciśnij ENTER aby zmienić liczbę graczy" );
            waitForInput();
            try
            {
                startMatch( numberOfRealPlayers );
            }
            catch( Exception e )
            {
                System.out.println("BŁĄD: " + e.getMessage());
            }

            closeAllSockets();
            playerSockets = null;
        }
    }

    private void startMatch( int numberOfRealPlayers ) throws Exception
    {
        System.out.println( "Rozpoczynanie meczu: " + numberOfRealPlayers + " graczy" );
        int numberOfRealPlayers = numberOfRealPlayers;
        int numberOfAvailableColors = PlayerColor.values().length - 1;

        if( numberOfRealPlayers < 1 || numberOfRealPlayers > numberOfAvailableColors )
        {
            throw new RuntimeException( "Nieprawidłowe parametry dot. liczby graczy" );
        }
        else
        {
            createMatch( numberOfRealPlayers );
            match.start();
        }

    }

    private void createMatch( int numberOfRealPlayers ) throws Exception
    {
        connectPlayers( numberOfRealPlayers );
        match = new Match( playerSockets );
    }

    private void connectPlayers( int numberOfPlayersToConnect ) throws Exception
    {
        playerSockets = new ArrayList<>();
        try
        {
            for( int i = 0; i < numberOfPlayersToConnect; i++ )
            {
                System.out.println( "Oczekiwanie na gracza nr " + i );
                playerSockets.add( serverSocket.accept() );
            }
        }
        catch( Exception e )
        {
            throw new Exception( "Wystąpił błąd przy próbie połączenia z klientem: " + e.getMessage() );
        }
    }

    private void waitForInput()
    {
        int seconds = 10;
        BufferedReader in = new BufferedReader( new InputStreamReader( System.in ) );
        long startTime = System.currentTimeMillis();
        try
        {
            //noinspection StatementWithEmptyBody
            while( ( System.currentTimeMillis() - startTime ) < seconds * 1000 && !in.ready() );
            if( in.ready() )
            {
                readNumberOfPlayers();
            }
        }
        catch( Exception ignored )
        {
            System.err.println( "Błąd strumienia wejścia!" );
            System.exit( -1 );
        }
    }

    private void readNumberOfPlayers()
    {
        boolean inputCorrect;
        do
        {
            inputCorrect = true;
            System.out.println( "Podaj: liczba graczy" );
            Scanner scanner = new Scanner( System.in );

            int newNumberOfRealPlayers;
            try
            {
                newNumberOfRealPlayers = scanner.nextInt();
                int total = newNumberOfRealPlayers;
                if( total >= 1 && total <= 6 )
                {
                    numberOfRealPlayers = newNumberOfRealPlayers;

                }
            }
            catch( Exception e )
            {
                System.out.println( "Podano niepoprawne wartości." );
                inputCorrect = false;
            }
        } while( !inputCorrect );
    }

    private void closeAllSockets()
    {
        for( Socket socket : playerSockets )
        {
            try
            {
                socket.close();
            }
            catch( Exception ignored ){}
        }
    }
}
