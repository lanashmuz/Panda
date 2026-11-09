package Controller;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import Model.EasyBoard;
import Model.GameConfiguration;
import Model.GameState;
import Model.Piece;
import View.EasyBoardFrame;
import View.GameSetupFrame;
import Model.PieceMovement;
import Model.Player;

public class GameController {

    private final GameSetupFrame setupFrame;
    private Piece selectedPiece;
    private Integer pendingDiceResult;

    public GameController(GameSetupFrame setupFrame) {
        this.setupFrame = setupFrame;
        setupFrame.setOnStartGame(this::startGame);
    }

    private void startGame(GameConfiguration configuration) {
        EasyBoard board = new EasyBoard(configuration);
        
        // initialize the positions of all pieces
        GameState gameState = new GameState(configuration, board.getCastles());
        
        // Handles piece movement rules.
        PieceMovement pieceMovement = new PieceMovement();

        // Temporary player for initial movement testing.
        // Will be replaced by turn management integration.
        Player currentPlayer = configuration.getPlayers().get(0);
        
        // create the game window and connect its state
        EasyBoardFrame boardFrame = new EasyBoardFrame(board);
        boardFrame.setGameState(gameState);
        
        // Reset selection and dice result for the new game
        selectedPiece = null;
        pendingDiceResult = null;

        // Receive the dice result from DicePanel.
        boardFrame.setOnDiceRolled(result -> {
            pendingDiceResult = result;
        });
        
        // Handle piece selection and attempt movement.
        boardFrame.setOnPieceSelected(piece -> {

            selectedPiece = piece;

            // A piece cannot move before a dice roll.
            if (pendingDiceResult == null) {
                return;
            }

            // Attempt to move the selected piece.
            boolean moved = pieceMovement.movePieceEasy(
                selectedPiece,
                currentPlayer,
                pendingDiceResult,
                board,
                gameState
            );

            if (moved) {

                // Consume the dice result after a valid move.
                pendingDiceResult = null;
                selectedPiece = null;

                // Update the board display.
                boardFrame.refreshBoard();
            }
        });

        
        // Close the board when Back is clicked
        boardFrame.setOnBackToMainMenu(boardFrame::dispose);

        // Return to setup after closing the board
        boardFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                setupFrame.setVisible(true);
            }
        });

        boardFrame.setVisible(true);

        // Keep the setup window and its entered details
        setupFrame.setVisible(false);
    }
}