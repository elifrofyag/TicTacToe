package org.ann;

import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class MultiUserSingleThreadClientApp {
    private static final String HOST = "127.0.0.1";
    private static int PORT = 8080;

    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        Board localBoard = new Board(System.out);

        System.out.println("Hello!");
        localBoard.printBoard();

        loop:
        while (true) {
            System.out.println("Player#" + "1" + "'s turn");
            String input = keyboard.nextLine().trim();

            if (input.equals("q")) {
                System.out.println("End of the game");
                return;
            }
            try {
                int move = Integer.parseInt(input);
                if (!localBoard.isValidCellNumber(move)) {
                    continue;
                }

                if (!localBoard.isAvailable(move)) {
                    System.out.println("The cell is occupied!");
                    continue;
                }
                String request = localBoard.serialize() + ";" + move;
                String response = sendRequestToServer(request);

                //parse response and update local board
                String[] parts = response.split(";");
                String status = parts[0];
                String boardState = parts[1];
                localBoard.deserialize(boardState);
                localBoard.printBoard();

                switch (status) {
                    case "WIN_HUMAN":
                        System.out.println("Player#1 won!");
                        break loop;
                    case "WIN_COMPUTER":
                        System.out.println("Player#2 won!");
                        break loop;
                    case "DRAW":
                        System.out.println("It is a draw!");
                        break loop;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please, input a valid number [1-9]");
            }


        }
    }

    private static String sendRequestToServer(String request) {
        try (Socket socket = new Socket(HOST, PORT);
             Scanner in = new Scanner(socket.getInputStream());
             PrintStream out = new PrintStream(socket.getOutputStream(), true)) {

            out.println(request);
            if (in.hasNextLine()) {
                return in.nextLine();
            } else {
                System.out.println("No response from server.");
                return "ERROR;0,0,0,0,0,0,0,0,0";
            }
        } catch (Exception e) {
            System.out.println("Error communicating with server: " + e.getMessage());
        }
        return "ERROR;0,0,0,0,0,0,0,0,0";


    }
}
