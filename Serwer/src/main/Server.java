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
    private int numberOfBots = 0;

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
            System.out.println( "Liczba botów  . . . " + numberOfBots );
            System.out.println();
            System.out.println( "Wciśnij ENTER aby zmienić liczbę graczy i botów" );
            waitForInput();
            try
            {
                startMatch( numberOfRealPlayers, numberOfBots );
            }
            catch( Exception e )
            {
                System.out.println("BŁĄD: " + e.getMessage());
            }

            closeAllSockets();
            playerSockets = null;
        }
    }

    private void startMatch( int numberOfRealPlayers, int numberOfBots ) throws Exception
    {
        System.out.println( "Rozpoczynanie meczu: " + numberOfRealPlayers + " graczy, " + numberOfBots + " botów" );
        int totalNumberOfPlayers = numberOfRealPlayers + numberOfBots;
        int numberOfAvailableColors = PlayerColor.values().length - 1;

        if( totalNumberOfPlayers < 1 || totalNumberOfPlayers > numberOfAvailableColors )
        {
            throw new RuntimeException( "Nieprawidłowe parametry dot. liczby graczy" );
        }
        else
        {
            createMatch( numberOfRealPlayers, numberOfBots );
            match.start();
        }

    }

    private void createMatch( int numberOfRealPlayers, int numberOfBots ) throws Exception
    {
        connectPlayers( numberOfRealPlayers );
        match = new Match( playerSockets, numberOfBots );
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
                readNumberOfPlayersAndBots();
            }
        }
        catch( Exception ignored )
        {
            System.err.println( "Błąd strumienia wejścia!" );
            System.exit( -1 );
        }
    }

    private void readNumberOfPlayersAndBots()
    {
        boolean inputCorrect;
        do
        {
            inputCorrect = true;
            System.out.println( "Podaj: liczba graczy liczba botów" );
            Scanner scanner = new Scanner( System.in );

            int newNumberOfRealPlayers;
            int newNumberOfBots;
            try
            {
                newNumberOfRealPlayers = scanner.nextInt();
                newNumberOfBots = scanner.nextInt();
                int total = newNumberOfRealPlayers + newNumberOfBots;
                if( total >= 1 && total <= 6 )
                {
                    numberOfRealPlayers = newNumberOfRealPlayers;
                    numberOfBots = newNumberOfBots;
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
