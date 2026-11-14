
package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/** Shared, themed exit confirmation for all Panda Ludo screens. */
public final class ExitConfirmation {

	private static final Color NAVY = new Color(26, 51, 93);

	private ExitConfirmation() {
		// Utility class.
	}

	public static boolean confirmExit(Component parent, boolean gameInProgress) {
		return showDialog(parent, "Exit Panda Ludo?", "Are you sure you want to exit Panda Ludo?",
				gameInProgress ? "Your current game progress will be lost." : null, true);
	}

	/** Displays a themed warning dialog with a single OK button. */
	public static void showWarning(Component parent, String title, String message) {
		showDialog(parent, title, message, null, false);
	}

	/** Builds the shared dialog for exit confirmations and warnings. */
	private static boolean showDialog(Component parent, String titleText, String messageText, String warningText,
			boolean exitConfirmation) {
		Window owner = SwingUtilities.getWindowAncestor(parent);
		JDialog dialog = new JDialog(owner, Dialog.ModalityType.APPLICATION_MODAL);

		// Remove the native Windows frame for a consistent game theme.
		dialog.setUndecorated(true);
		// Keep the dialog opaque so its appearance does not depend on the screen behind
		// it.
		dialog.setBackground(Color.WHITE);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		boolean[] confirmed = { false };

		JPanel content = new JPanel(new BorderLayout(0, 18)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics graphics) {
				Graphics2D g = (Graphics2D) graphics.create();
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				int w = getWidth();
				int h = getHeight();

				// Paint a consistent opaque background for all Panda Ludo dialogs.
				g.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, h, new Color(239, 247, 255)));

				g.fillRect(0, 0, w, h);

				// Draw a subtle blue outline.
				g.setColor(new Color(165, 203, 235));
				g.setStroke(new java.awt.BasicStroke(1.5f));
				g.drawRoundRect(1, 1, w - 3, h - 3, 26, 26);

				g.dispose();
			}
		};

		content.setOpaque(true);
		content.setBackground(Color.WHITE);
		content.setBorder(BorderFactory.createEmptyBorder(25, 28, 24, 38));

		// Header with a custom close button.
		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);

		JLabel title = new JLabel(titleText);
		title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
		title.setForeground(NAVY);

		JButton closeButton = new JButton("\u00D7");
		closeButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 27));
		closeButton.setForeground(NAVY);
		closeButton.setContentAreaFilled(false);
		closeButton.setBorderPainted(false);
		closeButton.setFocusPainted(false);
		closeButton.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		closeButton.addActionListener(e -> dialog.dispose());

		header.add(title, BorderLayout.WEST);
		header.add(closeButton, BorderLayout.EAST);
		content.add(header, BorderLayout.NORTH);

		// Confirmation message.
		JPanel message = new JPanel();
		message.setOpaque(false);
		message.setLayout(new BoxLayout(message, BoxLayout.Y_AXIS));

		JLabel question = new JLabel(messageText);
		question.setForeground(NAVY);
		question.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
		question.setAlignmentX(Component.LEFT_ALIGNMENT);
		message.add(question);

		if (warningText != null) {
			message.add(Box.createVerticalStrut(10));

			JLabel warning = new JLabel(warningText);
			warning.setForeground(new Color(190, 58, 69));
			warning.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
			warning.setAlignmentX(Component.LEFT_ALIGNMENT);
			message.add(warning);
		}

		content.add(message, BorderLayout.CENTER);

		// Use the same button style for exit confirmations and warnings.
		JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
		actions.setOpaque(false);

		if (exitConfirmation) {
			JButton cancelButton = styledButton("Cancel", new Color(67, 157, 246), new Color(17, 104, 208));

			JButton exitButton = styledButton("Exit", new Color(241, 105, 111), new Color(209, 55, 68));

			cancelButton.addActionListener(e -> dialog.dispose());

			exitButton.addActionListener(e -> {
				confirmed[0] = true;
				dialog.dispose();
			});

			actions.add(cancelButton);
			actions.add(exitButton);

			dialog.getRootPane().setDefaultButton(cancelButton);
		} else {
			JButton okButton = styledButton("OK", new Color(67, 157, 246), new Color(17, 104, 208));

			okButton.addActionListener(e -> dialog.dispose());

			actions.add(okButton);
			dialog.getRootPane().setDefaultButton(okButton);
		}

		content.add(actions, BorderLayout.SOUTH);

		dialog.setContentPane(content);
		dialog.setSize(new Dimension(520, warningText != null ? 238 : 213));
		// Round the dialog window without making it transparent.
		try {
			dialog.setShape(new RoundRectangle2D.Double(0, 0, dialog.getWidth(), dialog.getHeight(), 28, 28));
		} catch (UnsupportedOperationException e) {
			// Use the regular window shape if unsupported.
		}

		dialog.setLocationRelativeTo(parent);

		// Enter defaults to Cancel; Escape also dismisses safely.
		dialog.getRootPane().registerKeyboardAction(e -> dialog.dispose(),
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

		dialog.setVisible(true);
		return confirmed[0];
	}

	/** Creates a reusable rounded button with a vertical gradient. */
	private static JButton styledButton(String label, Color top, Color bottom) {
		JButton button = new JButton(label) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics graphics) {
				Graphics2D g = (Graphics2D) graphics.create();
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				g.setPaint(new GradientPaint(0, 0, top, 0, getHeight(), bottom));
				g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);

				g.dispose();
				super.paintComponent(graphics);
			}
		};

		button.setPreferredSize(new Dimension(128, 43));
		button.setOpaque(false);
		button.setContentAreaFilled(false);
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
		button.setForeground(Color.WHITE);
		button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		return button;
	}
}
