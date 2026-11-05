package View;

import java.awt.Dimension;

import javax.swing.JFrame;

import Model.EasyBoard;

// Provides a window for displaying the Easy board.
public class EasyBoardFrame extends JFrame {

    public EasyBoardFrame(EasyBoard board) {
        setTitle("Panda - Easy Board");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setContentPane(new EasyBoardPanel(board));

        pack();
        setMinimumSize(new Dimension(450, 450));
        setLocationRelativeTo(null);
    }
}
