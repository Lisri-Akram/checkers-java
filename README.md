# Checkers

A two-player checkers game built with Java Swing. Play locally on one computer with a polished board, turn indicator, king pieces, and a one-click restart.

## Requirements

- Java Development Kit (JDK) 8 or later
- A desktop environment that can display Swing windows

## Run the game

Open a terminal in the project folder (the folder containing `README.md`) and compile the source:

```bash
javac -d . checkersgame/CheckersGame.java
```

Then launch it:

```bash
java checkersgame.CheckersGame
```

These commands work in Bash, including the MSYS2 UCRT64 terminal, and in PowerShell. Check that both `java` and `javac` are available with `java -version` and `javac -version`.

## How to play

1. White moves first. Select one of your pieces to highlight its legal moves, then click a highlighted square.
2. Jump diagonally over an opponent's piece to capture it.
3. Reach the opposite end of the board to become a king.
4. Win by capturing all opposing pieces or leaving your opponent without a legal move.

Use **Restart Game** to start a new match.

## Project structure

```text
checkers-java/
├── README.md
└── checkersgame/
    └── CheckersGame.java
```
