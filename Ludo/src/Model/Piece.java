package Model;

// Represents one piece belonging to a player color.
public class Piece {

    private final int id;
    private final String playerColor;

    public Piece(int id, String playerColor) {
        this.id = id;
        this.playerColor = playerColor;
    }

    public int getId() {
        return id;
    }

    public String getPlayerColor() {
        return playerColor;
    }
}