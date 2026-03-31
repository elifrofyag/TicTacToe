package org.ann;


public class App {
    public static void main(String[] args) {
        if (args.length != 1 || (!args[0].equals("1") && !args[0].equals("2"))) {
            System.out.println("Usage: 1 (human starts) or 2 (computer starts)");
            return;
        }

        Game game = new Game(Integer.parseInt(args[0]));
        game.start();
    }
}