package comp20050.SwEngProject;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;

public class BoardUIController {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Button closeButton;
    @FXML
    private Circle gameCircle;
    @FXML
    private Label gameFlag;

    private Color selectedColor = Color.RED; // Default color
    private int player = 0; // 0 = Red Player, 1 = Blue Player
    AudioClip clip = new AudioClip(getClass().getResource("/sounds/ncp.mp3").toString());

    @FXML
    private void initialize() {
        String css = this.getClass().getResource("/css/style.css").toExternalForm();        //for hover
        rootPane.getStylesheets().add(css);

        closeButton.setOnMouseEntered(event -> {        //enlarged exit
            ScaleTransition st = new ScaleTransition(Duration.millis(200), closeButton);
            st.setToX(1.2);
            st.setToY(1.2);
            st.play();
        });

        closeButton.setOnMouseExited(event -> {         //enlarged exit
            ScaleTransition st = new ScaleTransition(Duration.millis(200), closeButton);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }

    public void changePlayer() {
        if (player == 0) {
            clip.play();
            selectedColor = Color.BLUE;
            gameCircle.setFill(selectedColor);
            gameFlag.setText("Blue to play");
            player = 1;
        } else if (player == 1) {
            clip.play();
            selectedColor = Color.RED;
            gameCircle.setFill(selectedColor);
            gameFlag.setText("Red to play");
            player = 0;
        }
    }

    // Changes the color of the circles and makes them appear on top of the hex board
    public void changeColor(MouseEvent click) {
        Polygon hexagon = (Polygon) click.getSource();
        int hexagonX = (int) (hexagon.getLayoutX() + hexagon.getTranslateX());
        int hexagonY = (int) (hexagon.getLayoutY() + hexagon.getTranslateY());

        Circle matchingCircle = findCircleByCoords(hexagonX, hexagonY);

        if (matchingCircle != null) {
            matchingCircle.toFront();
            if ((matchingCircle.getFill() != Color.BLUE) && (matchingCircle.getFill() != Color.RED)) {
                matchingCircle.setFill(selectedColor);
                changePlayer();
            }
        }
    }

    private Circle findCircleByCoords(double x, double y) {
        for (Node node : rootPane.getChildren()) {
            if (node instanceof Circle && node.getId() != null) {
                Circle circle = (Circle) node;
                if (Math.abs(circle.getLayoutX() - x) < 1 && Math.abs(circle.getLayoutY() - y) < 1) {
                    return circle;
                }
            }
        }
        return null;
    }

    @FXML
    public void handleClose(MouseEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
