package comp20050.SwEngProject;

import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

import java.util.HashSet;
import java.util.Set;

public class GameLogic {

    private AnchorPane rootPane;
    private Color selectedColor;
    // Global counters for stones
    private static int blueStoneCount = 0;
    private static int redStoneCount = 0;

    public GameLogic(AnchorPane rootPane) {
        this.rootPane = rootPane;
    }
    public void setSelectedColor(Color color) {
        this.selectedColor = color;
    }

    // Getter methods for stone counts
    public static int getBlueStoneCount() {
        return blueStoneCount;
    }

    public static int getRedStoneCount() {
        return redStoneCount;
    }

    // Reset stone counts (for new games)
    public static void resetStoneCounts() {
        blueStoneCount = 0;
        redStoneCount = 0;
    }

    // Increment the stone count for the placed color
    public void incrementStoneCount() {
        if (selectedColor == Color.BLUE) {
            blueStoneCount++;
        } else if (selectedColor == Color.RED) {
            redStoneCount++;
        }
    }

    public boolean nonCaptureMove(Hexagon hex) {
        int blueCount = 0;      //not including stone being placed
        int redCount = 0;

        for (Hexagon dir : Hexagon.directions) {
            int q = hex.getQ() + dir.getQ();
            int r = hex.getR() + dir.getR();
            int s = hex.getS() + dir.getS();
            Polygon adjacentHex = Utilities.getHexagonNode(rootPane, q, r, s);

            if(adjacentHex == null) continue;
            Circle adjacentCircle = Utilities.findCircleByCoords(
                    rootPane,
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

        if(blueCount == 0 && redCount == 0) { //if no immediate neighbours => non capture move
            return true;
        }

        if(selectedColor == Color.RED) {
            return redCount == 0;
        } else if(selectedColor == Color.BLUE) {
            return blueCount == 0;
        }
        return false;
    }

    public boolean validCapture(Hexagon hex) {
        return processOpponentGroups(hex, null);
    }

    public void removeCircles(Hexagon hexagon) {
        Set<Circle> circlesToRemove = new HashSet<>();
        processOpponentGroups(hexagon, circlesToRemove);

        for (Circle circle : circlesToRemove) {
            circle.setFill(Color.LIGHTGRAY);
            circle.toBack();
        }
        
        // After removing, check if there are still opponent stones on the board
        checkForWinner();
    }
    
    // Check if there are any opponent stones left on the board
    public Color checkForWinner() {
        Color opponentColor = (selectedColor == Color.RED) ? Color.BLUE : Color.RED;
        boolean opponentStonesExist = false;
        
        for (javafx.scene.Node node : rootPane.getChildren()) {
            if (node instanceof Circle) {
                Circle circle = (Circle) node;
                if (circle.getFill() == opponentColor) {
                    opponentStonesExist = true;
                    break;
                }
            }
        }
        
        // If no opponent stones exist, return the winner's color
        if (!opponentStonesExist) {
            return selectedColor;
        }
        
        return null; // No winner yet
    }

    private boolean processOpponentGroups(Hexagon hex, Set<Circle> circlesToRemove) {
        Color opponentColor = (selectedColor == Color.RED) ? Color.BLUE : Color.RED;

        Set<Hexagon> friendlyGroup = new HashSet<>();
        friendlyGroup.add(hex);
        findConnectedGroup(hex, selectedColor, friendlyGroup);

        System.out.println("Friendly group size: " + friendlyGroup.size());

        Set<Hexagon> processedOpponentHexes = new HashSet<>();
        boolean canCapture = false;

        for (Hexagon friendlyHex : friendlyGroup) {
            for (Hexagon dir : Hexagon.directions) {
                int q = friendlyHex.getQ() + dir.getQ();
                int r = friendlyHex.getR() + dir.getR();
                int s = friendlyHex.getS() + dir.getS();

                Polygon adjacentHex = Utilities.getHexagonNode(rootPane, q, r, s);
                if (adjacentHex == null) continue;

                Circle adjacentCircle = Utilities.findCircleByCoords(
                        rootPane,
                        adjacentHex.getLayoutX() + adjacentHex.getTranslateX(),
                        adjacentHex.getLayoutY() + adjacentHex.getTranslateY()
                );

                if (adjacentCircle != null && adjacentCircle.getFill() == opponentColor) {
                    Hexagon adjacentHexagon = (Hexagon) adjacentHex.getUserData();

                    if (!processedOpponentHexes.contains(adjacentHexagon)) {
                        Set<Hexagon> opponentGroup = new HashSet<>();
                        findConnectedGroup(adjacentHexagon, opponentColor, opponentGroup);

                        processedOpponentHexes.addAll(opponentGroup);                           // Add all to processed set to avoid recounting
                        
                        // Remove the log of opponent group size for cleaner output
                        // System.out.println("Opponent group size: " + opponentGroup.size());

                        if (friendlyGroup.size() > opponentGroup.size()) {
                            canCapture = true;

                            if (circlesToRemove != null) {
                                for (Hexagon capturedHex : opponentGroup) {
                                    Polygon hexPolygon = Utilities.getHexagonNode(rootPane,
                                            capturedHex.getQ(), capturedHex.getR(), capturedHex.getS());
                                    if (hexPolygon != null) {
                                        Circle circle = Utilities.findCircleByCoords(
                                                rootPane,
                                                hexPolygon.getLayoutX() + hexPolygon.getTranslateX(),
                                                hexPolygon.getLayoutY() + hexPolygon.getTranslateY()
                                        );
                                        if (circle != null && circle.getFill() == opponentColor) {
                                            circlesToRemove.add(circle);
                                            capturedHex.setUnoccupied(false);
                                        }
                                    }
                                }
                            } else {
                                // If we're just checking for validity, we can return early
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return canCapture;
    }


    private void findConnectedGroup(Hexagon hex, Color color, Set<Hexagon> group) {
        group.add(hex);

        for (Hexagon dir : Hexagon.directions) {
            int q = hex.getQ() + dir.getQ();
            int r = hex.getR() + dir.getR();
            int s = hex.getS() + dir.getS();
            Polygon adjacentHex = Utilities.getHexagonNode(rootPane, q, r, s);

            if (adjacentHex == null) continue;

            Hexagon adjacentHexagon = (Hexagon) adjacentHex.getUserData();
            if (group.contains(adjacentHexagon)) continue;      // Skip already visited hexagons

            Circle adjacentCircle = Utilities.findCircleByCoords(
                    rootPane,
                    adjacentHex.getLayoutX() + adjacentHex.getTranslateX(),
                    adjacentHex.getLayoutY() + adjacentHex.getTranslateY()
            );

            if (adjacentCircle != null && adjacentCircle.getFill() == color) {
                findConnectedGroup(adjacentHexagon, color, group);
            }
        }
    }
}