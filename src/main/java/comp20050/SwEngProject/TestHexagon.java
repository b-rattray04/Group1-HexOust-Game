package comp20050.SwEngProject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestHexagon {
    @Test
    public void testHexagonConstructor() {
        Hexagon hex = new Hexagon(2, -1, -1);
        assertEquals(2, hex.getQ());
        assertEquals(-1, hex.getR());
        assertEquals(-1, hex.getS());
        assertFalse(hex.isOccupied());
    }

    @Test
    public void invalidHexCoords() {
        assertThrows(IllegalArgumentException.class, () -> {
            Hexagon hex = new Hexagon(1, 1, 1);
        });
    }

    @Test
    public void testOccupied() {
        Hexagon hex = new Hexagon(0, 0, 0);

        hex.setOccupied(true);
        assertTrue(hex.isOccupied());

        hex.setUnoccupied(false);
        assertFalse(hex.isOccupied());
    }

    @Test
    public void testEdgeCoords() {
        Hexagon hex = new Hexagon(0, -6, 6);

        assertEquals(0, hex.getQ());
        assertEquals(-6, hex.getR());
        assertEquals(6, hex.getS());
    }

    @Test
    public void testSetOccupied() {
        Hexagon hex = new Hexagon(1, -1, 0);
        hex.setOccupied(true);
        assert(hex.isOccupied());
    }

    @Test
    public void testSetUnoccupied() {
        Hexagon hex = new Hexagon(1, -1, 0);
        hex.setOccupied(true);
        hex.setUnoccupied(false);
        assert(!hex.isOccupied());
    }
}
