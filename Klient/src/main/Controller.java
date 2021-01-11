package main;

import board.Board;
import board.Field;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Controller
{
    /** Referencja do panelu, w którym znajdują się pola typu Circle */
    @FXML
    private Pane boardPane;
    @FXML
    private Label infoBar;

    private List<Field> fields = new ArrayList<>();
    private CommunicationManager communicationManager;
    private Board board;
    private Player player;
    private boolean guiBlocked = true;

    /**
     * Funkcja uruchamiana przy starcie aplikacji
     */
    @FXML
    private void initialize()
    {
        loadAllFieldsFromBoard();
        showAlert( "Połącz się z serwerem, aby zagrać" );
    }

    /**
     * Wczytuje pola do tablicy fields
     */
    private void loadAllFieldsFromBoard()
    {
        int x = 1;  // Współrzędna x pola (kolumna)
        int y = 1;  // Współrzędna y pola (wiersz)

        for( Node node : boardPane.getChildren() )
        {
            loadNodeAsField( node, x, y );

            // Zmiana współrzędnych
            if( x == 13 )
            {
                x = 1;
                y++;
            }
            else
            {
                x++;
            }
        }
    }

    private void loadNodeAsField( Node node, int x, int y )
    {
        if( node instanceof Circle )
        {
            fields.add( new Field( x, y, (Circle)node ) );
            node.setOnMouseClicked( this :: onFieldClick );
        }
        else
        {
            throw new RuntimeException( "Jeden z elementów wewnątrz Pane jest typu innego niż Circle" );
        }
    }

    /**
     * Obsługuje zdarzenie kliknięcia w pole (typu Circle)
     */
    private void onFieldClick( MouseEvent event )
    {
        boolean eventIsCircle = ( event.getSource() instanceof Circle );

        if( !guiBlocked && eventIsCircle )
        {
            Circle clickedCircleReference = (Circle)event.getSource();

            Field clickedField = getFieldByCircleReference( clickedCircleReference );
            makeClickOnField( clickedField );
        }
    }

    private Field getFieldByCircleReference( Circle circle )
    {
        for( Field field : fields )
        {
            if( field.circleEquals( circle ) )
                return field;
        }
        return null;
    }

    private void makeClickOnField( Field field )
    {
        if( field != null )
        {
            System.out.println( "Kliknięto w pole: (" + field.getX() + ", " + field.getY() + ")" );
            player.handleClickOnField( field.getX(), field.getY() );
        }
    }

    @FXML
    private void onExit()
    {
        Alert alert = new Alert( Alert.AlertType.CONFIRMATION);
        alert.setTitle("Potwierdzenie");
        alert.setHeaderText("Czy na pewno chcesz wyjść?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            Platform.exit();
        }
    }

    @FXML
    private void onHelp() throws IOException
    {
        AnchorPane pane = FXMLLoader.load( getClass().getResource( "instruction.fxml" ) );
        Stage stage = new Stage();
        stage.setScene(new Scene(pane));
        stage.setResizable( false );
        stage.showAndWait();
    }



    private void showError( String text )
    {
        Platform.runLater( () -> {
            infoBar.setStyle( "-fx-background-color: red; -fx-alignment: center" );
            infoBar.setTextFill( Color.WHITE );
            infoBar.setText( text );
        } );
    }

    private void showAlert( String text )
    {
        Platform.runLater( () -> {
            infoBar.setStyle("-fx-background-color: orange; -fx-alignment: center");
            infoBar.setTextFill( Color.WHITE );
            infoBar.setText( text );
        } );

    }

    private void showSuccess( String text )
    {
        Platform.runLater( () -> {
            infoBar.setStyle( "-fx-background-color: green; -fx-alignment: center" );
            infoBar.setTextFill( Color.WHITE );
            infoBar.setText( text );
        } );
    }

    private void connectAndStartMatch( String host, int port )
    {
        try
        {
            createConnection( host, port );
            startMatch();
        }
        catch( Exception e )
        {
            showError( e.getMessage() );
        }
    }

    private void startMatch() throws Exception
    {
        createBoard();
        createPlayer();
        player.startMatch();
    }

    private void createConnection( String host, int port ) throws Exception
    {
        communicationManager = new CommunicationManager( host, port );
    }

    private void createBoard()
    {
        board = new Board( fields );
        board.deselectAndUnmarkAllFields();
    }

    private void createPlayer() throws Exception
    {
        try
        {
            player = new Player( communicationManager, board,
                    this :: blockGUI, this :: showSuccess, this :: showAlert, this :: showError );
        }
        catch( Exception e )
        {
            throw new Exception("Problem z utworzeniem gracza: " + e.getMessage());
        }
    }

    private void blockGUI( boolean state )
    {
        this.guiBlocked = state;
    }

    /**
     * Tworzenie nowego okna dialogowego na wpisanie parametrów połączenia
     */
    @FXML
    private void onNewConnection()
    {
        // Dialog
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Nowe połączenie");
        dialog.setHeaderText("Nazwiązywanie nowego połączenia");

        // Przyciski
        ButtonType connectButtonType = new ButtonType("Połącz", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(connectButtonType, ButtonType.CANCEL);

        // Pola tekstowe
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField ipAddressField = new TextField();
        ipAddressField.setPromptText("adres IP hosta");
        ipAddressField.setText( "localhost" );              // ustawienie domyślne
        TextField portField = new TextField();
        portField.setPromptText("port");
        portField.setText("8888");                          // ustawienie domyślne

        grid.add(new Label("IP:"), 0, 0);
        grid.add(ipAddressField, 1, 0);
        grid.add(new Label("Port:"), 0, 1);
        grid.add(portField, 1, 1);

        // Zablokuj przycisk, gdy nie podano jednej z opcji
        Node connectButton = dialog.getDialogPane().lookupButton(connectButtonType);
        connectButton.setDisable(false);

        ipAddressField.textProperty().addListener(
                (observable, oldValue, newValue) -> connectButton.setDisable(newValue.trim().isEmpty())
        );

        dialog.getDialogPane().setContent(grid);

        // Domyślny focus na pole z IP
        Platform.runLater( ipAddressField :: requestFocus );

        // Przekonwertuj wprowadzone dane, aby dialog zwrócił je jako para
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == connectButtonType) {
                return new Pair<>(ipAddressField.getText(), portField.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        String host;
        int port;
        if( result.isPresent() )
        {
            Pair<String, String> r = result.get();
            host = r.getKey();
            try
            {
                port = Integer.parseInt( r.getValue() );
            }
            catch( Exception e )
            {
                Alert alert = new Alert( Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Podano nieprawidłowy port");

                alert.showAndWait();
                return;
            }

            connectAndStartMatch( host, port );
        }
    }
}
