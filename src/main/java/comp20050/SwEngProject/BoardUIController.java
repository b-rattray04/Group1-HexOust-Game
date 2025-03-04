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
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

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
    private int player = 0;         // 0 = Red Player, 1 = Blue Player



    @FXML
    private void initialize() {

        // load CSS
        String css = this.getClass().getResource("/css/style.css").toExternalForm();        //for hover
        rootPane.getStylesheets().add(css);

        // hover effect for quit button
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
    /*
    *public Color getColor(MouseEvent mouseEvent) {
    *    Circle circle = (Circle) mouseEvent.getSource();
    *    Paint fill = circle.getFill(); // Get the fill color
    *
    *    if (fill instanceof Color color) { // Ensure it's a Color object
    *        return color;
    *    } else {
    *        return Color.BLUE; // Default fallback
    *    }
    *}
    *unused so far was trying to mess around with a color being taken in and stored when a mouse click is made on the stone
    *public void selectColor(MouseEvent mouseEvent) {
    *    selectedColor = getColor(mouseEvent); // Store the selected color
    *}
    */

    // change player after a move is made
    public void changePlayer(){
        if (player == 0){
            selectedColor = Color.BLUE;
            gameCircle.setFill(selectedColor);
            gameFlag.setText("Blue to play");
            player = 1;
        } else if (player == 1){
            selectedColor = Color.RED;
            gameCircle.setFill(selectedColor);
            gameFlag.setText("Red to play");
            player = 0;
        }
    }

    //changes the color of the circles and makes them be on top of the hex board
    public void changeColor(MouseEvent click) {
        Polygon hexagon = (Polygon) click.getSource();

        int hexagonX = (int) (hexagon.getLayoutX() + hexagon.getTranslateX());
        int hexagonY = (int) (hexagon.getLayoutY() + hexagon.getTranslateY());

        Circle matchingCircle = findCircleByCoords(hexagonX, hexagonY);

        if (matchingCircle != null) {
            matchingCircle.toFront();
            if ((matchingCircle.getFill() != Color.BLUE) && (matchingCircle.getFill() != Color.RED)) {
                matchingCircle.setFill(selectedColor);
                System.out.println("Move made on circle with id: " + matchingCircle.getId());
                changePlayer(); // change player after move

                Board board = new Board();
                String hexagonId = matchingCircle.getId();

                int index = Integer.parseInt(hexagonId);

                int[] coords = board.findCoords(index);

                if (coords != null) {
                    int x = coords[0];
                    int y = coords[1];

                    List<Hexagon> adjacentHexagons = board.findAdjacentHexagons(x, y);

                    for (Hexagon hex : adjacentHexagons) {
                        System.out.println("Adjacent Hexagon at coordinates: (" + hex.getX() + ", " + hex.getY() + ")");
                    }
                }
            }
        }
    }

    // finds circle at coordinates
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

    // close game when quit button selected
    @FXML
    public void handleClose(MouseEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}