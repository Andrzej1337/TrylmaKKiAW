package board;

import shared.Coord;
import shared.PlayerColor;

import java.util.List;

/**
 * Reprezentacja planszy wraz z polami i pionkami
 */
public class Board
{
    private List<Field> fields;
    private Field selectedField;

    public Board( List<Field> fields )
    {
        this.fields = fields;
    }

    public void removeAllPieces()
    {
        for( Field field : fields )
        {
            if( !field.isDisabled() )
                field.setColor( PlayerColor.NONE );
        }
    }

    public Coord getCoordOfSelectedField()
    {
        if( selectedField != null )
        {
            int x = selectedField.getX();
            int y = selectedField.getY();
            return new Coord( x, y );
        }
        else
        {
            return null;
        }
    }

    /**
     * Zaznaczenie pionka (pola) na pozycji (x, y).
     * Funkcja odznacza również uprzednio zaznaczone pole.
     */
    public void selectField( int x, int y )
    {
        deselectAllFields();
        selectedField = getField( x, y );

        if( selectedField != null )
        {
            selectedField.setSelected( true );
        }
    }

    public void deselectAndUnmarkAllFields()
    {
        deselectAllFields();
        unmarkAllPossibleJumpTargets();
        selectedField = null;
    }

    private void deselectAllFields()
    {
        for( Field field : fields )
            field.setSelected( false );
    }

    public void unmarkAllPossibleJumpTargets()
    {
        for( Field field : fields )
        {
            field.markAsPossibleJumpTarget( false );
        }
    }

    public void addPiece( int x, int y, PlayerColor color )
    {
        Field field = getField( x, y );
        if( field != null )
            field.setColor( color );
    }

    public boolean isFieldEmpty( int x, int y )
    {
        Field field = getField( x, y );
        if( field != null )
            return field.getColor() == PlayerColor.NONE;
        else
            return true;
    }

    /**
     * Zwraca kolor pionka na polu (x, y)
     * @return kolor pionka na (x, y) lub NONE, gdy pionka nie ma lub pole nie istnieje
     */
    public PlayerColor getColor( int x, int y )
    {
        Field field = getField( x, y );
        if( field != null )
            return field.getColor();
        else
            return PlayerColor.NONE;
    }

    /**
     * Podświetla pole (x, y)
     * Używane do podświetlania pól, na które można skoczyć.
     */
    public void markFieldAsPossibleJumpTarget( int x, int y )
    {
        Field field = getField( x, y );
        if( field != null )
        {
            field.markAsPossibleJumpTarget( true );
        }
    }

    /**
     * Zwraca pole Field znajdujące się na współrzędnych (x, y)
     * @return pole na (x, y) lub null, gdy pole nie istnieje
     */
    private Field getField( int x, int y )
    {
        for( Field field : fields )
        {
            if( field.getX() == x && field.getY() == y )
                return field;
        }
        return null;
    }
}
