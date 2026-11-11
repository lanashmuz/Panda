package Controller;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import Model.EasyBoard;
import Model.GameConfiguration;
import Model.GameState;
import Model.PieceMovement;
import Model.Player;
import View.DicePanel;
import View.EasyBoardFrame;
import View.GameSetupFrame;

public class GameController {

    private final GameSetupFrame setupFrame;
    private TurnManager turnManager;
    private DicePanel dicePanel; 
    private Integer pendingDiceResult;
    
    private EasyBoard board;
    private GameState gameState;
    private PieceMovement pieceMovement;

    public GameController(GameSetupFrame setupFrame) {
        this.setupFrame = setupFrame;
        setupFrame.setOnStartGame(this::startGame);
    }

    private void startGame(GameConfiguration configuration) {
        this.board = new EasyBoard(configuration);
        this.gameState = new GameState(configuration, board.getCastles());
        this.pieceMovement = new PieceMovement();
        
        EasyBoardFrame boardFrame = new EasyBoardFrame(board, this);
        boardFrame.setGameState(gameState);

        this.dicePanel = boardFrame.getDicePanel();
        pendingDiceResult = null;

        // Handle piece selection and attempt movement.
        boardFrame.setOnPieceSelected(piece -> {
            if (pendingDiceResult == null) {
                return;
            }

            // Ensure the piece belongs to the active player trying to move
            Player activePlayer = turnManager.getActivePlayer();

            boolean moved = pieceMovement.movePieceEasy(
                piece,
                activePlayer,
                pendingDiceResult,
                board,
                gameState
            );

            if (moved) {
                int usedDiceResult = pendingDiceResult;
                pendingDiceResult = null;
                
                boardFrame.refreshBoard();
                
                // Finalize turn (grants extra roll on 6 or passes to next player)
                turnManager.finalizeTurn(usedDiceResult);
                refreshActivePlayerUI();
                
                boardFrame.setDiceRollEnabled(true);
            }
        });
        
        boardFrame.setOnBackToMainMenu(boardFrame::dispose);

        boardFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                setupFrame.setVisible(true);
            }
        });

        boardFrame.setVisible(true);
        setupFrame.setVisible(false);
        
        initializeGame(configuration.getPlayers());
    }
    
    public void initializeGame(List<Player> players) {
        this.turnManager = new TurnManager(players);
        refreshActivePlayerUI();
    }

    public void attemptDiceRoll(Player playerRequestingRoll) {
        if (turnManager != null && turnManager.canRollDice(playerRequestingRoll)) {
            // Lock in the player who is rolling right now
            Player rollingPlayer = turnManager.getActivePlayer();
            
            int stepsResult = (int) (Math.random() * 6) + 1; 
            dicePanel.updateDiceResultDisplay(stepsResult);
            
            // Process roll through TurnManager
            boolean isValidRoll = turnManager.processDiceRoll(stepsResult);
            if (!isValidRoll) {
                // Third consecutive six reached, turn passed automatically
                refreshActivePlayerUI();
                boardFrameSetDiceRollEnabled(true);
                return;
            }

            // Check if this specific player has any legal moves with this roll result
            boolean hasLegalMove = turnManager.hasLegalMove(rollingPlayer, stepsResult, board, gameState, pieceMovement);

            if (hasLegalMove) {
                pendingDiceResult = stepsResult;
                System.out.println(rollingPlayer.getName() + " has legal moves. Select a piece.");
            } else {
                System.out.println(rollingPlayer.getName() + " has no legal moves for roll: " + stepsResult);
                pendingDiceResult = null;
                // No legal move: finalize turn (passes turn unless it's a valid 6 castle entry)
                turnManager.finalizeTurn(stepsResult);
                refreshActivePlayerUI();
                boardFrameSetDiceRollEnabled(true);
            }
            
        } else {
            System.out.println("It is not " + playerRequestingRoll.getName() + "'s turn to roll.");
        }
    }
    
    // Helper to safely re-enable dice from controller if needed
    private void boardFrameSetDiceRollEnabled(boolean enabled) {
        if (dicePanel != null) {
            dicePanel.setRollEnabled(enabled);
        }
    }

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