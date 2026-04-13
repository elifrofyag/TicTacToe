package org.ann;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestAppBlackBox {

    private final PrintStream originalOut = System.out;
    private PipedOutputStream outputStream;
    private BufferedReader reader;

    /*
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }
    */

    /**
     * prof manuel claevel's approach
     * Using PipedOutputStream and PipedInputStream to capture System.out output in real-time.
     * This allows us to read the output line by line as it is produced by the App.main method.
     */
    @BeforeEach
    void setUp() {
        outputStream = new PipedOutputStream();
        try {
            PipedInputStream inputStream = new PipedInputStream(outputStream); // Connect in constructor
            reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        System.setOut(new PrintStream(outputStream)); }

    @AfterEach
    void tearDown() { System.setOut(originalOut); }



    @Test
    public void testNoArgs() throws IOException {
        App.main(new String[]{});

        /*
        String output = outContent.toString().trim();
        assertEquals("Please, input a valid option [1-2]", output);
        */

        assertEquals("Please, input a valid option [1-2]", reader.readLine());

    }

    @Test
    public void testInvalidArg() throws IOException {
        App.main(new String[]{" a"});

        assertEquals("Please, input a valid option [1-2]", reader.readLine());
    }

    @Test
    public void argWithExtraSpaces() throws IOException {
        App.main(new String[]{" 1"});

        assertEquals("Please, input a valid option [1-2]", reader.readLine());
    }

    @Test
    public void extraOptionAfterInvalid() throws IOException {
        App.main(new String[]{"a", "extra"});

        assertEquals("Please, input a valid option [1-2]", reader.readLine());
    }



    @Test
    public void quotedValidOption() throws IOException {
        App.main(new String[]{"'1'"});

        assertEquals("Please, input a valid option [1-2]", reader.readLine());
    }





}
