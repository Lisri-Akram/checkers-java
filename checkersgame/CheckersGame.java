
package checkersgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CheckersGame extends JFrame {
    private static final int BOARD_SIZE = 8;
    private static final Color APP_BACKGROUND = new Color(20, 29, 34);
    private static final Color CARD_BACKGROUND = new Color(31, 43, 49);
    private static final Color LIGHT_SQUARE = new Color(229, 218, 197);
    private static final Color DARK_SQUARE = new Color(69, 103, 85);
    private static final Color MOVE_HINT_SQUARE = new Color(119, 144, 99);
    private static final Color GOLD = new Color(226, 181, 91);
    private static final Color MUTED_TEXT = new Color(166, 178, 179);

    private JPanel boardPanel;
    private CheckerSquare[][] squares;
    private int currentPlayer = 1;
    private JLabel playerTurnLabel;
    private JLabel turnColorDot;
    private CheckerPiece selectedPiece = null;

    private enum PieceColor {
        WHITE, BLACK, NONE
    }

    private class CheckerPiece {
        PieceColor color;
        boolean isKing;

        CheckerPiece(PieceColor color) {
            this.color = color;
            this.isKing = false;
        }
    }

    private class CheckerSquare extends JButton {
        private CheckerPiece piece;
        private final boolean isDark;
        private boolean moveHint;

        CheckerSquare(boolean isDark) {
            this.isDark = isDark;
            setPreferredSize(new Dimension(70, 70));
            setBackground(isDark ? DARK_SQUARE : LIGHT_SQUARE);
            setBorder(BorderFactory.createLineBorder(getBackground(), 1));
            setContentAreaFilled(true);
            setFocusPainted(false);
            setRolloverEnabled(false);
            setMargin(new Insets(0, 0, 0, 0));
        }

        void setPiece(CheckerPiece piece) {
            this.piece = piece;
            updateIcon();
        }

        CheckerPiece getPiece() {
            return piece;
        }

        void removePiece() {
            this.piece = null;
            setIcon(null);
        }

        void setSelectedSquare(boolean selected) {
            Color borderColor = selected ? GOLD : getBackground();
            int borderWidth = selected ? 4 : 1;
            setBorder(BorderFactory.createLineBorder(borderColor, borderWidth));
        }

        void setMoveHint(boolean moveHint) {
            this.moveHint = moveHint;
            setBackground(moveHint ? MOVE_HINT_SQUARE : (isDark ? DARK_SQUARE : LIGHT_SQUARE));
            setBorder(BorderFactory.createLineBorder(getBackground(), 1));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (moveHint) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                    int diameter = Math.min(getWidth(), getHeight()) / 5;
                    int x = (getWidth() - diameter) / 2;
                    int y = (getHeight() - diameter) / 2;
                    g2.setColor(new Color(248, 224, 172, 230));
                    g2.fillOval(x, y, diameter, diameter);
                } finally {
                    g2.dispose();
                }
            }
        }

        private void updateIcon() {
            if (piece == null) {
                setIcon(null);
                return;
            }

            setIcon(createPieceIcon(piece.color, piece.isKing));
        }
    }

    private Icon createPieceIcon(PieceColor color, boolean isKing) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                    boolean isWhite = color == PieceColor.WHITE;
                    Color topColor = isWhite ? new Color(255, 253, 245) : new Color(91, 105, 108);
                    Color bottomColor = isWhite ? new Color(198, 190, 174) : new Color(30, 39, 43);
                    Color outline = isWhite ? new Color(166, 155, 132) : new Color(15, 23, 26);

                    g2.setColor(new Color(0, 0, 0, 55));
                    g2.fillOval(x + 6, y + 9, 48, 48);
                    g2.setPaint(new GradientPaint(x, y, topColor, x + 48, y + 48, bottomColor));
                    g2.fillOval(x + 5, y + 5, 48, 48);
                    g2.setColor(outline);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawOval(x + 5, y + 5, 48, 48);

                    if (isKing) {
                        g2.setColor(GOLD);
                        int[] crownX = {x + 18, x + 20, x + 26, x + 32, x + 35};
                        int[] crownY = {y + 30, y + 19, y + 25, y + 19, y + 30};
                        g2.fillPolygon(crownX, crownY, crownX.length);
                        g2.fillRect(x + 19, y + 31, 15, 3);
                    }
                } finally {
                    g2.dispose();
                }
            }

            @Override
            public int getIconWidth() { return 58; }
            @Override
            public int getIconHeight() { return 58; }
        };
    }

    public CheckersGame() {
        setTitle("Checkers");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(780, 860);
        setMinimumSize(new Dimension(720, 800));
        setLocationRelativeTo(null);

        JPanel content = new JPanel(new BorderLayout(0, 22));
        content.setBackground(APP_BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(28, 34, 26, 34));
        setContentPane(content);

        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setOpaque(false);

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("CHECKERS");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(245, 241, 231));
        titleBlock.add(title);
        header.add(titleBlock, BorderLayout.WEST);

        JPanel turnCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        turnCard.setBackground(CARD_BACKGROUND);
        turnCard.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 14));
        turnColorDot = new JLabel("\u25CF");
        turnColorDot.setFont(new Font("SansSerif", Font.PLAIN, 20));
        playerTurnLabel = new JLabel();
        playerTurnLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        playerTurnLabel.setForeground(new Color(245, 241, 231));
        turnCard.add(turnColorDot);
        turnCard.add(playerTurnLabel);
        header.add(turnCard, BorderLayout.EAST);
        content.add(header, BorderLayout.NORTH);

        boardPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        boardPanel.setPreferredSize(new Dimension(560, 560));
        squares = new CheckerSquare[BOARD_SIZE][BOARD_SIZE];
        initializeBoard();

        JPanel boardFrame = new JPanel(new BorderLayout());
        boardFrame.setBackground(CARD_BACKGROUND);
        boardFrame.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 68, 70), 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        boardFrame.add(boardPanel, BorderLayout.CENTER);

        JPanel boardHolder = new JPanel(new GridBagLayout());
        boardHolder.setOpaque(false);
        boardHolder.add(boardFrame);
        content.add(boardHolder, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout(16, 0));
        footer.setOpaque(false);
        JLabel instructions = new JLabel(
            "<html><b>HOW TO PLAY</b> &nbsp; Select a piece to see its legal moves, then choose a highlighted square.</html>"
        );
        instructions.setFont(new Font("SansSerif", Font.PLAIN, 12));
        instructions.setForeground(MUTED_TEXT);
        footer.add(instructions, BorderLayout.CENTER);
        JButton restartButton = new JButton("Restart Game");
        restartButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        restartButton.setForeground(APP_BACKGROUND);
        restartButton.setBackground(GOLD);
        restartButton.setFocusPainted(false);
        restartButton.setBorder(BorderFactory.createEmptyBorder(11, 18, 11, 18));
        restartButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        restartButton.addActionListener(e -> restartGame());
        footer.add(restartButton, BorderLayout.EAST);
        content.add(footer, BorderLayout.SOUTH);
        updatePlayerTurnLabel();
    }

    private void initializeBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                boolean isDark = (row + col) % 2 != 0;
                CheckerSquare square = new CheckerSquare(isDark);

                if (isDark) {
                    if (row < 3) {
                        square.setPiece(new CheckerPiece(PieceColor.BLACK));
                    } else if (row > 4) {
                        square.setPiece(new CheckerPiece(PieceColor.WHITE));
                    }
                }

                final int finalRow = row;
                final int finalCol = col;
                square.addActionListener(e -> handleSquareClick(finalRow, finalCol));

                squares[row][col] = square;
                boardPanel.add(square);
            }
        }
    }

    private void handleSquareClick(int row, int col) {
        CheckerSquare clickedSquare = squares[row][col];

        if (selectedPiece == null) {
            if (clickedSquare.getPiece() != null && 
                isCurrentPlayerPiece(clickedSquare.getPiece())) {
                selectedPiece = clickedSquare.getPiece();
                clickedSquare.setSelectedSquare(true);
                showMoveHints(selectedPiece);
            }
        } else {
            if (isValidMove(selectedPiece, row, col)) {
                movePiece(selectedPiece, row, col);
                
                currentPlayer = (currentPlayer == 1) ? 2 : 1;
                updatePlayerTurnLabel();

                if (checkGameOver()) {
                    return;
                }
            }

            resetSelection();
        }
    }

    private boolean isCurrentPlayerPiece(CheckerPiece piece) {
        return (currentPlayer == 1 && piece.color == PieceColor.WHITE) ||
               (currentPlayer == 2 && piece.color == PieceColor.BLACK);
    }

    private boolean isValidMove(CheckerPiece piece, int toRow, int toCol) {
        Point currentLocation = findPieceLocation(piece);
        if (currentLocation == null) return false;

        int fromRow = currentLocation.x;
        int fromCol = currentLocation.y;

        int rowDiff = toRow - fromRow;
        int colDiff = toCol - fromCol;
        if (squares[toRow][toCol].getPiece() != null) return false;

        int rowDistance = Math.abs(rowDiff);
        int colDistance = Math.abs(colDiff);
        if (rowDistance != colDistance || (rowDistance != 1 && rowDistance != 2)) {
            return false;
        }

        if (!piece.isKing) {
            int allowedDirection = piece.color == PieceColor.WHITE ? -1 : 1;
            if (Integer.signum(rowDiff) != allowedDirection) return false;
        }

        if (rowDistance == 1) return true;

        int midRow = (fromRow + toRow) / 2;
        int midCol = (fromCol + toCol) / 2;
        CheckerPiece midPiece = squares[midRow][midCol].getPiece();
        return midPiece != null && midPiece.color != piece.color;
    }

    private void movePiece(CheckerPiece piece, int toRow, int toCol) {
        Point currentLocation = findPieceLocation(piece);
        if (currentLocation == null) return;

        int fromRow = currentLocation.x;
        int fromCol = currentLocation.y;

        squares[fromRow][fromCol].removePiece();

        if (Math.abs(fromRow - toRow) == 2) {
            int midRow = (fromRow + toRow) / 2;
            int midCol = (fromCol + toCol) / 2;
            squares[midRow][midCol].removePiece();
        }

        squares[toRow][toCol].setPiece(piece);

        if ((piece.color == PieceColor.WHITE && toRow == 0) ||
            (piece.color == PieceColor.BLACK && toRow == BOARD_SIZE - 1)) {
            piece.isKing = true;
        }
    }

    private Point findPieceLocation(CheckerPiece piece) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (squares[row][col].getPiece() == piece) {
                    return new Point(row, col);
                }
            }
        }
        return null;
    }

    private void resetSelection() {
        for (CheckerSquare[] row : squares) {
            for (CheckerSquare square : row) {
                square.setMoveHint(false);
                square.setSelectedSquare(false);
            }
        }
        selectedPiece = null;
    }

    private void showMoveHints(CheckerPiece piece) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (isValidMove(piece, row, col)) {
                    squares[row][col].setMoveHint(true);
                }
            }
        }
    }

    private void updatePlayerTurnLabel() {
        boolean whiteTurn = currentPlayer == 1;
        playerTurnLabel.setText(whiteTurn ? "WHITE TO MOVE" : "BLACK TO MOVE");
        turnColorDot.setForeground(whiteTurn
            ? new Color(245, 241, 231)
            : new Color(93, 107, 111));
    }

    private boolean checkGameOver() {
        int whiteCount = 0;
        int blackCount = 0;
        boolean whiteCanMove = false;
        boolean blackCanMove = false;

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                CheckerPiece piece = squares[row][col].getPiece();
                if (piece != null) {
                    if (piece.color == PieceColor.WHITE) {
                        whiteCount++;
                        if (!whiteCanMove) whiteCanMove = hasValidMove(piece);
                    } else {
                        blackCount++;
                        if (!blackCanMove) blackCanMove = hasValidMove(piece);
                    }
                }
            }
        }

        if (blackCount == 0 || !blackCanMove) {
            showWinnerDialog("White");
            return true;
        } else if (whiteCount == 0 || !whiteCanMove) {
            showWinnerDialog("Black");
            return true;
        }

        return false;
    }

    private boolean hasValidMove(CheckerPiece piece) {
        Point location = findPieceLocation(piece);
        if (location == null) return false;

        int row = location.x;
        int col = location.y;

        int[] rowDirections = piece.isKing ? new int[]{-1, 1} : 
            (piece.color == PieceColor.WHITE ? new int[]{-1} : new int[]{1});
        int[] colDirections = new int[]{-1, 1};

        for (int rowDir : rowDirections) {
            for (int colDir : colDirections) {
                int newRow = row + rowDir;
                int newCol = col + colDir;

                if (newRow >= 0 && newRow < BOARD_SIZE && 
                    newCol >= 0 && newCol < BOARD_SIZE) {
                    if (isValidMove(piece, newRow, newCol)) {
                        return true;
                    }
                }

                newRow = row + 2 * rowDir;
                newCol = col + 2 * colDir;

                if (newRow >= 0 && newRow < BOARD_SIZE && 
                    newCol >= 0 && newCol < BOARD_SIZE) {
                    if (isValidMove(piece, newRow, newCol)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void showWinnerDialog(String winner) {
        JOptionPane.showMessageDialog(this, 
            winner + " Player Wins!", 
            "Game Over", 
            JOptionPane.INFORMATION_MESSAGE);
        restartGame();
    }

    private void restartGame() {
        boardPanel.removeAll();
        initializeBoard();
        currentPlayer = 1;
        selectedPiece = null;
        updatePlayerTurnLabel();
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CheckersGame game = new CheckersGame();
            game.setVisible(true);
        });
    }
}