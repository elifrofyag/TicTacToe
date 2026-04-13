package org.ann;


public class App {
    public static void main(String[] args) {
        if (args.length != 1 || (!args[0].equals("1") && !args[0].equals("2"))) {
                System.out.println("Please, input a valid option [1-2]");
                return;
        }
        Game game = new Game(Integer.parseInt(args[0]));
        game.start();
    }
}