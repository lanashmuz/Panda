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
    
    // FIX: Variables declared at the class level so the whole file can access them
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
        
        // DELETED: Player currentPlayer = configuration.getPlayers().get(0);
        pendingDiceResult = null;

		boardFrame.setOnDiceRolled(result -> {
		    pendingDiceResult = result;
            
            // FIX 1: Fetch the current active player dynamically
            Player activePlayer = turnManager.getActivePlayer();
            
		    boolean hasLegalMove = gameState.getPieces()
		            .stream()
		            .anyMatch(piece ->
		                    pieceMovement.canMoveEasy(
		                            piece,
		                            activePlayer,
		                            result,
		                            board,
		                            gameState
		                    )
		            );
		
		    if (!hasLegalMove) {
		        pendingDiceResult = null;
		        boardFrame.setDiceRollEnabled(true);
                refreshActivePlayerUI(); // Update UI if the turn was auto-passed
		    }
		});
        
        boardFrame.setOnPieceSelected(piece -> {
            if (pendingDiceResult == null) {
                return;
            }

            // FIX 2: Fetch the current active player dynamically
            Player activePlayer = turnManager.getActivePlayer();

            boolean moved = pieceMovement.movePieceEasy(
                piece,
                activePlayer,
                pendingDiceResult,
                board,
                gameState
            );

            if (moved) {
                // Save the result before clearing it
                int usedDiceResult = pendingDiceResult;
                pendingDiceResult = null;
                
                boardFrame.refreshBoard();
                
                // FIX 3: Tell the TurnManager the move is finished so it can rotate
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
            int stepsResult = (int) (Math.random() * 6) + 1; 
            
            // FIX: Only one handleDiceRoll call, passing the required arguments
            turnManager.handleDiceRoll(stepsResult, board, gameState, pieceMovement);
            
            dicePanel.updateDiceResultDisplay(stepsResult);
            refreshActivePlayerUI();
            
        } else {
            System.out.println("It is not " + playerRequestingRoll.getName() + "'s turn to roll.");
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