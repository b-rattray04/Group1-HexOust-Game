package comp20050.SwEngProject;

import java.util.ArrayList;
import java.util.Objects;

// represents hexagon tile in the grid
// Uses axial coords q,r,s
public class Hexagon {
    protected final int q; // coordinate q
    protected final int r; // coordinate r
    protected final int s; // coordinate s
    private boolean occupied = false; // track if hexagon is occupied

    // constructs hexagon with given coords, throws exception if their sum != 0
    public Hexagon(int q, int r, int s) {
        this.q = q;
        this.r = r;
        this.s = s;
        if (q + r + s != 0) {
            throw new IllegalArgumentException("q + r + s must be 0.");
        }
    }

    // marks hexagon as occupied -> true if occupied, false if not
    public void setOccupied(boolean occupied) {
        this.occupied = true;
    }

    // marks hexagon as unoccupied -> true if unoccupied, false if not
    public void setUnoccupied(boolean unoccupied) {
        this.occupied = false;
    }

    // checks if hexagon is occupied -> true if occupied, false if not
    public boolean isOccupied() {
        return occupied;
    }

    // getters for coords
    public int getQ() {
        return q;
    }
    public int getR() {
        return r;
    }
    public int getS() { return s; }

    // returns new hexagon object that is the sum of this hexagon and another
    public Hexagon add(Hexagon h) {
        return new Hexagon(q + h.q, r + h.r, s + h.s);
    }

    // returns new hexagon object that is the difference of this hexagon and another
    public Hexagon subtract(Hexagon h) {
        return new Hexagon(q - h.q, r - h.r, s - h.s);
    }

    // Calculates distance of this hexagon from the origin
    public int length() {
        return ((Math.abs(q) + Math.abs(r) + Math.abs(s)) / 2);
    }

    // Calculates distance to another hexagon (h = target hexagon)
    public int distance(Hexagon h) {
        return subtract(h).length();
    }

    /*
    predefined directions for hexagonal grid using axial coords
    (1, 0, -1): SE    0
    (0, 1, -1): S     1
    (-1, 1, 0): SW    2
    (-1, 0, 1): NW    3
    (0, -1, 1): N     4
    (1, -1, 0): NE    5
     */
    static public ArrayList<Hexagon> directions = new ArrayList<Hexagon>(){{add(new Hexagon(1, 0, -1)); add(new Hexagon(0, 1, -1)); add(new Hexagon(-1, 1, 0)); add(new Hexagon(-1, 0, 1)); add(new Hexagon(0, -1, 1)); add(new Hexagon(1, -1, 0));}};

    // Retrieves hexagon direction for a given index in the ArrayList i.e. direction(0) returns (1,0,-1)
    static public Hexagon direction(int direction)
    {
        return Hexagon.directions.get(direction);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hexagon hex = (Hexagon) o;
        return q == hex.q && r == hex.r && s == hex.s;
    }

    @Override
    public int hashCode() {
        return Objects.hash(q, r, s);
    }
}