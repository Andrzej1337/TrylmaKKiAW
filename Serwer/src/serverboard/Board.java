package serverboard;


import shared.Coord;
import shared.PlayerColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Reprezentuje planszę na której odbywa się rozgrywka
 */
public abstract class Board

{
    int columns, rows;
    Field[][] fields;

    Board()
    {

    }

    /**
     * Dodaje pionek o podanym kolorze na pole (x,y)
     */
    public void addPiece(int x, int y, PlayerColor color) throws UnplayableFieldException
    {
        if (fields[x][y].isPlayable())
        {
            fields[x][y].setCurrentColor(color);
        } else
        {
            throw new UnplayableFieldException();
        }
    }

    /**
     * Usuwa pionek z pola (x,y)
     */
    public void removePiece(int x, int y) throws UnplayableFieldException
    {
        if (fields[x][y].isPlayable())
        {
            // TODO CHECK UNEXPECTED BEHAVIOR
            fields[x][y].setCurrentColor(PlayerColor.NONE);
            //setField(x, y, new Field(PlayerColor.NONE, fields[x][y].getNativeColor(), fields[x][y].getTargetColor(), true));

        } else
        {
            throw new UnplayableFieldException();
        }
    }

    /**
     * Zwraca kolor pionka na polu (x,y)
     */

    public PlayerColor getColor(int x, int y) throws UnplayableFieldException
    {
        if (fields[x][y].isPlayable())
        {
            return fields[x][y].getCurrentColor();
        } else
        {
            throw new UnplayableFieldException();
        }
    }

    /**
     * Zwraca board w postaci stringa
     */
    public abstract String getAsString();

    /**
     * Sprawdza czy wszyskie pionki danego koloru są już w swoim celu (warunek zwycięstwa)
     */
    public abstract boolean isWinner(PlayerColor color);

    public Field getField(int x, int y)
    {
        if (x < 1 || y < 1 || x > columns || y > rows)
        {
            return null;
        } else
        {
            return fields[x][y];
        }
    }

    public void setField(int x, int y, Field f)
    {
        if (x < 1 || y < 1 || x > columns || y > rows)
        {
            throw new NullPointerException();
        } else
        {
            fields[x][y] = f;
        }
    }

    /**
     * Zwraca współrzędne wszystkich pól znajdujących się w odległości 1 lub 2
     * od pola (x, y), na któe można potencjalnie skoczyć (w linii prostej)
     */
    public List<Coord> getNearbyCoords( int x, int y )
    {
        List<Coord> coords = new ArrayList<>();

        // pola odległe o 1

        // na lewo
        coords.add( new Coord( x - 1, y ) );
        // na prawo
        coords.add( new Coord( x + 1, y ) );
        // góra lewo (dla wiersza nieparzystego, aby dojść do pola góra lewo trzeba zmniejszyć x o 1)
        coords.add( new Coord( ( y % 2 == 0 ? x : x - 1 ), y - 1 ) );
        // góra prawo (dla wiersza parzystego, aby dojść do pola góra prawo trzeba zwiększyć x o 1)
        coords.add( new Coord( ( y % 2 == 0 ? x + 1 : x ), y - 1 ) );
        // dół lewo (dla wiersza nieparzystego trzeba zmniejszyć x o 1)
        coords.add( new Coord( ( y % 2 == 0 ? x : x - 1 ), y + 1 ) );
        // dół prawo (dla wiersza parzystego trzeba zwiększyc x o 1)
        coords.add( new Coord( ( y % 2 == 0 ? x + 1 : x ), y + 1 ) );

        // pola odległe o 2

        // na lewo
        coords.add( new Coord( x - 2, y ) );
        // na prawo
        coords.add( new Coord( x + 2, y ) );
        // góra lewo
        coords.add( new Coord( x - 1, y - 2 ) );
        // góra prawo
        coords.add( new Coord( x + 1, y - 2 ) );
        // dół lewo
        coords.add( new Coord( x - 1, y + 2 ) );
        // dół prawo
        coords.add( new Coord( x + 1, y + 2 ) );

        return coords;
    }

    public int getColumns()
    {
        return columns;
    }

    public int getRows()
    {
        return rows;
    }
}
