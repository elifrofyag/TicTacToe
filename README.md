# TicTacToe
A simple implementation of the classic Tic Tac Toe game in Java, serving a sole purpose of applying Software Engineering practices and good programming.

## How to run single App using Maven
1. Navigate to the project directory in your terminal.
2. Run to compile:
    ```bash
    mvn clean package
    ```
3. Run with arguments to start the game:
    ```bash
    java -jar target/tictactoe-1.0-SNAPSHOT.jar [arguments]
    ```
   Replace `[arguments]` with the desired game configuration (1 - human starts first, 2 computer starts first).

## How to run Client-Server version using Maven
1. Run to compile:
    ```bash
    mvn clean package -DskipTests
    ```
2. Start the server:
    ```bash
    java -jar target/TicTacToe-0.4-SNAPSHOT-server.jar [arguments]
    ```
3. Start the client in a separate terminal:
    ```bash
    java -jar target/TicTacToe-0.4-SNAPSHOT-client.jar
    ```
4. Follow the prompts in the client terminal to play the game.
