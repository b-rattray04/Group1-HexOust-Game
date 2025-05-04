package comp20050.SwEngProject;

import javafx.scene.Node;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Circle;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;


public class Utilities {

    /**
     * show dialog with yes or no options
     *
     * @param title title of confirmation dialog
     * @param message   text displayed in the dialog
     * @return  return true if 'yes' selected, false if 'no'
     */
    public static boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Customize button text if needed
        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");
        alert.getButtonTypes().setAll(yesButton, noButton);

        // Show dialog and wait for response
        java.util.Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == yesButton;
    }

    /**
     * searches for a polygon on the board based on coordinates
     *
     * @param q q coord
     * @param r r coord
     * @param s s coord
     * @return  returns the node representing the hexagon with coordinates matching input, null if not found
     */
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

    /**
     * searches for a circle on the board based on coordinates
     * used to match a circle with its corresponding hexagon
     *
     * @param x x coord of circle
     * @param y y coord
     * @return  return the circle at given coords, null if not found
     */
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