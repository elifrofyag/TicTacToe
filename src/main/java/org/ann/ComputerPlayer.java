package org.ann;

public class ComputerPlayer extends Player{
    public ComputerPlayer (int token){
        super(token);
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
