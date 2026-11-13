
package View;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Font;
import java.awt.Insets;

public class GameFrame extends JFrame {

    private Runnable onBackToMainMenu;

    public GameFrame(JPanel boardPanel, JPanel sidebarPanel) {
        setTitle("Panda - Ludo");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main game layout
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        // Back to game setup button
        JButton backButton = new JButton("← Back to Game Setup");

        backButton.setPreferredSize(new Dimension(215, 42));
        backButton.setFont(
                new Font(Font.SANS_SERIF, Font.BOLD, 14)
        );
        backButton.setMargin(new Insets(8, 14, 8, 14));
        
        backButton.addActionListener(e -> {
            if (onBackToMainMenu != null) {
                onBackToMainMenu.run();
            }
        });

        JPanel navigationPanel = new JPanel(new BorderLayout());
        navigationPanel.add(backButton, BorderLayout.WEST);
        mainPanel.add(navigationPanel, BorderLayout.NORTH);

        // Board area
        mainPanel.add(boardPanel, BorderLayout.CENTER);

        // Sidebar area
        JPanel sidebarContainer = new JPanel(new BorderLayout());
        sidebarContainer.setPreferredSize(new Dimension(280, 0));
        sidebarContainer.add(sidebarPanel, BorderLayout.NORTH);

        mainPanel.add(sidebarContainer, BorderLayout.EAST);

        setContentPane(mainPanel);
        pack();

        setMinimumSize(new Dimension(900, 650));
        setLocationRelativeTo(null);
    }

    public void setOnBackToMainMenu(Runnable onBackToMainMenu) {
        this.onBackToMainMenu = onBackToMainMenu;
    }
}
