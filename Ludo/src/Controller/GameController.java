package Controller;

import Model.Player;
import Model.EasyBoard;
import Model.GameConfiguration;
import View.EasyBoardFrame;
import View.GameSetupFrame;
import View.DicePanel; // Make sure you have this import

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class GameController {

    private final GameSetupFrame setupFrame;
    private TurnManager turnManager;
    private DicePanel dicePanel; 

    // Restored: The constructor Main.java is looking for
    public GameController(GameSetupFrame setupFrame) {
        this.setupFrame = setupFrame;
        setupFrame.setOnStartGame(this::startGame);
    }

    // Restored: The logic to transition from setup to the game board
 // Update startGame to link the frame, the controller, and the panel
    private void startGame(GameConfiguration configuration) {
        EasyBoard board = new EasyBoard(configuration);
        
        // Pass 'this' (the controller) into the frame
        EasyBoardFrame boardFrame = new EasyBoardFrame(board, this);

        // Grab the real on-screen DicePanel so we can update its text later
        this.dicePanel = boardFrame.getDicePanel();

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