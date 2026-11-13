package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class EasyBoard {

    public static final int PATH_LENGTH = 52;
    private final List<Square> commonPath;
    private final List<Castle> castles;
    private final List<HomePath> homePaths; 

    public EasyBoard(GameConfiguration configuration) {
        List<Square> squares = new ArrayList<>();
    
        for (int i = 0; i < PATH_LENGTH; i++) {
            squares.add(new Square(i, SquareType.REGULAR, null));
        }
    
        placeFixedSquares(squares);
        placeSafeSquares(squares);
    
        commonPath = List.copyOf(squares);
    
        List<Castle> playerCastles = new ArrayList<>();
        List<HomePath> playerHomePaths = new ArrayList<>();
    
        for (Player player : configuration.getPlayers()) {
            String color = player.getColor();
    
            playerCastles.add(new Castle(color));
            playerHomePaths.add(new HomePath(color));
        }
    
        castles = List.copyOf(playerCastles);
        homePaths = List.copyOf(playerHomePaths);
    }

    private void placeFixedSquares(List<Square> squares) {

        squares.set(0, new Square(0, SquareType.START, "Green"));
        squares.set(13, new Square(13, SquareType.START, "Yellow"));
        squares.set(26, new Square(26, SquareType.START, "Blue"));
        squares.set(39, new Square(39, SquareType.START, "Red"));

        squares.set(50, new Square(50, SquareType.ARROW, "Green"));
        squares.set(11, new Square(11, SquareType.ARROW, "Yellow"));
        squares.set(24, new Square(24, SquareType.ARROW, "Blue"));
        squares.set(37, new Square(37, SquareType.ARROW, "Red"));
    }

    public List<Square> getCommonPath() {
        return commonPath;
    }

    public List<Castle> getCastles() {
        return castles;
    }
    
    public List<HomePath> getHomePaths() {
        return homePaths;
    }

    private void placeSafeSquares(List<Square> squares) {
        List<Integer> eligibleIndices = new ArrayList<>();
    
        for (int i = 0; i < squares.size(); i++) {
            if (squares.get(i).getType() == SquareType.REGULAR) {
                eligibleIndices.add(i);
            }
        }
    
        Collections.shuffle(eligibleIndices);
    
        for (int i = 0; i < 4; i++) {
            int index = eligibleIndices.get(i);
    
            squares.set(
                index,
                new Square(index, SquareType.SAFE, null)
            );
        }
    }
}