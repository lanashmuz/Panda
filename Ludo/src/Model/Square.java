package Model;

public class Square {

    private final int index;
    private final SquareType type;
    private final String playerColor;

    public Square(int index, SquareType type, String playerColor) {
        this.index = index;
        this.type = type;
        this.playerColor = playerColor;
    }

    public int getIndex() {
        return index;
    }

    public SquareType getType() {
        return type;
    }

    public String getPlayerColor() {
        return playerColor;
    }
}