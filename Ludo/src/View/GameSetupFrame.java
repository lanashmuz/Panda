
package View;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;

import Model.GameConfiguration;
import Model.Player;

/**
 * Game setup screen – Panda Ludo theme.
 *
 * All inputs are real Swing components. This class changes only the View and
 * preserves setOnStartGame(...) / setOnBackToStart(...) used by Main and
 * GameController. Medium and Hard remain unavailable in iteration 1.
 */
public class GameSetupFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final Color NAVY = new Color(26, 51, 93);
    private static final Color BLUE = new Color(22, 115, 219);
    private static final String[] COLORS = {"Red", "Blue", "Green", "Yellow"};

    private Consumer<GameConfiguration> onStartGame;
    private Runnable onBackToStart;
    private int numberOfPlayers = 2;

    private final PlayerCard[] players = new PlayerCard[4];
    private final CountButton[] countButtons = new CountButton[3];
    private final JLabel warningLabel = new JLabel("", SwingConstants.CENTER);

    public GameSetupFrame() {
        setTitle("Panda Ludo - Game Setup");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        URL icon = getClass().getResource("/View/Images/panda_app_icon.png");
        if (icon != null) {
            setIconImage(new ImageIcon(icon).getImage());
        }

        setContentPane(new SetupPanel());
        pack();
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    /** Called by GameController; do not change its signature. */
    public void setOnStartGame(Consumer<GameConfiguration> action) {
        onStartGame = action;
    }

    /** Called by Main; do not change its signature. */
    public void setOnBackToStart(Runnable action) {
        onBackToStart = action;
    }

    private void updatePlayerCount(int count) {
        numberOfPlayers = count;
        for (int i = 0; i < players.length; i++) {
            players[i].setActive(i < count);
        }
        for (int i = 0; i < countButtons.length; i++) {
            countButtons[i].setSelected(i + 2 == count);
        }
        validateUniqueColors();
    }

    private boolean validateUniqueColors() {
        Set<String> used = new HashSet<>();
        for (int i = 0; i < numberOfPlayers; i++) {
            String color = (String) players[i].colorBox.getSelectedItem();
            if (!used.add(color)) {
                warningLabel.setText("Each player must have a unique color.");
                return false;
            }
        }
        warningLabel.setText("");
        return true;
    }

    private void handleStartGame() {
        for (int i = 0; i < numberOfPlayers; i++) {
            if (players[i].nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a name for every player.",
                        "Missing player name", JOptionPane.WARNING_MESSAGE);
                players[i].nameField.requestFocusInWindow();
                return;
            }
        }
        if (!validateUniqueColors()) {
            JOptionPane.showMessageDialog(this,
                    "Each player must have a unique color.",
                    "Duplicate player colors", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (onStartGame == null) {
            throw new IllegalStateException("Start Game handler is missing.");
        }

        List<Player> selected = new ArrayList<>();
        for (int i = 0; i < numberOfPlayers; i++) {
            String name = players[i].nameField.getText().trim();
            String color = (String) players[i].colorBox.getSelectedItem();
            selected.add(new Player(name, color));
        }
        onStartGame.accept(new GameConfiguration(selected, "Easy"));
    }

    /** Image-based layout with real, independently interactive controls. */
    private final class SetupPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private static final int ART_W = 1672;
        private static final int ART_H = 941;

        private final Image background;
        private final MenuButton backButton;
        private final MenuButton startButton;
        private final JLabel playerCountLabel;
        private final JLabel difficultyLabel;
        private final DifficultyButton[] difficultyButtons = new DifficultyButton[3];

        SetupPanel() {
            setLayout(null);
            setOpaque(true);
            setBackground(new Color(161, 215, 245));
            setPreferredSize(new Dimension(1210, 680));

            URL image = getClass().getResource("/View/Images/panda_setup_background.png");
            background = image != null ? new ImageIcon(image).getImage() : null;

            backButton = new MenuButton("Back to Main Menu", MenuButton.Kind.BACK);
            backButton.addActionListener(e -> {
                if (onBackToStart != null) {
                    onBackToStart.run();
                }
            });
            add(backButton);

            playerCountLabel = new JLabel("\u265f   Number of Players:", SwingConstants.RIGHT);
            playerCountLabel.setForeground(NAVY);
            add(playerCountLabel);

            ButtonGroup countGroup = new ButtonGroup();
            for (int i = 0; i < 3; i++) {
                final int count = i + 2;
                CountButton button = new CountButton(String.valueOf(count));
                button.addActionListener(e -> updatePlayerCount(count));
                countGroup.add(button);
                countButtons[i] = button;
                add(button);
            }

            for (int i = 0; i < players.length; i++) {
                players[i] = new PlayerCard(i);
                add(players[i]);
            }

            difficultyLabel = new JLabel("\u25cf  Difficulty:", SwingConstants.RIGHT);
            difficultyLabel.setForeground(NAVY);
            add(difficultyLabel);

            difficultyButtons[0] = new DifficultyButton("Easy", true);
            difficultyButtons[1] = new DifficultyButton("Medium", false);
            difficultyButtons[2] = new DifficultyButton("Hard", false);
            for (DifficultyButton b : difficultyButtons) {
                add(b);
            }

            warningLabel.setForeground(new Color(190, 49, 65));
            add(warningLabel);

            startButton = new MenuButton("START GAME", MenuButton.Kind.START);
            startButton.addActionListener(e -> handleStartGame());
            add(startButton);

            updatePlayerCount(2);
        }

        private double scale() {
            return Math.min(getWidth() / (double) ART_W,
                    getHeight() / (double) ART_H);
        }

        private void place(JComponent component, int x, int y, int w, int h,
                           int fontSize, boolean bold) {
            double s = scale();
            int offsetX = (int) Math.round((getWidth() - ART_W * s) / 2.0);
            int offsetY = (int) Math.round((getHeight() - ART_H * s) / 2.0);
            component.setBounds(offsetX + (int) Math.round(x * s),
                    offsetY + (int) Math.round(y * s),
                    Math.max(1, (int) Math.round(w * s)),
                    Math.max(1, (int) Math.round(h * s)));
            if (fontSize > 0) {
                component.setFont(new Font(Font.SANS_SERIF,
                        bold ? Font.BOLD : Font.PLAIN,
                        Math.max(12, (int) Math.round(fontSize * s))));
            }
        }

        @Override
        public void doLayout() {
            place(backButton, 40, 40, 300, 66, 23, true);
            place(playerCountLabel, 300, 300, 475, 62, 33, true);
            for (int i = 0; i < 3; i++) {
                place(countButtons[i], 840 + i * 128, 300, 112, 65, 32, true);
            }
            place(players[0], 120, 398, 703, 137, 0, false);
            place(players[1], 848, 398, 703, 137, 0, false);
            place(players[2], 120, 558, 703, 137, 0, false);
            place(players[3], 848, 558, 703, 137, 0, false);
            place(difficultyLabel, 345, 724, 300, 61, 30, true);
            place(difficultyButtons[0], 670, 723, 211, 69, 26, true);
            place(difficultyButtons[1], 904, 723, 235, 69, 25, true);
            place(difficultyButtons[2], 1164, 723, 215, 69, 25, true);
            place(warningLabel, 480, 795, 710, 26, 23, true);
            place(startButton, 620, 825, 435, 81, 37, true);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            double s = scale();
            int w = (int) Math.round(ART_W * s);
            int h = (int) Math.round(ART_H * s);
            int x = (getWidth() - w) / 2;
            int y = (getHeight() - h) / 2;
            if (background != null) {
                g.drawImage(background, x, y, w, h, this);
            } else {
                g.setColor(new Color(244, 251, 255));
                g.fillRoundRect(x + 35, y + 105, w - 70, h - 135, 45, 45);
                g.setColor(NAVY);
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 36));
                g.drawString("PANDA LUDO - GAME SETUP", x + 90, y + 90);
            }
            g.dispose();
        }
    }

    private static Color playerColor(String name) {
        if ("Red".equals(name)) return new Color(230, 55, 65);
        if ("Blue".equals(name)) return new Color(26, 113, 234);
        if ("Green".equals(name)) return new Color(31, 177, 95);
        return new Color(237, 179, 22);
    }

    /** Per-player colored card, with editable name and real JComboBox. */
    private final class PlayerCard extends JPanel {
        private static final long serialVersionUID = 1L;
        private final int index;
        private final JLabel playerIcon;
        private final JLabel playerLabel;
        private final PromptField nameField;
        private final JComboBox<String> colorBox;
        private boolean active;

        PlayerCard(int index) {
            this.index = index;
            setLayout(null);
            setOpaque(false);

            playerIcon = new JLabel();
            playerIcon.setHorizontalAlignment(SwingConstants.CENTER);
            add(playerIcon);

            playerLabel = new JLabel("Player " + (index + 1) + ":");
            playerLabel.setForeground(NAVY);
            add(playerLabel);

            nameField = new PromptField("Player " + (index + 1));
            add(nameField);

            colorBox = new JComboBox<>(COLORS);
            colorBox.setSelectedIndex(index);
            colorBox.setFocusable(true);
            colorBox.setBackground(Color.WHITE);
            colorBox.setForeground(NAVY);
            colorBox.setRenderer(new DefaultListCellRenderer() {
                private static final long serialVersionUID = 1L;
                @Override
                public java.awt.Component getListCellRendererComponent(
                        JList<?> list, Object value, int itemIndex,
                        boolean isSelected, boolean cellHasFocus) {
                    JLabel label = (JLabel) super.getListCellRendererComponent(
                            list, value, itemIndex, isSelected, cellHasFocus);
                    String selected = value == null ? "Red" : value.toString();
                    label.setIcon(new ColorDotIcon(playerColor(selected)));
                    label.setIconTextGap(12);
                    label.setBorder(BorderFactory.createEmptyBorder(0, 11, 0, 4));
                    return label;
                }
            });
            colorBox.addActionListener(e -> {
                repaint();
                validateUniqueColors();
            });
            colorBox.setBorder(new RoundedOutlineBorder(new Color(177, 202, 228), 2, 18));
            add(colorBox);
            setActive(index < 2);
        }

        void setActive(boolean enabled) {
            active = enabled;
            playerIcon.setVisible(enabled);
            playerLabel.setVisible(enabled);
            nameField.setVisible(enabled);
            colorBox.setVisible(enabled);
            repaint();
        }

        @Override
        public void doLayout() {
            double s = getWidth() / 703.0;
            int labelSize = Math.max(12, (int) Math.round(27 * s));
            int textSize = Math.max(12, (int) Math.round(25 * s));
            playerIcon.setBounds((int) (20 * s), (int) (29 * s),
                    (int) (68 * s), (int) (80 * s));
            playerLabel.setBounds((int) (100 * s), (int) (25 * s),
                    (int) (135 * s), (int) (86 * s));
            playerLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, labelSize));
            nameField.setBounds((int) (239 * s), (int) (36 * s),
                    (int) (222 * s), (int) (63 * s));
            nameField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, textSize));
            colorBox.setBounds((int) (476 * s), (int) (36 * s),
                    (int) (203 * s), (int) (63 * s));
            colorBox.setFont(new Font(Font.SANS_SERIF, Font.BOLD, textSize));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            double sx = getWidth() / 703.0;
            double sy = getHeight() / 137.0;
            g.scale(sx, sy);
            Shape r = new RoundRectangle2D.Double(5, 5, 692, 124, 38, 38);
            if (active) {
                Color color = playerColor((String) colorBox.getSelectedItem());
                g.setColor(new Color(47, 90, 130, 30));
                g.fill(new RoundRectangle2D.Double(7, 11, 692, 124, 40, 40));
                g.setPaint(new GradientPaint(0, 8, new Color(255, 255, 255, 250),
                        0, 129, new Color(color.getRed(), color.getGreen(), color.getBlue(), 28)));
                g.fill(r);
                g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 145));
                g.setStroke(new BasicStroke(2.5f));
                g.draw(r);
                g.setColor(color);
                paintPawn(g, 60, 52);
            } else {
                g.setColor(new Color(255, 255, 255, 110));
                g.fill(r);
                g.setColor(new Color(152, 177, 204, 130));
                g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND, 1f, new float[]{11f, 8f}, 0f));
                g.draw(r);
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
                g.setColor(new Color(114, 144, 172, 165));
                String text = "+  Player " + (index + 1);
                int tw = g.getFontMetrics().stringWidth(text);
                g.drawString(text, (703 - tw) / 2, 78);
            }
            g.dispose();
        }
    }

    /** Real text field; placeholder is visual only, never submitted as a name. */
    private static final class PromptField extends JTextField {
        private static final long serialVersionUID = 1L;
        private final String prompt;

        PromptField(String prompt) {
            this.prompt = prompt;
            setOpaque(false);
            setForeground(NAVY);
            setCaretColor(BLUE);
            setBorder(BorderFactory.createEmptyBorder(3, 14, 3, 12));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Color.WHITE);
            g.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 17, 17);
            g.setColor(hasFocus() ? BLUE : new Color(176, 199, 223));
            g.setStroke(new BasicStroke(hasFocus() ? 2.5f : 1.7f));
            g.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 17, 17);
            g.dispose();
            super.paintComponent(graphics);
            if (getText().isEmpty() && !hasFocus()) {
                Graphics2D h = (Graphics2D) graphics.create();
                h.setFont(getFont());
                h.setColor(new Color(127, 147, 167));
                FontMetrics fm = h.getFontMetrics();
                h.drawString(prompt, 14, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                h.dispose();
            }
        }
    }

    private static final class RoundedOutlineBorder extends AbstractBorder {
        private static final long serialVersionUID = 1L;
        private final Color color;
        private final int thickness;
        private final int radius;
        RoundedOutlineBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
        }
        @Override
        public java.awt.Insets getBorderInsets(java.awt.Component c) {
            return new java.awt.Insets(3, 5, 3, 5);
        }
        @Override
        public void paintBorder(java.awt.Component c, Graphics graphics,
                                int x, int y, int width, int height) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(color);
            g.setStroke(new BasicStroke(thickness));
            g.drawRoundRect(x + 1, y + 1, width - 3, height - 3, radius, radius);
            g.dispose();
        }
    }

    private static final class ColorDotIcon implements javax.swing.Icon {
        private final Color color;
        ColorDotIcon(Color color) { this.color = color; }
        @Override public int getIconWidth() { return 20; }
        @Override public int getIconHeight() { return 20; }
        @Override public void paintIcon(java.awt.Component c, Graphics graphics, int x, int y) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(color);
            g.fillOval(x + 1, y + 1, 18, 18);
            g.setColor(new Color(255, 255, 255, 130));
            g.fillOval(x + 5, y + 4, 6, 6);
            g.dispose();
        }
    }

    /** Player counter segment with selected blue pill. */
    private static final class CountButton extends JToggleButton {
        private static final long serialVersionUID = 1L;
        CountButton(String label) {
            super(label);
            setFocusPainted(false);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g.setColor(new Color(51, 92, 135, 32));
            g.fillRoundRect(2, 5, w - 4, h - 6, h, h);
            g.setPaint(isSelected()
                    ? new GradientPaint(0, 0, new Color(65, 159, 255),
                            0, h, new Color(12, 89, 190))
                    : new GradientPaint(0, 0, Color.WHITE,
                            0, h, new Color(220, 234, 247)));
            g.fillRoundRect(2, 1, w - 4, h - 7, h, h);
            g.setColor(isSelected() ? new Color(13, 98, 191) : new Color(182, 203, 228));
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(2, 1, w - 4, h - 7, h, h);
            g.setFont(getFont());
            g.setColor(isSelected() ? Color.WHITE : NAVY);
            FontMetrics fm = g.getFontMetrics();
            g.drawString(getText(), (w - fm.stringWidth(getText())) / 2,
                    (h + fm.getAscent() - fm.getDescent()) / 2 - 2);
            g.dispose();
        }
    }

    /** The Easy button is enabled; unavailable modes remain visibly locked. */
    private static final class DifficultyButton extends JButton {
        private static final long serialVersionUID = 1L;
        private final boolean available;
        DifficultyButton(String title, boolean available) {
            super(title);
            this.available = available;
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setEnabled(available);
            if (!available) setToolTipText("Coming Soon");
        }
        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g.setPaint(available
                    ? new GradientPaint(0, 0, new Color(177, 242, 182),
                            0, h, new Color(70, 189, 93))
                    : new GradientPaint(0, 0, new Color(247, 250, 254),
                            0, h, new Color(216, 228, 239)));
            g.fillRoundRect(2, 2, w - 5, h - 8, h, h);
            g.setColor(available ? new Color(73, 178, 92) : new Color(198, 214, 232));
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(2, 2, w - 5, h - 8, h, h);
            g.setColor(available ? new Color(16, 103, 46) : new Color(119, 141, 163));
            g.setFont(getFont());
            FontMetrics fm = g.getFontMetrics();
            String text = available ? "\u2713  Easy" : getText();
            int baseline = available
                    ? (h + fm.getAscent() - fm.getDescent()) / 2 - 3
                    : (h + fm.getAscent() - fm.getDescent()) / 2 - 9;
            g.drawString(text, (w - fm.stringWidth(text)) / 2, baseline);
            if (!available) {
                g.setFont(getFont().deriveFont(Font.PLAIN, Math.max(10f, getFont().getSize2D() * .60f)));
                FontMetrics sm = g.getFontMetrics();
                String caption = "Coming Soon";
                g.drawString(caption, (w - sm.stringWidth(caption)) / 2,
                        baseline + sm.getHeight() - 1);
            }
            g.dispose();
        }
    }

    /** Rounded green call-to-action and rounded white back button. */
    private static final class MenuButton extends JButton {
        private static final long serialVersionUID = 1L;
        private enum Kind { BACK, START }
        private final Kind kind;
        MenuButton(String text, Kind kind) {
            super(text);
            this.kind = kind;
            setOpaque(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            boolean start = kind == Kind.START;
            g.setColor(new Color(22, 71, 90, 48));
            g.fillRoundRect(3, 7, w - 6, h - 8, h, h);
            if (start) {
                g.setPaint(new GradientPaint(0, 3,
                        getModel().isRollover() ? new Color(100, 223, 92) : new Color(76, 208, 70),
                        0, h, new Color(5, 133, 58)));
            } else {
                g.setPaint(new GradientPaint(0, 0, Color.WHITE,
                        0, h, new Color(224, 238, 249)));
            }
            g.fillRoundRect(2, 1, w - 4, h - 9, h, h);
            g.setColor(start ? new Color(36, 156, 70) : new Color(194, 216, 235));
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(2, 1, w - 4, h - 9, h, h);
            g.setFont(getFont());
            g.setColor(start ? Color.WHITE : NAVY);
            FontMetrics fm = g.getFontMetrics();
            String label = getText();
            int labelWidth = fm.stringWidth(label);
            int baseline = (h + fm.getAscent() - fm.getDescent()) / 2 - 4;
            int x = (w - labelWidth) / 2 + (start ? 24 : 10);
            g.drawString(label, x, baseline);
            if (start) {
                Path2D triangle = new Path2D.Double();
                int size = Math.max(16, h / 3);
                int tx = x - 44, ty = h / 2 - 4;
                triangle.moveTo(tx - 7, ty - size / 2.0);
                triangle.lineTo(tx + size, ty);
                triangle.lineTo(tx - 7, ty + size / 2.0);
                triangle.closePath();
                g.fill(triangle);
            } else {
                int cx = Math.max(23, (w - labelWidth) / 2 - 22);
                int cy = h / 2 - 3;
                g.setStroke(new BasicStroke(Math.max(2f, h / 20f),
                        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawLine(cx + 12, cy, cx - 10, cy);
                g.drawLine(cx - 10, cy, cx, cy - 9);
                g.drawLine(cx - 10, cy, cx, cy + 9);
            }
            g.dispose();
        }
    }

    private static void paintPawn(Graphics2D g, int x, int y) {
        Color pawnColor = g.getColor();
        Color highlight = new Color(
                Math.min(255, pawnColor.getRed() + 55),
                Math.min(255, pawnColor.getGreen() + 55),
                Math.min(255, pawnColor.getBlue() + 55));
        g.setPaint(new GradientPaint(x - 28, y - 25, highlight,
                x + 24, y + 39, pawnColor.darker()));
        g.fillOval(x - 17, y - 38, 34, 34);
        g.fillRoundRect(x - 19, y - 5, 38, 31, 13, 13);
        Path2D base = new Path2D.Double();
        base.moveTo(x - 23, y + 22);
        base.lineTo(x + 23, y + 22);
        base.lineTo(x + 29, y + 38);
        base.lineTo(x - 29, y + 38);
        base.closePath();
        g.fill(base);
        g.setColor(new Color(255, 255, 255, 95));
        g.fillOval(x - 8, y - 32, 11, 11);
    }
}
