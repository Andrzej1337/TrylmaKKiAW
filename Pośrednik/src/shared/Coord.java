package shared;

import java.util.Objects;

/**
 * Klasa pomocnicza do przechowywania współrzędnych
 */
public class Coord
{
    private int x;
    private int y;
    public Coord(int x, int y )
    {
        this.x = x;
        this.y = y;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    @Override
    public boolean equals( Object o )
    {
        if( o instanceof Coord )
        {
            Coord c = (Coord)o;
            return x == c.x && y == c.y;
        }
        else
            return false;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( x, y );
    }
}
