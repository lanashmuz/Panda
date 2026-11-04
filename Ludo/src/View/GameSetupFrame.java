package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;


/**
 * Represents the game setup window.
 * Allows players to configure the game before starting.
 */
public class GameSetupFrame extends JFrame {

	private JComboBox<Integer> playerCountComboBox;
	private JPanel playerDetailsPanel;
	private JLabel colorWarningLabel;
	
	// Store the color selection boxes for validation
	private final List<JComboBox<String>> colorComboBoxes = new ArrayList<>();
	
	// Creates and configures the game setup window.
	public GameSetupFrame() {
		setTitle("Panda Ludo - Game Setup");
		setSize(600,450);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
	// Create the main panel with spacing
		JPanel mainPanel = new JPanel(new BorderLayout(10,10));
		mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
	
	// Add a selection box for choosing 2 to 4 players
	JPanel playerCountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	JLabel playerCountLabel = new JLabel("Number of players:");
	Integer[] playerOptions = {2,3,4};
	playerCountComboBox = new JComboBox<>(playerOptions);
	
	playerCountPanel.add(playerCountLabel);
	playerCountPanel.add(playerCountComboBox);

	// Create a panel for player names and colors
	playerDetailsPanel = new JPanel(new GridLayout(0, 3, 10, 10));
	
	// Group the player settings at the top of the window
	JPanel setupPanel = new JPanel(new BorderLayout(0, 10));
	setupPanel.add(playerCountPanel, BorderLayout.NORTH);
	setupPanel.add(playerDetailsPanel, BorderLayout.CENTER);
	
	mainPanel.add(setupPanel, BorderLayout.NORTH);
	
	// Display color validation messages
	colorWarningLabel = new JLabel("");
	colorWarningLabel.setForeground(Color.RED);
	mainPanel.add(colorWarningLabel, BorderLayout.SOUTH);
	
	
	// Update the name fields when the player count changes
	playerCountComboBox.addActionListener(e -> updatePlayerNameFields());
	
	// Display the initial fields for two players
	updatePlayerNameFields();
	
	add(mainPanel);
	
	}
	
	// Updates player name and color fields based on player count
	private void updatePlayerNameFields() {
		playerDetailsPanel.removeAll();
		colorComboBoxes.clear();
		
		int count = (Integer) playerCountComboBox.getSelectedItem();
		String [] colors = {"Red", "Blue", "Green", "Yellow"};
		
		for (int i = 1; i <= count; i++) {
			// Add a name field for each player
			JLabel nameLabel = new JLabel("Player " + i + ":");
			JTextField nameField= new JTextField(15);
			// Add a color selection box for each player
			JComboBox<String> colorComboBox = new JComboBox<>(colors);
			colorComboBox.setSelectedIndex(i - 1);
			// Validate colors whenever a selection changes
			colorComboBox.addActionListener(e -> validateUniqueColors());
			colorComboBoxes.add(colorComboBox);
			
			playerDetailsPanel.add(nameLabel);
			playerDetailsPanel.add(nameField);
			playerDetailsPanel.add(colorComboBox);
			
		}
		
		// Refresh the panel after changing its components
		playerDetailsPanel.revalidate();
		playerDetailsPanel.repaint();
		validateUniqueColors();
		
	}
	
	/** Checks whether all players have different colors
	 * @return true if all selected colors are unique */
	private boolean validateUniqueColors() {
		Set<String> selectedColors = new HashSet<>();
		
		for (JComboBox<String> colorComboBox : colorComboBoxes) {
			String color = (String) colorComboBox.getSelectedItem();
			// Show an error when a color is already selected
			if (!selectedColors.add(color)) {
				colorWarningLabel.setText(
						"Each player must have a unique color.");
				return false;
			}
		}
		// Clear the warning when all colors are unique
		colorWarningLabel.setText("");
		return true;
	}

}
