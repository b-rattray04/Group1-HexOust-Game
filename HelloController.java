package comp20050.softwareengineeringproject2;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.paint.Color;


public class HelloController {

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

    //changes the color of the hexagons
    public void changeColor(MouseEvent click) {
        Polygon hexagon = (Polygon) click.getSource();
        hexagon.setFill(selectedColor); // Apply the selected color
    }
}