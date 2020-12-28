package shared;

/**
 * Klasa reprezentuje komunikaty pomiędzy serwerem a klientem
 */
public class Response
{
    private String code;
    private int[] numbers;
    private String[] words;

    Response(String code, int[] numbers, String[] words)
    {
        this.code = code;
        this.numbers = numbers;
        this.words = words;
    }

    public String getCode()
    {
        return code;
    }

    public int[] getNumbers()
    {
        return numbers;
    }

    public String[] getWords()
    {
        return words;
    }

}
