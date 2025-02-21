package comp20050.softwareengineeringproject2;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.paint.Color;


public class HelloController {
    @FXML
    private AnchorPane rootPane;
    private Color selectedColor = Color.RED; // Default color

    public Color getColor(MouseEvent mouseEvent) {
        Circle circle = (Circle) mouseEvent.getSource();
        Paint fill = circle.getFill(); // Get the fill color

        if (fill instanceof Color color) { // Ensure it's a Color object
            return color;
        } else {
            return Color.BLUE; // Default fallback
        }
    }

    //unused so far was trying to mess around with a color beiing taken in and stored when a mouse click is made on the stone
    public void selectColor(MouseEvent mouseEvent) {
        selectedColor = getColor(mouseEvent); // Store the selected color
    }

    //changes the color of the circles and makes them be on top of the hex board
    public void changeColor(MouseEvent click) {
        Polygon hexagon = (Polygon) click.getSource();
        int hexagonX = (int) (hexagon.getLayoutX() + hexagon.getTranslateX());
        int hexagonY = (int) (hexagon.getLayoutY() + hexagon.getTranslateY());

        Circle matchingCircle = findCircleByCoords(hexagonX, hexagonY);

        if (matchingCircle != null) {
            matchingCircle.setFill(Color.RED);
            matchingCircle.toFront();
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
}