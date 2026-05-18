package org.ann;

import java.util.Scanner;
import java.io.PrintStream;

public class HumanPlayer extends Player{

    private Scanner scanner;

    public HumanPlayer(int token, Scanner scanner, PrintStream out) {
        super(token, out);
        this.scanner = scanner;
    }

    @Override
    public boolean makeMove(Board board) {
        while (true) {
            String input;
            out.println("[TURN_PROMPT]");
            try {
                input = scanner.nextLine().trim();
            } catch (RuntimeException e) {
                System.out.println("unexpected exception while reading human input");
                return false;
            }
            if (input.equals("q")) {
                System.out.println("End of the game");
                return false;
            }
            if (input.isEmpty()) {
                continue;
            }

            try {
                int move = Integer.parseInt(input);
                if (!board.isValidCellNumber(move)){
                    continue;
                }
                if (board.isAvailable(move)) {
                    board.placeMove(move, this.token);
                    break;
                } else {
                    System.out.println("The cell is occupied!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please, input a valid number [1-9]");
            }
        }
        return true;
    }
}

