package Controller;

import Model.Player;
import Model.EasyBoard;
import Model.GameConfiguration;
import Model.GameState;
import Model.PieceMovement;
import View.EasyBoardFrame;
import View.GameSetupFrame;
import View.DicePanel; 

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class GameController {

    private final GameSetupFrame setupFrame;
    private TurnManager turnManager;
    private DicePanel dicePanel; 
    private Integer pendingDiceResult;

    // Restored: The constructor Main.java is looking for
    public GameController(GameSetupFrame setupFrame) {
        this.setupFrame = setupFrame;
        setupFrame.setOnStartGame(this::startGame);
    }

    // Update startGame to link the frame, the controller, and the panel
    private void startGame(GameConfiguration configuration) {
        EasyBoard board = new EasyBoard(configuration);
        
        // initialize the positions of all pieces
        GameState gameState = new GameState(configuration, board.getCastles());
        
        // Handles piece movement rules.
        PieceMovement pieceMovement = new PieceMovement();

        // Pass 'this' (the controller) into the frame
        EasyBoardFrame boardFrame = new EasyBoardFrame(board, this);
        boardFrame.setGameState(gameState);

        // Grab the real on-screen DicePanel so we can update its text later
        this.dicePanel = boardFrame.getDicePanel();

        // Temporary player for initial movement testing.
        // Will be replaced by turn management integration.
        Player currentPlayer = configuration.getPlayers().get(0);
        
        // Reset selection and dice result for the new game
        pendingDiceResult = null;

        // Receive the dice result from DicePanel.
		boardFrame.setOnDiceRolled(result -> {
		    pendingDiceResult = result;
		    // Check whether at least one piece has a legal move.
		    boolean hasLegalMove = gameState.getPieces()
		            .stream()
		            .anyMatch(piece ->
		                    pieceMovement.canMoveEasy(
		                            piece,
		                            currentPlayer,
		                            result,
		                            board,
		                            gameState
		                    )
		            );
		
		    // No legal move: discard this roll and unlock the die.
		    if (!hasLegalMove) {
		        pendingDiceResult = null;
		        boardFrame.setDiceRollEnabled(true);
		    }
		});
        
        // Handle piece selection and attempt movement.
        boardFrame.setOnPieceSelected(piece -> {
            // A piece cannot move before a dice roll.
            if (pendingDiceResult == null) {
                return;
            }

            // Attempt to move the selected piece.
            boolean moved = pieceMovement.movePieceEasy(
                piece,
                currentPlayer,
                pendingDiceResult,
                board,
                gameState
            );

            if (moved) {
                // Consume the dice result after a valid move.
                pendingDiceResult = null;
                // Update the board display.
                boardFrame.refreshBoard();
                // Allow the next dice roll
                boardFrame.setDiceRollEnabled(true);
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
        setupFrame.setVisible(false);
        
        // Start the turns
        initializeGame(configuration.getPlayers());
    }
    
    // New: Sets up the turn rotation once the game starts
    public void initializeGame(List<Player> players) {
        this.turnManager = new TurnManager(players);
        refreshActivePlayerUI();
    }

    // New: Handles the roll request from the DicePanel
    public void attemptDiceRoll(Player playerRequestingRoll) {
        if (turnManager != null && turnManager.canRollDice(playerRequestingRoll)) {
            // Generate standard 1-6 roll
            int stepsResult = (int) (Math.random() * 6) + 1; 
            
            // Process the roll in TurnManager
            turnManager.handleDiceRoll(stepsResult);
            
            // Push the new data to the DicePanel
            dicePanel.updateDiceResultDisplay(stepsResult);
            refreshActivePlayerUI();
            
        } else {
            System.out.println("It is not " + playerRequestingRoll.getName() + "'s turn to roll.");
        }
    }

    // New: Helper method to keep UI updates clean
    public void refreshActivePlayerUI() {
        if (dicePanel != null && turnManager != null) {
            String activePlayerName = turnManager.getActivePlayer().getName();
            dicePanel.updateActivePlayerDisplay(activePlayerName);
        }
    }

    public TurnManager getTurnManager() {
        return turnManager;
    }
}