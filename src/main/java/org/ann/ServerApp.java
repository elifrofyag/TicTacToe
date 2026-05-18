package org.ann;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerApp {
    public static void main(String[] args) {
        if (args.length != 1 || (!args[0].equals("1") && !args[0].equals("2"))) {
            System.out.println("Please, input a valid option [1-2]");
            return;
        }
        int port = 8080;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port + "waiting for client");

            Socket clientSocket = serverSocket.accept();
            System.out.println("client connected! starting game");

            Scanner networkIn = new Scanner(clientSocket.getInputStream());
            PrintStream networkOut = new PrintStream(clientSocket.getOutputStream(), true);

            Game game = new Game(Integer.parseInt(args[0]), networkIn, networkOut);
            game.start();

            clientSocket.close();
            System.out.println("game over. server shuts down");

        } catch (IOException e) {
            System.out.println("Server Error: " + e.getMessage());
        }
    }
}