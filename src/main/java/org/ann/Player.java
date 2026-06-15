package org.ann;

import java.io.PrintStream;

public abstract class Player {
    protected int token; // 1 for human, 2 for computer
    protected PrintStream out;

    public Player(int token, PrintStream out) {
        this.token = token;
        this.out = out;
    }
    public Player(int token){
        this.token = token;
    }

    public int getToken() {
        return token;
    }

    public abstract boolean makeMove(Board board);
}