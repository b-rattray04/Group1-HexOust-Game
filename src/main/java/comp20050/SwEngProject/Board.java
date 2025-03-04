package comp20050.SwEngProject;

import java.util.ArrayList;

public class Board {
    private final Layout layout;
    private final ArrayList<ArrayList<Point>> grid;

    public Board(double size, double originX, double originY, int sides) {
        this.layout = new Layout(Layout.flat, new Point(size, size), new Point(originX, originY));
        this.grid = new ArrayList<>();

        for (int q = -sides; q <= sides; q++) {
            for (int r = -sides; r <= sides; r++) {
                for (int s = -sides; s <= sides; s++) {
                    if (q + r + s == 0) { // Ensure valid hex coordinates
                        Hexagon hex = new Hexagon(q, r, s);
                        grid.add(layout.polygonCorners(hex));
                    }
                }
            }
        }
    }

    public ArrayList<Hexagon> getNeighbors(Hexagon hex, int sides) {
        ArrayList<Hexagon> neighbors = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Hexagon neighbor = hex.adjacent(i);
            if (Math.abs(neighbor.q) <= sides && Math.abs(neighbor.r) <= sides && Math.abs(neighbor.s) <= sides) {
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }

    public ArrayList<ArrayList<Point>> getGrid() {
        return grid;
    }

    // example of running the getNeighbours method
    public static void main(String[] args) {
        Board board = new Board(30, 400, 335, 3); // size = radius of hexs, origin points = centre coords, sides = num hexes from the centre

        Hexagon myHex = new Hexagon(0, 0, 0);
        ArrayList<Hexagon> adjacentHexes = board.getNeighbors(myHex, 3);

        System.out.println("Neighbors of (" + myHex.q + "," + myHex.r + "," + myHex.s + "):");
        for (Hexagon neighbor : adjacentHexes) {
            System.out.println(" -> (" + neighbor.q + "," + neighbor.r + "," + neighbor.s + ")");
        }
    }
}
