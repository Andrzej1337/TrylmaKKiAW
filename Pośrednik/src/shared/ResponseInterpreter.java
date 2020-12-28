package shared;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Character.isDigit;

public class ResponseInterpreter
{
    public static Response[] getResponses(String line)
    {
        // usuwanie znaku nowej linii z końca (jeśli występuje)
        line = line.replace("\n","");

        // dzielenie linii na poszczególne komunikaty (rozdzielone @)
        String[] strResponses = line.split("@");

        // Wczytywanie poszczególnych komunikatów
        List<Response> responses = new ArrayList<>();
        for( String strResponse : strResponses )
        {
            // Rozdzielanie komunikatu względem spacji
            String[] parts = strResponse.split(" ");

            // Komunikat powinien zawierać co najmniej jeden element
            if( parts.length < 1 )
                throw new RuntimeException( "Otrzymano niepoprawną odpowiedź od serwera: '" + line + "'" );

            // Pierwsze słowo komunikatu to kod
            String code = parts[ 0 ];

            // Wczytywanie kolejnych liczb i słów
            List<Integer> numbers = new ArrayList<>();
            List<String> words = new ArrayList<>();
            for( int i = 1; i < parts.length; i++ )
            {
                // pierwszy znak to cyfra, więc spodziewam się liczby
                if( isDigit( parts[i].charAt( 0 ) ) )
                {
                    numbers.add( Integer.valueOf( parts[i] ) );
                }
                else if( parts[i].length() > 3 && parts[i].charAt( 0 ) == '-' && isDigit( parts[i].charAt( 1 ) ) )
                {
                    // liczba ujemna
                    numbers.add( Integer.valueOf( parts[i] ) );
                }
                else // w przeciwnym razie będzie to słowo (np. słowo RED oznaczająca kolor pionka)
                {
                    words.add( parts[ i ] );
                }
            }

            // Dodawanie sparsowanego komunikatu
            int[] numbersArray = numbers.stream().mapToInt( i -> i ).toArray();
            String[] wordsArray = words.toArray( new String[0] );
            Response response = new Response( code, numbersArray, wordsArray );

            responses.add( response );
        } // for

        return responses.toArray( new Response[0] );
    }
}
