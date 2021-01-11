package serverboard;

import org.junit.jupiter.api.Test;
import shared.PlayerColor;

import static org.junit.jupiter.api.Assertions.*;

class FieldTest
{
    @Test
    void colorsTest()
    {
        Field f = new Field(PlayerColor.BLUE, PlayerColor.VIOLET, PlayerColor.BLUE, true);
        assertEquals(f.getCurrentColor(), PlayerColor.BLUE);
        assertEquals(f.getNativeColor(), PlayerColor.VIOLET);
        assertEquals(f.getTargetColor(), PlayerColor.BLUE);
    }
}