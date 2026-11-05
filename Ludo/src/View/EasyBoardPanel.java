package View;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import Model.EasyBoard;
import Model.Square;
import Model.SquareType;

// Draws the common path using data from the board model.
public class EasyBoardPanel extends JPanel {

    private static final int GRID_SIZE = 15;

    // Each pair contains {row, column}, ordered by model index.
    private static final int[][] PATH_CELLS = {
        {6, 1}, {6, 2}, {6, 3}, {6, 4}, {6, 5},
        {5, 6}, {4, 6}, {3, 6}, {2, 6}, {1, 6}, {0, 6},
        {0, 7}, {0, 8},
        {1, 8}, {2, 8}, {3, 8}, {4, 8}, {5, 8},
        {6, 9}, {6, 10}, {6, 11}, {6, 12}, {6, 13}, {6, 14},
        {7, 14}, {8, 14},
        {8, 13}, {8, 12}, {8, 11}, {8, 10}, {8, 9},
        {9, 8}, {10, 8}, {11, 8}, {12, 8}, {13, 8}, {14, 8},
        {14, 7}, {14, 6},
        {13, 6}, {12, 6}, {11, 6}, {10, 6}, {9, 6},
        {8, 5}, {8, 4}, {8, 3}, {8, 2}, {8, 1}, {8, 0},
        {7, 0}, {6, 0}
    };

    private final EasyBoard board;

    public EasyBoardPanel(EasyBoard board) {
        this.board = board;

        setPreferredSize(new Dimension(600, 600));
        setBackground(new Color(245, 247, 250));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int cellSize = Math.min(getWidth(), getHeight()) / GRID_SIZE;

        if (cellSize < 1) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();

        try {
            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );

            int left = (getWidth() - GRID_SIZE * cellSize) / 2;
            int top = (getHeight() - GRID_SIZE * cellSize) / 2;

            g2.translate(left, top);
            g2.setFont(new Font(
                Font.DIALOG,
                Font.BOLD,
                Math.max(12, cellSize / 2)
            ));

            for (Square square : board.getCommonPath()) {
                int[] position = PATH_CELLS[square.getIndex()];

                int x = position[1] * cellSize;
                int y = position[0] * cellSize;

                drawSquare(g2, square, x, y, cellSize);
            }
        } finally {
            g2.dispose();
        }
    }

    private void drawSquare(
        Graphics2D g2,
        Square square,
        int x,
        int y,
        int size
    ) {
        Color background = Color.WHITE;

        if (square.getType() == SquareType.START) {
            background = colorFor(square.getPlayerColor());
        } else if (square.getType() == SquareType.SAFE) {
            background = new Color(255, 248, 220);
        }

        g2.setColor(background);
        g2.fillRect(x, y, size, size);

        g2.setColor(Color.DARK_GRAY);
        g2.drawRect(x, y, size - 1, size - 1);

        String symbol = switch (square.getType()) {
            case START -> "#";
            case SAFE -> "★";
            case ARROW -> arrowFor(square.getPlayerColor());
            default -> "";
        };

        Color symbolColor = Color.BLACK;

        if (square.getType() == SquareType.ARROW) {
            symbolColor = colorFor(square.getPlayerColor());
        } else if (square.getType() == SquareType.START
            && !"Yellow".equals(square.getPlayerColor())) {
            symbolColor = Color.WHITE;
        }

        g2.setColor(symbolColor);

        FontMetrics metrics = g2.getFontMetrics();

        int textX = x + (size - metrics.stringWidth(symbol)) / 2;
        int textY = y + (size - metrics.getHeight()) / 2
            + metrics.getAscent();

        g2.drawString(symbol, textX, textY);
    }

    private Color colorFor(String playerColor) {
        return switch (playerColor) {
            case "Green" -> new Color(0, 153, 90);
            case "Yellow" -> new Color(245, 196, 40);
            case "Blue" -> new Color(45, 91, 171);
            case "Red" -> new Color(220, 58, 62);
            default -> Color.GRAY;
        };
    }

    private String arrowFor(String playerColor) {
        return switch (playerColor) {
            case "Green" -> "→";
            case "Yellow" -> "↓";
            case "Blue" -> "←";
            case "Red" -> "↑";
            default -> "";
        };
    }
}
