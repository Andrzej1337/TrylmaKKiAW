package board;

import javafx.scene.shape.Circle;
import org.junit.jupiter.api.Test;
import shared.PlayerColor;

import static org.junit.jupiter.api.Assertions.*;

class FieldTest
{
    @Test
    void getX()
    {
        Field field = new Field( 5, 10, null );
        assertEquals( 5, field.getX() );
    }

    @Test
    void getY()
    {
        Field field = new Field( 5, 10, null );
        assertEquals( 10, field.getY() );
    }

    @Test
    void getsetColor()
    {
        Field field = createDummyField();

        assertEquals( PlayerColor.NONE, field.getColor() );

        field.setColor( PlayerColor.RED );
        field.setColor( PlayerColor.GREEN );

        assertEquals( PlayerColor.GREEN, field.getColor() );
    }

    @Test
    void circleEquals()
    {
        Circle c1 = new Circle();
        Circle c2 = new Circle();
        Field field = new Field( 0, 0, c1 );

        assertFalse( field.circleEquals( c2 ) );
        assertFalse( field.circleEquals( null ) );

        assertTrue( field.circleEquals( c1 ) );
    }


    private Field createDummyField()
    {
        Circle circle = new Circle();
        return new Field( 0, 0, circle );
    }
}