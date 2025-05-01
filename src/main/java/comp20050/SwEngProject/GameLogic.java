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

    public boolean nonCaptureMove(Hexagon hex) {
        int[] counts = countAdjacentColors(hex);
        int redCount = counts[0];
        int blueCount = counts[1];

        System.out.println("Red: " + redCount +  " | Blue: " + blueCount);

        if (redCount == 0 && blueCount == 0) return true;
        return selectedColor == Color.RED ? redCount == 0 : blueCount == 0;
    }


    public boolean validCapture(Hexagon hex) {
        return processOpponentGroups(hex, null);
    }

    public void removeCircles(Hexagon hexagon, Set<Circle> circlesToRemove) {
        if (circlesToRemove == null) return;
        processOpponentGroups(hexagon, circlesToRemove);
        for (Circle circle : circlesToRemove) {

            circle.setFill(Color.LIGHTGRAY);
            circle.toBack();
        }
    }

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

    private void captureOpponentGroup(Set<Hexagon> group, Color color, Set<Circle> captured) {
        for (Hexagon hex : group) {
            Circle circle = getCircleFromHex(hex);
            if (circle != null && circle.getFill() == color) {
                captured.add(circle);
                hex.setUnoccupied(false);
            }
        }
    }

    private Set<Hexagon> getConnectedGroup(Hexagon start, Color color) {
        Set<Hexagon> group = new HashSet<>();
        findConnectedGroup(start, color, group);
        return group;
    }

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

    private Hexagon getHexagonFromDirection(Hexagon center, Hexagon direction) {
        int q = center.getQ() + direction.getQ();
        int r = center.getR() + direction.getR();
        int s = center.getS() + direction.getS();
        Polygon hexPolygon = Utilities.getHexagonNode(rootPane, q, r, s);
        return (hexPolygon != null) ? (Hexagon) hexPolygon.getUserData() : null;
    }

    private Circle getCircleFromHex(Hexagon hex) {
        Polygon polygon = Utilities.getHexagonNode(rootPane, hex.getQ(), hex.getR(), hex.getS());
        if (polygon == null) return null;
        return Utilities.findCircleByCoords(
                rootPane,
                polygon.getLayoutX() + polygon.getTranslateX(),
                polygon.getLayoutY() + polygon.getTranslateY()
        );
    }

    private Circle getAdjacentCircle(Hexagon center, Hexagon direction) {
        Hexagon neighbor = getHexagonFromDirection(center, direction);
        return neighbor != null ? getCircleFromHex(neighbor) : null;
    }
}