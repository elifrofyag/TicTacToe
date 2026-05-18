package org.ann;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientApp {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 8080;

        try (Socket socket = new Socket(host, port);
             Scanner serverIn = new Scanner(socket.getInputStream());
             PrintStream serverOut = new PrintStream(socket.getOutputStream(), true);
             Scanner keyboard = new Scanner(System.in)) {

            System.out.println("connected to tictactoe server");

            while (serverIn.hasNextLine()) {
                String message = serverIn.nextLine();

                if (message.equals("[TURN_PROMPT]")) {
                    // protocol: server is asking for input from human player
                    String userInput = keyboard.nextLine();
                    serverOut.println(userInput);
                }
                else if (message.equals("[GAME_OVER]")) {
                    // protocol: server announce game has ended
                    break;
                }
                else {
                    // standard message: print it to human's screen
                    System.out.println(message);
                }
            }
            System.out.println("Disconnected from server.");

        } catch (IOException e) {
            System.out.println("Could not connect to server at " + host + ":" + port);
        }
    }
}