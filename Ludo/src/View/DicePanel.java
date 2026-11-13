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

import java.awt.Color;
import java.awt.Font;
import javax.swing.SwingConstants;

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
		JLabel diceTitleLabel = new JLabel("ROLL RESULT");
		diceResultLabel = new JLabel("-");
		rollButton = new JButton("Roll Dice");
		

		// Make the active player easier to read
		activePlayerLabel.setFont(
		        new Font(Font.SANS_SERIF, Font.BOLD, 15)
		);

		// Style the dice title
		diceTitleLabel.setFont(
		        new Font(Font.SANS_SERIF, Font.BOLD, 12)
		);
		diceTitleLabel.setForeground(new Color(90, 100, 115));

		// Highlight the dice result
		diceResultLabel.setFont(
		        new Font(Font.SANS_SERIF, Font.BOLD, 40)
		);
		diceResultLabel.setHorizontalAlignment(SwingConstants.CENTER);
		diceResultLabel.setOpaque(true);
		diceResultLabel.setBackground(new Color(240, 244, 248));
		diceResultLabel.setBorder(
		        BorderFactory.createEmptyBorder(12, 12, 12, 12)
		);


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



		// Align sidebar components
		activePlayerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		diceTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		diceResultLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		rollButton.setAlignmentX(Component.LEFT_ALIGNMENT);

		// Make dice result and button fill available width
		diceResultLabel.setMaximumSize(
		        new Dimension(Integer.MAX_VALUE, 86)
		);

		rollButton.setMaximumSize(
		        new Dimension(Integer.MAX_VALUE, 40)
		);

		// Arrange components vertically
		add(activePlayerLabel);
		add(Box.createVerticalStrut(18));

		add(diceTitleLabel);
		add(Box.createVerticalStrut(6));
		add(diceResultLabel);
		add(Box.createVerticalStrut(14));

		add(rollButton);


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
		diceResultLabel.setText(String.valueOf(result));

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