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

    @FXML
    public void handleClose(MouseEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

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

    @FXML
    private void resetGame() {
        // 1. Reset all game state variables
        selectedColor = Color.RED;
        redTurn = true;
        gameStarted = false;
        redPlacementCounter = 0;
        bluePlacementCounter = 0;

        // 2. Reset UI text elements
        gameFlag.setText("Red To Play");
        gameFlag.setFont(Font.font("Chalkboard", 24));
        gameFlag.setLayoutX(335); // Adjust to your original position
        for(Node node : rootPane.getChildren()) {
            if (node instanceof Label && node != gameFlag) {
                ((Label) node).setText(" ");
                ((Label) node).setTextFill(Color.BLACK);
            }
        }
        gameFlag.setTextFill(Color.BLACK); // Reset text color
        gameCircle.setFill(Color.RED);
        gameCircle.toFront();
        invalidMove.setText("");
        Coords.setText("");
        updateStoneCounters();

        // Create temporary list to avoid concurrent modification
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

        for(Node node : rootPane.getChildren()) {
            if(node instanceof Polygon) {
                Polygon hexagon = (Polygon) node;
                //get the hexagon object
                Hexagon hex = (Hexagon) hexagon.getUserData();
                hex.setUnoccupied(true);
                node.setDisable(false);
            }
        }

        gameLogic = new GameLogic(rootPane);
        gameLogic.setSelectedColor(selectedColor);

        //UI refresh
        rootPane.requestLayout();
    }

    @FXML
    private void initialize() {
        String css = this.getClass().getResource("/css/style.css").toExternalForm();        //for hover
        rootPane.getStylesheets().add(css);

        // hover effect for quit button
        closeButton.setOnMouseEntered(event -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), closeButton);
            st.setToX(1.2);
            st.setToY(1.2);
            st.play();
        });

        closeButton.setOnMouseExited(event -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), closeButton);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });

        // hover effect for restart button
        resetButton.setOnMouseEntered(event -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), resetButton);
            st.setToX(1.2);
            st.setToY(1.2);
            st.play();
        });

        resetButton.setOnMouseExited(event -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), resetButton);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });

        initialiseHex();
        gameLogic = new GameLogic(rootPane);
        gameLogic.setSelectedColor(selectedColor);

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

    public Hexagon showCoords(MouseEvent click) {
        Polygon clickedPolygon = (Polygon) click.getSource();
        Hexagon hex = (Hexagon) clickedPolygon.getUserData();
        if (hex != null) {
            String coords = "Q: " + hex.getQ() + ", R: " + hex.getR() + ", S: " + hex.getS();
            Coords.setText(coords);
        }
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

    public void onMouseClicked(MouseEvent event) {
        Hexagon hex = showCoords(event);
        if (hex.isOccupied()) {
            invalidMove.setText("Hexagon is occupied");
            return;
        }

        gameLogic.setSelectedColor(selectedColor);

        if (gameLogic.nonCaptureMove(hex)) {
            hex.setOccupied(true);
            changeColor(event);
            invalidMove.setText("Non-Capture Move");

            if (selectedColor == Color.RED) {
                redPlacementCounter++;
            } else bluePlacementCounter++;

            updateStoneCounters();

            // Check if both players have placed at least one stone
            if (!gameStarted && redPlacementCounter >= 1 && bluePlacementCounter >= 1) {
                gameStarted = true;
            }

            // Only check win condition after the game has "officially" started
            if (gameStarted) {
                checkWinCondition();
            }

            changePlayer();
        }
        else if (!gameLogic.nonCaptureMove(hex) && gameLogic.validCapture(hex)) {
            hex.setOccupied(true);
            invalidMove.setText("Capture Move");
            changeColor(event);

            if (selectedColor == Color.RED) {
                redPlacementCounter++;
            } else bluePlacementCounter++;



            Set<Circle> circlesToRemove = new HashSet<>();
            gameLogic.removeCircles(hex, circlesToRemove);

            int capturedStones = circlesToRemove.size();
            if (selectedColor == Color.RED) {
                bluePlacementCounter -= capturedStones;
            } else {
                redPlacementCounter -= capturedStones;
            }

            updateStoneCounters();
            checkWinCondition();
        }
        else {
            invalidMove.setText("Not a valid move");
        }
    }

    private void updateStoneCounters() {
        redCounter.setText("Red: " + redPlacementCounter);
        blueCounter.setText("Blue: " + bluePlacementCounter);
    }

    private void checkWinCondition() {
        if (bluePlacementCounter <= 0) {
            for(Node node : rootPane.getChildren()) {
                if (node instanceof Label) {
                    ((Label) node).setText(" ");
                    ((Label) node).setTextFill(Color.RED);
                }
            }
            gameCircle.toBack();
            gameFlag.setFont(new Font(50));
            gameFlag.setLayoutX(295);
            gameFlag.setText("RED WINS!");
            disableFurtherMoves();
        }
        else if (redPlacementCounter <= 0) {
            for(Node node : rootPane.getChildren()) {
                if (node instanceof Label) {
                    ((Label) node).setText(" ");
                    ((Label) node).setTextFill(Color.BLUE);
                }
            }
            gameCircle.toBack();
            gameFlag.setFont(new Font(50));
            gameFlag.setLayoutX(285);
            gameFlag.setText("BLUE WINS!");
            disableFurtherMoves();
        }
    }

    private void disableFurtherMoves() {
        for (Node node : rootPane.getChildren()) {
            if (node instanceof Polygon) {
                node.setDisable(true);
            }
        }
    }

}