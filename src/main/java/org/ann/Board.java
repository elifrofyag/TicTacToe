package org.ann;

public class Board {
    private int[] cells = new int[9];

    public void printBoard() {
        for (int i = 0; i < 9; i += 3) {
            System.out.println("-------------");
            System.out.println("| " + cells[i] + " | " + cells[i+1] + " | " + cells[i+2] + " |");
        }
        System.out.println("-------------");
    }

    public boolean isAvailable(int cellNumber) {
        if (cellNumber < 1 || cellNumber > 9) return false;
        return cells[cellNumber - 1] == 0;
    }

    public void placeMove(int cellNumber, int token){
        cells[cellNumber - 1] = token;
    }

    public boolean isFull() {
        for (int cell : cells) {
            if (cell == 0) return false;
        }
        return true;
    }

    public int checkWinner() {
        int[][] winConditions = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                {0, 4, 8}, {2, 4, 6}
        };

        for (int[] w : winConditions) {
            if (cells[w[0]] != 0 && cells[w[0]] == cells[w[1]] && cells[w[0]] == cells[w[2]]) {
                return cells[w[0]];
            }
        }
        return 0;
    }

    void setUpTestBoard(int[] testScenario) {
        System.arraycopy(testScenario, 0, cells, 0, testScenario.length);
    }

    int getCellValue(int cellNumber) {
        return cells[cellNumber - 1];
    }


}