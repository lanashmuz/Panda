
package View;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Window;
import java.net.URL;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;

import Model.Question;

/**
 * Displays an educational question with selectable answer buttons.
 */
public class QuestionDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private static final Color NAVY = new Color(25, 57, 112);
	private static final Color LIGHT_BLUE = new Color(239, 247, 255);

	private int selectedAnswerIndex = -1;

	public QuestionDialog(Window owner, Question question) {
		super(owner, "Panda Ludo - Question", Dialog.ModalityType.APPLICATION_MODAL);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				ExitConfirmation.showWarning(QuestionDialog.this, "Answer Required",
						"You must answer the question before continuing the game.");
			}
		});

		setSize(760, 570);
		setLocationRelativeTo(owner);

		URL icon = getClass().getResource("/View/Images/panda_app_icon.png");

		if (icon != null) {
			setIconImage(new ImageIcon(icon).getImage());
		}

		// Center the entire question layout within the dialog.
		JPanel content = new JPanel(new GridBagLayout()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				Graphics2D g2 = (Graphics2D) g.create();
				g2.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), LIGHT_BLUE));
				g2.fillRect(0, 0, getWidth(), getHeight());
				g2.dispose();
			}
		};

		content.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setOpaque(false);

		JLabel difficultyLabel = new JLabel(question.getDifficulty().toUpperCase(Locale.ROOT) + " QUESTION");

		difficultyLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
		difficultyLabel.setForeground(getDifficultyColor(question.getDifficulty()));
		difficultyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		mainPanel.add(difficultyLabel);
		mainPanel.add(Box.createVerticalStrut(12));

		// Center the question text and support multiple lines.
		JTextPane questionText = new JTextPane();
		questionText.setText(question.getQuestionText());
		questionText.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 21));
		questionText.setForeground(NAVY);
		questionText.setEditable(false);
		questionText.setFocusable(false);
		questionText.setOpaque(false);
		questionText.setBorder(null);

		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		questionText.getStyledDocument().setParagraphAttributes(0, questionText.getDocument().getLength(), center,
				false);

		questionText.setSize(650, Short.MAX_VALUE);
		int textHeight = Math.max(38, Math.min(110, questionText.getPreferredSize().height + 8));

		questionText.setPreferredSize(new Dimension(650, textHeight));
		questionText.setMaximumSize(new Dimension(650, textHeight));
		questionText.setAlignmentX(Component.CENTER_ALIGNMENT);

		mainPanel.add(questionText);
		mainPanel.add(Box.createVerticalStrut(18));

		// Allow selecting one answer before submitting.
		JPanel answersPanel = new JPanel(new GridLayout(4, 1, 0, 10));
		answersPanel.setOpaque(false);
		answersPanel.setPreferredSize(new Dimension(660, 302));
		answersPanel.setMaximumSize(new Dimension(660, 302));
		answersPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		JButton submitButton = new JButton("Submit Answer");
		submitButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		submitButton.setBackground(NAVY);
		submitButton.setForeground(Color.WHITE);
		submitButton.setFocusPainted(false);
		submitButton.setEnabled(false);
		submitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		submitButton.setPreferredSize(new Dimension(220, 45));
		submitButton.setMaximumSize(new Dimension(220, 45));

		JButton[] answerButtons = new JButton[question.getOptions().size()];

		for (int i = 0; i < answerButtons.length; i++) {
			final int index = i;

			AnswerButton answerButton = new AnswerButton(formatAnswerText(question.getOptions().get(i)));

			answerButton.addActionListener(e -> {
				selectedAnswerIndex = index;

				// Highlight only the selected answer.
				for (int j = 0; j < answerButtons.length; j++) {
					answerButtons[j].setSelected(j == index);
				}

				submitButton.setEnabled(true);
			});

			answerButtons[i] = answerButton;
			answersPanel.add(answerButton);
		}

		mainPanel.add(answersPanel);
		mainPanel.add(Box.createVerticalStrut(18));

		// Submit the answer only after the player confirms.
		submitButton.addActionListener(e -> setVisible(false));

		mainPanel.add(submitButton);
		content.add(mainPanel);

		setContentPane(content);
	}

	private static Color getDifficultyColor(String difficulty) {
		return switch (difficulty.toLowerCase(Locale.ROOT)) {
		case "easy" -> new Color(19, 150, 85);
		case "medium" -> new Color(185, 125, 20);
		case "hard" -> new Color(210, 55, 70);
		default -> NAVY;
		};
	}

	private static String formatAnswerText(String text) {
		String safeText = text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>");

		return "<html><div style='text-align:center;'>" + safeText + "</div></html>";
	}

	/**
	 * Rounded answer button with hover and selection states.
	 */
	private static class AnswerButton extends JButton {

		private static final long serialVersionUID = 1L;

		AnswerButton(String text) {
			super(text);

			setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
			setForeground(NAVY);
			setHorizontalAlignment(SwingConstants.CENTER);
			setContentAreaFilled(false);
			setBorderPainted(false);
			setFocusPainted(false);
			setRolloverEnabled(true);
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			boolean hovered = getModel().isRollover();
			boolean selected = getModel().isSelected();

			Color top = Color.WHITE;
			Color bottom = new Color(232, 245, 255);

			if (hovered) {
				top = new Color(235, 247, 255);
				bottom = new Color(210, 232, 250);
			}

			if (selected) {
				top = new Color(211, 234, 255);
				bottom = new Color(185, 218, 250);
			}

			g2.setPaint(new GradientPaint(0, 0, top, 0, getHeight(), bottom));

			g2.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);

			g2.setColor(
					selected ? new Color(45, 125, 210) : hovered ? new Color(75, 151, 220) : new Color(177, 211, 239));

			g2.setStroke(new BasicStroke(selected ? 2f : 1.3f));

			g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);

			g2.dispose();
			super.paintComponent(g);
		}
	}

	public int getSelectedAnswerIndex() {
		return selectedAnswerIndex;
	}
}
