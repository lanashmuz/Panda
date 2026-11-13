package Controller;

import javax.swing.SwingUtilities;
import View.GameSetupFrame;

public class Main {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			GameSetupFrame setupFrame = new GameSetupFrame();
			new GameController(setupFrame);
			setupFrame.setVisible(true);
		});
	}
}