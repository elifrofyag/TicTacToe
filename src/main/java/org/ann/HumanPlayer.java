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
                System.out.print("enter move [1-9]: ");
                if (scanner.hasNextInt()) {
                    int move = scanner.nextInt();
                    if (board.isAvailable(move)) {
                        board.placeMove(move, this.token);
                        break;
                    }
                } else {
                    scanner.next();
                }
            }
        }
    }

