package org.ann;
import java.util.Scanner;

public class HumanPlayer extends Player{

    private Scanner scanner;

    public HumanPlayer(int token, Scanner scanner) {
        super(token);
        this.scanner = scanner;
    }

    @Override
    public void makeMove(Board board) {
        while (true) {
            System.out.println("Player#" + this.token + "'s turn");
            if (scanner.hasNextInt()) {
                int move = scanner.nextInt();
                if (!board.isValidCellNumber(move)){
                    continue;
                }
                if (board.isAvailable(move)) {
                    board.placeMove(move, this.token);
                    break;
                } else {
                    System.out.println("The cell is occupied!");
                }
            } else {
                System.out.println("Please, input a valid number [1-9]");
                scanner.next();
            }
        }
    }
}

