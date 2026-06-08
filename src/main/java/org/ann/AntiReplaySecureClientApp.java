package org.ann;

import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class AntiReplaySecureClientApp {
    private static final String HOST = "127.0.0.1";
    private static int PORT = 8080;

    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        Board localBoard = new Board(System.out);

        String currentSignature = "";
        String currentNonce = "";
        String currentDeadline ="";

        System.out.println("Hello!");

        String initResponse = sendRequestToServer("START");
        if (initResponse.startsWith("ERROR")) {
            System.out.println("failed to init secure session with server: " + initResponse);
            return;
        }

        String[] initParts = initResponse.split(";");
        // initParts[0] is "CONTINUE"
        localBoard.deserialize(initParts[1]);
        currentDeadline = initParts[2];
        currentNonce = initParts[3];
        currentSignature = initParts[4];

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
                String request = localBoard.serialize() + ";" + move + ";" + currentDeadline + ";" + currentNonce + ";" + currentSignature;
                String response = sendRequestToServer(request);

                //parse response and update local board
                String[] parts = response.split(";");
                String status = parts[0];

                if (status.equals("CHEATER_DETECTED")) {
                    System.out.println("Server rejected move: security token mismatch!");
                    break loop;
                } else if (status.equals("TIMEOUT")){
                    System.out.println("you took longer than 10 sec. game over");
                    break loop;
                } else if (status.equals("REPLAY_ATTACK")){
                    System.out.println("sever rejected move: replay attack detected!");
                    break loop;
                } else if (status.equals("INVALID_MOVE")){
                    System.out.println("server rejected move: cell is occupied or invalid ");
                }

                String boardState = parts[1];
                localBoard.deserialize(boardState);

                currentDeadline = parts[2];
                currentNonce = parts[3];
                currentSignature = parts[4];

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

            System.out.println("sent: " + request);
            out.println(request);
            if (in.hasNextLine()) {
                return in.nextLine();
            } else {
                System.out.println("No response from server.");
                return "ERROR;0,0,0,0,0,0,0,0,0;0;null;null";            }
        } catch (Exception e) {
            System.out.println("Error communicating with server: " + e.getMessage());
        }
        return "ERROR;0,0,0,0,0,0,0,0,0;0;null;null";
    }
}


