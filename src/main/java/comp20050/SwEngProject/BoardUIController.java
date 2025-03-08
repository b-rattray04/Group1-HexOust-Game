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

public class BoardUIController {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Button closeButton;
    @FXML
    private Circle gameCircle;
    @FXML
    private Label gameFlag;
    @FXML
    private Label Coords;
    @FXML
    private Label invalidMove;
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

        initialiseHex();
    }
    private void initialiseHex() {
        for (Node node : rootPane.getChildren()) {
            if (node instanceof Polygon) {
                Polygon hexagon = (Polygon) node;
                Hexagon hex = splitCoords(hexagon.getId());
                hexagon.setUserData(hex);
                hexagon.setOnMouseClicked(this::onMouseClicked);
            }
        }
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
            }
        }
    }

    public Hexagon showCoords(MouseEvent click) {
        Polygon clickedPolygon = (Polygon) click.getSource();
        Hexagon hex = (Hexagon) clickedPolygon.getUserData();
        if (hex != null) {
            String coords = "Q: " + hex.getQ() + ", R: " + hex.getR() + ", S: " + hex.getS();
            Coords.setText(coords);
        }
            System.out.println("Coordinates: q=" + hex.getQ() + ", r=" + hex.getR() + ", s=" + hex.getS());
        return hex;
    }


    public static Hexagon splitCoords(String coordString) {
        String[] parts = coordString.split("_");
        int[] values = new int[3];

        for (int i = 0; i < 3; i++) {
            String part = parts[i].trim();
            if (part.startsWith("m")) {
                values[i] = -1 * Integer.parseInt(part.substring(1));
            } else {
                values[i] = Integer.parseInt(part);
            }
        }
        return new Hexagon(values[0], values[1], values[2]);
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

    public void onMouseClicked(MouseEvent event) {
        Hexagon hex = showCoords(event);
        if (!hasAdjacentOccupied(hex)) {
            hex.setOccupied(true);
            invalidMove.setText("");
            changeColor(event);
        } else {
            invalidMove.setText("Invalid move");
        }
    }
    public Hexagon getHexagonAt(int q, int r, int s) {
        for (Node node : rootPane.getChildren()) {
            if (node instanceof Polygon) {
                Hexagon hex = (Hexagon) node.getUserData();
                if (hex != null && hex.getQ() == q && hex.getR() == r && hex.getS() == s) {
                    return hex;
                }
            }
        }
        return null; // Return null if no matching hexagon is found
    }

    public boolean hasAdjacentOccupied(Hexagon hex) {
        if (hex.isOccupied()) {
            return false;
        }

        int x = hex.getQ();
        int y = hex.getR();
        int z = hex.getS();

        // Corner cases
        if (x == -6 && y == 6 && z == 0) {
            return isAdjacentOccupied(hex, 4) || isAdjacentOccupied(hex, 5) || isAdjacentOccupied(hex, 0);
        } else if (x == -6 && y == 0 && z == 6) {
            return isAdjacentOccupied(hex, 5) || isAdjacentOccupied(hex, 0) || isAdjacentOccupied(hex, 1);
        } else if (x == 0 && y == -6 && z == 6) {
            return isAdjacentOccupied(hex, 0) || isAdjacentOccupied(hex, 1) || isAdjacentOccupied(hex, 2);
        } else if (x == 6 && y == -6 && z == 0) {
            return isAdjacentOccupied(hex, 1) || isAdjacentOccupied(hex, 2) || isAdjacentOccupied(hex, 3);
        } else if (x == 6 && y == 0 && z == -6) {
            return isAdjacentOccupied(hex, 2) || isAdjacentOccupied(hex, 3) || isAdjacentOccupied(hex, 4);
        } else if (x == 0 && y == 6 && z == -6) {
            return isAdjacentOccupied(hex, 3) || isAdjacentOccupied(hex, 4) || isAdjacentOccupied(hex, 5);
        }

        // Edge cases
        else if (x == -6) {
            return isAdjacentOccupied(hex, 4) || isAdjacentOccupied(hex, 5) || isAdjacentOccupied(hex, 0) || isAdjacentOccupied(hex, 1);
        } else if (x == 6) {
            return isAdjacentOccupied(hex, 1) || isAdjacentOccupied(hex, 2) || isAdjacentOccupied(hex, 3) || isAdjacentOccupied(hex, 4);
        } else if (y == 6) {
            return isAdjacentOccupied(hex, 3) || isAdjacentOccupied(hex, 4) || isAdjacentOccupied(hex, 5) || isAdjacentOccupied(hex, 0);
        } else if (y == -6) {
            return isAdjacentOccupied(hex, 0) || isAdjacentOccupied(hex, 1) || isAdjacentOccupied(hex, 2) || isAdjacentOccupied(hex, 3);
        } else if (z == -6) {
            return isAdjacentOccupied(hex, 2) || isAdjacentOccupied(hex, 3) || isAdjacentOccupied(hex, 4) || isAdjacentOccupied(hex, 5);
        } else if (z == 6) {
            return isAdjacentOccupied(hex, 5) || isAdjacentOccupied(hex, 0) || isAdjacentOccupied(hex, 1) || isAdjacentOccupied(hex, 2);
        }

        // Non-edge case
        else {
            return isAdjacentOccupied(hex, 0) || isAdjacentOccupied(hex, 1) || isAdjacentOccupied(hex, 2) ||
                    isAdjacentOccupied(hex, 3) || isAdjacentOccupied(hex, 4) || isAdjacentOccupied(hex, 5);
        }
    }

    private boolean isAdjacentOccupied(Hexagon hex, int direction) {
        int[][] directions = {{1,0,-1}, {0,1,-1}, {-1,1,0}, {-1,0,1}, {0,-1,1}, {1,-1,0}};
        int[] dir = directions[direction];
        Hexagon adjacent = getHexagonAt(hex.getQ() + dir[0], hex.getR() + dir[1], hex.getS() + dir[2]);
        return adjacent != null && adjacent.isOccupied();
    }


    // close game when quit button selected
    @FXML
    public void handleClose(MouseEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}