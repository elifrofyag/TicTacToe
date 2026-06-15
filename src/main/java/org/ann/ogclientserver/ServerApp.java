package org.ann.ogclientserver;

import org.ann.Game;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerApp {
    public static void main(String[] args) {
//        if (args.length != 1 || (!args[0].equals("1") && !args[0].equals("2"))) {
//            System.out.println("Please, input a valid option [1-2]");
//            return;
//        }
        int port = 8080;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("server started on port " + port + " waiting for client");
            while (true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("client connected from " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());

                Scanner networkIn = new Scanner(clientSocket.getInputStream());
                PrintStream networkOut = new PrintStream(clientSocket.getOutputStream(), true);

                Game game = new Game(1, networkIn, networkOut);
                game.start();

                clientSocket.close();
                System.out.println("client disconnected");
            }
        } catch (IOException e) {
            System.out.println("Server Error: " + e.getMessage());
        }
    }
}