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
        System.out.println("Hello!");
        board.printBoard();

        Thread gameThread = new Thread(() -> {
            while (true) {
                currentPlayer.makeMove(board);
                board.printBoard();

                int winner = board.checkWinner();
                if (winner != 0) {
                    System.out.println(winner == 1 ? "Player#1 won!" : "Player#2 won!");
                    break;
                }

                if (board.isFull()) {
                    System.out.println("It is a draw!");
                    break;
                }

                currentPlayer = (currentPlayer == human) ? computer : human;
            }
        });
        gameThread.start();

    }
}