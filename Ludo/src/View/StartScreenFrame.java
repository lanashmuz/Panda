
package View;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class StartScreenFrame extends JFrame {

    private Runnable onStartNewGame;

    public StartScreenFrame() {

        setTitle("Panda Ludo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 460);
        setMinimumSize(new Dimension(440, 420));
        setLocationRelativeTo(null);

        // Main screen layout
        JPanel mainPanel = new JPanel(new BorderLayout(0, 25));
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(35, 55, 35, 55)
        );

        // Game title
        JLabel titleLabel = new JLabel(
                "PANDA LUDO", SwingConstants.CENTER
        );
        titleLabel.setFont(
                new Font(Font.SANS_SERIF, Font.BOLD, 32)
        );

        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Menu buttons
        JPanel buttonsPanel = new JPanel(
                new GridLayout(4, 1, 0, 14)
        );

        JButton startButton = new JButton("Start New Game");
        JButton historyButton = new JButton("Game History");
        JButton questionsButton = new JButton("Question Manager");
        JButton exitButton = new JButton("Exit");

        // Features planned for future iterations
        historyButton.setEnabled(false);
        questionsButton.setEnabled(false);

        // Notify the controller when starting a game
        startButton.addActionListener(e -> {
            if (onStartNewGame != null) {
                onStartNewGame.run();
            }
        });

        // Close the application
        exitButton.addActionListener(e ->
                dispatchEvent(
                        new WindowEvent(
                                this, WindowEvent.WINDOW_CLOSING
                        )
                )
        );

        buttonsPanel.add(startButton);
        buttonsPanel.add(historyButton);
        buttonsPanel.add(questionsButton);
        buttonsPanel.add(exitButton);

        mainPanel.add(buttonsPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    public void setOnStartNewGame(Runnable onStartNewGame) {
        this.onStartNewGame = onStartNewGame;
    }
}
