package org.ann;

public class ComputerPlayer extends Player{
    public ComputerPlayer (int token){
        super(token);
    }

    @Override
    public void makeMove(Board board){
        System.out.println("computer's turn: ");

        for (int i = 0; i <= 9; i++){
            if (board.isAvailable(i)){
                board.placeMove(i, this.token);
                break;
            }
        }
    }

}
