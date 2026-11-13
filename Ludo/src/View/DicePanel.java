package View;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.IntConsumer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Controller.GameController;
import Model.Player;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;

public class DicePanel extends JPanel {

	private JButton rollButton;
	private JLabel activePlayerLabel;
	private JLabel diceResultLabel;
	private GameController gameController;
	private IntConsumer onRoll;

	// 1. Pass GameController into the constructor
	public DicePanel(GameController gameController) {
		this.gameController = gameController;
		// Arrange dice controls vertically
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

		// 2. Initialize UI components
		activePlayerLabel = new JLabel("Active Player: Waiting for game to start...");
		diceResultLabel = new JLabel("Roll Result: -");
		rollButton = new JButton("Roll Dice");

		// 3. Update the Action Listener to handle both Turn Logic and Hila's validation
		rollButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Lock the button until the roll is handled (Hila's logic)
				rollButton.setEnabled(false);

				// Fetch the current active player from the controller
				Player currentPlayer = gameController.getTurnManager().getActivePlayer();

				// Trigger the controller logic instead of rolling locally
				gameController.attemptDiceRoll(currentPlayer);
			}
		});


		// Align components to the left
		activePlayerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		diceResultLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		rollButton.setAlignmentX(Component.LEFT_ALIGNMENT);

		// Set a consistent button height
		rollButton.setMaximumSize(
		        new Dimension(Integer.MAX_VALUE, 38)
		);

		// Add components vertically with spacing
		add(activePlayerLabel);
		add(Box.createVerticalStrut(12));

		add(diceResultLabel);
		add(Box.createVerticalStrut(12));

		add(rollButton);

	}

	// 4. Methods for the GameController to update the UI
	public void updateActivePlayerDisplay(String playerName) {
		activePlayerLabel.setText("Active Player: " + playerName);
	}

	public void updateDiceResultDisplay(int result) {
		diceResultLabel.setText("Roll Result: " + result);

		// Pass the result to Hila's movement validation logic
		if (onRoll != null) {
			onRoll.accept(result);
		}
	}

	// Sets the action to perform after rolling the die
	public void setOnRoll(IntConsumer onRoll) {
		this.onRoll = onRoll;
	}

	// give controller option to restart the dice
	public void setRollEnabled(boolean enabled) {
		rollButton.setEnabled(enabled);
	}
}