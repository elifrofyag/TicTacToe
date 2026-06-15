package org.ann.http;

import org.ann.Board;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    private static final String URL = "http://127.0.0.1:8080/play";
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        Board localBoard = new Board(System.out);

        System.out.println("Hello!");

        GameRequest initRequest = new GameRequest("START", null, 0);
        GameResponse initResponse = sendRequestToServer(initRequest);
        if ("ERROR".equals(initResponse.status)) {
            System.out.println("Failed to initialize session with the server.");
            return;
        }

        System.out.println("init board state" + initResponse.boardState);

        localBoard.deserialize(initResponse.boardState);
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

                GameRequest turnRequest = new GameRequest("MOVE", localBoard.serialize(), move);
                GameResponse response = sendRequestToServer(turnRequest);

                if ("ERROR".equals(response.status)) {
                    System.out.println("Server communication failed mid-game.");
                    break;
                }

                localBoard.deserialize(response.boardState);
                localBoard.printBoard();

                switch (response.status) {
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

    private static GameResponse sendRequestToServer(GameRequest requestObj) {
        try {
            // gamerequest obj to json
            String jsonPayload = gson.toJson(requestObj);

            // build request
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
                    .build();

            //send request and get response
            HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() == 200) {
                return gson.fromJson(res.body(), GameResponse.class);
            } else {
                System.out.println("server returned unexpected HTTP status code: " + res.statusCode());
            }
        } catch (Exception e) {
            System.out.println("err communicating with server: " + e.getMessage());
        }

        GameResponse errRes = new GameResponse();
        errRes.status = "ERROR";
        errRes.boardState = "0,0,0,0,0,0,0,0,0";
        return errRes;
    }

    private static class GameRequest {
        String action;
        String boardState;
        int move;

        public GameRequest(String action, String boardState, int move) {
            this.action = action;
            this.boardState = boardState;
            this.move = move;
        }
    }

    private static class GameResponse {
        String status;
        String boardState;
    }
}

