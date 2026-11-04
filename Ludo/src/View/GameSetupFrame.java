package View;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
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
	private JPanel playerNamesPanel;
	private JPanel playerDetailsPanel;
	
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
	JPanel playerCountPanel = new JPanel(new FlowLayout());
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
	
	// Update the name fields when the player count changes
	playerCountComboBox.addActionListener(e -> updatePlayerNameFields());
	
	// Display the initial fields for two players
	updatePlayerNameFields();
	
	add(mainPanel);
	
	}
	
	// Updates player name and color fields based on player count
	private void updatePlayerNameFields() {
		playerDetailsPanel.removeAll();
		
		int count = (Integer) playerCountComboBox.getSelectedItem();
		String [] colors = {"Red", "Blue", "Green", "Yellow"};
		
		for (int i = 1; i <= count; i++) {
			// Add a name field for each player
			JLabel nameLabel = new JLabel("Player " + i + ":");
			JTextField nameField= new JTextField(15);
			// Add a color selection box for each player
			JComboBox<String> colorComboBox = new JComboBox<>(colors);
			colorComboBox.setSelectedIndex(i - 1);
			
			playerDetailsPanel.add(nameLabel);
			playerDetailsPanel.add(nameField);
			playerDetailsPanel.add(colorComboBox);
			
		}
		
		// Refresh the panel after changing its components
		playerDetailsPanel.revalidate();
		playerDetailsPanel.repaint();
	}

}
