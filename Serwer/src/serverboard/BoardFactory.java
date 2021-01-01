package serverboard;

/**
 * Fabryka klasy Board
 */
public interface BoardFactory
{
    Board createBoard(int numberOfPlayers);
}
