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
}
