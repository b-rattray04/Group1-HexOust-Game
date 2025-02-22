package comp20050.SwEngProject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HexOustApplication extends Application {           //To start the game
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("GraphicalUserInterface.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("HexOust");
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    int check = 0;
}