package comp20050.SwEngProject;

import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

import java.util.HashSet;
import java.util.Set;

/**
 * Used for core game logic including move validation, non-capture and capture moves
 */
public class GameLogic {

    private AnchorPane rootPane;
    private Color selectedColor;

    /**
     * constructor creates new instance of gameLogic for provided rootPane
     *
     * @param rootPane  javaFX pane for the board
     */
    public GameLogic(AnchorPane rootPane) {
        this.rootPane = rootPane;
    }

    /**
     * set current player's colour
     *
     * @param color selected colour
     */
    public void setSelectedColor(Color color) {
        this.selectedColor = color;
    }

    /**
     * counts number of adjacent hexagons are occupied with each colour
     *
     * @param hex   hexagon to start at
     * @return returns an array containing red and blue counts
     */
    private int[] countAdjacentColors(Hexagon hex) {
        int blueCount = 0;
        int redCount = 0;

        for (Hexagon dir : Hexagon.directions) {
            Circle adjacentCircle = getAdjacentCircle(hex, dir);
            if (adjacentCircle != null) {
                Color color = (Color) adjacentCircle.getFill();
                if (color == Color.RED) redCount++;
                if (color == Color.BLUE) blueCount++;
            }
        }
        return new int[]{redCount, blueCount};
    }

    /**
     * checks for valid NCP
     *
     * @param hex   hexagon to check
     * @return  true if move is an NCP and is valid
     */
    public boolean nonCaptureMove(Hexagon hex) {
        int[] counts = countAdjacentColors(hex);
        int redCount = counts[0];
        int blueCount = counts[1];

        System.out.println("Red: " + redCount +  " | Blue: " + blueCount);

        if (redCount == 0 && blueCount == 0) return true;
        return selectedColor == Color.RED ? redCount == 0 : blueCount == 0;
    }


    /**
     * check if a move is a valid capture move
     *
     * @param hex   hexagon to test for valid capture
     * @return  true if capture is valid
     */
    public boolean validCapture(Hexagon hex) {
        return processOpponentGroups(hex, null);
    }

    /**
     * removes captured stones from the board
     *
     * @param hexagon   hexagon where stone was placed
     * @param circlesToRemove   set to store removed stones
     */
    public void removeCircles(Hexagon hexagon, Set<Circle> circlesToRemove) {
        if (circlesToRemove == null) return;
        processOpponentGroups(hexagon, circlesToRemove);
        for (Circle circle : circlesToRemove) {
            circle.setFill(Color.LIGHTGRAY);
            circle.toBack();
        }
    }

    /**
     * logic for processing groups that can be captured
     *
     * @param hex   placed hexagon
     * @param circlesToRemove   set to store captured groups
     * @return  true if a group can be captured
     */
    private boolean processOpponentGroups(Hexagon hex, Set<Circle> circlesToRemove) {
        Color opponentColor = (selectedColor == Color.RED) ? Color.BLUE : Color.RED;

        Set<Hexagon> friendlyGroup = getConnectedGroup(hex, selectedColor);
        int friendlyGroupSize = friendlyGroup.size();
        System.out.println("Friendly group size: " + friendlyGroupSize);

        Set<Hexagon> processedOpponentHexes = new HashSet<>();
        boolean canCapture = false;

        for (Hexagon friendlyHex : friendlyGroup) {
            for (Hexagon dir : Hexagon.directions) {
                Hexagon neighbour = getHexagonFromDirection(friendlyHex, dir);
                if (neighbour == null || processedOpponentHexes.contains(neighbour)) continue;

                Circle circle = getCircleFromHex(neighbour);

                if (circle != null && circle.getFill() == opponentColor) {
                    Set<Hexagon> opponentGroup = getConnectedGroup(neighbour, opponentColor);
                    processedOpponentHexes.addAll(opponentGroup);
                    int opponentGroupSize = opponentGroup.size();
                    System.out.println("Opponent group size: " + opponentGroupSize);

                    if (friendlyGroupSize > opponentGroupSize) {
                        canCapture = true;
                        if (circlesToRemove != null) {
                            captureOpponentGroup(opponentGroup, opponentColor, circlesToRemove);
                        } else {
                            return true;
                        }
                    }
                }
            }
        }
        return canCapture;
    }

    /**
     * add hexagons of a captured group to the captured set and change status to unoccupied
     *
     * @param group set of hexagons in the group
     * @param color opponent's colour
     * @param captured  set used to store the captured circles
     */
    private void captureOpponentGroup(Set<Hexagon> group, Color color, Set<Circle> captured) {
        for (Hexagon hex : group) {
            Circle circle = getCircleFromHex(hex);
            if (circle != null && circle.getFill() == color) {
                captured.add(circle);
                hex.setUnoccupied(false);
            }
        }
    }

    /**
     * find all connected hexagons with same colour starting from provided hexagon
     *
     * @param start hexagon to start at
     * @param color colour of hexagons
     * @return  returns set of connected hexagons with the same colour
     */
    protected Set<Hexagon> getConnectedGroup(Hexagon start, Color color) {
        Set<Hexagon> group = new HashSet<>();
        findConnectedGroup(start, color, group);
        return group;
    }

    /**
     * recursive method to find all hexagons connected to the provided hexagon with the same colour
     *
     * @param hex   current hexagon being checked
     * @param color colour of the hexagons
     * @param group group we are creating
     */
    public void findConnectedGroup(Hexagon hex, Color color, Set<Hexagon> group) {
        group.add(hex);

        for (Hexagon dir : Hexagon.directions) {
            Hexagon neighbour = getHexagonFromDirection(hex, dir);
            if (neighbour == null || group.contains(neighbour)) {
                continue;
            }
            Circle circle = getCircleFromHex(neighbour);

            if (circle != null && circle.getFill() == color) {
                findConnectedGroup(neighbour, color, group);
            }
        }
    }

    /**
     * finds hexagon in a given direction from centre hexagon
     *
     * @param center    centre hexagon
     * @param direction direction to look in
     * @return returns the hexagon, null if not found
     */
    private Hexagon getHexagonFromDirection(Hexagon center, Hexagon direction) {
        int q = center.getQ() + direction.getQ();
        int r = center.getR() + direction.getR();
        int s = center.getS() + direction.getS();
        Polygon hexPolygon = Utilities.getHexagonNode(rootPane, q, r, s);
        return (hexPolygon != null) ? (Hexagon) hexPolygon.getUserData() : null;
    }

    /**
     * finds the circle representing a stone placed on a hexagon
     *
     * @param hex   hexagon we want to check
     * @return  returns circle at the given coordinates, null if not found
     */
    private Circle getCircleFromHex(Hexagon hex) {
        Polygon polygon = Utilities.getHexagonNode(rootPane, hex.getQ(), hex.getR(), hex.getS());
        if (polygon == null) return null;
        return Utilities.findCircleByCoords(
                rootPane,
                polygon.getLayoutX() + polygon.getTranslateX(),
                polygon.getLayoutY() + polygon.getTranslateY()
        );
    }

    /**
     * Finds the circle on a neighbouring hexagon in a given direction
     *
     * @param center    centre hexagon
     * @param direction direction we want to look in
     * @return  returns the circle in the adjacent hexagon, null if no circle was found
     */
    private Circle getAdjacentCircle(Hexagon center, Hexagon direction) {
        Hexagon neighbor = getHexagonFromDirection(center, direction);
        return neighbor != null ? getCircleFromHex(neighbor) : null;
    }
}