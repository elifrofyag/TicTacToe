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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class AntiReplaySecureServerApp {
    private static final String SECRET_KEY = "super_secret_key";
    private static final int PORT = 8080;
    private static final ConcurrentHashMap<String, Long> usedNonces = new ConcurrentHashMap<>();

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
                    System.out.println(" [server 35] client request error: " + e.getMessage());
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
    private static String signBoard(String boardData, long deadline, String nonce) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String dataToSign = boardData + deadline + nonce + SECRET_KEY;
            byte[] hash = digest.digest(dataToSign.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to sign data", e);
        }
    }

    /**
     * Run through usedNonces set and clean up all expired nonces;
     * this is called everytime server process a new request from client
     */
    private static void cleanUpExpiredNonces(){
        long now = System.currentTimeMillis();
        usedNonces.entrySet().removeIf(entry -> (now > entry.getValue()));
    }

    private static String processRequest(String request, PrintStream out) {
        cleanUpExpiredNonces();

        if (request.equals("START")) {
            Board initialBoard = new Board(out);
            return generateResponse("CONTINUE", initialBoard);
        }

        // request format: (board state ; human move; deadline; nonce; signature)
        String[] parts = request.split(";");
        if (parts.length != 5) {
            System.out.println("Invalid request format: " + request);
            return "CHEATER_DETECTED;0,0,0,0,0,0,0,0,0;0;null;null";
        }

        String boardState = parts[0];
        int humanMove = Integer.parseInt(parts[1]);
        long deadlineFromClient = Long.parseLong(parts[2]);
        String nonceFromClient = parts[3];
        String signatureFromClient = parts[4];

        // check deadline
        long now = System.currentTimeMillis();
        if (now > deadlineFromClient){
            System.out.println("Request timeout. Deadline: " + deadlineFromClient + ", Now: " + now);
            return "TIMEOUT;0,0,0,0,0,0,0,0,0;0;null;null";
        }
        // check signature
        String expectedSignature = signBoard(boardState, deadlineFromClient, nonceFromClient);
        if (!expectedSignature.equals(signatureFromClient)) {
            System.out.println("Invalid signature. Expected: " + expectedSignature + ", Received: " + signatureFromClient);
            return "CHEATER_DETECTED;0,0,0,0,0,0,0,0,0;null";
        }
        // check replay attack
        if (usedNonces.containsKey(nonceFromClient)) {
            System.out.println("Replay attack detected with nonce: " + nonceFromClient);
            return "REPLAY_ATTACK;0,0,0,0,0,0,0,0,0;0;null;null";
        }

        usedNonces.put(nonceFromClient, deadlineFromClient);

        Board board = new Board(out);
        board.deserialize(boardState);

        if (!board.isValidCellNumber(humanMove) || !board.isAvailable(humanMove)) {
            return generateResponse("INVALID_MOVE", board);
        }

        board.placeMove(humanMove, 1);
        if (board.checkWinner() == 1) return generateResponse("WIN_HUMAN", board);
        if (board.isFull()) return generateResponse("DRAW", board);

        ComputerPlayer computer = new ComputerPlayer(2, out);
        computer.makeMove(board);
        if (board.checkWinner() == 2) return generateResponse("WIN_COMPUTER", board);
        if (board.isFull()) return generateResponse("DRAW", board);

        return generateResponse("CONTINUE", board);
    }

    // response format: status; new board state; deadline; nonce; signature(board, deadline, nonce)
    private static String generateResponse(String status, Board board){
        String newBoardState = board.serialize();
        long newDeadline = System.currentTimeMillis() + 10000;
        String newNonce = UUID.randomUUID().toString();
        String newSignature = signBoard(newBoardState, newDeadline,newNonce);
        return status + ";" + newBoardState + ";" + newDeadline + ";" + newNonce + ";" + newSignature;
    }
}
