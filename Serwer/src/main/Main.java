package main;

public class Main
{


    public static void main(String[] args)
    {
        Server server;
        try
        {
            server = new Server( 8888 );
            server.run();
        }
        catch(Exception e )
        {
            System.out.println( e.getMessage() );
        }
    }
}
