package comp20050.SwEngProject;

import java.util.ArrayList;

public class Hexagon {
    protected final int q;
    protected final int r;
    protected final int s;
    private boolean occupied = false;

    public Hexagon(int q, int r, int s) {
        this.q = q;
        this.r = r;
        this.s = s;
        if (q + r + s != 0) {
            throw new IllegalArgumentException("q + r + s must be 0.");
        }
    }

    public void setOccupied(boolean occupied) {
        this.occupied = true;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public int getQ() {
        return q;
    }

    public int getR() {
        return r;
    }

    public int getS() {
        return s;
    }

    static public ArrayList<Hexagon> directions = new ArrayList<Hexagon>() {{
        add(new Hexagon(1, 0, -1));     // SE 0
        add(new Hexagon(0, 1, -1));     // S  1
        add(new Hexagon(-1, 1, 0));     // SW 2
        add(new Hexagon(-1, 0, 1));     // NW 3
        add(new Hexagon(0, -1, 1));     // N  4
        add(new Hexagon(1, -1, 0));     // NE 5
    }};
}