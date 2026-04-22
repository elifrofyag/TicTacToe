package org.ann;
import java.util.Scanner;

public class HumanPlayer extends Player{

    private Scanner scanner;

    public HumanPlayer(int token, Scanner scanner) {
        super(token);
        this.scanner = scanner;
    }

    @Override
    public boolean makeMove(Board board) {
        while (true) {

            String input = scanner.nextLine().trim();
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
                    System.out.println("Please, input a valid number [1-9]");
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

