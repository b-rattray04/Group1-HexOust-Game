package comp20050.SwEngProject;

import javafx.scene.Node;
import javafx.scene.shape.Polygon;
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
}
