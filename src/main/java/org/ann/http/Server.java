package org.ann.http;

import org.ann.Board;
import org.ann.ComputerPlayer;

import com.google.gson.Gson;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class Server {

    private static final Gson gson = new Gson();

    public static void main(String[] args){
        int port = 8080;
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/play", new PlayHandler());
            server.setExecutor(null);
            server.start();
            System.out.println("HTTP server on http://localhost:" + port + "/play");
        } catch (IOException e) {
            System.out.println("server err: " + e.getMessage());
        }
    }

    static class PlayHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {

            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Content-Type", "application/json");

            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    // read json payload from request
                    InputStream is = exchange.getRequestBody();
                    String requestBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    System.out.println("request body: " + requestBody);

                    // convert json to java obj GameRequest
                    GameRequest request = gson.fromJson(requestBody, GameRequest.class);


                    // process logic and get GameReponse
                    GameResponse response = processRequest(request);

                    // convert GameReponse to json
                    String responseJson = gson.toJson(response);
                    byte[] responseBytes = responseJson.getBytes(StandardCharsets.UTF_8);

                    // send
                    exchange.sendResponseHeaders(200, responseBytes.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(responseBytes);
                    os.close();
                } catch (Exception e) {
                    System.out.println("Server caught an error: " + e.getMessage());
                }

            } else {
                exchange.sendResponseHeaders(405, -1); //Method Not Allowed
            }
        }
    }

    private static GameResponse processRequest(GameRequest request) {
        if ("START".equals(request.action)) {
            Board initialBoard = new Board();
            return new GameResponse("CONTINUE", initialBoard.serialize());
        }

        if (request.boardState == null) {
            return new GameResponse("INVALID_REQUEST_FORMAT", "");
        }

        Board board = new Board();
        board.deserialize(request.boardState);

        if (!board.isValidCellNumber(request.move) || !board.isAvailable(request.move)) {
            return new GameResponse("INVALID_MOVE", board.serialize());
        }

        board.placeMove(request.move, 1);

        if (board.checkWinner() == 1) return new GameResponse("WIN_HUMAN", board.serialize());
        if (board.isFull()) return new GameResponse("DRAW", board.serialize());

        ComputerPlayer computer = new ComputerPlayer(2);
        computer.makeMove(board);

        if (board.checkWinner() == 2) return new GameResponse("WIN_COMPUTER", board.serialize());
        if (board.isFull()) return new GameResponse("DRAW", board.serialize());

        return new GameResponse("CONTINUE", board.serialize());
    }

    private static class GameRequest {
        String action;     // "START" "MOVE"
        String boardState; // "0,0,0,0,0,0,0,0,0"
        int move;          // 5
    }

    private static class GameResponse {
        String status;     // "CONTINUE", "WIN_HUMAN", "INVALID_MOVE"
        String boardState; // "0,0,0,0,1,0,0,0,0"

        public GameResponse(String status, String boardState) {
            this.status = status;
            this.boardState = boardState;
        }
    }
}

