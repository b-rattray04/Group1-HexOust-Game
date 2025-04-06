package comp20050.SwEngProject;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestUtilities {

    @BeforeAll
    public static void initialiseJavaFX() {
        Platform.startup(() -> {});
    }

    // test finding an existing hexagon
    @Test
    public void testGetHexagonNode() {
        AnchorPane rootPane = mock(AnchorPane.class);
        Polygon hexagon = mock(Polygon.class);
        Hexagon hexData = new Hexagon(1, 0, -1);

        when(rootPane.getChildren()).thenReturn(FXCollections.observableArrayList(hexagon));
        when(hexagon.getUserData()).thenReturn(hexData);

        Polygon result = Utilities.getHexagonNode(rootPane, 1, 0, -1);
        assertEquals(hexagon, result);
    }

    // test finding an existing circle
    @Test
    public void testFindCircleByCoords() {
        AnchorPane rootPane = mock(AnchorPane.class);
        Circle circle = mock(Circle.class);

        when(rootPane.getChildren()).thenReturn(FXCollections.observableArrayList(circle)); // returns list of UI elements for children of rootPane
        when(circle.getId()).thenReturn("someId");
        when(circle.getLayoutX()).thenReturn(100.0);
        when(circle.getLayoutY()).thenReturn(200.0);

        Circle result = Utilities.findCircleByCoords(rootPane, 100.5, 200.5);
        assertEquals(circle, result);
    }
}