package comp20050.SwEngProject;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private final Hexagon[][] board;
    int[] numHexes = {7, 8, 9, 10, 11, 12, 13, 12, 11, 10, 9, 8, 7};

    public Board() {
        board = new Hexagon[13][13];
        initialize();
    }

    private void initialize() {
        for(int i = 0; i < 13; i++) {
            for (int j = 0; j < numHexes[i]; j++) {
                board[i][j] = new Hexagon(i, j);
            }
        }
    }

    public Hexagon getBoard(int x, int y) {
        if (x >= 0 && x < 13 && y >= 0 && y < numHexes[x]) {
            return board[x][y];
        }
        return null;
    }

    public int[] findCoords(int index)  {
        int current = 0;

        for (int i =  0;  i < numHexes.length; i++) {
            for (int j = 0; j < numHexes[i]; j++) {
                if (current == index) {
                    return new int[]{i, j};
                }
                current++;
            }
        }
        return null;
    }

    public Hexagon getHexagon(int index) {
        int[] coords = findCoords(index);
        if (coords != null) {
            return getBoard(coords[0], coords[1]);
        }
        return null;
    }

    public List<Hexagon> findAdjacentHexagons(int x, int y) {
        List<Hexagon> adjacent = new ArrayList<Hexagon>();

        int[][] directions = {{-1, 1}, {0, 1}, {1, 1}, {1, -1}, {0, -1}, {-1, -1}};

        for (int[] direction : directions) {
            int newX = x + direction[0];
            int newY = y + direction[1];

            if (newX >= 0 && newX < 13 && newY >= 0 && newY < numHexes[newX]) {
                adjacent.add(getBoard(newX, newY));
            }
        }
        return adjacent;
    }
}
