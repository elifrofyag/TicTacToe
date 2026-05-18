package org.ann;

import java.io.PrintStream;
import java.util.Scanner;

public class Game {
    private Board board;
    private Player human;
    private Player computer;
    private Player currentPlayer;
    private PrintStream out;

    public Game(int startArg, Scanner in, PrintStream out) {
        this.out = out;
        board = new Board(out);
        human = new HumanPlayer(1, in, out);
        computer = new ComputerPlayer(2, out);
        currentPlayer = (startArg == 1) ? human : computer;
    }

    public void start() {
        out.println("Hello!");
        board.printBoard();

//        Thread gameThread = new Thread(() -> {
            while (true) {
                out.println("Player#" + currentPlayer.token + "'s turn");

                boolean shouldContinue = currentPlayer.makeMove(board);
                if (!shouldContinue) {
                    return;
                }
                board.printBoard();

                int winner = board.checkWinner();
                if (winner != 0) {
                    out.println(winner == 1 ? "Player#1 won!" : "Player#2 won!");
                    out.println("[GAME_OVER]");
                    return;
                }

                if (board.isFull()) {
                    out.println("It is a draw!");
                    out.println("[GAME_OVER]");
                    return;
                }

                currentPlayer = (currentPlayer == human) ? computer : human;
            }
//        });
//        gameThread.start();

    }
}