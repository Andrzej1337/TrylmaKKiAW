package main;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandBuilderTest
{

    @Test
    void getCommand_singleWithoutData()
    {
        CommandBuilder cb = new CommandBuilder();

        cb.addCommand( "COMMAND" );
        assertEquals( "COMMAND", cb.getCommand() );
    }

    @Test
    void getCommand_multiWithoutData()
    {
        CommandBuilder cb = new CommandBuilder();

        cb.addCommand( "COMMAND" );
        cb.addCommand( "ABC" );
        cb.addCommand( "123" );
        assertEquals( "COMMAND@ABC@123", cb.getCommand() );
    }

    @Test
    void getCommand_singleWithData()
    {
        CommandBuilder cb = new CommandBuilder();

        cb.addCommand( "COMMAND", "Data lata 123" );
        assertEquals( "COMMAND Data lata 123", cb.getCommand() );
    }

    @Test
    void getCommand_multiWithData()
    {
        CommandBuilder cb = new CommandBuilder();

        cb.addCommand( "CMD", "data" );
        cb.addCommand( "CODE" );
        cb.addCommand( "ABC 123 abc" );
        assertEquals( "CMD data@CODE@ABC 123 abc", cb.getCommand() );
    }
}