package movement;

import serverboard.Board;

/**
 * Interfejs obsługujący zasady poruszania się pionkami po planszy
 */
public interface MovementStrategy
{
    /**
     * Weryfikuje ruch z pola (x1,y1) na pole (x2,y2) dla danej planszy. Może przyjmować dodatkowe warunki.
     */
    int verifyMove(Board board, int x1, int y1, int x2, int y2, AdditionalVerifyCondition[] additionalVerifyConditions);

    /**
     * Wykonuje ruch z pola(x1,y1) na pole (x2,y2) dla danej planszy
     */
    Board makeMove(Board board, int x1, int y1, int x2, int y2);
}
