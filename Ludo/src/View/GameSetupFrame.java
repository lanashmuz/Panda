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

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import Model.Player;
import Model.GameConfiguration;

import java.util.function.Consumer;

/**
 * Represents the game setup window.
 * Allows players to configure the game before starting.
 */
public class GameSetupFrame extends JFrame {

	private JComboBox<Integer> playerCountComboBox;
	private JPanel playerDetailsPanel;
	private JLabel colorWarningLabel;
	private Consumer<GameConfiguration> onStartGame;
	
	// Store the color selection boxes for validation
	private final List<JComboBox<String>> colorComboBoxes = new ArrayList<>();
	
	// Store player name fields for creating player objects
	private final List<JTextField> playerNameFields = new ArrayList<>();
	
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
	
	// Create the difficulty selection panel
	JPanel difficultyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	JLabel difficultyLabel = new JLabel("Difficuly:");
	
	JRadioButton easyButton = new JRadioButton("Easy", true);
	JRadioButton mediumButton = new JRadioButton("Medium");
	JRadioButton hardButton = new JRadioButton("Hard");
	
	// Only Easy mode is available for Iteration 1
	mediumButton.setEnabled(false);
	hardButton.setEnabled(false);
	
	// Allow only one difficulty to be selected
	ButtonGroup difficultyGroup = new ButtonGroup();
    difficultyGroup.add(easyButton);
    difficultyGroup.add(mediumButton);
    difficultyGroup.add(hardButton);
    
    // Add difficulty options to the setup menu
    difficultyPanel.add(difficultyLabel);
    difficultyPanel.add(easyButton);
    difficultyPanel.add(mediumButton);
    difficultyPanel.add(hardButton);
    
    setupPanel.add(difficultyPanel, BorderLayout.SOUTH);
    
	mainPanel.add(setupPanel, BorderLayout.NORTH);
	
	// Display color validation messages
	colorWarningLabel = new JLabel("");
	colorWarningLabel.setForeground(Color.RED);

	// Create the bottom panel with validation messages and Start button
	JPanel bottomPanel = new JPanel(new BorderLayout(0, 10));
	bottomPanel.add(colorWarningLabel,BorderLayout.NORTH);
	
	JButton startButton = new JButton("Start Game");
	startButton.addActionListener(e -> handleStartGame());
	
	bottomPanel.add(startButton, BorderLayout.SOUTH);
	mainPanel.add(bottomPanel, BorderLayout.SOUTH);
	// Update the name fields when the player count changes
	playerCountComboBox.addActionListener(e -> updatePlayerNameFields());
	
	// Display the initial fields for two players
	updatePlayerNameFields();
	
	add(mainPanel);
	
	}
	
	// Updates player name and color fields based on player count
	private void updatePlayerNameFields() {

        // Preserve existing player details when the count changes
        List<String> previousNames = new ArrayList<>();
        List<String> previousColors = new ArrayList<>();

        for (JTextField nameField : playerNameFields) {
            previousNames.add(nameField.getText());
        }

        for (JComboBox<String> colorBox : colorComboBoxes) {
            previousColors.add((String) colorBox.getSelectedItem());
        }

		playerDetailsPanel.removeAll();
		playerNameFields.clear();
		colorComboBoxes.clear();
		
		int count = (Integer) playerCountComboBox.getSelectedItem();
		String [] colors = {"Red", "Blue", "Green", "Yellow"};
		
		for (int i = 1; i <= count; i++) {
			// Add a name field for each player
			JLabel nameLabel = new JLabel("Player " + i + ":");
			JTextField nameField= new JTextField(15);
            // Restore the name if this player already existed
            if (i <= previousNames.size()) {
                nameField.setText(previousNames.get(i - 1));
            }

			playerNameFields.add(nameField);
			// Add a color selection box for each player
			JComboBox<String> colorComboBox = new JComboBox<>(colors);

            // Restore the previous color or assign a default color
            if (i <= previousColors.size()) {
                colorComboBox.setSelectedItem(previousColors.get(i - 1));
            } else {
                colorComboBox.setSelectedIndex(i - 1);
            }

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
	
	// Creates a game configuration from the selected player details
	private GameConfiguration buildGameConfiguration() {
		List<Player> players = new ArrayList<>();
		
		// Collect each player's name and selected color
		for (int i = 0; i < playerNameFields.size(); i++) {
			String name = playerNameFields.get(i).getText().trim();
	        String color = (String) colorComboBoxes.get(i).getSelectedItem();

	        players.add(new Player(name, color));
	    }
		
		// Easy is the only available difficulty in Iteration 1
		return new GameConfiguration(players, "Easy");

		}
	

    /**
     * Validates player details before preparing the game
     */
    private void handleStartGame() {
		// Check that every player has a name
		for (JTextField nameField : playerNameFields) {
			if (nameField.getText().trim().isEmpty()) {
				JOptionPane.showMessageDialog(this,
						"Please enter a name for every player.");
				return;
			}
		}
	
		// Prevent starting with duplicate colors
		if (!validateUniqueColors()) {
			JOptionPane.showMessageDialog(this,
					"Each player must have a unique color.");
			return;
		}
	
		if (onStartGame == null) {
			throw new IllegalStateException("Start Game handler is missing.");
		}
	
		// Pass the selected configuration to the controller
		GameConfiguration configuration = buildGameConfiguration();
		onStartGame.accept(configuration);
	}
    
    // Locks the setup fields after the game starts
    private void lockSetupFields() {
    	
    	// Prevent changes to the number of players
    	playerCountComboBox.setEnabled(false);
    	
    	// Present changes to player names
    	for (JTextField nameField : playerNameFields) {
    		nameField.setEditable(false);
    	}
    	
    	// Prevent changes to player colors
    	for (JComboBox<String> colorBox : colorComboBoxes) {
    		colorBox.setEditable(false);
    	}
    }

	public void setOnStartGame(Consumer<GameConfiguration> onStartGame) {
		this.onStartGame = onStartGame;
	}
}
