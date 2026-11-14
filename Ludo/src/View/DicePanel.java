package View;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.function.IntConsumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import Controller.GameController;
import Model.Player;

/** Visual refresh; keeps dice-roll rules in GameController. */
public class DicePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private final JButton rollButton;
	private final JLabel activePlayerLabel;
	private final JLabel diceResultLabel;
	private final GameController gameController;
	private IntConsumer onRoll;

	public DicePanel(GameController controller) {
		this.gameController = controller;
		setOpaque(false);
		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createEmptyBorder(2, 7, 4, 7));

		activePlayerLabel = new JLabel("Active Player: Waiting...", SwingConstants.CENTER);
		activePlayerLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
		activePlayerLabel.setForeground(new Color(26, 58, 113));

		JLabel title = new JLabel("ROLL RESULT", SwingConstants.CENTER);
		title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
		title.setForeground(new Color(72, 111, 158));

		diceResultLabel = new JLabel("-", SwingConstants.CENTER);
		diceResultLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 47));
		diceResultLabel.setForeground(new Color(31, 61, 107));
		JPanel diceBox = new RoundedDiceBox();
		diceBox.setLayout(new java.awt.BorderLayout());
		diceBox.add(diceResultLabel);
		diceBox.setPreferredSize(new Dimension(118, 70));

		rollButton = new BlueRollButton();
		rollButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 21));
		rollButton.setPreferredSize(new Dimension(285, 54));
		rollButton.addActionListener(e -> {
			if (gameController == null || gameController.getTurnManager() == null)
				return;
			rollButton.setEnabled(false);
			Player current = gameController.getTurnManager().getActivePlayer();
			gameController.attemptDiceRoll(current);
		});

		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(2, 0, 5, 0);
		c.gridy = 0;
		add(activePlayerLabel, c);

		c.gridy = 1;
		c.insets = new Insets(2, 0, 5, 0);
		add(title, c);

		c.gridy = 2;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(2, 0, 6, 0);
		add(diceBox, c);

		c.gridy = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(3, 0, 0, 0);
		add(rollButton, c);
	}

	public void updateActivePlayerDisplay(String playerName) {
		activePlayerLabel.setText("Active Player: " + playerName);
	}

	public void updateDiceResultDisplay(int result) {
		diceResultLabel.setText(String.valueOf(result));
		if (onRoll != null)
			onRoll.accept(result);
	}

	public void setOnRoll(IntConsumer callback) {
		onRoll = callback;
	}

	public void setRollEnabled(boolean enabled) {
		rollButton.setEnabled(enabled);
		rollButton.repaint();
	}

	private static final class RoundedDiceBox extends JPanel {
		private static final long serialVersionUID = 1L;

		RoundedDiceBox() {
			setOpaque(false);
		}

		@Override
		protected void paintComponent(Graphics graphics) {
			Graphics2D g = (Graphics2D) graphics.create();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(new Color(39, 89, 145, 33));
			g.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 9, 26, 26);
			g.setColor(Color.WHITE);
			g.fillRoundRect(2, 2, getWidth() - 7, getHeight() - 10, 24, 24);
			g.setColor(new Color(164, 194, 229));
			g.setStroke(new BasicStroke(2));
			g.drawRoundRect(2, 2, getWidth() - 7, getHeight() - 10, 24, 24);

			g.dispose();
			super.paintComponent(graphics);
		}
	}

	private static final class BlueRollButton extends JButton {
		private static final long serialVersionUID = 1L;

		BlueRollButton() {
			super("ROLL DICE");
			setOpaque(false);
			setContentAreaFilled(false);
			setBorderPainted(false);
			setFocusPainted(false);
			setForeground(Color.WHITE);
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		@Override
		protected void paintComponent(Graphics graphics) {
			Graphics2D g = (Graphics2D) graphics.create();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			int w = getWidth(), h = getHeight();
			g.setColor(new Color(36, 73, 127, 37));
			g.fillRoundRect(3, 6, w - 6, h - 8, h, h);
			Color bright = isEnabled() ? new Color(25, 152, 252) : new Color(156, 180, 205);
			Color dark = isEnabled() ? new Color(12, 87, 203) : new Color(115, 136, 157);
			g.setPaint(new GradientPaint(0, 2, bright, 0, h, dark));
			g.fillRoundRect(2, 2, w - 5, h - 9, h, h);
			if (getModel().isRollover() && isEnabled()) {
				g.setColor(new Color(255, 255, 255, 70));
				g.setStroke(new BasicStroke(2));
				g.drawRoundRect(5, 4, w - 12, h - 16, h, h);
			}
			// Draw the text manually so it stays readable when the button is disabled.
			g.setFont(getFont());
			g.setColor(isEnabled() ? Color.WHITE : new Color(26, 51, 93));

			java.awt.FontMetrics fm = g.getFontMetrics();
			String text = getText();

			int textX = (w - fm.stringWidth(text)) / 2;
			int textY = (h + fm.getAscent() - fm.getDescent()) / 2 - 3;

			g.drawString(text, textX, textY);

			g.dispose();
		}
	}
}
