package org.ann;

import java.io.PrintStream;

public class ComputerPlayer extends Player{
    public ComputerPlayer (int token, PrintStream out) {
        super(token, out);
    }

    public ComputerPlayer(int token){
        super(token);
    }

    @Override
    public boolean makeMove(Board board){
        for (int i = 1; i <= Board.SIZE * Board.SIZE; i++){
            if (board.isAvailable(i)){
                board.placeMove(i, this.token);
                break;
            }
        }
        return true;
    }

}
