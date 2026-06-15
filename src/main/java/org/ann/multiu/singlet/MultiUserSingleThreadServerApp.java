package org.ann.multiu.singlet;

import org.ann.Board;
import org.ann.ComputerPlayer;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * single thread server app to handle multiple users at the "same" time
 * this version, user created the initial board, so there is no "START" protocol
 *
 * @ see org.ann.http.Server and Client for the version where user send "START" and server init the board.
 */
public class MultiUserSingleThreadServerApp {
    public static void main(String[] args){
        int port = 8080;

        try (ServerSocket ss = new ServerSocket(port)){
            System.out.println("server started on port " + port );

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

    private static String processRequest(String request, PrintStream out) {
        // request format: "0,0,0,0,0,0,0,0,0;5" (board state ; human move)
        String[] parts = request.split(";");
        if (parts.length != 2) {
            return "INVALID_REQUEST_FORMAT;";
        }
        String boardState = parts[0];
        int humanMove = Integer.parseInt(parts[1]);

        Board board = new Board(out);
        board.deserialize(boardState);

        if (!board.isAvailable(humanMove)) {
            return "INVALID_MOVE;" + board.serialize();
        }

        board.placeMove(humanMove, 1);

        //check human win draw
        if (board.checkWinner() == 1) return "WIN_HUMAN;" + board.serialize();
        if (board.isFull()) return "DRAW;" + board.serialize();

        ComputerPlayer computer = new ComputerPlayer(2, out);
        computer.makeMove(board);

        //check computer win draw
        if (board.checkWinner() == 2) return "WIN_COMPUTER;" + board.serialize();
        if (board.isFull()) return "DRAW;" + board.serialize();

        return "CONTINUE;" + board.serialize();
    }
}
