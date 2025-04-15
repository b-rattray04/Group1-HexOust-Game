package comp20050.SwEngProject;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;

public class BoardUIController {
    private Color selectedColor = Color.RED;
    private boolean redTurn = true;
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
    @FXML
    private Label blueStoneCountLabel;
    @FXML
    private Label redStoneCountLabel;

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
        closeButton.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), closeButton);
            st.setToX(1.2);
            st.setToY(1.2);
            st.play();
        });

        closeButton.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), closeButton);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });

        initialiseHex();
        gameLogic = new GameLogic(rootPane);
        gameLogic.setSelectedColor(selectedColor);
        
        // Reset stone counts at the start of the game
        GameLogic.resetStoneCounts();
        updateStoneCountLabels();
    }
    
    // Update the UI with current stone counts
    private void updateStoneCountLabels() {
        if (blueStoneCountLabel != null) {
            blueStoneCountLabel.setText("Blue Stones: " + GameLogic.getBlueStoneCount());
        }
        if (redStoneCountLabel != null) {
            redStoneCountLabel.setText("Red Stones: " + GameLogic.getRedStoneCount());
        }
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
                // Increment stone count when a new stone is placed
                gameLogic.incrementStoneCount();
                // Update the stone count labels
                updateStoneCountLabels();
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

    // Show the winner popup
    private void showWinnerPopup(Color winnerColor) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("WinnerPopup.fxml"));
            Parent root = loader.load();
            
            // Get the winner label from the FXML
            Label winnerLabel = (Label) root.lookup("#winnerLabel");
            
            // Set the winner message and color
            String winnerText = winnerColor == Color.RED ? "Red Wins!" : "Blue Wins!";
            winnerLabel.setText(winnerText);
            winnerLabel.setTextFill(winnerColor);
            
            // Get the close button and set its action
            Button closePopupButton = (Button) root.lookup("#closeButton");
            
            // Create a new stage for the popup
            Stage popupStage = new Stage();
            popupStage.initStyle(StageStyle.UNDECORATED);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setScene(new Scene(root));
            
            // Set up close button action
            closePopupButton.setOnAction(event -> popupStage.close());
            
            // Show the popup
            popupStage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Check for a winner and show popup if there is one
    private void checkForWinnerAndShowPopup() {
        Color winner = gameLogic.checkForWinner();
        if (winner != null) {
            showWinnerPopup(winner);
        }
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
            invalidMove.setText("Non-Capture Move");
            changeColor(event);
            changePlayer();
        }
        else if (!gameLogic.nonCaptureMove(hex) && gameLogic.validCapture(hex)) {
            hex.setOccupied(true);
            invalidMove.setText("Capture Move");
            changeColor(event);
            gameLogic.removeCircles(hex);
            
            // Check for winner after a capture move
            checkForWinnerAndShowPopup();
            
            // Only change player if there's no winner
            if (gameLogic.checkForWinner() == null) {
                changePlayer();
            }
        }
        else {
            invalidMove.setText("Not a valid move");
        }
    }
}