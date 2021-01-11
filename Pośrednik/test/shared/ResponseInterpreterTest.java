package shared;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResponseInterpreterTest
{
    @Test
    void getResponses_SingleWithoutParameters()
    {
        String testLine = "KOD\n";
        Response[] response = ResponseInterpreter.getResponses( testLine );

        assertEquals( 1, response.length );
        assertEquals( "KOD", response[0].getCode() );
    }

    @Test
    void getResponses_SingleWithParameters()
    {
        String testLine = "KOD param1 X 123 -321";
        Response[] response = ResponseInterpreter.getResponses( testLine );

        assertEquals( 1, response.length );
        assertEquals( "KOD", response[ 0 ].getCode() );

        assertEquals( 2, response[ 0 ].getWords().length );
        assertEquals( "param1", response[ 0 ].getWords()[ 0 ] );
        assertEquals( "X", response[ 0 ].getWords()[ 1 ] );

        assertEquals( 2, response[ 0 ].getNumbers().length );
        assertEquals( 123, response[ 0 ].getNumbers()[ 0 ] );
        assertEquals( -321, response[ 0 ].getNumbers()[ 1 ] );
    }

    @Test
    void getResponses_ManyWithoutParameters()
    {
        String testLine = "KOD@A@CC";
        Response[] responses = ResponseInterpreter.getResponses( testLine );

        assertEquals( 3, responses.length );

        assertEquals( "KOD", responses[ 0 ].getCode() );
        assertEquals( "A", responses[ 1 ].getCode() );
        assertEquals( "CC", responses[ 2 ].getCode() );
    }

    @Test
    void getResponses_ManyWithParameters()
    {
        String testLine = "KOD A B 123@TEST@KOD 10";
        Response[] responses = ResponseInterpreter.getResponses( testLine );

        assertEquals( 3, responses.length );

        assertEquals( "KOD", responses[ 0 ].getCode() );
        assertEquals( 2, responses[ 0 ].getWords().length );
        assertEquals( "A", responses[ 0 ].getWords()[ 0 ] );
        assertEquals( "B", responses[ 0 ].getWords()[ 1 ] );
        assertEquals( 1, responses[ 0 ].getNumbers().length );
        assertEquals( 123, responses[ 0 ].getNumbers()[ 0 ] );

        assertEquals( "TEST", responses[ 1 ].getCode() );
        assertEquals( 0, responses[ 1 ].getWords().length );

        assertEquals( "KOD", responses[ 2 ].getCode() );
        assertEquals( 1, responses[ 2 ].getNumbers().length );
        assertEquals( 10, responses[ 2 ].getNumbers()[ 0 ] );

    }
}
