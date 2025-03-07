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

    public void setUnoccupied(boolean unoccupied) {
        this.occupied = false;
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
    public int getS() { return s; }

    public Hexagon add(Hexagon h) {
        return new Hexagon(q + h.q, r + h.r, s + h.s);
    }

    public Hexagon subtract(Hexagon h) {
        return new Hexagon(q - h.q, r - h.r, s - h.s);
    }

    public int length() {
        return (int)((Math.abs(q) + Math.abs(r) + Math.abs(s)) / 2);
    }

    public int distance(Hexagon h) {
        return subtract(h).length();
    }

    public Hexagon adjacent(int direction)
    {
        return add(Hexagon.direction(direction));
    }

    static public ArrayList<Hexagon> directions = new ArrayList<Hexagon>(){{add(new Hexagon(1, 0, -1)); add(new Hexagon(0, 1, -1)); add(new Hexagon(-1, 1, 0)); add(new Hexagon(-1, 0, 1)); add(new Hexagon(0, -1, 1)); add(new Hexagon(1, -1, 0));}};
    //(1, 0, -1): SE    0
    //(0, 1, -1): S     1
    //(-1, 1, 0): SW    2
    //(-1, 0, 1): NW    3
    //(0, -1, 1): N     4
    //(1, -1, 0): NE    5
    static public Hexagon direction(int direction)
    {
        return Hexagon.directions.get(direction);
    }


}



