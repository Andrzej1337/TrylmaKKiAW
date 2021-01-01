package movement;

/**
 * Dodatkowy warunek do weryfikacji poprawności ruchu.
 * Reprezentuje zasadę: "Po skoku można wykonać kolejny skok"
 */
public class JumpStatusVerifyCondition implements AdditionalVerifyCondition
{
    /**
     * przyjmuje wartości zwracane przez funkcję BasicMovementStrategyVerify {-1,0,1,2}
     */
    private int status;

    public JumpStatusVerifyCondition(int status)
    {
        this.status = status;
    }

    /**
     * weryfikuje jaki ruch został wykonany. Zwraca false jeśli był to ruch przeskakujący (wykorzystanie w verifyMove)
     */
    public boolean verify()
    {
        return status != 2;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

}
