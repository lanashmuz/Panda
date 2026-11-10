package View;

import Model.GameState;
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
import java.awt.BasicStroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import java.awt.Cursor;
import java.awt.event.MouseMotionAdapter;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Draws the common path using data from the board model.
public class EasyBoardPanel extends JPanel {

    private static final int GRID_SIZE = 15;
    private final EasyBoard board;
    private GameState gameState;
    private Consumer<Piece> onPieceSelected;

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


    public EasyBoardPanel(EasyBoard board) {
        this.board = board;

        setPreferredSize(new Dimension(600, 600));
        setBackground(new Color(245, 247, 250));

        // detects when a player clicks on a game piece
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Piece selectedPiece = findClickedPiece(e.getX(), e.getY());

                if (selectedPiece != null && onPieceSelected != null) {
                    onPieceSelected.accept(selectedPiece);
                }
            }
        });

        // changes the cursor when hovering over a piece
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Piece hoveredPiece = findClickedPiece(e.getX(), e.getY());

                if (hoveredPiece != null) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        });
    }
    
    // connect the board display to the current game state
    public void setGameState(GameState gameState) {
    	if (gameState == null) {
    		throw new IllegalArgumentException("Game state can't be null");
    	}
    	this.gameState = gameState;
    	repaint();
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
            drawMovedPieces(g2,cellSize);
    
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
            	//don't draw pieces that have already left the castle
            	if (gameState != null && !gameState.isInCastle(piece)) {
            		continue;
            	}
            	
                int id = piece.getId();
    
                int centerX =
                    (origin[1] + 2 + 2 * (id % 2)) * cellSize;
    
                int centerY =
                    (origin[0] + 2 + 2 * (id / 2)) * cellSize;
    
                drawPandaPiece(
                    g2,
                    piece.getPlayerColor(),
                    centerX - cellSize / 2,
                    centerY - cellSize / 2,
                    cellSize
                );
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
	// Draws a panda face in a lighter shade of the player's color.
	private void drawPandaPiece(
	    Graphics2D g2,
	    String playerColor,
	    int x,
	    int y,
	    int size
	) {
	    Graphics2D panda = (Graphics2D) g2.create();
	
	    try {
	        panda.translate(x, y);
	        panda.scale(size / 100.0, size / 100.0);
	
	        Color base = colorFor(playerColor);
	
	        Color face = new Color(
	            (base.getRed() + 3 * 255) / 4,
	            (base.getGreen() + 3 * 255) / 4,
	            (base.getBlue() + 3 * 255) / 4
	        );
	
	        Color dark = new Color(32, 35, 40);
	
	        panda.setStroke(new BasicStroke(
	            3f,
	            BasicStroke.CAP_ROUND,
	            BasicStroke.JOIN_ROUND
	        ));
	
	        // Subtle shadow.
	        panda.setColor(new Color(0, 0, 0, 25));
	        panda.fillOval(12, 20, 84, 78);
	
	        // Ears.
	        panda.setColor(dark);
	        panda.fillOval(7, 2, 30, 30);
	        panda.fillOval(63, 2, 30, 30);
	
	        // Face and player-color outline.
	        panda.setColor(face);
	        panda.fillOval(8, 14, 84, 82);
	
	        panda.setColor(base.darker());
	        panda.drawOval(8, 14, 84, 82);
	
	        // Eye patches.
	        panda.setColor(dark);
	        panda.fillOval(21, 34, 24, 31);
	        panda.fillOval(55, 34, 24, 31);
	
	        // Eyes.
	        panda.setColor(Color.WHITE);
	        panda.fillOval(29, 44, 9, 11);
	        panda.fillOval(62, 44, 9, 11);
	
	        panda.setColor(dark);
	        panda.fillOval(33, 48, 5, 6);
	        panda.fillOval(63, 48, 5, 6);
	
	        // Cheeks.
	        panda.setColor(new Color(245, 159, 170));
	        panda.fillOval(16, 64, 14, 7);
	        panda.fillOval(69, 64, 14, 7);
	
	        // Nose and smile.
	        panda.setColor(dark);
	        panda.fillOval(43, 61, 14, 10);
	        panda.drawLine(50, 70, 50, 74);
	        panda.drawArc(38, 68, 12, 12, 180, 180);
	        panda.drawArc(50, 68, 12, 12, 180, 180);
	
	    } finally {
	        panda.dispose();
	    }
	}
	
	
	// Returns a grid cell inside the player's
	// colored finish triangle.
	private int[] finishCell(String playerColor) {
	
	    return switch (playerColor) {
	
	        case "Green" -> new int[] {7, 6};
	
	        case "Yellow" -> new int[] {6, 7};
	
	        case "Blue" -> new int[] {7, 8};
	
	        case "Red" -> new int[] {8, 7};
	
	        default -> throw new IllegalArgumentException(
	            "Unknown color: " + playerColor
	        );
	    };
	}
	
	
	// Draws pieces according to their current positions
	// in the game state.
	private void drawMovedPieces(Graphics2D g2, int cellSize) {
	
	    if (gameState == null) {
	        return;
	    }
	
	    Map<Piece, Rectangle> pieceBounds =
	            calculateMovedPieceBounds(cellSize);
	
	    for (Map.Entry<Piece, Rectangle> entry
	            : pieceBounds.entrySet()) {
	
	        Piece piece = entry.getKey();
	        Rectangle bounds = entry.getValue();
	
	        drawPandaPiece(
	                g2,
	                piece.getPlayerColor(),
	                bounds.x,
	                bounds.y,
	                bounds.width
	        );
	    }
	}


    // registers a callback for piece selection
    public void setOnPieceSelected(Consumer<Piece> onPieceSelected) {
        this.onPieceSelected = onPieceSelected;
    }


    // identifies the piece at the clicked screen position
    private Piece findClickedPiece(int mouseX, int mouseY) {
        if (gameState == null) {
            return null;
        }

        int cellSize = Math.min(getWidth(), getHeight()) / GRID_SIZE;
        if (cellSize < 1) {
            return null;
        }

        int left = (getWidth() - GRID_SIZE * cellSize) / 2;
        int top = (getHeight() - GRID_SIZE * cellSize) / 2;

        int x = mouseX - left;
        int y = mouseY - top;

        // check pieces that are still in their castles
        for (Castle castle : board.getCastles()) {
            int[] origin = castleCell(castle.getPlayerColor());
            
            for (Piece piece : castle.getPieces()) {
                if (!gameState.isInCastle(piece)) {
                    continue;
                }

                int id = piece.getId();

                int centerX = (origin[1] + 2 + 2 * (id % 2)) * cellSize;
                int centerY = (origin[0] + 2 + 2 * (id / 2)) * cellSize;

                int pieceX = centerX - cellSize / 2;
                int pieceY = centerY - cellSize / 2;

                if (isInsidePiece(x, y, pieceX, pieceY, cellSize)) {
                    return piece;
                }
            }
        }

	    // Check pieces that have already left the castle.
	    Map<Piece, Rectangle> pieceBounds =
	            calculateMovedPieceBounds(cellSize);
	
	    for (Map.Entry<Piece, Rectangle> entry : pieceBounds.entrySet()) {
	
	        Rectangle bounds = entry.getValue();
	
	        if (isInsidePiece(
	                 x,
	                 y,
	                 bounds.x,
	                 bounds.y,
	                 bounds.width
	        )) {
	            return entry.getKey();
	        }
	    }
        return null;
    }

    // checks whether the click is inside a piece's area
    private boolean isInsidePiece(int x, int y, int pieceX, int pieceY, int size) {
        return x >= pieceX
                && x < pieceX + size
                && y >= pieceY
                && y < pieceY + size;
    }

	//
	private int[] cellForMovedPiece(Piece piece) {
	
	    GameState.PiecePosition position =
	            gameState.getPiecePosition(piece);
	
	    switch (position.getZone()) {
	
	        case BOARD_PATH:
	            if (position.getPath()
	                    != GameState.BoardPathId.PATH_52) {
	                return null;
	            }
	
	            return PATH_CELLS[position.getIndex()];
	
	        case HOME_PATH:
	            return homeCell(
	                    piece.getPlayerColor(),
	                    position.getIndex()
	            );
	
	        case FINISHED:
	            return finishCell(piece.getPlayerColor());
	
	        case CASTLE:
	        default:
	            return null;
	    }
	}

	
	private Map<Piece, Rectangle> calculateMovedPieceBounds(int cellSize) {
	
	    Map<Point, List<Piece>> groups = new LinkedHashMap<>();
	    Map<Piece, Rectangle> bounds = new LinkedHashMap<>();
	
	    if (gameState == null) {
	        return bounds;
	    }
	
	    // Keep piece order stable when drawing and clicking.
	    List<Piece> pieces = new ArrayList<>(gameState.getPieces());
	
	    pieces.sort(Comparator.comparing(Piece::getPlayerColor)
	                    .thenComparingInt(Piece::getId));
	
	    // Group pieces by their board cell.
	    for (Piece piece : pieces) {
	        int[] cell = cellForMovedPiece(piece);
	        if (cell == null) {
	            continue;
	        }
	
	        Point cellPoint = new Point(cell[1], cell[0]);
	
	        groups.computeIfAbsent(cellPoint,key -> new ArrayList<>()).add(piece);
	    }
	
	    // Allocate a separate drawing area for each piece.
	    for (Map.Entry<Point, List<Piece>> entry : groups.entrySet()) {
	        List<Piece> group = entry.getValue();
	        int count = group.size();
	        int columns = (int) Math.ceil(Math.sqrt(count));
	        int rows = (int) Math.ceil((double) count / columns);
	
	        int pieceSize = Math.max(1, cellSize / Math.max(columns, rows));
	
	        int startX = entry.getKey().x * cellSize
	                + (cellSize - columns * pieceSize) / 2;
	
	        int startY = entry.getKey().y * cellSize
	                + (cellSize - rows * pieceSize) / 2;
	
	        for (int i = 0; i < count; i++) {
	
	            int column = i % columns;
	            int row = i / columns;
	
	            int x = startX + column * pieceSize;
	            int y = startY + row * pieceSize;
	
	            bounds.put(
	                    group.get(i),
	                    new Rectangle(x, y, pieceSize, pieceSize)
	            );
	        }
	    }
	
	    return bounds;
	}



}
