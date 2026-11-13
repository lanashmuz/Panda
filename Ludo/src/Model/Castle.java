package Model;

import java.util.ArrayList;
import java.util.List;

// Holds the pieces currently in a player's castle.
public class Castle {

    public static final int PIECES_PER_PLAYER = 4;
    private final String playerColor;
    private final List<Piece> pieces = new ArrayList<>();

    
    public Castle(String playerColor) {
        this.playerColor = playerColor;

        for (int i = 0; i < PIECES_PER_PLAYER; i++) {
            pieces.add(new Piece(i, playerColor));
        }
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public List<Piece> getPieces() {
        return List.copyOf(pieces);
    }
}