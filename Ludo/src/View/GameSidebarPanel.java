
package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Model.Player;

import javax.swing.JTextArea;
import javax.swing.UIManager;

public class GameSidebarPanel extends JPanel {

	private final JTextArea statusLabel;
	private final Map<Player, JPanel> playerRows = new LinkedHashMap<>();

    public GameSidebarPanel(List<Player> players, DicePanel dicePanel) {

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Arrange sidebar sections vertically
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Current turn and dice section
        JPanel diceSection = createSection("CURRENT TURN & DICE");
        diceSection.add(dicePanel, BorderLayout.CENTER);

        contentPanel.add(diceSection);
        contentPanel.add(Box.createVerticalStrut(15));

        // Players section
        JPanel playersSection = createSection("PLAYERS");

        JPanel playersList = new JPanel(
                new GridLayout(0, 1, 0, 8)
        );

        for (Player player : players) {

            JPanel playerRow = new JPanel(
                    new FlowLayout(FlowLayout.LEFT, 8, 2)
            );

            JLabel colorLabel = new JLabel("●");
            colorLabel.setForeground(getPlayerColor(player.getColor()));

            JLabel nameLabel = new JLabel(player.getName());

            playerRow.add(colorLabel);
            playerRow.add(nameLabel);
            playerRows.put(player, playerRow);
            playersList.add(playerRow);
        }

        playersSection.add(playersList, BorderLayout.CENTER);

        contentPanel.add(playersSection);
        contentPanel.add(Box.createVerticalStrut(15));

        // Game status section
        JPanel statusSection = createSection("GAME STATUS");

		statusLabel = new JTextArea("Game ready. Roll the dice!");
		statusLabel.setEditable(false);
		statusLabel.setLineWrap(true);
		statusLabel.setWrapStyleWord(true);
		statusLabel.setOpaque(false);
		statusLabel.setFocusable(false);
		statusLabel.setFont(UIManager.getFont("Label.font"));

        statusSection.add(statusLabel, BorderLayout.CENTER);

        contentPanel.add(statusSection);

        add(contentPanel, BorderLayout.NORTH);
    }

    // Creates a titled sidebar section
    private JPanel createSection(String title) {

        JPanel section = new JPanel(new BorderLayout());

        section.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(title),
                        BorderFactory.createEmptyBorder(12, 12, 12, 12)
                )
        );

        return section;
    }

    // Returns the display color of each player
    private Color getPlayerColor(String color) {

        return switch (color) {
            case "Red" -> new Color(220, 58, 62);
            case "Green" -> new Color(0, 153, 90);
            case "Blue" -> new Color(45, 91, 171);
            case "Yellow" -> new Color(245, 196, 40);
            default -> Color.GRAY;
        };
    }

    // Updates the game status message
    public void updateStatus(String message) {
        statusLabel.setText(message);
    }
    

public void updateActivePlayer(Player activePlayer) {

    for (Map.Entry<Player, JPanel> entry : playerRows.entrySet()) {

        Player player = entry.getKey();
        JPanel playerRow = entry.getValue();

        boolean isActive = player == activePlayer;

        // Highlight the active player's row
        playerRow.setOpaque(true);
        playerRow.setBackground(
                isActive ? new Color(232, 242, 255) : getBackground()
        );

        // Add a colored border for the active player
        playerRow.setBorder(
                BorderFactory.createCompoundBorder(
                        isActive
                                ? BorderFactory.createMatteBorder(
                                        0, 4, 0, 0,
                                        getPlayerColor(player.getColor()))
                                : BorderFactory.createEmptyBorder(0, 4, 0, 0),
                        BorderFactory.createEmptyBorder(6, 8, 6, 8)
                )
        );
    }

    repaint();
}

}
