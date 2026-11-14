package View;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

/** Design 2: game board remains a real, interactive panel. */
public class GameFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final Color NAVY = new Color(25, 57, 112);
	private Runnable onBackToMainMenu;
	private final BrandTitle brandTitle = new BrandTitle();

	public GameFrame(JPanel boardPanel, JPanel sidebarPanel) {
		setTitle("Panda Ludo - Game Play");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		URL icon = getClass().getResource("/View/Images/panda_app_icon.png");
		if (icon != null)
			setIconImage(new ImageIcon(icon).getImage());

		SkyBackground root = new SkyBackground();
		root.setLayout(new BorderLayout(0, 4));
		root.setBorder(BorderFactory.createEmptyBorder(8, 17, 15, 17));

		root.add(createHeader(icon), BorderLayout.NORTH);

		JPanel columns = new JPanel(new BorderLayout(18, 0));
		columns.setOpaque(false);

		RoundedBoardCard boardCard = new RoundedBoardCard();
		boardCard.setLayout(new BorderLayout());
		boardCard.setBorder(BorderFactory.createEmptyBorder(13, 13, 13, 13));
		boardPanel.setOpaque(false);
		boardCard.add(boardPanel, BorderLayout.CENTER);
		columns.add(boardCard, BorderLayout.CENTER);

		JScrollPane sidebarScroll = new JScrollPane(sidebarPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sidebarScroll.setBorder(null);
		sidebarScroll.setOpaque(false);
		sidebarScroll.getViewport().setOpaque(false);
		sidebarScroll.getVerticalScrollBar().setUnitIncrement(16);
		sidebarScroll.setPreferredSize(new Dimension(375, 640));
		columns.add(sidebarScroll, BorderLayout.EAST);

		root.add(columns, BorderLayout.CENTER);
		setContentPane(root);
		root.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int desired = Math.max(302, Math.min(410, root.getWidth() * 29 / 100));
				if (sidebarScroll.getPreferredSize().width != desired) {
					sidebarScroll.setPreferredSize(new Dimension(desired, 640));
					columns.revalidate();
				}
			}
		});

		setSize(1260, 815);
		setMinimumSize(new Dimension(980, 650));
		setLocationRelativeTo(null);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	private JPanel createHeader(URL icon) {
		JPanel bar = new JPanel(new BorderLayout(12, 0));
		bar.setOpaque(false);
		bar.setPreferredSize(new Dimension(100, 104));

		JPanel left = new JPanel(new BorderLayout());
		left.setOpaque(false);
		left.setPreferredSize(new Dimension(270, 100));
		JButton back = new PillButton("\u2190  Back to Game Setup");
		back.addActionListener(e -> {
			if (onBackToMainMenu != null)
				onBackToMainMenu.run();
		});
		JPanel centeredBack = new JPanel(new BorderLayout());
		centeredBack.setOpaque(false);
		centeredBack.setBorder(BorderFactory.createEmptyBorder(23, 4, 27, 0));
		centeredBack.add(back);
		left.add(centeredBack, BorderLayout.CENTER);
		bar.add(left, BorderLayout.WEST);

		bar.add(brandTitle, BorderLayout.CENTER);

		JPanel right = new JPanel(new BorderLayout());
		right.setOpaque(false);
		right.setPreferredSize(new Dimension(270, 100));
		if (icon != null) {
			Image image = new ImageIcon(icon).getImage().getScaledInstance(88, 88, Image.SCALE_SMOOTH);
			JLabel panda = new JLabel(new ImageIcon(image));
			panda.setHorizontalAlignment(SwingConstants.RIGHT);
			panda.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30));
			right.add(panda, BorderLayout.CENTER);
		}
		bar.add(right, BorderLayout.EAST);
		return bar;
	}

	/** Important: retained callback expected by GameController. */
	public void setOnBackToMainMenu(Runnable callback) {
		this.onBackToMainMenu = callback;
	}

	/** Updates the displayed game duration without changing the timer itself. */
	public void updateElapsedTime(long elapsedSeconds) {
		brandTitle.setElapsedSeconds(Math.max(0, elapsedSeconds));
	}

	private static final class SkyBackground extends JPanel {
		private static final long serialVersionUID = 1L;

		@Override
		protected void paintComponent(Graphics graphics) {
			super.paintComponent(graphics);
			Graphics2D g = (Graphics2D) graphics.create();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			int w = getWidth(), h = getHeight();
			g.setPaint(new GradientPaint(0, 0, new Color(152, 216, 252), 0, h, new Color(193, 235, 255)));
			g.fillRect(0, 0, w, h);
			g.setColor(new Color(255, 255, 255, 52));
			g.fill(new Ellipse2D.Double(-120, h * .18, 360, 275));
			g.fill(new Ellipse2D.Double(w - 230, h * .60, 460, 290));
			g.setColor(new Color(255, 255, 255, 38));
			g.fill(new Ellipse2D.Double(w * .20, -95, 460, 190));
			g.dispose();
		}
	}

	private static final class RoundedBoardCard extends JPanel {
		private static final long serialVersionUID = 1L;

		RoundedBoardCard() {
			setOpaque(false);
		}

		@Override
		protected void paintComponent(Graphics graphics) {
			Graphics2D g = (Graphics2D) graphics.create();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			int w = getWidth(), h = getHeight();
			g.setColor(new Color(26, 76, 131, 47));
			g.fill(new RoundRectangle2D.Double(3, 6, w - 7, h - 9, 35, 35));
			g.setColor(new Color(252, 254, 255));
			g.fill(new RoundRectangle2D.Double(2, 2, w - 6, h - 10, 34, 34));
			g.setColor(new Color(255, 255, 255));
			g.setStroke(new BasicStroke(3));
			g.draw(new RoundRectangle2D.Double(4, 4, w - 10, h - 14, 32, 32));
			g.dispose();
			super.paintComponent(graphics);
		}
	}

	private static final class BrandTitle extends JComponent {
		private static final long serialVersionUID = 1L;
		private long elapsedSeconds;

		void setElapsedSeconds(long seconds) {
			elapsedSeconds = seconds;
			repaint();
		}

		@Override
		protected void paintComponent(Graphics graphics) {
			Graphics2D g = (Graphics2D) graphics.create();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			int size = Math.max(26, Math.min(45, getWidth() / 12));
			Font font = new Font(Font.SANS_SERIF, Font.BOLD, size);
			g.setFont(font);
			FontMetrics fm = g.getFontMetrics();
			String title = "PANDA LUDO";
			int x = (getWidth() - fm.stringWidth(title)) / 2;
			int baseline = 58;
			g.setColor(new Color(255, 255, 255, 210));
			g.drawString(title, x - 2, baseline + 2);
			String first = "PANDA ";
			g.setColor(NAVY);
			g.drawString(first, x, baseline);
			x += fm.stringWidth(first);
			Color[] colors = { new Color(230, 54, 65), new Color(20, 175, 91), new Color(245, 188, 22),
					new Color(31, 112, 227) };
			for (int i = 0; i < 4; i++) {
				String letter = "LUDO".substring(i, i + 1);
				g.setColor(colors[i]);
				g.drawString(letter, x, baseline);
				x += fm.stringWidth(letter);
			}
			Font label = new Font(Font.SANS_SERIF, Font.BOLD, 15);
			g.setFont(label);
			FontMetrics sfm = g.getFontMetrics();

			String modeText = "EASY MODE";

			// Format the elapsed time as MM:SS, or HH:MM:SS for longer games.
			long hours = elapsedSeconds / 3600;
			long minutes = (elapsedSeconds % 3600) / 60;
			long seconds = elapsedSeconds % 60;

			String duration = hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds)
					: String.format("%02d:%02d", minutes, seconds);

			String timerText = "TIME " + duration;

			// Center both badges as a single group.
			int modeWidth = sfm.stringWidth(modeText) + 38;
			int timerWidth = sfm.stringWidth(timerText) + 30;
			int gap = 12;
			int left = (getWidth() - modeWidth - timerWidth - gap) / 2;

			// Draw the Easy Mode badge.
			g.setColor(new Color(17, 107, 204));
			g.fillRoundRect(left, 69, modeWidth, 29, 28, 28);

			g.setColor(Color.WHITE);
			g.drawString(modeText, left + 19, 89);

			// Draw the elapsed-time badge.
			int timeX = left + modeWidth + gap;

			g.setColor(new Color(245, 250, 255));
			g.fillRoundRect(timeX, 69, timerWidth, 29, 28, 28);

			g.setColor(new Color(90, 153, 208));
			g.setStroke(new BasicStroke(1.5f));
			g.drawRoundRect(timeX, 69, timerWidth, 29, 28, 28);

			g.setColor(NAVY);
			g.drawString(timerText, timeX + 15, 89);
			g.dispose();
		}
	}

	private static final class PillButton extends JButton {
		private static final long serialVersionUID = 1L;

		PillButton(String text) {
			super(text);
			setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));
			setForeground(NAVY);
			setBorderPainted(false);
			setContentAreaFilled(false);
			setFocusPainted(false);
			setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		}

		@Override
		protected void paintComponent(Graphics graphics) {
			Graphics2D g = (Graphics2D) graphics.create();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			int w = getWidth(), h = getHeight();
			g.setColor(new Color(29, 84, 130, 42));
			g.fillRoundRect(4, 6, w - 8, h - 8, h, h);
			g.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, h,
					getModel().isRollover() ? new Color(210, 232, 250) : new Color(232, 245, 255)));
			g.fillRoundRect(2, 1, w - 6, h - 9, h, h);
			g.setColor(new Color(177, 211, 239));
			g.setStroke(new BasicStroke(2));
			g.drawRoundRect(2, 1, w - 6, h - 9, h, h);
			g.dispose();
			super.paintComponent(graphics);
		}
	}
}
