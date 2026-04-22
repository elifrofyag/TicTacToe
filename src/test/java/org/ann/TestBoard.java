package org.ann;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;
import java.io.*;



public class TestBoard {
    private Board board;

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        board = new Board(new PrintStream(outputStreamCaptor));
    }

    // unit test: check if the board makeMove correctly updates the corresponding cell with the player's token
    @Test
    public void testBoardPlaceMove(){
        board.placeMove(1, 1);
        int cellValue = board.getCellValue(1);
        assertEquals(1, cellValue, "Cell 1 should be updated to 1 after the move");
    }

    // unit test: check if the board isAvailable correctly identifies whether a cell is available for a move
    @Test
    public void testIsAvailable(){
        board.setUpTestBoard(new int[]{1, 2, 0, 0, 0, 0, 0, 0, 0});
        assertFalse(board.isAvailable(2), "Cell 2 should not be available");
        assertTrue(board.isAvailable(3), "Cell 3 should be available");
    }

    //unit test: check if the board isValidCellNumber correctly identifies valid and invalid cell numbers
    @Test
    public void testIsValidCellNumber(){
        assertFalse(board.isValidCellNumber(0), "Cell 0 should not be available (out of bounds)");
        assertFalse(board.isValidCellNumber(10), "Cell 10 should not be available (out of bounds)");
    }

    // unit test: check if the board isFull correctly identifies when the board is full and when it is not
    @Test
    public void testIsFullTrue(){
        board.setUpTestBoard(new int[]{1, 2, 1, 2, 1, 2, 1, 2, 1});
        assertTrue(board.isFull(), "The board should be full");

    }

    @Test
    public void testIsFullFalse(){
        board.setUpTestBoard(new int[]{1, 2, 0, 0, 0, 0, 0, 0, 0});
        assertFalse(board.isFull(), "The board should not be full");
    }

    // unit test: check if the board checkWinner correctly identifies when a player has won
    @Test
    public void testCheckWinner(){
        board.setUpTestBoard(new int[] {1,1,1,2,1,2,0,0,0});
        assertEquals(1, board.checkWinner(), "P1 should be the winner");
    }

    // unit test: check if the board checkWinner correctly identifies when there is no winner
    @Test
    public void testCheckWinnerNoWinner(){
        board.setUpTestBoard(new int[] {1,2,1,2,1,2,2,1,2});
        assertEquals(0, board.checkWinner(), "There should be no winner");
    }

    // faster approach: docs.junit.org/6.0.3/writing-tests/parameterized-classes-and-tests.html
    static Stream<Arguments> provideWinConditions() {
        return Stream.of(
                // rows
                Arguments.of(new int[]{1, 1, 1, 2, 0, 0, 2, 0, 0}, 1, "P1 wins on top row"),
                Arguments.of(new int[]{0, 2, 0, 2, 2, 2, 1, 0, 1}, 2, "P2 wins on middle row"),
                Arguments.of(new int[]{0, 0, 2, 1, 0, 1, 1, 1, 1}, 1, "P1 wins on bottom row"),

                // cols
                Arguments.of(new int[]{1, 2, 0, 1, 0, 2, 1, 0, 0}, 1, "P1 wins on left column"),
                Arguments.of(new int[]{0, 2, 0, 1, 2, 1, 0, 2, 0}, 2, "P2 wins on middle column"),

                // diagonals
                Arguments.of(new int[]{1, 2, 0, 2, 1, 0, 0, 0, 1}, 1, "P1 wins top left to bottom right diagonal"),
                Arguments.of(new int[]{0, 1, 2, 1, 2, 0, 2, 0, 0}, 2, "P2 wins top right to bottom left diagonal"),

                // no winner
                Arguments.of(new int[]{1, 2, 1, 2, 1, 2, 2, 1, 2}, 0, "Full board no winner (draw)"),
                Arguments.of(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0}, 0, "Empty board no winner")
        );
    }

    @ParameterizedTest(name = "{index} => {2}")
    @MethodSource("provideWinConditions")
    public void testCheckWinner(int[] boardState, int expectedWinner, String description) {
        board.setUpTestBoard(boardState);
        int actualWinner = board.checkWinner();
        assertEquals(expectedWinner, actualWinner, description);
    }

    @Test
    public void testPrintBoard() {
        board.placeMove(1, 1);
        board.placeMove(5, 2);
        board.placeMove(9, 1);

        board.printBoard();
        String nl = System.lineSeparator();
        String expectedOutput =
                "| 1 | 0 | 0 |" + nl +
                "| 0 | 2 | 0 |" + nl +
                "| 0 | 0 | 1 |" + nl ;
        assertEquals(expectedOutput, outputStreamCaptor.toString());
    }

}




