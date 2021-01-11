package movement;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PreviousPawnVerifyConditionTest
{

    @Test
    void verify()
    {
        PreviousPawnVerifyCondition p = new PreviousPawnVerifyCondition();
        p.setPreviousXY(1, 1);
        p.setCurrentXY(1, 1);
        assertFalse(p.verify());
        p.setCurrentXY(2, 2);
        assertTrue(p.verify());
    }

}