package Controller;

import java.awt.event.WindowAdapter;
import Model.StepsDie;
import java.awt.event.WindowEvent;
import java.util.List;

import Model.EasyBoard;
import Model.GameConfiguration;
import Model.GameState;
import Model.PieceMovement;
import Model.Player;
import View.DicePanel;
import View.GameSetupFrame;
import View.EasyBoardPanel;
import View.GameFrame;
import View.GameSidebarPanel;

public class GameController {

	private final GameSetupFrame setupFrame;
	private TurnManager turnManager;
	private DicePanel dicePanel;
	private GameSidebarPanel sidebarPanel;
	private EasyBoard board;
	private GameState gameState;
	private PieceMovement pieceMovement;
	private final StepsDie stepsDie = new StepsDie();

	public GameController(GameSetupFrame setupFrame) {
		this.setupFrame = setupFrame;
		setupFrame.setOnStartGame(this::startGame);
	}

	private void startGame(GameConfiguration configuration) {
		this.board = new EasyBoard(configuration);
		this.gameState = new GameState(configuration, board.getCastles());
		this.pieceMovement = new PieceMovement();

		// Create the Easy board panel
		EasyBoardPanel boardPanel = new EasyBoardPanel(board);
		boardPanel.setGameState(gameState);

		// Create the dice panel
		this.dicePanel = new DicePanel(this);

		// Create the sidebar with player information and dice
		this.sidebarPanel =
		        new GameSidebarPanel(configuration.getPlayers(), dicePanel);

		// Create the general game window
		GameFrame gameFrame = new GameFrame(boardPanel, sidebarPanel);
		
		gameState.clearPendingDiceResult();

		// Handle piece selection and attempt movement.
		boardPanel.setOnPieceSelected(piece -> {
			if (gameState.getPendingDiceResult() == null) {
				return;
			}

			// Ensure the piece belongs to the active player trying to move
			Player activePlayer = turnManager.getActivePlayer();

			boolean moved = pieceMovement.movePieceEasy(piece, activePlayer, gameState.getPendingDiceResult(), board,
					gameState);

			if (moved) {
				int usedDiceResult = gameState.getPendingDiceResult();
				gameState.clearPendingDiceResult();

				boardPanel.repaint();

				// Finalize turn (grants extra roll on 6 or passes to next player)
				turnManager.finalizeTurn(usedDiceResult);
				refreshActivePlayerUI();
				

				// Update game status after a successful move
				if (usedDiceResult == 6) {
				    sidebarPanel.updateStatus(
				            activePlayer.getName() + " rolled 6! Roll again."
				    );
				} else {
				    sidebarPanel.updateStatus(
				            activePlayer.getName() + " moved a piece. "
				            + turnManager.getActivePlayer().getName()
				            + "'s turn."
				    );
				}

				
				dicePanel.setRollEnabled(true);			}
		});

		gameFrame.setOnBackToMainMenu(gameFrame::dispose);
		
		gameFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				setupFrame.setVisible(true);
			}
		});

		initializeGame(configuration.getPlayers());
		
		sidebarPanel.updateStatus(
		        "Game started! "
		        + turnManager.getActivePlayer().getName()
		        + ", roll the dice."
		);

		setupFrame.setVisible(false);
		gameFrame.setVisible(true);
	}

	public void initializeGame(List<Player> players) {
		this.turnManager = new TurnManager(players);
		refreshActivePlayerUI();
	}

	public void attemptDiceRoll(Player playerRequestingRoll) {
		if (turnManager != null && turnManager.canRollDice(playerRequestingRoll)) {
			// Lock in the player who is rolling right now
			Player rollingPlayer = turnManager.getActivePlayer();
			// Generate the standard die result through the Model.
			int stepsResult = stepsDie.roll();

			dicePanel.updateDiceResultDisplay(stepsResult);

			// Process roll through TurnManager
			boolean isValidRoll = turnManager.processDiceRoll(stepsResult);
			if (!isValidRoll) {
				gameState.clearPendingDiceResult();
				// Third consecutive six reached, turn passed automatically
				refreshActivePlayerUI();
				sidebarPanel.updateStatus(
				        rollingPlayer.getName()
				        + " rolled three 6s! Turn lost. "
				        + turnManager.getActivePlayer().getName()
				        + "'s turn."
				);
				boardFrameSetDiceRollEnabled(true);
				return;
			}

			// Check if this specific player has any legal moves with this roll result
			boolean hasLegalMove = turnManager.hasLegalMove(rollingPlayer, stepsResult, board, gameState,
					pieceMovement);

			if (hasLegalMove) {
				// Store the roll until the player completes a legal move.
				gameState.setPendingDiceResult(stepsResult);
				sidebarPanel.updateStatus(
				        rollingPlayer.getName()
				        + " rolled " + stepsResult
				        + ". Select a piece to move."
				);
				System.out.println(rollingPlayer.getName() + " has legal moves. Select a piece.");
			} else {
				System.out.println(rollingPlayer.getName() + " has no legal moves for roll: " + stepsResult);
				gameState.clearPendingDiceResult();
				// No legal move: finalize turn (passes turn unless it's a valid 6 castle entry)
				turnManager.finalizeTurn(stepsResult);
				refreshActivePlayerUI();

				// Show the consequence of having no legal move
				if (stepsResult == 6) {
				    sidebarPanel.updateStatus(
				            rollingPlayer.getName()
				            + " has no legal move. Roll again!"
				    );
				} else {
				    sidebarPanel.updateStatus(
				            rollingPlayer.getName()
				            + " has no legal move. "
				            + turnManager.getActivePlayer().getName()
				            + "'s turn."
				    );
				}

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

    if (turnManager == null) {
        return;
    }

    Player activePlayer = turnManager.getActivePlayer();

    if (dicePanel != null) {
        dicePanel.updateActivePlayerDisplay(activePlayer.getName());
    }

    if (sidebarPanel != null) {
        sidebarPanel.updateActivePlayer(activePlayer);
    }
}


	public TurnManager getTurnManager() {
		return turnManager;
	}
}