package movement;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JumpStatusVerifyConditionTest
{

    @Test
    void verify()
    {
        JumpStatusVerifyCondition jumpStatusVerifyCondition = new JumpStatusVerifyCondition(0);
        assertTrue(jumpStatusVerifyCondition.verify());
        jumpStatusVerifyCondition.setStatus(2);
        assertFalse(jumpStatusVerifyCondition.verify());
    }
}