package board;

import javafx.scene.shape.Circle;
import org.junit.jupiter.api.Test;
import shared.Coord;
import shared.PlayerColor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest
{
    @Test
    void isEmpty_notExistingField()
    {
        Board board = createDummyBoard();

        assertTrue( board.isFieldEmpty( -1, -1 ) );
    }

    @Test
    void isEmpty()
    {
        Board board = createDummyBoard();

        assertTrue( board.isFieldEmpty( 0, 0 ) );
    }

    @Test
    void addPiece()
    {
        Board board = createDummyBoard();

        board.addPiece( 0, 1, PlayerColor.BLUE );
        assertFalse( board.isFieldEmpty( 0, 1 ) );
    }

    @Test
    void getColor_notExistingField()
    {
        Board board = createDummyBoard();

        assertEquals( PlayerColor.NONE, board.getColor( -1, -1 ) );
    }

    @Test
    void getColor()
    {
        Board board = createDummyBoard();

        assertEquals( PlayerColor.NONE, board.getColor( 1, 1 ) );

        board.addPiece( 1, 1, PlayerColor.ORANGE );
        assertEquals( PlayerColor.ORANGE, board.getColor( 1, 1 ) );
    }

    @Test
    void removeAllPieces()
    {
        Board board = createDummyBoard();
        board.addPiece( 0, 0, PlayerColor.GREEN );
        board.addPiece( 1, 1, PlayerColor.VIOLET );

        board.removeAllPieces();
        assertTrue( board.isFieldEmpty( 0, 0 ) );
        assertTrue( board.isFieldEmpty( 1, 1 ) );
    }

    @Test
    void select_notExistingField()
    {
        Board board = createDummyBoard();

        // Zaznaczenie nieistniejącego pola powinno nie rzucić wyjątku
        board.selectField( -1, -1 );
    }

    @Test
    void getCoordOfSelectedField()
    {
        Board board = createDummyBoard();

        assertNull( board.getCoordOfSelectedField() );

        board.selectField( 0, 0 );
        board.selectField( 1, 1 );
        assertEquals( new Coord( 1, 1 ), board.getCoordOfSelectedField() );
    }

    @Test
    void deselectAllFields()
    {
        Board board = createDummyBoard();

        assertNull( board.getCoordOfSelectedField() );

        board.selectField( 1, 1 );
        board.deselectAndUnmarkAllFields();
        assertNull( board.getCoordOfSelectedField() );
    }

    @Test
    void mark_notExistingField()
    {
        Board board = createDummyBoard();

        // Podświetlenie nieistniejącego pola powinno nie rzucić wyjątku
        board.markFieldAsPossibleJumpTarget( -1, -1 );
    }



    private Board createDummyBoard()
    {
        List<Field> fields = new ArrayList<>();

        fields.add( new Field( 0, 0, new Circle() ) );
        fields.add( new Field( 0, 1, new Circle() ) );
        fields.add( new Field( 0, 2, new Circle() ) );
        fields.add( new Field( 1, 0, new Circle() ) );
        fields.add( new Field( 1, 1, new Circle() ) );
        fields.add( new Field( 1, 2, new Circle() ) );

        return new Board( fields );
    }

}