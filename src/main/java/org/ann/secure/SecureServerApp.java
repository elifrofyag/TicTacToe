package org.ann.secure;

import org.ann.Board;
import org.ann.ComputerPlayer;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Scanner;


public class SecureServerApp {
    private static final String SECRET_KEY = "super_secret_key";
    private static final int PORT = 8080;

    public static void main(String[] args){

        try (ServerSocket ss = new ServerSocket(PORT)) {
            System.out.println("secure server started on port " + PORT );

            while (true){
                try (Socket clientSocket = ss.accept();
                     Scanner in = new Scanner(clientSocket.getInputStream());
                     PrintStream out = new PrintStream(clientSocket.getOutputStream(), true)) {

                    if (in.hasNextLine()) {
                        String request = in.nextLine();
                        String response = processRequest(request, out);
                        out.println(response);
                    }
                } catch (Exception e) {
                    System.out.println("Request error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Server Error: " + e.getMessage());
        }
    }

    /**
     * Simple signing mechanism using SHA-256 hash of the board state + secret key.
     * @param boardData
     * @return Base64-encoded hash string
     */
    private static String signBoard(String boardData) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String dataToSign = boardData + SECRET_KEY;
            byte[] hash = digest.digest(dataToSign.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to sign data", e);
        }
    }

    private static String processRequest(String request, PrintStream out) {

        if (request.equals("START")) {
            Board initialBoard = new Board(out);
            String serialized = initialBoard.serialize();
            String signature = signBoard(serialized);
            return "CONTINUE;" + serialized + ";" + signature;
        }

        // request format: (board state ; human move; signature)  "0,0,0,0,0,0,0,0,0;4;signature"
        String[] parts = request.split(";");
        if (parts.length != 3) {
            System.out.println("Invalid request format: " + request);
            return "CHEATER_DETECTED;0,0,0,0,0,0,0,0,0;null";
        }

        String boardState = parts[0];
        int humanMove = Integer.parseInt(parts[1]);
        String clientSignature = parts[2];

        // verify signature
        String expectedSignature = signBoard(boardState);
        if (!expectedSignature.equals(clientSignature)) {
            System.out.println("Invalid signature. Expected: " + expectedSignature + ", Received: " + clientSignature);
            return "CHEATER_DETECTED;0,0,0,0,0,0,0,0,0;null";
        }

        Board board = new Board(out);
        board.deserialize(boardState);

        if (!board.isAvailable(humanMove)) {
            return "INVALID_MOVE;" + board.serialize() + ";" + clientSignature;
        }

        board.placeMove(humanMove, 1);

        //check human win draw
        if (board.checkWinner() == 1) {
            String newBoardState = board.serialize();
            return "WIN_HUMAN;" + newBoardState + ";" + signBoard(newBoardState);
        }
        if (board.isFull()) {
            String newBoardState = board.serialize();
            return "DRAW;" + newBoardState + ";" + signBoard(newBoardState);
        }

        ComputerPlayer computer = new ComputerPlayer(2, out);
        computer.makeMove(board);

        //check computer win draw
        String finalBoardData = board.serialize();
        String finalSignature = signBoard(finalBoardData);

        if (board.checkWinner() == 2) return "WIN_COMPUTER;" + finalBoardData + ";" + finalSignature;
        if (board.isFull()) return "DRAW;" + finalBoardData + ";" + finalSignature;

        return "CONTINUE;" + finalBoardData + ";" + finalSignature;
    }
}
