package comp20050.SwEngProject;

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
        String css = this.getClass().getResource("/css/style.css").toExternalForm();
        rootPane.getStylesheets().add(css);
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