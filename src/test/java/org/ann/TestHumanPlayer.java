package org.ann;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
public class TestHumanPlayer {
    private Board board;

    @BeforeEach
    public void setUp(){
        board = new Board(System.out);
    }

    /**
     * Helper method to fake keyboard input.
     * converts a String -> InputStream for Scanner to read
     */
    private Scanner createMockScanner(String simulatedInput) {
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        return new Scanner(in);
    }

    @Test
    public void testMakeMove_ValidInputFirstTry() {
        Scanner mock = createMockScanner("5" + System.lineSeparator());
        HumanPlayer human = new HumanPlayer(1, mock, System.out);

        human.makeMove(board);

        int res = board.getCellValue(5);
        assertEquals(1,res,"Cell 5 should have value 1 meaning occupied by human");
    }

    //scenario: cell 5 already occupied, makeMove on cell 5 -> should not allow, makeMove again on cell 3
    @Test
    public void testMakeMove_OccupiedCellThenValidMove() {
        // Arrange: Pre-fill cell 5 so it's taken
        board.placeMove(5, 2);

        Scanner fakeKeyboard = createMockScanner("5" + System.lineSeparator()+ "3" + System.lineSeparator());
        HumanPlayer human = new HumanPlayer(1, fakeKeyboard, System.out);

        human.makeMove(board);

        assertFalse(board.isAvailable(3), "Cell 3 should be occupied after recovering from an invalid move");
    }

    //scenario: 15 (invalid) then -2 (invalid) then 9 (valid)
    @Test
    public void testMakeMove_OutOfBoundsThenValidMove() {
        Scanner fakeKeyboard = createMockScanner("15\n-2\n9\n");
        HumanPlayer human = new HumanPlayer(1, fakeKeyboard, System.out);

        human.makeMove(board);

        assertFalse(board.isAvailable(9), "Cell 9 should be occupied after recovering from out-of-bounds inputs");
    }

    //scenario: letters (invalid) then valid int input to test the 'else {scanner.next();}' line in HumanPlayer
    @Test
    public void testMakeMove_TextInputThenValidMove() {
        Scanner fakeKeyboard = createMockScanner("hello\n1\n");
        HumanPlayer human = new HumanPlayer(1, fakeKeyboard, System.out);
        human.makeMove(board);

        assertFalse(board.isAvailable(1), "Cell 1 should be occupied after recovering from string input");
    }

    // scenario: place two Human tokens to set up a win, simulate human type 3 to take cell 3
    // check if human actually won to test if the correct token was placed, like if  HumanPlayer accidentally placed a '2' -> checkWinner would be 0.
    @Test
    public void testMakeMove_PlacesCorrectToken() {
        board.placeMove(1, 1);
        board.placeMove(2, 1);

        Scanner fakeKeyboard = createMockScanner("3\n");
        HumanPlayer human = new HumanPlayer(1, fakeKeyboard, System.out);

        human.makeMove(board);

        assertEquals(1, board.checkWinner(), "Human should have triggered a win with token 1");
    }
}

