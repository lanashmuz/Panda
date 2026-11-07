package Model;

import java.util.ArrayList;
import java.util.List;

public class HomePath {

    public static final int LENGTH = 6;

    private final String playerColor;
    private final List<Square> squares;

    public HomePath(String playerColor) {
        this.playerColor = playerColor;

        List<Square> homeSquares = new ArrayList<>();

        for (int i = 0; i < LENGTH; i++) {
            homeSquares.add(
                new Square(i, SquareType.HOME, playerColor)
            );
        }

        squares = List.copyOf(homeSquares);
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public List<Square> getSquares() {
        return squares;
    }
}