package View;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import Model.Player;

/** Rounded sidebar cards. No gameplay logic lives here. */
public class GameSidebarPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final JTextArea statusLabel;
    private final Map<Player, PlayerRow> playerRows = new LinkedHashMap<>();

    public GameSidebarPanel(List<Player> players, DicePanel dicePanel) {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(2, 1, 4, 7));

        Section diceSection = new Section("CURRENT TURN & DICE", "");
        diceSection.addContent(dicePanel);
        add(diceSection);
        add(Box.createVerticalStrut(8));
        
        Section playerSection = new Section("PLAYERS", "");
        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        for (Player player : players) {
            PlayerRow row = new PlayerRow(player);
            playerRows.put(player, row);
            list.add(row);
            list.add(Box.createVerticalStrut(3));
        }
        playerSection.addContent(list);
        add(playerSection);
        add(Box.createVerticalStrut(8));
        
        Section statusSection = new Section("GAME STATUS", "");
        JPanel statusContent = new JPanel(new BorderLayout());
        statusContent.setOpaque(false);
        statusContent.setBorder(BorderFactory.createEmptyBorder(8, 9, 8, 9));
        statusLabel = new JTextArea("Game ready. Roll the dice!");
        statusLabel.setEditable(false);
        statusLabel.setLineWrap(true);
        statusLabel.setWrapStyleWord(true);
        statusLabel.setOpaque(false);
        statusLabel.setFocusable(false);
        statusLabel.setForeground(new Color(24, 52, 95));
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        statusContent.add(statusLabel, BorderLayout.CENTER);
        statusSection.addContent(statusContent);
        add(statusSection);
    }

    public void updateStatus(String message) {
        statusLabel.setText(message);
        revalidate();
        repaint();
    }

    public void updateActivePlayer(Player activePlayer) {
        for (Map.Entry<Player, PlayerRow> entry : playerRows.entrySet()) {
            entry.getValue().setActive(entry.getKey() == activePlayer);
        }
        repaint();
    }

    private static Color playerColor(String color) {
        return switch (color) {
            case "Red" -> new Color(227, 54, 71);
            case "Green" -> new Color(19, 170, 97);
            case "Blue" -> new Color(29, 111, 230);
            case "Yellow" -> new Color(238, 175, 20);
            default -> new Color(121, 144, 168);
        };
    }

    private static final class Section extends JPanel {
        private static final long serialVersionUID = 1L;
        private final JPanel body;
        Section(String name, String symbol) {
            setOpaque(false);
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(3, 8, 5, 8));
            JLabel heading = new HeaderPill(name);
            heading.setForeground(Color.WHITE);
            heading.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));
            heading.setHorizontalAlignment(SwingConstants.CENTER);
            heading.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
            add(heading, BorderLayout.NORTH);
            body = new JPanel(new BorderLayout());
            body.setOpaque(false);
            body.setBorder(BorderFactory.createEmptyBorder(4, 3, 0, 3));
            add(body, BorderLayout.CENTER);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        }
        void addContent(JPanel panel) {
            body.add(panel, BorderLayout.CENTER);
        }
        @Override protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g.setColor(new Color(29, 81, 140, 44));
            g.fill(new RoundRectangle2D.Double(2, 5, w - 5, h - 7, 33, 33));
            g.setPaint(new GradientPaint(0, 0, Color.WHITE,
                    0, h, new Color(235, 246, 255)));
            g.fill(new RoundRectangle2D.Double(1, 1, w - 4, h - 8, 33, 33));
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(2.5f));
            g.draw(new RoundRectangle2D.Double(2, 2, w - 6, h - 10, 32, 32));
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    private static final class HeaderPill extends JLabel {
        private static final long serialVersionUID = 1L;
        HeaderPill(String text) {
            super(text);
            setOpaque(false);
        }
        @Override protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.setPaint(new GradientPaint(0, 0, new Color(43, 131, 221),
                    0, getHeight(), new Color(21, 87, 165)));
            g.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    private static final class PlayerRow extends JPanel {
        private static final long serialVersionUID = 1L;
        private final Player player;
        private boolean active;
        PlayerRow(Player player) {
            this.player = player;
            setOpaque(false);
            setLayout(new BorderLayout(11, 0));
            setPreferredSize(new Dimension(280, 38));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            setBorder(BorderFactory.createEmptyBorder(4, 18, 4, 15));
            JLabel coloredDot = new JLabel("\u25cf");
            coloredDot.setForeground(playerColor(player.getColor()));
            coloredDot.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
            add(coloredDot, BorderLayout.WEST);
            JLabel name = new JLabel(player.getName());
            name.setForeground(new Color(25, 54, 106));
            name.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));
            add(name, BorderLayout.CENTER);
        }
        void setActive(boolean current) {
            active = current;
            repaint();
        }
        @Override protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            Color tint = playerColor(player.getColor());
            g.setColor(active ? new Color(tint.getRed(), tint.getGreen(), tint.getBlue(), 40)
                    : new Color(222, 238, 255, 137));
            g.fillRoundRect(2, 1, getWidth() - 4, getHeight() - 3, 23, 23);
            g.setColor(new Color(tint.getRed(), tint.getGreen(), tint.getBlue(), active ? 150 : 55));
            g.setStroke(new BasicStroke(active ? 2.2f : 1.4f));
            g.drawRoundRect(2, 1, getWidth() - 4, getHeight() - 3, 23, 23);
            g.dispose();
            super.paintComponent(graphics);
        }
    }
}
