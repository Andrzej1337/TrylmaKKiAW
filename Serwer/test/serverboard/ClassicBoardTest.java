package serverboard;

import org.junit.jupiter.api.Test;
import serverboard.ClassicBoard;
import serverboard.Field;
import serverboard.UnplayableFieldException;
import shared.PlayerColor;

import static org.junit.jupiter.api.Assertions.*;

class ClassicBoardTest
{

    @Test
    void constructorTest()
    {
        ClassicBoard classicBoard = new ClassicBoard();
        Field field = classicBoard.getField(5, 5);
        assertFalse(field.isPlayable());
        assertNull(field.getCurrentColor());
        assertNull(field.getNativeColor());
        assertNull(field.getTargetColor());
    }

    @Test
    void throwUnplayableFieldException()
    {
        ClassicBoard classicBoard = new ClassicBoard();
        assertThrows(UnplayableFieldException.class, () -> classicBoard.addPiece(1, 1, PlayerColor.BLUE));
        assertThrows(UnplayableFieldException.class, () -> classicBoard.removePiece(1, 1));
        assertThrows(UnplayableFieldException.class, () -> classicBoard.getColor(1, 1));
    }

    @Test
    void addPieceTest()
    {
        ClassicBoard classicBoard = new ClassicBoard();
        //początkowo pole ma currentColor = NONE
        classicBoard.setField(1, 1, new Field( PlayerColor.NONE, PlayerColor.NONE, PlayerColor.NONE, true));
        try
        {
            //dodajemy pionek koloru GREEN
            classicBoard.addPiece(1, 1, PlayerColor.GREEN );
        } catch (UnplayableFieldException ex)
        {
            fail();
        }
        assertEquals(classicBoard.getField(1, 1).getCurrentColor(), PlayerColor.GREEN );
    }

    @Test
    void removePieceTest()
    {
        ClassicBoard classicBoard = new ClassicBoard();
        //początkowo pole ma currentColor = GREEN
        classicBoard.setField(1, 1, new Field(PlayerColor.GREEN, PlayerColor.NONE, PlayerColor.NONE, true));
        try
        {
            //usuwamy pionek
            classicBoard.removePiece(1, 1);
        } catch (UnplayableFieldException ex)
        {
            fail();
        }
        assertEquals(classicBoard.getField(1, 1).getCurrentColor(), PlayerColor.NONE);
    }
}