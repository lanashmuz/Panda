package Controller;

import javax.swing.SwingUtilities;

import View.GameSetupFrame;

public class Main {

	public static void main(String[] args) {
		// Start the Swing interface on the Event Dispatch Thread
		SwingUtilities.invokeLater(() -> {
			GameSetupFrame setupFrame = new GameSetupFrame();
			setupFrame.setVisible(true);
		});

	}

}
