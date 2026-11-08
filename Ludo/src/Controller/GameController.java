package Controller;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import Model.EasyBoard;
import Model.GameConfiguration;
import Model.GameState;
import Model.Piece;
import View.EasyBoardFrame;
import View.GameSetupFrame;

public class GameController {

    private final GameSetupFrame setupFrame;
    private Piece selectedPiece;

    public GameController(GameSetupFrame setupFrame) {
        this.setupFrame = setupFrame;
        setupFrame.setOnStartGame(this::startGame);
    }

    private void startGame(GameConfiguration configuration) {
        EasyBoard board = new EasyBoard(configuration);
        
        // initialize the positions of all pieces
        GameState gameState = new GameState(configuration, board.getCastles());
        
        // create the game window and connect its state
        EasyBoardFrame boardFrame = new EasyBoardFrame(board);
        boardFrame.setGameState(gameState);
        
        // store the piece selected by the player
        boardFrame.setOnPieceSelected(piece -> {
            selectedPiece = piece;
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