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

    public GameLogic(AnchorPane rootPane) {
        this.rootPane = rootPane;
    }

    public void setSelectedColor(Color color) {
        this.selectedColor = color;
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

    public boolean validCapture(Hexagon hex) {
        int blueCount = 0;
        int redCount = 0;

        // Get opponent color
        Color opponentColor = (selectedColor == Color.RED) ? Color.BLUE : Color.RED;

        // Track adjacent opponent stones to check for capture
        Set<Hexagon> adjacentOpponentHexes = new HashSet<>();

        for (Hexagon dir : Hexagon.directions) {
            // Calculate neighbor coordinates
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
                if (adjacentColor == Color.RED) {
                    redCount++;

                    // If opponent is red, add to opponent hexes
                    if (opponentColor == Color.RED) {
                        Hexagon adjacentHexagon = (Hexagon) adjacentHex.getUserData();
                        adjacentOpponentHexes.add(adjacentHexagon);
                    }
                }
                if (adjacentColor == Color.BLUE) {
                    blueCount++;

                    // If opponent is blue, add to opponent hexes
                    if (opponentColor == Color.BLUE) {
                        Hexagon adjacentHexagon = (Hexagon) adjacentHex.getUserData();
                        adjacentOpponentHexes.add(adjacentHexagon);
                    }
                }
            }
        }

        System.out.println("Blue before placement " + blueCount);
        System.out.println("Red before placement " + redCount);

        // Basic validation checks
        if (blueCount == 0 && redCount == 0) {
            // No neighbors, invalid capture move
            return false;
        }

        if (selectedColor == Color.RED && redCount != 0 && blueCount == 0) {
            // Only red neighbors for red player, invalid capture
            return false;
        }

        if (selectedColor == Color.BLUE && blueCount != 0 && redCount == 0) {
            // Only blue neighbors for blue player, invalid capture
            return false;
        }

        // If there are no opponent stones adjacent, can't capture
        if (adjacentOpponentHexes.isEmpty()) {
            return false;
        }

        // Calculate the size of the friendly group (including the new stone)
        int friendlyGroupSize = 1; // Start with 1 for the new stone being placed
        Set<Hexagon> visited = new HashSet<>();

        // Add connected friendly stones to calculate group size
        for (Hexagon dir : Hexagon.directions) {
            int q = hex.getQ() + dir.getQ();
            int r = hex.getR() + dir.getR();
            int s = hex.getS() + dir.getS();

            Polygon adjacentHex = Utilities.getHexagonNode(rootPane, q, r, s);
            if (adjacentHex == null) continue;

            Circle adjacentCircle = Utilities.findCircleByCoords(
                    rootPane,
                    adjacentHex.getLayoutX() + adjacentHex.getTranslateX(),
                    adjacentHex.getLayoutY() + adjacentHex.getTranslateY()
            );

            if (adjacentCircle != null && adjacentCircle.getFill() == selectedColor) {
                Hexagon adjacentHexagon = (Hexagon) adjacentHex.getUserData();
                if (!visited.contains(adjacentHexagon)) {
                    friendlyGroupSize += findConnectedGroupSize(adjacentHexagon, selectedColor, visited);
                }
            }
        }

        // Check if we can capture any opponent group
        boolean canCapture = false;
        Set<Hexagon> processedOpponentHexes = new HashSet<>();

        for (Hexagon opponentHex : adjacentOpponentHexes) {
            if (!processedOpponentHexes.contains(opponentHex)) {
                // Find the size of this opponent group
                Set<Hexagon> opponentGroupVisited = new HashSet<>();
                int opponentGroupSize = findConnectedGroupSize(opponentHex, opponentColor, opponentGroupVisited);

                // Add all visited hexes to processed set to avoid recounting
                processedOpponentHexes.addAll(opponentGroupVisited);

                System.out.println("Friendly group size: " + friendlyGroupSize + ", Opponent group size: " + opponentGroupSize);

                // If our group is larger than this opponent group, we can capture it
                if (friendlyGroupSize > opponentGroupSize) {
                    canCapture = true;
                    break;
                }
            }
        }

        return canCapture;
    }

    // Recursively finds the size of a connected group of stones of the same color
    private int findConnectedGroupSize(Hexagon hex, Color color, Set<Hexagon> visited) {
        visited.add(hex);

        int groupSize = 1;

        // Check all neighbors
        for (Hexagon dir : Hexagon.directions) {
            int q = hex.getQ() + dir.getQ();
            int r = hex.getR() + dir.getR();
            int s = hex.getS() + dir.getS();

            Polygon adjacentHex = Utilities.getHexagonNode(rootPane, q, r, s);
            if (adjacentHex == null) continue;

            Hexagon adjacentHexagon = (Hexagon) adjacentHex.getUserData();
            if (visited.contains(adjacentHexagon)) continue; // Skip already visited hexagons

            Circle adjacentCircle = Utilities.findCircleByCoords(
                    rootPane,
                    adjacentHex.getLayoutX() + adjacentHex.getTranslateX(),
                    adjacentHex.getLayoutY() + adjacentHex.getTranslateY()
            );

            // If this neighbor has a stone of the same color, recursively add its group
            if (adjacentCircle != null && adjacentCircle.getFill() == color) {
                groupSize += findConnectedGroupSize(adjacentHexagon, color, visited);
            }
        }
        return groupSize;
    }

    private void findConnectedGroup(Hexagon hex, Color color, Set<Hexagon> group) {
        // Add this hexagon to the group
        group.add(hex);

        // Check all neighbors
        for (Hexagon dir : Hexagon.directions) {
            int q = hex.getQ() + dir.getQ();
            int r = hex.getR() + dir.getR();
            int s = hex.getS() + dir.getS();

            Polygon adjacentHex = Utilities.getHexagonNode(rootPane, q, r, s);
            if (adjacentHex == null) continue;

            Hexagon adjacentHexagon = (Hexagon) adjacentHex.getUserData();
            if (group.contains(adjacentHexagon)) continue; // Skip already visited hexagons

            Circle adjacentCircle = Utilities.findCircleByCoords(
                    rootPane,
                    adjacentHex.getLayoutX() + adjacentHex.getTranslateX(),
                    adjacentHex.getLayoutY() + adjacentHex.getTranslateY()
            );

            // If this neighbor has a stone of the same color, recursively add it to the group
            if (adjacentCircle != null && adjacentCircle.getFill() == color) {
                findConnectedGroup(adjacentHexagon, color, group);
            }
        }
    }

    public void removeCircles(Hexagon hexagon) {
        // Get opponent color
        Color opponentColor = (selectedColor == Color.RED) ? Color.BLUE : Color.RED;

        // Step 1: Find all hexagons in the friendly group (including the newly placed stone)
        Set<Hexagon> friendlyGroup = new HashSet<>();
        friendlyGroup.add(hexagon); // Add the newly placed stone

        // Find all connected friendly stones
        findConnectedGroup(hexagon, selectedColor, friendlyGroup);
        int friendlyGroupSize = friendlyGroup.size();

        System.out.println("Friendly group size: " + friendlyGroupSize);

        // Step 2: Find all distinct opponent groups adjacent to the friendly group
        Set<Hexagon> processedOpponentHexes = new HashSet<>();
        Set<Circle> circlesToRemove = new HashSet<>();

        // For each stone in the friendly group
        for (Hexagon friendlyHex : friendlyGroup) {
            // Check all neighbors
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

                // If this is an opponent stone and we haven't processed its group yet
                if (adjacentCircle != null && adjacentCircle.getFill() == opponentColor) {
                    Hexagon adjacentHexagon = (Hexagon) adjacentHex.getUserData();

                    if (!processedOpponentHexes.contains(adjacentHexagon)) {
                        // Find all stones in this opponent group
                        Set<Hexagon> opponentGroup = new HashSet<>();
                        findConnectedGroup(adjacentHexagon, opponentColor, opponentGroup);

                        // Add all to processed set to avoid recounting
                        processedOpponentHexes.addAll(opponentGroup);

                        int opponentGroupSize = opponentGroup.size();
                        System.out.println("Opponent group size: " + opponentGroupSize);

                        // If our group is larger, capture this opponent group
                        if (friendlyGroupSize > opponentGroupSize) {
                            // Find all circles in this group and mark them for removal
                            for (Hexagon capturedHex : opponentGroup) {
                                Polygon hexPolygon = Utilities.getHexagonNode(rootPane, capturedHex.getQ(), capturedHex.getR(), capturedHex.getS());
                                if (hexPolygon != null) {
                                    Circle circle = Utilities.findCircleByCoords(
                                            rootPane,
                                            hexPolygon.getLayoutX() + hexPolygon.getTranslateX(),
                                            hexPolygon.getLayoutY() + hexPolygon.getTranslateY()
                                    );
                                    if (circle != null && circle.getFill() == opponentColor) {
                                        circlesToRemove.add(circle);
                                        // Mark hexagon as unoccupied
                                        capturedHex.setUnoccupied(false);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Remove all captured stones
        for (Circle circle : circlesToRemove) {
            circle.setFill(Color.LIGHTGRAY);
            circle.toBack();
        }

        System.out.println("Removed " + circlesToRemove.size() + " opponent stones");
    }
}