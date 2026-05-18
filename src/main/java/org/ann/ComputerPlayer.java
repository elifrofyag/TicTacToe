package org.ann;

import java.io.PrintStream;

public class ComputerPlayer extends Player{
    public ComputerPlayer (int token, PrintStream out) {
        super(token, out);
    }

    @Override
    public boolean makeMove(Board board){
        for (int i = 1; i <= 9; i++){
            if (board.isAvailable(i)){
                board.placeMove(i, this.token);
                break;
            }
        }
        return true;
    }

}
