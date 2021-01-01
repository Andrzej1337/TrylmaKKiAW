package movement;

import serverboard.Board;
import serverboard.BoardFactory;
import serverboard.ClassicBoard;
import serverboard.UnplayableFieldException;
import shared.Coord;
import shared.PlayerColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa reprezentuje mistrza gry, który pośredniczy pomiędzy serwerem a planszą
 */
public class GameMaster
{
    private Board board;
    private MovementStrategy movementStrategy;
    private BoardFactory boardFactory;

    public GameMaster(MovementStrategy ms, BoardFactory bf)
    {
        board = new ClassicBoard();
        movementStrategy = ms;
        boardFactory = bf;
    }

    public void initializeBoard(int numberOfPlayers)
    {
        board = boardFactory.createBoard( numberOfPlayers );
    }

    public PlayerColor[] getPossibleColorsForPlayers( int numberOfPlayers )
    {
        switch( numberOfPlayers )
        {
        case 1:
            return new PlayerColor[]{PlayerColor.RED};
        case 2:
            return new PlayerColor[]{PlayerColor.RED, PlayerColor.GREEN};
        case 3:
            return new PlayerColor[]{PlayerColor.RED, PlayerColor.BLUE, PlayerColor.YELLOW};
        case 4:
            return new PlayerColor[]{PlayerColor.BLUE, PlayerColor.YELLOW, PlayerColor.VIOLET, PlayerColor.ORANGE};
        case 6:
            return new PlayerColor[]{PlayerColor.RED, PlayerColor.GREEN, PlayerColor.BLUE, PlayerColor.ORANGE, PlayerColor.YELLOW, PlayerColor.VIOLET};
        }
        throw new RuntimeException( "Podano nieprawidłową liczbę graczy: " + numberOfPlayers );
    }

    /**
     * weryfikuje poprawność ruchu z pola (x1,y1) na pole (x2,y2) na podstawie podanych zasad movementstrategy
     */

    public int verifyMove(int x1, int y1, int x2, int y2, AdditionalVerifyCondition[] additionalVerifyConditions)
    {
        return movementStrategy.verifyMove(board, x1, y1, x2, y2, additionalVerifyConditions);
    }

    /**
     * wykonuje ruch pionkiem z pola (x1,y1) na pole (x2,y2) na podstawie podanych zasad movementstrategy
     */

    public void makeMove(int x1, int y1, int x2, int y2)
    {
        board = movementStrategy.makeMove(board, x1, y1, x2, y2);
    }

    /**
     * Zwraca kolor pola(x,y)
     */

    public PlayerColor getColorAtPos(int x, int y)
    {
        try
        {
            return board.getColor(x, y);
        } catch (UnplayableFieldException ufexc)
        {
            return null;
        }
    }

    public List<Coord> getPossibleMovesForPos( int x, int y, AdditionalVerifyCondition[] additionalVerifyConditions )
    {
        int result; // rezultat funkcji verifyMove (jeśli 0, to ruch niepoprawny)
        List<Coord> possibleMoves = new ArrayList<>();
        List<Coord> nearbyCoords = board.getNearbyCoords( x, y );
        for( Coord coord : nearbyCoords )
        {
            result = verifyMove( x, y, coord.getX(), coord.getY(), additionalVerifyConditions );
            if( result != 0 )
                possibleMoves.add( coord );
        }

        return possibleMoves;
    }


    /**
     sprawdza czy gracz o podanym kolorze jest zwycięzcą
     */
    public boolean isWinner(PlayerColor color)
    {
        return board.isWinner(color);
    }

    /**
     * Zwraca ustawienie pionków na planszy w postaci stringa
     */
    public String getBoardAsString()
    {
        return board.getAsString();
    }

    public Board getBoard()
    {
        return board;
    }

}
