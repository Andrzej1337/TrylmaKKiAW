package main;

import movement.*;


import player.PlayerLeftException;
import player.RealPlayer;
import serverboard.ClassicBoardFactory;
import shared.Coord;
import shared.PlayerColor;
import shared.Response;
import shared.ResponseInterpreter;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class Match
{
    private GameMaster gameMaster;
    private List<RealPlayer> players;
    private PlayerColor[] availableColors;

    private JumpStatusVerifyCondition jumpStatus;
    private PreviousPawnVerifyCondition previousPawn;
    private AdditionalVerifyCondition[] conditions;
    private int moveDistance = 0;

    private boolean turnFinished;
    private int place;

    Match( List<Socket> playerSockets) throws Exception
    {
        gameMaster = new GameMaster(new BasicMovementStrategy(), new ClassicBoardFactory());
        players = new ArrayList<>();

        int numberOfRealPlayers = playerSockets.size();



        System.out.print("TOTAL: " + numberOfRealPlayers);
        gameMaster.initializeBoard(numberOfRealPlayers);
        availableColors = gameMaster.getPossibleColorsForPlayers( numberOfRealPlayers );

        turnFinished = true;
        place = 1;

        addRealPlayers( playerSockets );
        }

    private void addRealPlayers( List<Socket> playerSockets ) throws Exception
    {
        int numberOfRealPlayers = playerSockets.size();

        for( int i = 0; i < numberOfRealPlayers; i++ )
        {
            System.out.print("Dodano gracz");
            players.add( new RealPlayer( playerSockets.get( i ), availableColors[ i ] ) );
        }
    }


    void start()
    {
        try
        {
            sendWelcomeToPlayers();
            sendStartBoardToPlayers();
            play();
        }
        catch( PlayerLeftException e )
        {
            endMatchWithError( "Jeden z graczy opuścił grę" );
        }
        catch( Exception e )
        {
            System.err.println( "Nieoczekiwany błąd: " + e.getMessage() );
            e.printStackTrace();
            endMatchWithError( "Wystąpił problem z połączeniem" );
        }
    }

    private void sendWelcomeToPlayers()
    {
        for( int i = 0; i < players.size(); i++ )
        {
            players.get( i ).sendCommand( "WELCOME " + availableColors[ i ] );
        }
    }

    private void sendStartBoardToPlayers()
    {
        CommandBuilder commandBuilder = new CommandBuilder();
        commandBuilder.addCommand( "START" );
        commandBuilder.addCommand( "BOARD", gameMaster.getBoardAsString() );
        sendToAll( commandBuilder.getCommand() );
    }

    private void play() throws Exception
    {
        int indexOfPlayerHavingTurn = 0;
        do
        {
            playTurnForPlayer( players.get( indexOfPlayerHavingTurn ) );
            indexOfPlayerHavingTurn = getNextPlayerIndex( indexOfPlayerHavingTurn );
        } while( !allPlayersFinished() );
        System.out.println("Koniec meczu");
    }

    private void playTurnForPlayer( RealPlayer player ) throws PlayerLeftException
    {
        setDefaultSettingsForNewTurn();
        player.sendCommand( "YOU" );
        System.out.println("Turę zaczyna gracz: " + player.getColor().toString());
        readResponsesAndExecute(player);
    }

    private void setDefaultSettingsForNewTurn()
    {
        jumpStatus = new JumpStatusVerifyCondition( 0 );
        previousPawn = new PreviousPawnVerifyCondition();
        conditions = new AdditionalVerifyCondition[]{ jumpStatus, previousPawn };

        turnFinished = false;
    }

    private void readResponsesAndExecute(RealPlayer player) throws PlayerLeftException
    {
        do
        {
            Response response = readProperResponseFromPlayer( player );
            executeResponse( player, response );
        } while( !turnFinished );
    }

    private Response readProperResponseFromPlayer(RealPlayer player ) throws PlayerLeftException
    {
        System.out.print("Oczekiwanie na odpowiedź od gracza... ");
        String line = player.readResponse();
        System.out.println("odebrano: " + line);
        Response[] responses = ResponseInterpreter.getResponses( line );
        if( responses.length != 1 )
        {
            System.err.println("Odebrano niepoprawny komunikat od gracza " + player.getColor().toString());
        }
        return responses[0];
    }

    private void executeResponse( RealPlayer player, Response response )
    {
        String responseType = response.getCode();
        switch( responseType )
        {
        case "SKIP":
            sendStopAndFinishTurn( player );
            break;
        case "CLUES":
            executeCluesResponse( player, response );
            break;
        case "MOVE":
            executeMoveResponse( player, response );
            break;
        default:
            sendNokAndPrintIncorrectResponse( player );
        }
    }

    private void sendStopAndFinishTurn(RealPlayer player )
    {
        player.sendCommand( "STOP" );
        turnFinished = true;
    }

    private void executeCluesResponse(RealPlayer player, Response response )
    {
        boolean correctCluesResponse = response.getCode().equals( "CLUES" ) && response.getNumbers().length == 2;
        if( correctCluesResponse )
        {
            int x = response.getNumbers()[ 0 ];
            int y = response.getNumbers()[ 1 ];
            sendClues( player, x, y );
        }
        else
        {
            sendNokAndPrintIncorrectResponse( player );
        }
    }

    private void sendClues(RealPlayer player, int x, int y )
    {
        ((PreviousPawnVerifyCondition)conditions[1]).setCurrentXY( x, y );

        List<Coord> possibleMoves = gameMaster.getPossibleMovesForPos( x, y, conditions );
        String command = getCluesCommand( possibleMoves );

        player.sendCommand( command );
    }

    private void executeMoveResponse(RealPlayer player, Response response )
    {
        boolean correctMoveResponse = response.getCode().equals( "MOVE" ) && response.getNumbers().length == 4;
        if( correctMoveResponse )
        {
            int fromX = response.getNumbers()[ 0 ];
            int fromY = response.getNumbers()[ 1 ];
            int toX   = response.getNumbers()[ 2 ];
            int toY   = response.getNumbers()[ 3 ];
            verifyMoveAndExecute( player, fromX, fromY, toX, toY );
        }
        else
        {
            sendNokAndPrintIncorrectResponse( player );
        }
    }

    private void verifyMoveAndExecute(RealPlayer player, int fromX, int fromY, int toX, int toY )
    {
        previousPawn.setCurrentXY( fromX, fromY );

        moveDistance = gameMaster.verifyMove( fromX, fromY, toX, toY, conditions );
        if( moveDistance == 0 )
        {
            System.out.println( "Ruch niepoprawny" );
            player.sendCommand( "NOK" );
        }
        else
        {
            makeMove( player, fromX, fromY, toX, toY );
        }
    }

    private void makeMove(RealPlayer player, int fromX, int fromY, int toX, int toY )
    {
        jumpStatus.setStatus( moveDistance );
        previousPawn.setPreviousXY( toX, toY );
        gameMaster.makeMove( fromX, fromY, toX, toY );
        boolean playerFinished = gameMaster.isWinner( player.getColor() );

        if( playerFinished )
        {
            makePlayerFinishedAndSendResponses( player );
            turnFinished = true;
        }
        else if( moveDistance == 1 )
        {
            sendResponsesAfterShortJump( player );
            turnFinished = true;
        }
        else
        {
            sendResponsesAfterLongJump( player );
        }
    }

    private void makePlayerFinishedAndSendResponses(RealPlayer player )
    {
        System.out.println( "Gracz " + player.getColor().toString() + " zakończył na miejscu " + place );
        player.sendCommand( "END " + place );
        player.setFinished( true );
        place++;
        sendToAll( "BOARD " + gameMaster.getBoardAsString() );
    }

    private void sendResponsesAfterShortJump(RealPlayer player )
    {
        System.out.println("Wykonano krótki ruch. Koniec tury");

        player.sendCommand( "OK@STOP" );
        sendToAll( "BOARD " + gameMaster.getBoardAsString() );
    }

    private void sendResponsesAfterLongJump(RealPlayer player )
    {
        System.out.println("Wykonano skok nad pionkiem");

        // wysyłanie OK@BOARD
        CommandBuilder commandBuilder = new CommandBuilder();
        commandBuilder.addCommand( "OK" );
        commandBuilder.addCommand( "BOARD", gameMaster.getBoardAsString() );
        player.sendCommand( commandBuilder.getCommand() );

        String msg = "BOARD " + gameMaster.getBoardAsString();
        sendToAllExceptOne( msg, player );
    }

    private void sendNokAndPrintIncorrectResponse(RealPlayer player )
    {
        System.err.println( "Otrzymano nieprawidłowy komunikat od gracza " + player.getColor().toString() );
        player.sendCommand( "NOK" );
    }

    private boolean allPlayersFinished()
    {
        for( RealPlayer player : players )
        {
            if( !player.isFinished() )
                return false;
        }
        return true;
    }

    /**
     * Zwraca id gracza, którego tura nastąpi po 'activePlayer'.
     * @param activePlayer id gracza, które tura ostatnio trwała
     * @return id kolejnego gracza w kolejne lub -1 gdy wszyscy gracze skończyli mecz
     */
    private int getNextPlayerIndex( int activePlayer )
    {
        int nextPlayer = activePlayer;
        boolean nextPlayerFinished;
        do
        {
            nextPlayer++;
            if( nextPlayer >= players.size() )
            {
                nextPlayer = 0;
            }

            nextPlayerFinished = players.get( nextPlayer ).isFinished();

            if( nextPlayerFinished ) // sprawdź czy pozostali jeszcze jacyś gracze
            {
                int howManyFinished = 0;
                for( RealPlayer p : players )
                {
                    if( p.isFinished() )
                        howManyFinished++;
                }
                if( howManyFinished == players.size() )
                {
                    return -1;
                }
            }
        } while( nextPlayerFinished ); // dopóki nie znajdziesz gracza, który jeszcze nie skończył

        return nextPlayer;
    }

    /**
     * Wysyła komendę do wszystkich graczy z tablicy 'players'
     */
    private void sendToAll(String command)
    {
        for (RealPlayer player : players)
        {
            player.sendCommand(command);
        }
    }

    /**
     * Wysyła komendę do wszystkich graczy z tablicy 'players',
     * pomijając gracza 'excluded'
     */
    private void sendToAllExceptOne(String command, RealPlayer excluded)
    {
        for (RealPlayer player : players)
        {
            if (player != excluded)
                player.sendCommand(command);
        }
    }

    private String getCluesCommand( List<Coord> possibleMoves )
    {
        StringBuilder sb = new StringBuilder();
        for( Coord c : possibleMoves )
        {
            if( !sb.toString().equals( "" ) )
                sb.append( " " );
            sb.append( c.getX() ).append( " " ).append( c.getY() );
        }
        CommandBuilder commandBuilder = new CommandBuilder();
        commandBuilder.addCommand( "CLUES", sb.toString() );

        return commandBuilder.getCommand();
    }

    private void endMatchWithError( String message )
    {
        System.out.println("Przerwanie meczu: " + message);
        for( RealPlayer player : players )
        {
            try
            {
                player.sendCommand( "ERROR " + message );
            }
            catch( Exception ignored ){}
        }

        players.clear();
        players = null;
    }
}
