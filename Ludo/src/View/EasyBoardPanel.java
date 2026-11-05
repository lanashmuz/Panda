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

import java.awt.Polygon;

import Model.Castle;
import Model.HomePath;
import Model.Piece;

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
    
            drawCastles(g2, cellSize);
    
            for (Square square : board.getCommonPath()) {
                int[] position = PATH_CELLS[square.getIndex()];
    
                int x = position[1] * cellSize;
                int y = position[0] * cellSize;
    
                drawSquare(g2, square, x, y, cellSize);
            }
    
            drawHomePaths(g2, cellSize);
    
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

    // Draws all castle areas and the pieces currently inside them.
private void drawCastles(Graphics2D g2, int cellSize) {

    for (Square square : board.getCommonPath()) {
        if (square.getType() != SquareType.START) {
            continue;
        }

        String color = square.getPlayerColor();
        int[] origin = castleCell(color);

        int x = origin[1] * cellSize;
        int y = origin[0] * cellSize;
        int size = 6 * cellSize;

        g2.setColor(colorFor(color));
        g2.fillRect(x, y, size, size);

        g2.setColor(Color.DARK_GRAY);
        g2.drawRect(x, y, size - 1, size - 1);

        g2.setColor(Color.WHITE);
        g2.fillRoundRect(
            x + cellSize,
            y + cellSize,
            4 * cellSize,
            4 * cellSize,
            cellSize,
            cellSize
        );

        g2.setColor(Color.DARK_GRAY);
        g2.drawRoundRect(
            x + cellSize,
            y + cellSize,
            4 * cellSize - 1,
            4 * cellSize - 1,
            cellSize,
            cellSize
        );
    }

    for (Castle castle : board.getCastles()) {
        int[] origin = castleCell(castle.getPlayerColor());

        for (Piece piece : castle.getPieces()) {
            int id = piece.getId();

            int centerX =
                (origin[1] + 2 + 2 * (id % 2)) * cellSize;

            int centerY =
                (origin[0] + 2 + 2 * (id / 2)) * cellSize;

            int diameter = cellSize;
            int x = centerX - diameter / 2;
            int y = centerY - diameter / 2;

            g2.setColor(colorFor(piece.getPlayerColor()));
            g2.fillOval(x, y, diameter, diameter);

            g2.setColor(Color.DARK_GRAY);
            g2.drawOval(x, y, diameter - 1, diameter - 1);
        }
    }
}

// Returns the top-left grid cell of a castle.
private int[] castleCell(String playerColor) {
    return switch (playerColor) {
        case "Green" -> new int[] {0, 0};
        case "Yellow" -> new int[] {0, 9};
        case "Blue" -> new int[] {9, 9};
        case "Red" -> new int[] {9, 0};
        default -> throw new IllegalArgumentException(
            "Unknown color: " + playerColor
        );
    };
}

// Draws the physical home lanes for all four board colors.
private void drawHomePaths(Graphics2D g2, int cellSize) {
    for (Square square : board.getCommonPath()) {
        if (square.getType() != SquareType.ARROW) {
            continue;
        }

        String color = square.getPlayerColor();

        for (int i = 0; i < HomePath.LENGTH - 1; i++) {
            int[] position = homeCell(color, i);

            int x = position[1] * cellSize;
            int y = position[0] * cellSize;

            g2.setColor(colorFor(color));
            g2.fillRect(x, y, cellSize, cellSize);

            g2.setColor(Color.DARK_GRAY);
            g2.drawRect(x, y, cellSize - 1, cellSize - 1);
        }

        drawFinishTriangle(g2, color, cellSize);
    }
}

// Returns the grid position of a rectangular home-path square.
private int[] homeCell(String playerColor, int index) {
    return switch (playerColor) {
        case "Green" -> new int[] {7, 1 + index};
        case "Yellow" -> new int[] {1 + index, 7};
        case "Blue" -> new int[] {7, 13 - index};
        case "Red" -> new int[] {13 - index, 7};
        default -> throw new IllegalArgumentException(
            "Unknown color: " + playerColor
        );
    };
}

// Draws the final home position as a colored center triangle.
private void drawFinishTriangle(
    Graphics2D g2,
    String playerColor,
    int cellSize
) {
    int low = 6 * cellSize;
    int high = 9 * cellSize;
    int middle = GRID_SIZE * cellSize / 2;

    Polygon triangle = switch (playerColor) {
        case "Green" -> new Polygon(
            new int[] {low, middle, low},
            new int[] {low, middle, high},
            3
        );

        case "Yellow" -> new Polygon(
            new int[] {low, high, middle},
            new int[] {low, low, middle},
            3
        );

        case "Blue" -> new Polygon(
            new int[] {high, high, middle},
            new int[] {low, high, middle},
            3
        );

        case "Red" -> new Polygon(
            new int[] {low, middle, high},
            new int[] {high, middle, high},
            3
        );

        default -> throw new IllegalArgumentException(
            "Unknown color: " + playerColor
        );
    };

    g2.setColor(colorFor(playerColor));
    g2.fillPolygon(triangle);

    g2.setColor(Color.DARK_GRAY);
    g2.drawPolygon(triangle);
}
}
