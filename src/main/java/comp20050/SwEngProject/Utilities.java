package comp20050.SwEngProject;

import javafx.scene.Node;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Circle;
import javafx.scene.layout.AnchorPane;

public class Utilities {

    public static Polygon getHexagonNode(AnchorPane rootPane, int q, int r, int s) {
        for (Node node : rootPane.getChildren()) {
            if (node instanceof Polygon) {
                Hexagon hex = (Hexagon) node.getUserData(); // retrieve data for the hexagon i.e. q,r,s coords and occupied bool
                if (hex != null && hex.getQ() == q && hex.getR() == r && hex.getS() == s) {
                    return (Polygon) node;
                }
            }
        }
        return null;
    }

    public static Circle findCircleByCoords(AnchorPane rootPane, double x, double y) {
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