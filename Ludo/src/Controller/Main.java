package Controller;

import javax.swing.SwingUtilities;
import View.GameSetupFrame;
import View.StartScreenFrame;
import javax.swing.JFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import View.ExitConfirmation;

public class Main {

	public static void main(String[] args) {

		SwingUtilities.invokeLater(() -> {

			// Create the start screen
			StartScreenFrame startFrame = new StartScreenFrame();

			// Create the game setup screen
			GameSetupFrame setupFrame = new GameSetupFrame();

			// Connect game setup to the game controller
			new GameController(setupFrame);

			// Open game setup when Start New Game is clicked
			startFrame.setOnStartNewGame(() -> {
				startFrame.setVisible(false);
				setupFrame.setVisible(true);
			});

			// Shared navigation back to the start screen
			Runnable showStartScreen = () -> {
				setupFrame.setVisible(false);
				startFrame.setVisible(true);
			};

			// Handle the Back to Main Menu button
			setupFrame.setOnBackToStart(showStartScreen);

			// Require confirmation before exiting from either screen.
			installExitConfirmation(startFrame);
			installExitConfirmation(setupFrame);

			// Display the start screen first
			startFrame.setVisible(true);
		});

	}
	
	/**
	 * Configures a window to request confirmation before closing the application.
	 */
	private static void installExitConfirmation(JFrame frame) {
	    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

	    frame.addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent e) {
	            if (ExitConfirmation.confirmExit(frame, false)) {
	                System.exit(0);
	            }
	        }
	    });
	}
}