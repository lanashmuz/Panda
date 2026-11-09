package View;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import Model.EasyBoard;
import Model.GameState;
import Model.Piece;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class EasyBoardFrame extends JFrame {

    private Runnable onBackToMainMenu;
    private final EasyBoardPanel boardPanel;
    private final DicePanel dicePanel;

    public EasyBoardFrame(EasyBoard board) {
        setTitle("Panda - Easy Board");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Notify the controller when Back is clicked
        JButton backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(e -> {
            if (onBackToMainMenu != null) {
                onBackToMainMenu.run();
            }
        });

        // Display the navigation button above the board
        JPanel navigationPanel =
                new JPanel(new FlowLayout(FlowLayout.LEFT));
        navigationPanel.add(backButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(navigationPanel, BorderLayout.NORTH);

        // create and display the game board
        boardPanel = new EasyBoardPanel(board);
        mainPanel.add(boardPanel, BorderLayout.CENTER);

        // Display the dice controls below the board
        dicePanel = new DicePanel();
        mainPanel.add(dicePanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        pack();
        setMinimumSize(new Dimension(450, 450));
        setLocationRelativeTo(null);
    }

    public void setOnBackToMainMenu(Runnable onBackToMainMenu) {
        this.onBackToMainMenu = onBackToMainMenu;
    }
    
    // connect the board panel to the current game state
    public void setGameState(GameState gameState) {
    	boardPanel.setGameState(gameState);
    	
    }

    // passes piece selection events to the controller
    public void setOnPieceSelected(Consumer<Piece> onPieceSelected) {
        boardPanel.setOnPieceSelected(onPieceSelected);
    }

    // Forwards dice roll events to the controller
    public void setOnDiceRolled(IntConsumer onDiceRolled) {
        dicePanel.setOnRoll(onDiceRolled);
    }
    
    // Refreshes the board after a piece moves
    public void refreshBoard() {
        boardPanel.repaint();
    }
	    
	public void setDiceRollEnabled(boolean enabled) {
	    dicePanel.setRollEnabled(enabled);
	}



}