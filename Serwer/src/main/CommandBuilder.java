package main;

/**
 * Klasa odpowiedzialna za budowanie komunikatów wysyłanych do graczy
 */
class CommandBuilder
{
    private StringBuilder command;

    CommandBuilder()
    {
        command = new StringBuilder();
    }

    /**
     * Dodaje dany kod do komendy i rozdziela go znakiem '@' od innych
     */
    void addCommand(String code)
    {
        if (command.toString().isEmpty())
        {
            command.append(code);
        } else
        {
            command.append("@");
            command.append(code);
        }
    }

    /**
     * Dodaje kod wraz z jego danymi do komendy
     */
    void addCommand(String code, String data)
    {
        if (command.toString().isEmpty())
        {
            command.append(code);
            command.append(" ");
            command.append(data);
        } else
        {
            command.append("@");
            command.append(code);
            command.append(" ");
            command.append(data);
        }

    }

    /**
     * zwraca utworzoną komendę
     */
    String getCommand()
    {
        return command.toString();
    }
}
