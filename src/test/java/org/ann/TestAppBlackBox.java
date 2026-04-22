package org.ann;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestAppBlackBox {

    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    public void setUpStreams() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    private BufferedReader getOutputReader() {
        return new BufferedReader(new InputStreamReader(
                new ByteArrayInputStream(outContent.toByteArray()), StandardCharsets.UTF_8));
    }

    private void provideInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8)));
    }


    //ts003 v0.4
    @Test
    public void testNoArgs() throws IOException {
        App.main(new String[]{});

        BufferedReader reader = getOutputReader();
        assertEquals("Please, input a valid option [1-2]", reader.readLine());

    }

    @Test
    public void testInvalidArg1() throws IOException {
        App.main(new String[]{" a"});
        BufferedReader reader = getOutputReader();
        assertEquals("Please, input a valid option [1-2]", reader.readLine());
    }

    @Test
    //ts004 v0.4
    public void testInvalidArg2() throws IOException {
        App.main(new String[]{"3"});
        BufferedReader reader = getOutputReader();
        assertEquals("Please, input a valid option [1-2]", reader.readLine());
    }

    //ts005 v0.4
    @Test
    public void testInvalidArg3() throws IOException {
        App.main(new String[]{"1", "2"});
        BufferedReader reader = getOutputReader();
        assertEquals("Please, input a valid option [1-2]", reader.readLine());
    }

    @Test
    public void argWithExtraSpaces() throws IOException {
        App.main(new String[]{" 1"});
        BufferedReader reader = getOutputReader();
        assertEquals("Please, input a valid option [1-2]", reader.readLine());
    }

    @Test
    public void extraArgAfterInvalid() throws IOException {
        App.main(new String[]{"a", "extra"});
        BufferedReader reader = getOutputReader();
        assertEquals("Please, input a valid option [1-2]", reader.readLine());
    }

    @Test
    public void extraArgAfterValid1() throws IOException {
        App.main(new String[]{"1", "extra"});
        BufferedReader reader = getOutputReader();
        assertEquals("Please, input a valid option [1-2]", reader.readLine());
    }


    @Test
    public void quotedValidOption() throws IOException {
        App.main(new String[]{"'1'"});
        BufferedReader reader = getOutputReader();
        assertEquals("Please, input a valid option [1-2]", reader.readLine());
    }

    //ts001 v0.4, ts009 v0.4
    @Test
    public void testHumanStartGame() throws IOException {
        provideInput("q" + System.lineSeparator());
        App.main(new String[]{"1"});

        // note: if you do not implement thread for the game, the test will block here because the game in main is waiting for user input
        // the app.main has to finish before it gets to the assertEquals
        // if you implement the game in a separate thread, the app.main will finish immediately and the test can proceed to read the output

        BufferedReader reader = getOutputReader();

        assertEquals("Hello!", reader.readLine());

        skipLines(3, reader);
        assertEquals("Player#1's turn", reader.readLine());
        assertEquals("End of the game", reader.readLine());
        assertNull(reader.readLine());
    }

    //ts002 v0.4, ts009 v0.4
    @Test
    public void testComputerStartGame() throws IOException {
        provideInput("q" + System.lineSeparator());
        App.main(new String[]{"2"});
        BufferedReader reader = getOutputReader();

        assertEquals("Hello!", reader.readLine());
        assertEquals("| 0 | 0 | 0 |", reader.readLine());
        assertEquals("| 0 | 0 | 0 |", reader.readLine());
        assertEquals("| 0 | 0 | 0 |", reader.readLine());
        assertEquals("Player#2's turn", reader.readLine());

        //skip board output after computer move
        skipLines(3, reader);
        assertEquals("Player#1's turn", reader.readLine());
        assertEquals("End of the game", reader.readLine());
        assertNull(reader.readLine());
    }

    //ts 008 v0.4
    @Test
    public void nonIntegerPlayerInput() throws IOException {
        provideInput("a" + System.lineSeparator() + "q" + System.lineSeparator());
        App.main(new String[]{"1"});
        BufferedReader reader = getOutputReader();

        skipLines(4, reader);
        assertEquals("Player#1's turn", reader.readLine());
        assertEquals("Please, input a valid number [1-9]", reader.readLine());
        assertEquals("End of the game", reader.readLine());
        assertNull(reader.readLine());
    }

    // ts 010 v0.4
    @Test
    public void quitCaseSensity() throws IOException {
        provideInput("Q" + System.lineSeparator() + "q" + System.lineSeparator());
        App.main(new String[]{"1"});
        BufferedReader reader = getOutputReader();

        skipLines(4, reader);
        assertEquals("Player#1's turn", reader.readLine());
        assertEquals("Please, input a valid number [1-9]", reader.readLine());
        assertEquals("End of the game", reader.readLine());
        assertNull(reader.readLine());
    }

    //ts007 v0.4
    @Test
    public void testTrueGamePlay() throws IOException {
        provideInput("1" + System.lineSeparator() + "q" + System.lineSeparator());
        App.main(new String[]{"1"});
        BufferedReader reader = getOutputReader();

        skipLines(4, reader);
        assertEquals("Player#1's turn", reader.readLine());
        assertEquals("| 1 | 0 | 0 |", reader.readLine());
        assertEquals("| 0 | 0 | 0 |", reader.readLine());
        assertEquals("| 0 | 0 | 0 |", reader.readLine());
        assertEquals("Player#2's turn", reader.readLine());
        assertEquals("| 1 | 2 | 0 |", reader.readLine());
        assertEquals("| 0 | 0 | 0 |", reader.readLine());
        assertEquals("| 0 | 0 | 0 |", reader.readLine());
        assertEquals("Player#1's turn", reader.readLine());
        assertEquals("End of the game", reader.readLine());
        assertNull(reader.readLine());
    }

    //ts011 v0.4
    @Test
    public void outOfRangePlayerInput() throws IOException {
        provideInput("10" + System.lineSeparator() + "q" + System.lineSeparator());
        App.main(new String[]{"1"});
        BufferedReader reader = getOutputReader();

        skipLines(4, reader);
        assertEquals("Player#1's turn", reader.readLine());
        assertEquals("Please, input a valid number [1-9]", reader.readLine());
        assertEquals("End of the game", reader.readLine());
        assertNull(reader.readLine());
    }

    //ts012 v0.4
    @Test
    public void occupiedCellPlayerInput() throws IOException {
        provideInput("1" + System.lineSeparator() + "1" + System.lineSeparator() + "q" + System.lineSeparator());
        App.main(new String[]{"1"});
        BufferedReader reader = getOutputReader();

        skipLines(4, reader);
        assertEquals("Player#1's turn", reader.readLine());
        skipLines(3, reader);
        assertEquals("Player#2's turn", reader.readLine());
        skipLines(3, reader);
        assertEquals("Player#1's turn", reader.readLine());
        assertEquals("The cell is occupied!", reader.readLine());
        assertEquals("End of the game", reader.readLine());
        assertNull(reader.readLine());

    }

    //ts013 v0.4
    @Test
    public void humanWinScenario() throws IOException {
        provideInput("4" + System.lineSeparator() + "5" + System.lineSeparator() + "6" + System.lineSeparator());
        App.main(new String[]{"1"});
        BufferedReader reader = getOutputReader();

        skipLines(4, reader);
        assertEquals("Player#1's turn", reader.readLine());
        skipLines(3, reader);
        assertEquals("Player#2's turn", reader.readLine());
        skipLines(3, reader);
        assertEquals("Player#1's turn", reader.readLine());
        skipLines(3, reader);
        assertEquals("Player#2's turn", reader.readLine());
        skipLines(3, reader);
        assertEquals("Player#1's turn", reader.readLine());
        assertEquals("| 2 | 2 | 0 |", reader.readLine());
        assertEquals("| 1 | 1 | 1 |", reader.readLine());
        assertEquals("| 0 | 0 | 0 |", reader.readLine());
        assertEquals("Player#1 won!", reader.readLine());
        assertNull(reader.readLine());

    }

    //ts014 v0.4
    @Test
    public void computerWinScenario() throws IOException {
        provideInput("4" + System.lineSeparator() + "5" + System.lineSeparator());
        App.main(new String[]{"2"});
        BufferedReader reader = getOutputReader();

        skipLines(4, reader);
        assertEquals("Player#2's turn", reader.readLine());
        skipLines(3, reader);
        assertEquals("Player#1's turn", reader.readLine());
        skipLines(3, reader);
        assertEquals("Player#2's turn", reader.readLine());
        skipLines(3, reader);
        assertEquals("Player#1's turn", reader.readLine());
        skipLines(3, reader);
        assertEquals("Player#2's turn", reader.readLine());
        assertEquals("| 2 | 2 | 2 |", reader.readLine());
        assertEquals("| 1 | 1 | 0 |", reader.readLine());
        assertEquals("| 0 | 0 | 0 |", reader.readLine());
        assertEquals("Player#2 won!", reader.readLine());
        assertNull(reader.readLine());

    }

    //ts015 v0.4
    @Test
    public void drawAfterHumanMove() throws IOException {
        provideInput("3" + System.lineSeparator() + "4" + System.lineSeparator() + "5" + System.lineSeparator() +
                "8" + System.lineSeparator() + "9" + System.lineSeparator());
        App.main(new String[]{"1"});
        BufferedReader reader = getOutputReader();

        skipLines(4, reader);
        assertEquals("Player#1's turn", reader.readLine());
        skipLines(3, reader);
        assertEquals("Player#2's turn", reader.readLine());
        skipLines(3, reader);
        assertEquals("Player#1's turn", reader.readLine());
        skipLines(3, reader);
        assertEquals("Player#2's turn", reader.readLine());
        skipLines(3, reader);
        assertEquals("Player#1's turn", reader.readLine());
        skipLines(3, reader);
        assertEquals("Player#2's turn", reader.readLine());
        skipLines(3, reader);
        assertEquals("Player#1's turn", reader.readLine());
        skipLines(3, reader);
        assertEquals("Player#2's turn", reader.readLine());
        skipLines(3, reader);
        assertEquals("Player#1's turn", reader.readLine());
        assertEquals("| 2 | 2 | 1 |", reader.readLine());
        assertEquals("| 1 | 1 | 2 |", reader.readLine());
        assertEquals("| 2 | 1 | 1 |", reader.readLine());
        assertEquals("It is a draw!", reader.readLine());
        assertNull(reader.readLine());
    }

    //ts016 v0.4
    @Test
    public void drawAfterComputerMove() throws IOException {
        provideInput("2" + System.lineSeparator() + "5" + System.lineSeparator() + "7" + System.lineSeparator()
                + "9" + System.lineSeparator());
        App.main(new String[]{"2"});
        BufferedReader reader = getOutputReader();

        skipLines(4, reader);
        assertEquals("Player#2's turn", reader.readLine());
        skipLines(3, reader);
        assertEquals("Player#1's turn", reader.readLine());
        skipLines(3, reader);
        assertEquals("Player#2's turn", reader.readLine());
        skipLines(3, reader);
        assertEquals("Player#1's turn", reader.readLine());
        skipLines(3, reader);
        assertEquals("Player#2's turn", reader.readLine());
        skipLines(3, reader);
        assertEquals("Player#1's turn", reader.readLine());
        skipLines(3, reader);
        assertEquals("Player#2's turn", reader.readLine());
        skipLines(3, reader);
        assertEquals("Player#1's turn", reader.readLine());
        skipLines(3, reader);
        assertEquals("Player#2's turn", reader.readLine());
        assertEquals("| 2 | 1 | 2 |", reader.readLine());
        assertEquals("| 2 | 1 | 2 |", reader.readLine());
        assertEquals("| 1 | 2 | 1 |", reader.readLine());
        assertEquals("It is a draw!", reader.readLine());
        assertNull(reader.readLine());
    }

    //ts018 v0.4
    @Test
    public void fullGamePlayHumanWins() throws IOException {
        provideInput("4" + System.lineSeparator() + "5" + System.lineSeparator() + "6" + System.lineSeparator());
        App.main(new String[]{"1"});
        BufferedReader reader = getOutputReader();

        skipLines(4, reader);
        assertEquals("Player#1's turn", reader.readLine());
        assertEquals("| 0 | 0 | 0 |", reader.readLine());
        assertEquals("| 1 | 0 | 0 |", reader.readLine());
        assertEquals("| 0 | 0 | 0 |", reader.readLine());

        assertEquals("Player#2's turn", reader.readLine());
        assertEquals("| 2 | 0 | 0 |", reader.readLine());
        assertEquals("| 1 | 0 | 0 |", reader.readLine());
        assertEquals("| 0 | 0 | 0 |", reader.readLine());

        assertEquals("Player#1's turn", reader.readLine());
        assertEquals("| 2 | 0 | 0 |", reader.readLine());
        assertEquals("| 1 | 1 | 0 |", reader.readLine());
        assertEquals("| 0 | 0 | 0 |", reader.readLine());

        assertEquals("Player#2's turn", reader.readLine());
        assertEquals("| 2 | 2 | 0 |", reader.readLine());
        assertEquals("| 1 | 1 | 0 |", reader.readLine());
        assertEquals("| 0 | 0 | 0 |", reader.readLine());

        assertEquals("Player#1's turn", reader.readLine());
        assertEquals("| 2 | 2 | 0 |", reader.readLine());
        assertEquals("| 1 | 1 | 1 |", reader.readLine());
        assertEquals("| 0 | 0 | 0 |", reader.readLine());

        assertEquals("Player#1 won!", reader.readLine());
        assertNull(reader.readLine());
    }

//    class BrokenInputStream extends InputStream {
//        @Override
//        public int read() throws IOException {
//            throw new IOException("input stream failure");
//        }
//    }
//
//    @Test
//    public void testInputStreamFailure() throws IOException {
//        System.setIn(new BrokenInputStream());
//        App.main(new String[]{"1"});
//        BufferedReader reader = getOutputReader();
//
//        skipLines(4, reader);
//        assertEquals("Player#1's turn", reader.readLine());
//        assertEquals("unexpected exception while reading human input", reader.readLine());
//        assertNull(reader.readLine());
//    }


    void skipLines(int number, BufferedReader reader) throws IOException {
        for (int i = 0; i < number; i = i + 1) {
            reader.readLine();
        }
    }
}
