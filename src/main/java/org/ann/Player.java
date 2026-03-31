package org.ann;

public abstract class Player {
    protected int token; // 1 for human, 2 for computer

    public Player(int token) {
        this.token = token;
    }

    public int getToken() {
        return token;
    }

    public abstract void makeMove(Board board);
}