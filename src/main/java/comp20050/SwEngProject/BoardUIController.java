package comp20050.SwEngProject;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * control board UI and game events
 */
public class BoardUIController {
    private Color selectedColor = Color.RED;
    private boolean redTurn = true;
    private GameLogic gameLogic;

    private int redPlacementCounter = 0;
    private int bluePlacementCounter = 0;
    boolean gameStarted = false;

    @FXML
    private AnchorPane rootPane;
    @FXML
    private Button closeButton;
    @FXML
    private Button resetButton;
    @FXML
    private Circle gameCircle;
    @FXML
    private Label gameFlag;
    @FXML
    private Label Coords;
    @FXML
    private Label invalidMove;
    @FXML
    private Label redCounter;
    @FXML
    private Label blueCounter;

    /**
     * closes game when close button selected
     */
    @FXML
    public void handleClose(MouseEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    /**
     * deals with restart button being selected
     */
    @FXML
    public void handleRestart(MouseEvent mouseEvent) {
        if(gameFlag.getText().contains("WINS")) {
            resetGame();
        } else {
            boolean confirm = Utilities.showConfirmationDialog(
                    "Reset Game",
                    "Are you sure you want to reset the game?\nAll progress will be lost."
            );
            if (confirm) {
                resetGame();
            }
        }
    }

    /**
     * reset game state, UI and board
     */
    @FXML
    private void resetGame() {
        // 1. Reset all game state variables
        resetGameState();

        // 2. Reset UI text elements
        resetUI();

        // Create temporary list to avoid concurrent modification
        clearBoard();

        gameLogic = new GameLogic(rootPane);
        gameLogic.setSelectedColor(selectedColor);

        //UI refresh
        rootPane.requestLayout();
    }

    /**
     * reset game state variables to starting values
     */
    private void resetGameState() {
        selectedColor = Color.RED;
        redTurn = true;
        gameStarted = false;
        redPlacementCounter = 0;
        bluePlacementCounter = 0;
    }

    /**
     * reset UI to starting appearance
     */
    private void resetUI() {
        gameFlag.setText("Red To Play");
        gameFlag.setFont(Font.font("Chalkboard", 24));
        gameFlag.setLayoutX(335);
        gameFlag.setTextFill(Color.BLACK);
        gameCircle.setFill(Color.RED);
        gameCircle.toFront();
        invalidMove.setText("");
        Coords.setText("");
        for (Node node : rootPane.getChildren()) {
            if (node instanceof Label && node != gameFlag) {
                ((Label) node).setText(" ");
                ((Label) node).setTextFill(Color.BLACK);
            }
        }
        updateStoneCounters();
    }

    /**
     * clear board of all placed stones
     */
    private void clearBoard() {
        List<Node> circles = new ArrayList<>();
        for (Node node : rootPane.getChildren()) {
            if (node instanceof Circle && node != gameCircle) {
                circles.add(node);
            }
        }
        rootPane.getChildren().removeAll(circles);
        rootPane.getChildren().addAll(0, circles);

        for (Node node : circles) {
            ((Circle)node).setFill(Color.LIGHTGRAY);
        }
        for (Node node : rootPane.getChildren()) {
            if(node instanceof Polygon) {
                Polygon hexagon = (Polygon) node;
                Hexagon hex = (Hexagon) hexagon.getUserData();
                hex.setUnoccupied(true);
                node.setDisable(false);
            }
        }
    }

    /**
     * initialise board and set up styling and game logic
     */
    @FXML
    private void initialize() {
        String css = this.getClass().getResource("/css/style.css").toExternalForm();        //for hover
        rootPane.getStylesheets().add(css);

        // hover effect for quit button
        hoverEffect(closeButton);
        hoverEffect(resetButton);

        initialiseHex();
        gameLogic = new GameLogic(rootPane);
        gameLogic.setSelectedColor(selectedColor);

    }

    /**
     * hover animation for the buttons
     *
     * @param button    button we want to add effect to
     */
    private void hoverEffect(Button button) {
        button.setOnMouseEntered(event -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
            st.setToX(1.2); st.setToY(1.2); st.play();
        });
        button.setOnMouseExited(event -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
            st.setToX(1.0); st.setToY(1.0); st.play();
        });
    }

    /**
     * initialise mouse click handlers and hexagon data
     */
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

    /**
     * switch current player and update UI
     */
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
        gameLogic.setSelectedColor(selectedColor);
    }

    /**
     * change colour of circle on a hexagon after a valid placement
     */
    public void changeColor(MouseEvent click) {
        Polygon hexagon = (Polygon) click.getSource();

        int hexagonX = (int) (hexagon.getLayoutX() + hexagon.getTranslateX());
        int hexagonY = (int) (hexagon.getLayoutY() + hexagon.getTranslateY());

        Circle matchingCircle = Utilities.findCircleByCoords(rootPane, hexagonX, hexagonY);

        if (matchingCircle != null) {
            matchingCircle.toFront();
            if ((matchingCircle.getFill() != Color.BLUE) && (matchingCircle.getFill() != Color.RED)) {
                matchingCircle.setFill(selectedColor);
            }
        }
    }

    /**
     * show coordinates of click hexagon
     *
     * @return  hexagon object corresponding to clicked polygon
     */
    public Hexagon showCoords(MouseEvent click) {
        Polygon clickedPolygon = (Polygon) click.getSource();
        Hexagon hex = (Hexagon) clickedPolygon.getUserData();
        if (hex != null) {
            String coords = "Q: " + hex.getQ() + ", R: " + hex.getR() + ", S: " + hex.getS();
            Coords.setText(coords);
        }
        return hex;
    }

    /**
     * split hexagon ID string into individual coordinates for hexagon object
     *
     * @param coordString   ID string e.g. 1_0_m1
     * @return  new hexagon instance
     */
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

    /**
     * click event handler for making a placement
     */
    public void onMouseClicked(MouseEvent event) {
        Hexagon hex = showCoords(event);
        if (hex.isOccupied()) {
            invalidMove.setText("Hexagon is occupied");
            return;
        }

        gameLogic.setSelectedColor(selectedColor);

        if (gameLogic.nonCaptureMove(hex)) {
            handleNCP(hex, event);
        }
        else if (!gameLogic.nonCaptureMove(hex) && gameLogic.validCapture(hex)) {
            handleCP(hex, event);
        }
        else {
            invalidMove.setText("Not a valid move");
        }
    }

    /**
     * handles NCP move
     *
     * @param hexagon   hexagon stone is placed on
     */
    private void handleNCP(Hexagon hexagon, MouseEvent event) {
        hexagon.setOccupied(true);
        changeColor(event);
        invalidMove.setText("Non-Capture Move");

        if (selectedColor == Color.RED) {
            redPlacementCounter++;
        } else bluePlacementCounter++;

        updateStoneCounters();

        if (!gameStarted && redPlacementCounter >= 1 && bluePlacementCounter >= 1) {
            gameStarted = true;
        }

        if (gameStarted) {
            checkWinCondition();
        }
        changePlayer();
    }

    /**
     * handles CP move
     *
     * @param hexagon   hexagon stone is placed on
     */
    private void handleCP(Hexagon hexagon, MouseEvent event) {
        hexagon.setOccupied(true);
        changeColor(event);
        invalidMove.setText("Capture Move");

        if (selectedColor == Color.RED) {
            redPlacementCounter++;
        } else bluePlacementCounter++;

        Set<Circle> circlesToRemove = new HashSet<>();
        gameLogic.removeCircles(hexagon, circlesToRemove);

        int capturedStones = circlesToRemove.size();
        if (selectedColor == Color.RED) {
            bluePlacementCounter -= capturedStones;
        } else {
            redPlacementCounter -= capturedStones;
        }
        updateStoneCounters();
        checkWinCondition();
    }

    /**
     * update UI to show number of stones of each colour
     */
    private void updateStoneCounters() {
        redCounter.setText("Red: " + redPlacementCounter);
        blueCounter.setText("Blue: " + bluePlacementCounter);
    }

    /**
     * checks if the game has been won
     */
    private void checkWinCondition() {
        if (bluePlacementCounter <= 0) {
            displayWinner(Color.RED, "RED WINS!", 295);
        }
        else if (redPlacementCounter <= 0) {
           displayWinner(Color.BLUE, "BLUE WINS!", 285);
        }
    }

    /**
     * update UI to display winner of the game and stop further input
     *
     * @param color winning colour
     * @param winMessage    winning message to display
     * @param layoutX   position for ensuring win text in centred
     */
    private void displayWinner(Color color, String winMessage, int layoutX) {
        for(Node node : rootPane.getChildren()) {
            if (node instanceof Label) {
                ((Label) node).setText(" ");
                ((Label) node).setTextFill(color);
            }
        }
        gameCircle.toBack();
        gameFlag.setFont(new Font(50));
        gameFlag.setLayoutX(layoutX);
        gameFlag.setText(winMessage);
        disableFurtherMoves();
    }

    /**
     * disable any further moves
     */
    private void disableFurtherMoves() {
        for (Node node : rootPane.getChildren()) {
            if (node instanceof Polygon) {
                node.setDisable(true);
            }
        }
    }
}