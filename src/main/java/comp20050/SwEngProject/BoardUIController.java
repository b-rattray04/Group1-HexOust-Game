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

import java.util.HashSet;
import java.util.Set;

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

    // Assigns each hexagon in the UI a hexagon object and adds event listeners
    private void initialiseHex() {
        for (Node node : rootPane.getChildren()) {
            if (node instanceof Polygon) {
                Polygon hexagon = (Polygon) node;
                Hexagon hex = splitCoords(hexagon.getId()); // convert ID into hex coordinates
                hexagon.setUserData(hex); // store hex data in Polygon
                hexagon.setOnMouseClicked(this::onMouseClicked); // click event handler
            }
        }
    }

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

    // display hexagon coords on UI display
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

    // Parses hexagon ID string into hexagon object
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

    // finds circle (player's stone) at given x,y coordinates
    private Circle findCircleByCoords(double x, double y) {
        // loop through all nodes in rootPane (game board) -> contains all UI elements of the board: Hexagons (Polygon), Circles (game pieces), etc
        // loops through all child nodes to find a circle
        for (Node node : rootPane.getChildren()) {
            // if the object is a circle, and it is a placed game piece
            if (node instanceof Circle && node.getId() != null) {
                Circle circle = (Circle) node;// converts node to a circle object via a cast
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

    public void onMouseClicked(MouseEvent event) {
        Hexagon hex = showCoords(event);

        if(hex != null){
            invalidMove.setText("Stone already here");
        }

        if (nonCaptureMove(hex) == false && validCapture(hex) == false) {
            invalidMove.setText("Invalid move");
        }
        //check if it is a capture move first
        else if(nonCaptureMove(hex) == false && validCapture(hex) == true) {
            hex.setOccupied(true);
            invalidMove.setText("Capture Successful");
            changeColor(event);
            //go back to the same player that made the move
            changePlayer();
        }
        //then check if it is a non capture move
        else if(nonCaptureMove(hex) == true && validCapture(hex) == false) {
            hex.setOccupied(true);
            invalidMove.setText("NonCapture Successful");
            changeColor(event);
        }

        //shouldnt reach here
        else {
            invalidMove.setText("SHOULDNT HAVE HAPPENED");
        }
    }

    public boolean nonCaptureMove(Hexagon hex) {
        //just the amount of coloured neighbours, no including the stone being placed
        int blueCount = 0;
        int redCount = 0;

        for (Hexagon dir : Hexagon.directions) {
            // Calculate neighbor coordinates
            int q = hex.getQ() + dir.getQ();
            int r = hex.getR() + dir.getR();
            int s = hex.getS() + dir.getS();

            Polygon adjacentHex = Utilities.getHexagonNode(rootPane, q, r, s);
            // Check for stone on adjacent hex

            if(adjacentHex == null) {
                continue;
            }

            Circle adjacentCircle = findCircleByCoords(
                    adjacentHex.getLayoutX() + adjacentHex.getTranslateX(),
                    adjacentHex.getLayoutY() + adjacentHex.getTranslateY()
            );

            if (adjacentCircle != null) {
                Color adjacentColor = (Color) adjacentCircle.getFill();
                if (adjacentColor == Color.RED) redCount++;
                if (adjacentColor == Color.BLUE) blueCount++;

            }
        }

        System.out.println("Red: " + redCount +  " | Blue: " + blueCount);

        //if the circle has no neighbours then its a non capture move
        if(blueCount == 0 && redCount == 0) {
            return true;
        }

        if(selectedColor == Color.RED) {
            if(redCount == 0) {
                return true;
            } else {
                return false;
            }
        } else if(selectedColor == Color.BLUE) {
            if(blueCount == 0) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    // checks if any adjacent hexagon is occupied by current player's colour
    public boolean hasAdjacentOccupied(Hexagon hex) {
        // check if current hexagon is already occupied
        if (hex.isOccupied()) return false;
        // loop through all possible adjacent hexagons using directions stored in the directions ArrayList in Hexagon class
        for (Hexagon dir : Hexagon.directions) {
            // Uses helper method getHexagonNode in Utilities class to find a hexagon at calculated coords
            // adds dir (direction) values to the hex coords to get coords of neighbour hexagons
            // if there is no adjacent hexagon at the position, adjacent is null
            Polygon adjacent = Utilities.getHexagonNode(rootPane, hex.getQ() + dir.getQ(), hex.getR() + dir.getR(), hex.getS() + dir.getS());
            // if adjacent is not null i.e. if there is an adjacent hexagon
            if (adjacent != null) {
                // finds Circle (player's stone) placed on that hexagon using findCircleByCoords method
                // value will be null if there is no stone on that hexagon
                Circle adjacentCircle = findCircleByCoords(adjacent.getLayoutX() + adjacent.getTranslateX(), adjacent.getLayoutY() + adjacent.getTranslateY());
                // if there is a circle on the adjacent hexagon, check the colour
                // if the colour is the same as the current player's colour, return true i.e. adjacent hexagon is occupied
                if (adjacentCircle != null && adjacentCircle.getFill().equals(selectedColor)) {
                    return true;
                }
            }
        }
        return false; // otherwise return false
    }

    public boolean validCapture(Hexagon hex) {
        int blueCount = 0;
        int redCount = 0;


        for (Hexagon dir : Hexagon.directions) {
            // Calculate neighbor coordinates
            int q = hex.getQ() + dir.getQ();
            int r = hex.getR() + dir.getR();
            int s = hex.getS() + dir.getS();

            Polygon adjacentHex = Utilities.getHexagonNode(rootPane, q, r, s);
            // Check for stone on adjacent hex

            if(adjacentHex == null) {
                continue;
            }

            Circle adjacentCircle = findCircleByCoords(
                    adjacentHex.getLayoutX() + adjacentHex.getTranslateX(),
                    adjacentHex.getLayoutY() + adjacentHex.getTranslateY()
            );

            if (adjacentCircle != null) {
                Color adjacentColor = (Color) adjacentCircle.getFill();
                if (adjacentColor == Color.RED) redCount++;
                if (adjacentColor == Color.BLUE) blueCount++;

            }
        }

        System.out.println("Blue before placement " + blueCount);
        System.out.println("Red before placement " + redCount);
        // Count opponent pieces that can be captured

        if(blueCount == 0 && redCount == 0) {
            //in the case of no neighbours, then is an invalid move
            return false;
        }

        if(selectedColor == Color.RED) {
            if(redCount != 0 && blueCount == 0) {
                //in the case of only having red neighbours, invalid move
                return false;
            } else if (redCount == blueCount) {
                //in this case there is an equal amount of red and blue neightbours so adding another red makes a bigger group
                return true;
            }
        }

        if(selectedColor == Color.BLUE) {
            if(blueCount != 0 && redCount == 0) {
                //in case of only having blue neighbours, invalid move
                return false;
            } else if(blueCount == redCount) {
                //in this case there is an equal amount of red and blue neightbours so adding another red makes a bigger group
                return true;
            }
        }



        return false;
    }



    // close game when quit button selected
    @FXML
    public void handleClose(MouseEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }


}