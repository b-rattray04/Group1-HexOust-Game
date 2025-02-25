package comp20050.SwEngProject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;

public class HexOustApplication extends Application { // To start the game
    private MediaPlayer mediaPlayer; // Declare MediaPlayer as a class-level variable

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("GraphicalUserInterface.fxml"));
        Scene scene = new Scene(root);

        stage.setScene(scene);              // Set up the stage
        stage.setTitle("HexOust");
        stage.setResizable(false);
        stage.show();
        playBackgroundMusic();
    }

    private void playBackgroundMusic() {
        String audioFilePath =getClass().getResource("/sounds/background.mp3").toString();
        Media media = new Media(audioFilePath);
        mediaPlayer = new MediaPlayer(media);

        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);      //loop
        mediaPlayer.setVolume(0.5);
        mediaPlayer.play();
    }

    @Override
    public void stop() throws Exception {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
