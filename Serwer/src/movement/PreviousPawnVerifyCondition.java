package movement;

/**
 * Dodatkowy warunek do weryfikacji poprawności ruchu.
 * Reprezentuje zasadę: "Po skoku można wykonać kolejny skok TYM SAMYM PIONKIEM"
 * Przechowuje koordynaty pionka którym ostatnio ruszyliśmy i porównuje je do pionka którym chcemy ruszyć
 */
public class PreviousPawnVerifyCondition implements AdditionalVerifyCondition
{

    private int currentX;
    private int currentY;
    private int previousX;
    private int previousY;

    public PreviousPawnVerifyCondition()
    {

    }

    /**
     * zwraca true jeżeli koordynaty "poprzednie" różnią się od obecnych czyli wykonywany jest ruch innym pionkiem co poprzednio (nieprwidłowy)
     */

    @Override
    public boolean verify()
    {
        return currentX != previousX || currentY != previousY;
    }

    public void setCurrentXY(int x, int y)
    {
        this.currentX = x;
        this.currentY = y;
    }

    public void setPreviousXY(int x, int y)
    {
        this.previousX = x;
        this.previousY = y;
    }


}
