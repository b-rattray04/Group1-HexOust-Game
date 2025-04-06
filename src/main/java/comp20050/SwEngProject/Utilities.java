package comp20050.SwEngProject;

import javafx.scene.Node;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Circle;
import javafx.scene.layout.AnchorPane;

public class Utilities {

    /* retrieves a hexagon from given AnchorPane based on coordinates
     * rootPane -> AnchorPane containing the hex grid
     * returns the corresponding Polygon representing the hexagon, null if not found
     */
    public static Polygon getHexagonNode(AnchorPane rootPane, int q, int r, int s) {
        // loop through all nodes in rootPane (board)
        for (Node node : rootPane.getChildren()) {
            // make sure node is a hexagon - ignores circles or any other game objects
            if (node instanceof Polygon) {
                Hexagon hex = (Hexagon) node.getUserData(); // retrieve data for the hexagon i.e. q,r,s coords and occupied bool
                // if hexagon is not null and compares hexagon to coordinates given to check if correct one has been found
                if (hex != null && hex.getQ() == q && hex.getR() == r && hex.getS() == s) {
                    return (Polygon) node; // return matching hexagon
                }
            }
        }
        return null; // return null if no match found
    }

    public static Circle findCircleByCoords(AnchorPane rootPane, double x, double y) {
        // loop through all nodes in rootPane (game board) -> contains all UI elements of the board: Hexagons (Polygon), Circles (game pieces), etc
        for (Node node : rootPane.getChildren()) {
            // if the object is a circle, and it is a placed game piece
            if (node instanceof Circle && node.getId() != null) {
                Circle circle = (Circle) node; // converts node to a circle object via a cast
                // compare circle's position to given x,y coords
                // layoutX = x coord of the circle, layoutY = y coord of circle
                // if circle is very close to given coords, assume it is correct to account for any small errors
                if (Math.abs(circle.getLayoutX() - x) < 1 && Math.abs(circle.getLayoutY() - y) < 1) {
                    return circle; // if circle is found, return it
                }
            }
        }
        return null; // otherwise return null
    }
}