
package View;

import Controller.GameController;
import Model.Player;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DicePanel extends JPanel {
    private JButton rollButton;
    private JLabel activePlayerLabel;
    private JLabel diceResultLabel;
    private GameController gameController;

    // 1. Pass GameController into the constructor
    public DicePanel(GameController gameController) {
        this.gameController = gameController;
        
        // 2. Initialize UI components
        activePlayerLabel = new JLabel("Active Player: Waiting for game to start...");
        diceResultLabel = new JLabel("Roll Result: -");
        rollButton = new JButton("Roll Dice");

        // 3. Update the Action Listener
        rollButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Fetch the current active player from the controller
                Player currentPlayer = gameController.getTurnManager().getActivePlayer(); 
                
                // Trigger the controller logic instead of rolling locally
                gameController.attemptDiceRoll(currentPlayer);
            }
        });

        // Add components to the panel
        add(activePlayerLabel);
        add(rollButton);
        add(diceResultLabel);
    }

    // 4. Methods for the GameController to update the UI
    public void updateActivePlayerDisplay(String playerName) {
        activePlayerLabel.setText("Active Player: " + playerName);
    }

    public void updateDiceResultDisplay(int result) {
        diceResultLabel.setText("Roll Result: " + result);
    }
}

//package View;
//
//import javax.swing.JButton;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import java.awt.FlowLayout;
//import Model.StepsDie;
//import java.util.function.IntConsumer;

//public class DicePanel extends JPanel {
//	private final JButton rollButton = new JButton("Roll Dice");
//	private final JLabel resultLabel = new JLabel("Result: -");
//	private final StepsDie stepsDie = new StepsDie();
//	private IntConsumer onRoll;
//	
//	public DicePanel() {
//	    setLayout(new FlowLayout());
//
//	    add(rollButton);
//	    add(resultLabel);
//	    
//	    // Rolls the die and displays the result
//	    rollButton.addActionListener(e -> {
//	    	int result = stepsDie.roll();
//	    	resultLabel.setText("Result: " + result);
//	    	
//	    	if (onRoll != null) {
//	    		onRoll.accept(result);
//	    	}
//	    });
//	}
//	
//	// Sets the action to perform after rolling the die
//	public void setOnRoll(IntConsumer onRoll) {
//	    this.onRoll = onRoll;
//	}
//}
