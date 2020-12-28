package board;

import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import shared.PlayerColor;

/**
 * Klasa reprezentująca pojedyncze, klikalne pole w grze
 */
public class Field
{
    private int x;                                  // Współrzędna X pola (kolumna)
    private int y;                                  // Współrzędna Y pola (wiersz)
    private Circle circle;                          // Referencja do odpowiadającego Circle w GUI
    private PlayerColor color = PlayerColor.NONE;   // Kolor pionka na danym polu

    public Field( int x, int y, Circle circle )
    {
        this.x = x;
        this.y = y;
        this.circle = circle;
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    void setColor( PlayerColor color )
    {
        this.color = color;
        Stop[] gradientPhases = getProperGradient( color );
        setGradient( gradientPhases );
    }

    private Stop[] getProperGradient( PlayerColor color )
    {
        Stop[] stops = { new Stop(0, Color.WHITE), null };

        switch( color )
        {
            case NONE:
                this.color = PlayerColor.NONE;
                stops[ 1 ] = new Stop( 1, Color.WHITE );
                break;
            case RED:
                stops[ 1 ] = new Stop( 1, Color.RED );
                break;
            case GREEN:
                stops[ 1 ] = new Stop( 1, Color.GREEN );
                break;
            case YELLOW:
                stops[ 1 ] = new Stop( 1, Color.YELLOW );
                break;
            case BLUE:
                stops[ 1 ] = new Stop( 1, Color.BLUE );
                break;
            case ORANGE:
                stops[ 1 ] = new Stop( 1, Color.ORANGE );
                break;
            case VIOLET:
                stops[ 1 ] = new Stop( 1, Color.VIOLET );
                break;
            default:
                throw new RuntimeException( "Podany kolor nie istnieje: '" + color + "'" );
        }
        return stops;
    }

    private void setGradient( Stop[] gradientStops )
    {
        RadialGradient gradient = new RadialGradient( 0, 0,
                0.7, 0.2, 0.5, true, CycleMethod.NO_CYCLE, gradientStops );

        circle.setFill( gradient );
    }

    PlayerColor getColor()
    {
        return color;
    }

    void setSelected( boolean state )
    {
        StrokeType strokeType;
        if( !circle.isDisabled() )
        {
            if( state )
                strokeType = StrokeType.OUTSIDE; // lekko powiększa kółko
            else
                strokeType = StrokeType.INSIDE;

            circle.setStrokeType( strokeType );
        }

    }

    public boolean circleEquals( Circle circle )
    {
        return this.circle == circle;
    }

    void markAsPossibleJumpTarget( boolean state )
    {
        if( !circle.isDisabled() && color == PlayerColor.NONE )
        {
            Paint fillColor;
            if( state )
                fillColor = Paint.valueOf( "#302f26" );
            else
                fillColor = Color.WHITE;

            circle.setFill( fillColor );
        }
    }

    boolean isDisabled()
    {
        return circle.isDisabled();
    }
}
