package comp20050.SwEngProject;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import javafx.util.Duration;

public class BoardUIController {
    // Game variables
    private Color selectedColor = Color.RED;
    private boolean redTurn = true;

    // Game logic instance
    private GameLogic gameLogic;

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

    // close game when quit button selected
    @FXML
    public void handleClose(MouseEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void initialize() {
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

        // Initialize game logic
        gameLogic = new GameLogic(rootPane);
        gameLogic.setSelectedColor(selectedColor);
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

    // changes player's turn
    private void changePlayer() {
        if (redTurn) {
            selectedColor = Color.BLUE;
            gameFlag.setText("Blue's Turn");
            gameCircle.setFill(Color.BLUE);
            redTurn = false;
        } else {
            selectedColor = Color.RED;
            gameFlag.setText("Red's Turn");
            gameCircle.setFill(Color.RED);
            redTurn = true;
        }

        // Update game logic with new player color
        gameLogic.setSelectedColor(selectedColor);
    }

    //changes the color of the circles and makes them be on top of the hex board
    public void changeColor(MouseEvent click) {
        Polygon hexagon = (Polygon) click.getSource();

        int hexagonX = (int) (hexagon.getLayoutX() + hexagon.getTranslateX());
        int hexagonY = (int) (hexagon.getLayoutY() + hexagon.getTranslateY());

        Circle matchingCircle = Utilities.findCircleByCoords(rootPane, hexagonX, hexagonY);

        if (matchingCircle != null) {
            matchingCircle.toFront();
            if ((matchingCircle.getFill() != Color.BLUE) && (matchingCircle.getFill() != Color.RED)) {
                matchingCircle.setFill(selectedColor);
                System.out.println("Move made on circle with id: " + matchingCircle.getId());
                // Player change will be handled in onMouseClicked
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

    public void onMouseClicked(MouseEvent event) {
        Hexagon hex = showCoords(event);
        if (hex.isOccupied()) {
            invalidMove.setText("Hexagon is occupied");
            return;
        }

        // Update game logic with current player color
        gameLogic.setSelectedColor(selectedColor);

        if (gameLogic.nonCaptureMove(hex) == true) {
            hex.setOccupied(true);
            invalidMove.setText("Non-Capture Move");
            changeColor(event);
            changePlayer();
        }
        else if (gameLogic.nonCaptureMove(hex) == false && gameLogic.validCapture(hex) == true) {
            hex.setOccupied(true);
            invalidMove.setText("Capture Move");
            changeColor(event);
            gameLogic.removeCircles(hex);
        }
        else {
            invalidMove.setText("Not a valid move");
        }
    }
}