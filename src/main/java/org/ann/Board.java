package org.ann;

import java.io.PrintStream;

public class Board {
    public static final int SIZE = 4;

    private int[] cells = new int[SIZE * SIZE];
    private PrintStream printer;

    public Board(PrintStream out) {
         this.printer = out;
    }

    public Board(){}

    public void printBoard() {
        for(int i = 0; i < SIZE; i++){
            for (int j = 0; j < SIZE; j++) {
                int cellValue = cells[i * SIZE + j];
                printer.print(" | " + cellValue);
            }
            printer.println(" | ");
        }
    }

    public boolean isValidCellNumber(int cellNumber) {
        if (cellNumber < 1 || cellNumber > SIZE*SIZE) {
            printer.println("Please, input a valid number [1-9]");
            return false;
        }
        return true;
    }

    public boolean isAvailable(int cellNumber) {
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
        // check row
        for (int i = 0; i < SIZE; i++) {
            int firstCell = cells[i * SIZE];
            if (firstCell == 0) continue;

            boolean win = true;
            for (int j = 1; j < SIZE; j++) {
                if (cells[i * SIZE + j] != firstCell) {
                    win = false;
                    break;
                }
            }
            if (win) return firstCell;
        }

        // check col
        for (int j = 0; j < SIZE; j++) {
            int firstCell = cells[j];
            if (firstCell == 0) continue;

            boolean win = true;
            for (int i = 1; i < SIZE; i++) {
                if (cells[i * SIZE + j] != firstCell) {
                    win = false;
                    break;
                }
            }
            if (win) return firstCell;
        }

        // diagonal top left bottom right
        int diagFirst = cells[0];
        if (diagFirst != 0) {
            boolean win = true;
            for (int i = 1; i < SIZE; i++) {
                if (cells[i * SIZE + i] != diagFirst) {
                    win = false;
                    break;
                }
            }
            if (win) return diagFirst;
        }

        // diagonal top right bottom left
        int antiDiagFirst = cells[SIZE - 1];
        if (antiDiagFirst != 0) {
            boolean win = true;
            for (int i = 1; i < SIZE; i++) {
                if (cells[i * SIZE + (SIZE - 1 - i)] != antiDiagFirst) {
                    win = false;
                    break;
                }
            }
            if (win) return antiDiagFirst;
        }

        return 0; // no winner
    }

    void setUpTestBoard(int[] testScenario) {
        System.arraycopy(testScenario, 0, cells, 0, testScenario.length);
    }

    int getCellValue(int cellNumber) {
        return cells[cellNumber - 1];
    }

    // convert board array into a comma-separated string
    public String serialize() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cells.length; i++) {
            sb.append(cells[i]);
            if (i < cells.length - 1) sb.append(",");
        }
        return sb.toString();
    }

    // convert comma-separated string back into board array
    public void deserialize(String data) {
        String[] parts = data.split(",");
        for (int i = 0; i < cells.length; i++) {
            cells[i] = Integer.parseInt(parts[i]);
        }
    }


}