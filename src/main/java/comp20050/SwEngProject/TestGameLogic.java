package comp20050.SwEngProject;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.layout.AnchorPane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestGameLogic {

    private GameLogic gameLogic;
    private AnchorPane mockRootPane;

    @BeforeAll
    public static void initialiseJavaFX() {
        Platform.startup(() -> {});
    }

    @BeforeEach
    public void setUp() {
        mockRootPane = mock(AnchorPane.class);
        Polygon hexagon = mock(Polygon.class);
        when(mockRootPane.getChildren()).thenReturn(FXCollections.observableArrayList(hexagon));
        gameLogic = new GameLogic(mockRootPane);
    }

    private void placeHexagon(AnchorPane rootPane, Hexagon hex, Color color) {
        Polygon polygon = new Polygon();
        polygon.setUserData(hex);
        polygon.setLayoutX(100 + 50 * hex.getQ());
        polygon.setLayoutY(100 + 50 * hex.getR());

        Circle circle = new Circle(10);
        circle.setFill(color);
        circle.setLayoutX(polygon.getLayoutX());
        circle.setLayoutY(polygon.getLayoutY());

        circle.setId(hex.getQ() + "_" + hex.getR() + "_" + hex.getS());

        rootPane.getChildren().addAll(polygon, circle);
    }


    @Test
    public void testNoAdjacentStonesNCP() {
        Hexagon hex = new Hexagon(0, 0, 0);
        gameLogic.setSelectedColor(Color.RED);

        when(Utilities.getHexagonNode(mockRootPane, 1, 0, -1)).thenReturn(null);
        when(Utilities.getHexagonNode(mockRootPane, 0, 1, -1)).thenReturn(null);
        when(Utilities.getHexagonNode(mockRootPane, -1, 1, 0)).thenReturn(null);
        when(Utilities.getHexagonNode(mockRootPane, -1, 0, 1)).thenReturn(null);
        when(Utilities.getHexagonNode(mockRootPane, 0, -1, 1)).thenReturn(null);
        when(Utilities.getHexagonNode(mockRootPane, 1, -1, 0)).thenReturn(null);

        boolean isValidMove = gameLogic.nonCaptureMove(hex);

        assertTrue(isValidMove);
    }

    @Test
    public void testRemoveCircles() {
        GameLogic gameLogic = new GameLogic(mockRootPane);
        Set<Circle> circlesToRemove = new HashSet<>();

        Hexagon hex = new Hexagon(0, 0, 0);
        Circle opponentCircle = new Circle();
        opponentCircle.setFill(Color.BLUE);

        circlesToRemove.add(opponentCircle);

        gameLogic.removeCircles(hex, circlesToRemove);

        assertTrue(circlesToRemove.contains(opponentCircle));
        assertEquals(Color.LIGHTGRAY, opponentCircle.getFill());
    }

    @Test
    public void testSingleHexGroup() {
        Hexagon hex = new Hexagon(0, 0, 0);
        gameLogic.setSelectedColor(Color.RED);

        Set<Hexagon> group = gameLogic.getConnectedGroup(hex, Color.RED);
        assertTrue(group.contains(hex));
    }

    @Test
    public void testConnectedGroup() {
        AnchorPane rootPane = new AnchorPane();
        gameLogic = new GameLogic(rootPane);
        gameLogic.setSelectedColor(Color.RED);

        Hexagon hex1 = new Hexagon(0, 0, 0);
        Hexagon hex2 = new Hexagon(1, -1, 0);
        Hexagon hex3 = new Hexagon(2, -2, 0); // not connected to group

        placeHexagon(rootPane, hex1, Color.RED);
        placeHexagon(rootPane, hex2, Color.RED);

        Set<Hexagon> group = gameLogic.getConnectedGroup(hex1, Color.RED);

        assertEquals(2, group.size());
        assertTrue(group.contains(hex1));
        assertTrue(group.contains(hex2));
        assertFalse(group.contains(hex3));
    }

    @Test
    public void testValidCaptureRealNodes() {
        AnchorPane rootPane = new AnchorPane();
        gameLogic = new GameLogic(rootPane);
        gameLogic.setSelectedColor(Color.RED);

        Hexagon red1 = new Hexagon(0, 0, 0);
        Hexagon red2 = new Hexagon(1, -1, 0);
        Hexagon blue = new Hexagon(1, 0, -1);

        placeHexagon(rootPane, red1, Color.RED);
        placeHexagon(rootPane, red2, Color.RED);
        placeHexagon(rootPane, blue, Color.BLUE);

        boolean canCapture = gameLogic.validCapture(red1);
        assertTrue(canCapture);
    }

}
