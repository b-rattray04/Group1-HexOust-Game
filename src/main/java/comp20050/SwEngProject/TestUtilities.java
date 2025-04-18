package comp20050.SwEngProject;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
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

    @Test
    public void testInvalidHexagonNode() {
        AnchorPane rootPane = new AnchorPane();
        Polygon polygon = new Polygon();
        Hexagon hexagon = new Hexagon(100, 0, -100);
        polygon.setUserData(hexagon);

        rootPane.getChildren().add(polygon);

        Polygon result = Utilities.getHexagonNode(rootPane, 1, 0, -100);
        assertNull(result);

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

    @Test
    public void testFindCircleByCoords_NoMatch() {
        AnchorPane rootPane = mock(AnchorPane.class);
        Circle circle = mock(Circle.class);

        when(rootPane.getChildren()).thenReturn(FXCollections.observableArrayList(circle));
        when(circle.getLayoutX()).thenReturn(50.0);
        when(circle.getLayoutY()).thenReturn(50.0);

        Circle result = Utilities.findCircleByCoords(rootPane, 250.0, 250.0);
        assertNull(result);
    }

    @Test
    public void testFindCircleByCoords_MultipleCircles() {
       AnchorPane rootPane = new AnchorPane();
       Circle circle1 = new Circle();
       circle1.setLayoutX(50.0);
       circle1.setLayoutY(50.0);
       circle1.setId("circle1");

       Circle circle2 = new Circle();
       circle2.setLayoutX(500.0);
       circle2.setLayoutY(500.0);
       circle2.setId("circle2");

       rootPane.getChildren().addAll(circle1, circle2);

       Circle result = Utilities.findCircleByCoords(rootPane, 500.0, 500.0);
       assertEquals(circle2, result);
    }

    @Test
    public void testFindCircleByCoords_NonCircle() {
        AnchorPane rootPane = mock(AnchorPane.class);
        Polygon polygon = mock(Polygon.class);

        when(rootPane.getChildren()).thenReturn(FXCollections.observableArrayList(polygon));

        Circle result = Utilities.findCircleByCoords(rootPane, 100.0, 100.0);
        assertNull(result);
    }

    @Test
    public void testFindCircleByCoords_CloseProximity() {
        AnchorPane rootPane = mock(AnchorPane.class);
        Circle circle1 = mock(Circle.class);
        Circle circle2 = mock(Circle.class);

        when(circle1.getId()).thenReturn("circle1");
        when(circle1.getLayoutX()).thenReturn(100.0);
        when(circle1.getLayoutY()).thenReturn(100.0);

        when(circle2.getId()).thenReturn("circle2");
        when(circle2.getLayoutX()).thenReturn(101.0);
        when(circle2.getLayoutY()).thenReturn(101.0);

        when(rootPane.getChildren()).thenReturn(FXCollections.observableArrayList(circle1, circle2));
        Circle result = Utilities.findCircleByCoords(rootPane, 100.5, 100.5);

        assertEquals(circle1, result);
    }
}