package comp20050.SwEngProject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/*
 * main class for launching game
 * initialises and displays the UI
 */
public class HexOustApplication extends Application {
    //To start the game
    @Override
    public void start(Stage stage) throws Exception {
        // load GUI from FXML file
        Parent root = FXMLLoader.load(getClass().getResource("GraphicalUserInterface.fxml")); // loads UI
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("HexOust");
        stage.setResizable(false); // stop window resizing
        stage.show();
    }

    // launches JavaFX application
    public static void main(String[] args) {
        launch(args);
    } // launch game
}