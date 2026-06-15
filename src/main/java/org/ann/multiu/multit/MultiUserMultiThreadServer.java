package org.ann.multiu.multit;

import org.ann.Game;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * server app which runs multiple threads for multiple user at the same time, each thread handle one client.;
 * client app of this multiusermultithread @see org.ann.ogclientserver.ClientApp
 */
public class MultiUserMultiThreadServer {
    private static final int PORT = 8080;
    private static final int MAX_THREADS = 3;

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(MAX_THREADS);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("server started on PORT " + PORT + " waiting for client");
            while (true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("client connected from " +
                        clientSocket.getInetAddress().getHostAddress()
                        + ":" + clientSocket.getPort());

                pool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.out.println("Server Error: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }
}

class ClientHandler implements Runnable {
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                Scanner networkIn = new Scanner(socket.getInputStream());
                PrintStream networkOut = new PrintStream(socket.getOutputStream(), true)
        ) {
            Game game = new Game(1, networkIn, networkOut);
            game.start();
            socket.close();
            System.out.println("client "+ socket.getInetAddress()+":"+socket.getPort()+" disconnected");
        } catch (IOException e) {
            System.out.println("client error: " + e.getMessage());
        }
    }
}
}
