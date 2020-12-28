package main;

import board.Board;
import javafx.application.Platform;
import shared.Coord;
import shared.PlayerColor;
import shared.Response;
import shared.ResponseInterpreter;

import java.util.function.Consumer;

class Player
{
    private Board board;
    private PlayerColor color;
    private CommunicationManager communicationManager;
    private Consumer<Boolean> blockGUI;
    private Consumer<String> printSuccess;
    private Consumer<String> printAlert;
    private Consumer<String> printError;

    private boolean yourTurn;
    private boolean finished;

    Player( CommunicationManager cm, Board board,
            Consumer<Boolean> blockGUI,
            Consumer<String> printSuccess,
            Consumer<String> printAlert,
            Consumer<String> printError )
    {
        this.communicationManager = cm;
        this.board = board;
        this.color = PlayerColor.RED;
        this.blockGUI = blockGUI;

        this.printSuccess = printSuccess;
        this.printAlert = printAlert;
        this.printError = printError;
    }

    void startMatch()
    {
        yourTurn = false;
        finished = false;
        printSuccess.accept( "Połączono. Oczekiwanie na pozostałych graczy..." );

        blockGUIandReadResponses();
    }

    /**
     * Obsługa kliknięcia w pole. Zaznaczanie pola, wykonywanie skoków itp.
     */
    void handleClickOnField( int x, int y )
    {
        boolean clickedOwnPiece = !board.isFieldEmpty( x, y ) && board.getColor( x, y ).equals( color );
        if( clickedOwnPiece )
        {
            selectOwnPiece( x, y );
        }
        else
        {
            clickField( x, y );
        }
    }

    private void selectOwnPiece( int x, int y )
    {
        Coord selected = board.getCoordOfSelectedField();
        boolean clickedAlreadySelectedPiece = selected != null && selected.getX() == x && selected.getY() == y;

        if( clickedAlreadySelectedPiece )
        {
            board.deselectAndUnmarkAllFields();
            sendSkipRequest();
        }
        else
        {
            System.out.println("Pytam o CLUES");
            board.selectField( x, y );
            askServerForClues( x, y );
        }

        blockGUIandReadResponses();
    }

    private void clickField( int x, int y )
    {
        boolean clickedEmptyField = board.isFieldEmpty( x, y );
        boolean isSomePieceSelected = board.getCoordOfSelectedField() != null;
        if( clickedEmptyField && isSomePieceSelected )
        {
            sendMoveRequest( x, y );
        }
        else // kliknięto gdzieś tam, odznacz pole (jeśli było zaznaczone)
        {
            board.deselectAndUnmarkAllFields();
        }
    }

    private void sendMoveRequest( int x, int y )
    {
        moveSelectedTo( x, y );
        board.deselectAndUnmarkAllFields();

        blockGUIandReadResponses();
    }

    private void moveSelectedTo( int destX, int destY )
    {
        Coord selected = board.getCoordOfSelectedField();

        int fromX = selected.getX();
        int fromY = selected.getY();

        sendMoveRequest( fromX, fromY, destX, destY );
    }

    private void sendMoveRequest( int fromX, int fromY, int toX, int toY )
    {
        String msg = "MOVE " + fromX + " " + fromY + " " + toX + " " + toY;
        communicationManager.writeLine( msg );
    }

    /**
     * Wysyła do serwera komunikat o zakończeniu tury
     */
    private void sendSkipRequest()
    {
        communicationManager.writeLine( "SKIP" );
    }

    private void blockGUIandReadResponses()
    {
        blockGUI.accept( true );
        Thread thread = new Thread( this:: readResponsesUntilYourTurn );
        thread.setDaemon( true );
        thread.start();
    }

    private void readResponsesUntilYourTurn()
    {
        do
        {
            try
            {
                waitForResponseAndExecute();
            }
            catch( Exception e ) // utracono połączenie z serwerem
            {
                if( !finished )
                    printError.accept( e.getMessage() );
                return;
            }
        } while( !yourTurn );

        blockGUI.accept( false );
    }

    private void waitForResponseAndExecute() throws Exception
    {
        String line = communicationManager.readLine();

        Response[] responses = ResponseInterpreter.getResponses( line );

        executeAllResponses( responses );
    }

    private void executeAllResponses( Response[] responses )
    {
        for( Response response : responses )
        {
            System.out.println( "Odebrano: " + response.getCode() );
            executeResponse( response );
        }
    }

    private void executeResponse( Response response )
    {
        switch( response.getCode() )
        {
            case "WELCOME":
                executeWelcomeResponse( response );
                yourTurn = false;
                printAlert.accept( "Grasz jako: " + color.toString() + ". Trwa tura innego gracza..." );
                break;
            case "YOU":
                printSuccess.accept( "("+color.toString()+") Twoja tura" );
                yourTurn = true;
                break;
            case "BOARD":
                Platform.runLater( () -> loadBoard( response ) );
                break;
            case "CLUES":
                Platform.runLater( () -> loadClues( response ) );
                break;
            case "END":
                printSuccess.accept( "Koniec meczu. Zajmujesz " + response.getNumbers()[ 0 ] + " miejsce" );
                yourTurn = false;
                finished = true;
                break;
            case "OK":
                System.out.println( "Ruch poprawny" );
                break;
            case "NOK":
                //printAlert.accept( "Ruch niepoprawny!" );
                System.out.println( "Ruch NIE poprawny" );
                break;
            case "STOP":
                printAlert.accept( "Trwa tura innego gracza..." );
                yourTurn = false;
                break;
            case "ERROR":
                executeErrorResponse( response );
        }
    }

    private void executeErrorResponse( Response response )
    {
        String errorMessage = String.join( " ", response.getWords() );
        throw new RuntimeException( "Przerwano mecz. " + errorMessage );
    }

    private void askServerForClues( int x, int y )
    {
        communicationManager.writeLine( "CLUES " + x + " " + y );
    }

    private void executeWelcomeResponse( Response response )
    {
        boolean incorrectWelcomeMessage =
                !response.getCode().equals( "WELCOME" )
                        || response.getWords().length != 1;

        if( incorrectWelcomeMessage )
        {
            System.err.println( "Otrzymano niepoprawny komunikat." );
        }

        readPlayerColorFromResponse( response );
    }

    private void readPlayerColorFromResponse( Response welcomeResponse )
    {

        color = PlayerColor.valueOf( welcomeResponse.getWords()[ 0 ] );

        System.out.println( "Jestem graczem: " + color.toString() );
    }

    private void loadBoard( Response response )
    {
        if( response.getCode().equals( "BOARD" ) )
        {
            // czyszczenie planszy
            board.removeAllPieces();

            // wypełnianie planszy pionkami
            int coordNum = 0;
            for( String word : response.getWords() )
            {
                PlayerColor color = PlayerColor.valueOf( word );
                int x = response.getNumbers()[ coordNum ];
                int y = response.getNumbers()[ coordNum+1 ];

                board.addPiece( x, y, color );
                coordNum += 2;
            }
        }
    }

    private void loadClues( Response response )
    {
        if( response.getCode().equals( "CLUES" ) )
        {
            board.unmarkAllPossibleJumpTargets();

            int numbers[] = response.getNumbers();

            System.out.print("Odebrano: ");
            for( int n : numbers )
            {
                System.out.print( n + " " );
            }
            System.out.println();

            for( int i = 0; i < numbers.length - 1; i += 2 )
            {
                int x = numbers[ i ];
                int y = numbers[ i+1 ];
                board.markFieldAsPossibleJumpTarget( x, y );
            }
        }
    }
}

