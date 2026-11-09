package View;
import Controller.GameController;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import Model.EasyBoard;

public class EasyBoardFrame extends JFrame {

    private Runnable onBackToMainMenu;

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
        mainPanel.add(new EasyBoardPanel(board), BorderLayout.CENTER);
        // Display the dice controls below the board.
//        mainPanel.add(new DicePanel(), BorderLayout.SOUTH);
        this.dicePanel = new DicePanel(gameController);
        setContentPane(mainPanel);
        pack();
        setMinimumSize(new Dimension(450, 450));
        setLocationRelativeTo(null);
    }

    public void setOnBackToMainMenu(Runnable onBackToMainMenu) {
        this.onBackToMainMenu = onBackToMainMenu;
    }
    public DicePanel getDicePanel() {
        return this.dicePanel;
    }
}

