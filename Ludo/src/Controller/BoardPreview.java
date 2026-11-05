package Controller;

import java.util.List;

import javax.swing.SwingUtilities;

import Model.EasyBoard;
import Model.GameConfiguration;
import Model.Player;
import View.EasyBoardFrame;

// Launches a standalone preview of the board.
public class BoardPreview {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameConfiguration configuration = new GameConfiguration(
                List.of(
                    new Player("Player 1", "Green"),
                    new Player("Player 2", "Yellow"),
                    new Player("Player 3", "Blue"),
                    new Player("Player 4", "Red")
                ),
                "Easy"
            );

            EasyBoard board = new EasyBoard(configuration);

            EasyBoardFrame frame = new EasyBoardFrame(board);
            frame.setVisible(true);
        });
    }
}
