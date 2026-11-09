
package Tests;

import java.util.List;
import Model.*;

public class PieceMovementSmokeTest {

    private static int passed = 0;

    private static void check(String name, boolean condition) {
        if (!condition) {
            throw new AssertionError("FAILED: " + name);
        }

        System.out.println("PASS: " + name);
        passed++;
    }

    public static void main(String[] args) {

        // Create a test game.
        Player green = new Player("Player 1", "Green");
        Player blue = new Player("Player 2", "Blue");

        GameConfiguration config = new GameConfiguration(
                List.of(green, blue), "Easy"
        );

        EasyBoard board = new EasyBoard(config);
        GameState state = new GameState(
                config, board.getCastles()
        );

        PieceMovement movement = new PieceMovement();

        // Select one green piece.
        Piece piece = board.getCastles().stream()
                .filter(c -> c.getPlayerColor().equals("Green"))
                .findFirst()
                .orElseThrow()
                .getPieces().get(0);

        // Test 1: Cannot leave castle without a six.
        check("Castle exit rejected with 3",
                !movement.canMoveEasy(
                        piece, green, 3, board, state));

        // Test 2: Checking a move must not change GameState.
        check("Castle exit allowed with 6",
                movement.canMoveEasy(
                        piece, green, 6, board, state));

        check("Validation does not move the piece",
                state.isInCastle(piece));

        // Test 3: Actual castle exit.
        check("Piece exits castle",
                movement.movePieceEasy(
                        piece, green, 6, board, state));

        int startIndex = movement
                .findStartSquare(board, piece)
                .getIndex();

        check("Piece reaches its starting square",
                state.getPiecePosition(piece).getIndex()
                        == startIndex);

        // Test 4: Clockwise movement.
        check("Piece moves 3 steps",
                movement.movePieceEasy(
                        piece, green, 3, board, state));

        check("Correct destination after 3 steps",
                state.getPiecePosition(piece).getIndex()
                        == (startIndex + 3) % 52);

        // Test 5: Another player cannot move this piece.
        check("Wrong player cannot move piece",
                !movement.canMoveEasy(
                        piece, blue, 2, board, state));

        // Test 6: Entering the home path.
        int arrowIndex = -1;

        for (Square square : board.getCommonPath()) {
            if (square.getType() == SquareType.ARROW
                    && "Green".equals(square.getPlayerColor())) {
                arrowIndex = square.getIndex();
                break;
            }
        }

        check("Green home arrow exists", arrowIndex >= 0);

        state.setBoardPathPosition(
                piece,
                GameState.BoardPathId.PATH_52,
                (arrowIndex - 1 + 52) % 52
        );

        check("Piece enters home path",
                movement.movePieceEasy(
                        piece, green, 3, board, state));

        check("Correct home path position",
                state.getPiecePosition(piece).getZone()
                        == GameState.PieceZone.HOME_PATH
                && state.getPiecePosition(piece).getIndex() == 1);

        // Test 7: Overshooting the finish is forbidden.
        state.setHomePathPosition(piece, 4);

        check("Overshooting finish is rejected",
                !movement.canMoveEasy(
                        piece, green, 2, board, state));

        check("Rejected move does not change position",
                state.getPiecePosition(piece).getZone()
                        == GameState.PieceZone.HOME_PATH
                && state.getPiecePosition(piece).getIndex() == 4);

        // Test 8: Exact finish.
        check("Exact finish is allowed",
                movement.movePieceEasy(
                        piece, green, 1, board, state));

        check("Piece is marked FINISHED",
                state.getPiecePosition(piece).getZone()
                        == GameState.PieceZone.FINISHED);

        check("Finished piece cannot move again",
                !movement.canMoveEasy(
                        piece, green, 1, board, state));

        System.out.println(
                "\nAll " + passed + " tests passed!"
        );
    }
}
