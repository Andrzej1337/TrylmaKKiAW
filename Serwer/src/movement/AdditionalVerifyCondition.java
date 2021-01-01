package movement;

/**
 * Klasa wspomagająca dla MovementStrategy, umożliwia dodawanie dodatkowych warunków weryfikacji ruchu
 */

public interface AdditionalVerifyCondition
{
    /**
     * Zwraca wynik dodatkowej weryfikacji
     */
    boolean verify();
}
