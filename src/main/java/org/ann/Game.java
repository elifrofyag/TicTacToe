package org.ann;

import java.util.Scanner;

public class Game {
    private Board board;
    private Player human;
    private Player computer;
    private Player currentPlayer;

    public Game(int startArg) {
        board = new Board(System.out);
        Scanner scanner = new Scanner(System.in);
        human = new HumanPlayer(1, scanner);
        computer = new ComputerPlayer(2);
        currentPlayer = (startArg == 1) ? human : computer;
    }

    public void start() {
        board.printBoard();
        while (true) {
            currentPlayer.makeMove(board);
            board.printBoard();

            int winner = board.checkWinner();
            if (winner != 0) {
                System.out.println(winner == 1 ? "user won!" : "computer won!");
                break;
            }

            if (board.isFull()) {
                System.out.println("a draw!");
                break;
            }

            currentPlayer = (currentPlayer == human) ? computer : human;
        }
    }
}