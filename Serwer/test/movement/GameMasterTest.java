package movement;

import org.junit.jupiter.api.Test;
import serverboard.ClassicBoardFactory;

import static org.junit.jupiter.api.Assertions.*;

class GameMasterTest
{

    @Test
    void initializeBoard_incorrectNumberOfPlayers()
    {
        GameMaster gm = createDummyGameMaster();

        assertThrows( RuntimeException.class, () -> gm.initializeBoard( -1 ) );
    }

    @Test
    void getPossibleColorsForPlayers_incorrectNumberOfPlayers()
    {
        GameMaster gm = createDummyGameMaster();

        assertThrows( RuntimeException.class, () -> gm.getPossibleColorsForPlayers( -1 ) );
    }

    private GameMaster createDummyGameMaster()
    {
        ClassicBoardFactory classicBoardFactory = new ClassicBoardFactory();
        BasicMovementStrategy basicMovementStrategy = new BasicMovementStrategy();

        return new GameMaster( basicMovementStrategy, classicBoardFactory );
    }
}