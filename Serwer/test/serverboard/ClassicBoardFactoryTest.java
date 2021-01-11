package serverboard;

import org.junit.jupiter.api.Test;

import shared.PlayerColor;

import static org.junit.jupiter.api.Assertions.*;


class ClassicBoardFactoryTest
{

    /**
     * Sprawdza czy niegrywalne pola pozostały niegrywalne
     */
    @Test
    void unplayableFieldTest()
    {
        Board board;
        ClassicBoardFactory classicBoardFactory = new ClassicBoardFactory();
        board = classicBoardFactory.createBoard(1);
        assertFalse(board.getField(1, 1).isPlayable());

    }

    /**
     * Sprawdza czy zostali utworzeni odpowiedni gracze
     */
    @Test
    void correctPlayerTest()
    {
        ClassicBoardFactory classicBoardFactory = new ClassicBoardFactory();
        Board board;


        board = classicBoardFactory.createBoard(1);
        assertEquals(board.getField(7, 17).getCurrentColor(), PlayerColor.RED);
        assertEquals(board.getField(7, 1).getCurrentColor(), PlayerColor.NONE);
        assertEquals(board.getField(1, 5).getCurrentColor(), PlayerColor.NONE);
        assertEquals(board.getField(2, 10).getCurrentColor(), PlayerColor.NONE);
        assertEquals(board.getField(10, 5).getCurrentColor(), PlayerColor.NONE);
        assertEquals(board.getField(11, 10).getCurrentColor(), PlayerColor.NONE);

        board = classicBoardFactory.createBoard(2);
        assertEquals(board.getField(7, 17).getCurrentColor(), PlayerColor.RED);
        assertEquals(board.getField(7, 1).getCurrentColor(), PlayerColor.GREEN);
        assertEquals(board.getField(1, 5).getCurrentColor(), PlayerColor.NONE);
        assertEquals(board.getField(2, 10).getCurrentColor(), PlayerColor.NONE);
        assertEquals(board.getField(10, 5).getCurrentColor(), PlayerColor.NONE);
        assertEquals(board.getField(11, 10).getCurrentColor(), PlayerColor.NONE);

        board = classicBoardFactory.createBoard(3);
        assertEquals(board.getField(7, 17).getCurrentColor(), PlayerColor.RED);
        assertEquals(board.getField(7, 1).getCurrentColor(), PlayerColor.NONE);
        assertEquals(board.getField(1, 5).getCurrentColor(), PlayerColor.BLUE);
        assertEquals(board.getField(2, 10).getCurrentColor(), PlayerColor.NONE);
        assertEquals(board.getField(10, 5).getCurrentColor(), PlayerColor.YELLOW);
        assertEquals(board.getField(11, 10).getCurrentColor(), PlayerColor.NONE);

        board = classicBoardFactory.createBoard(4);
        assertEquals(board.getField(7, 17).getCurrentColor(), PlayerColor.NONE);
        assertEquals(board.getField(7, 1).getCurrentColor(), PlayerColor.NONE);
        assertEquals(board.getField(1, 5).getCurrentColor(), PlayerColor.BLUE);
        assertEquals(board.getField(2, 10).getCurrentColor(), PlayerColor.VIOLET);
        assertEquals(board.getField(10, 5).getCurrentColor(), PlayerColor.YELLOW);
        assertEquals(board.getField(11, 10).getCurrentColor(), PlayerColor.ORANGE);

        board = classicBoardFactory.createBoard(6);
        assertEquals(board.getField(7, 17).getCurrentColor(), PlayerColor.RED);
        assertEquals(board.getField(7, 1).getCurrentColor(), PlayerColor.GREEN);
        assertEquals(board.getField(1, 5).getCurrentColor(), PlayerColor.BLUE);
        assertEquals(board.getField(2, 10).getCurrentColor(), PlayerColor.VIOLET);
        assertEquals(board.getField(10, 5).getCurrentColor(), PlayerColor.YELLOW);
        assertEquals(board.getField(11, 10).getCurrentColor(), PlayerColor.ORANGE);

    }
}