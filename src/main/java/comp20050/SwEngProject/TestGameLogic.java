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

    /*
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

    @Test
    public void testInvalidNCP() {
        AnchorPane rootPane = new AnchorPane();
        gameLogic = new GameLogic(rootPane);
        gameLogic.setSelectedColor(Color.RED);

        Hexagon hex = new Hexagon(0, 0, 0);
        Hexagon adjacent = new Hexagon(1, -1, 0);

        Polygon polygon = new Polygon();
        polygon.setUserData(adjacent);
        polygon.setLayoutX(150);
        polygon.setLayoutY(100);
        rootPane.getChildren().add(polygon);

        Circle red = new Circle(10);
        red.setFill(Color.RED);
        red.setLayoutX(polygon.getLayoutX());
        red.setLayoutY(polygon.getLayoutY());
        red.setId(adjacent.getQ() + "_" + adjacent.getR() + "_" + adjacent.getS());
        rootPane.getChildren().add(red);

        boolean isValidMove = gameLogic.nonCaptureMove(hex);
        assertFalse(isValidMove);
    }

    @Test
    public void testInvalidCP() {
        Hexagon hex = new Hexagon(0, 0, 0);
        gameLogic.setSelectedColor(Color.RED);

        for (Hexagon dir : Hexagon.directions) {
            when(Utilities.getHexagonNode(mockRootPane, hex.getQ() + dir.getQ(),
                    hex.getR() + dir.getR(), hex.getS() + dir.getS())).thenReturn(null);
        }

        boolean canCapture = gameLogic.validCapture(hex);
        assertFalse(canCapture);
    }

    @Test
    public void testCPLogic() {
        AnchorPane rootPane = new AnchorPane();
        GameLogic gameLogic = new GameLogic(rootPane);
        gameLogic.setSelectedColor(Color.RED);

        Hexagon red1 = new Hexagon(0, 0, 0);
        Hexagon red2 = new Hexagon(1, -1, 0);

        Polygon redPolygon1 = new Polygon(); redPolygon1.setUserData(red1);
        Polygon redPolygon2 = new Polygon(); redPolygon2.setUserData(red2);
        redPolygon1.setLayoutX(100); redPolygon1.setLayoutY(100);
        redPolygon2.setLayoutX(150); redPolygon2.setLayoutY(50);

        Circle redCircle1 = new Circle(10); redCircle1.setFill(Color.RED);
        Circle redCircle2 = new Circle(10); redCircle2.setFill(Color.RED);
        redCircle1.setLayoutX(100); redCircle1.setLayoutY(100);
        redCircle2.setLayoutX(150); redCircle2.setLayoutY(50);

        redCircle1.setId(red1.getQ() + "_" + red1.getR() + "_" + red1.getS());
        redCircle2.setId(red2.getQ() + "_" + red2.getR() + "_" + red2.getS());

        Hexagon blue = new Hexagon(1, 0, -1);
        Polygon bluePolygon = new Polygon(); bluePolygon.setUserData(blue);
        bluePolygon.setLayoutX(125); bluePolygon.setLayoutY(75);

        Circle blueCircle = new Circle(10); blueCircle.setFill(Color.BLUE);
        blueCircle.setLayoutX(125); blueCircle.setLayoutY(75);
        blueCircle.setId(blue.getQ() + "_" + blue.getR() + "_" + blue.getS());

        rootPane.getChildren().addAll(
                redPolygon1, redCircle1,
                redPolygon2, redCircle2,
                bluePolygon, blueCircle
        );

        Set<Circle> captured = new HashSet<>();
        gameLogic.removeCircles(red1, captured);

        assertTrue(captured.contains(blueCircle));
        assertEquals(Color.LIGHTGRAY, blueCircle.getFill());
    }

    @Test
    public void testValidCaptureSmallerGroup() {
        AnchorPane rootPane = new AnchorPane();
        gameLogic = new GameLogic(rootPane);
        gameLogic.setSelectedColor(Color.RED);

        Hexagon red1 = new Hexagon(0, 0, 0);
        Hexagon blue1 = new Hexagon(1, 0, -1);
        Hexagon blue2 = new Hexagon(2, -1, -1);

        placeHexagon(rootPane, red1, Color.RED);
        placeHexagon(rootPane, blue1, Color.BLUE);
        placeHexagon(rootPane, blue2, Color.BLUE);

        boolean result = gameLogic.validCapture(red1);
        assertFalse(result);
    }

    @Test
    public void testNCPMixedColors() {
        AnchorPane rootPane = new AnchorPane();
        gameLogic = new GameLogic(rootPane);
        gameLogic.setSelectedColor(Color.RED);

        Hexagon center = new Hexagon(0, 0, 0);
        Hexagon redNeighbor = new Hexagon(1, -1, 0);
        Hexagon blueNeighbor = new Hexagon(-1, 1, 0);

        placeHexagon(rootPane, redNeighbor, Color.RED);
        placeHexagon(rootPane, blueNeighbor, Color.BLUE);

        boolean result = gameLogic.nonCaptureMove(center);
        assertFalse(result);
    }

    @Test
    public void testCPEqualSize() {
        AnchorPane rootPane = new AnchorPane();
        gameLogic = new GameLogic(rootPane);
        gameLogic.setSelectedColor(Color.RED);

        Hexagon red1 = new Hexagon(0, 0, 0);
        Hexagon red2 = new Hexagon(1, -1, 0);
        Hexagon blue1 = new Hexagon(1, 0, -1);
        Hexagon blue2 = new Hexagon(2, -1, -1);

        placeHexagon(rootPane, red1, Color.RED);
        placeHexagon(rootPane, red2, Color.RED);
        placeHexagon(rootPane, blue1, Color.BLUE);
        placeHexagon(rootPane, blue2, Color.BLUE);

        boolean result = gameLogic.validCapture(red1);
        assertFalse(result);
    }

    @Test
    public void testGetConnectedGroup() {
        AnchorPane rootPane = new AnchorPane();
        gameLogic = new GameLogic(rootPane);
        gameLogic.setSelectedColor(Color.RED);

        Hexagon h1 = new Hexagon(0, 0, 0);
        Hexagon h2 = new Hexagon(1, -1, 0);
        Hexagon h3 = new Hexagon(1, 0, -1);
        Hexagon h4 = new Hexagon(0, 1, -1);
        Hexagon h5 = new Hexagon(-1, 1, 0);
        Hexagon h6 = new Hexagon(-1, 0, 1);

        placeHexagon(rootPane, h1, Color.RED);
        placeHexagon(rootPane, h2, Color.RED);
        placeHexagon(rootPane, h3, Color.RED);
        placeHexagon(rootPane, h4, Color.RED);
        placeHexagon(rootPane, h5, Color.RED);
        placeHexagon(rootPane, h6, Color.RED);

        Set<Hexagon> group = gameLogic.getConnectedGroup(h1, Color.RED);
        assertEquals(6, group.size());
    }

    @Test
    public void testGetHexagonFromDirectionReturnsNull() {
        Hexagon base = new Hexagon(0, 0, 0);
        for (Hexagon dir : Hexagon.directions) {
            when(Utilities.getHexagonNode(mockRootPane,
                    base.getQ() + dir.getQ(),
                    base.getR() + dir.getR(),
                    base.getS() + dir.getS())).thenReturn(null);
        }
        Hexagon result = gameLogic.getHexagonFromDirection(base, Hexagon.directions.get(0));
        assertNull(result);
    }

    @Test
    public void testGetCircleFromHexNullPoly() {
        Hexagon hex = new Hexagon(0, 0, 0);
        when(Utilities.getHexagonNode(mockRootPane, hex.getQ(), hex.getR(), hex.getS())).thenReturn(null);
        Circle circle = gameLogic.getCircleFromHex(hex);
        assertNull(circle);
    }

     */


}
