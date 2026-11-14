
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
import java.awt.event.WindowEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class StartScreenFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private Runnable onStartNewGame;

    public StartScreenFrame() {
        setTitle("Panda Ludo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Load the application icon from the Images folder
        URL iconLocation = getClass().getResource(
                "/View/Images/panda_app_icon.png");

        if (iconLocation != null) {
            setIconImage(
                    new javax.swing.ImageIcon(iconLocation).getImage());
        }

        StartMenuPanel menu = new StartMenuPanel();
        setContentPane(menu);

        pack();
        setMinimumSize(new Dimension(800, 520));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    // Called by Main.java when the game starts
    public void setOnStartNewGame(Runnable onStartNewGame) {
        this.onStartNewGame = onStartNewGame;
    }

    // Main menu panel
    private final class StartMenuPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        private static final int ART_WIDTH = 1536;
        private static final int ART_HEIGHT = 1001;

        private final Image artwork;

        private final MenuButton startButton =
                new MenuButton("START NEW GAME", ButtonType.START);

        private final MenuButton historyButton =
                new MenuButton("GAME HISTORY", ButtonType.HISTORY);

        private final MenuButton questionsButton =
                new MenuButton("QUESTION MANAGER", ButtonType.QUESTIONS);

        private final MenuButton exitButton =
                new MenuButton("EXIT", ButtonType.EXIT);

        // Button positions on the background illustration
        private final int[][] buttonBounds = {
                {423, 502, 684, 100},
                {423, 613, 684, 96},
                {423, 718, 684, 96},
                {433, 831, 666, 96}
        };

        StartMenuPanel() {
            setLayout(null);
            setBackground(new Color(173, 219, 246));
            setPreferredSize(new Dimension(1075, 700));

            artwork = loadArtwork();

            // Features that are not yet implemented
            historyButton.setEnabled(false);
            questionsButton.setEnabled(false);

            historyButton.setToolTipText(
                    "Available in a future iteration");

            questionsButton.setToolTipText(
                    "Available in a future iteration");

            // Start the game using the original controller callback
            startButton.addActionListener(e -> {
                if (onStartNewGame != null) {
                    onStartNewGame.run();
                }
            });

            // Close the application
            exitButton.addActionListener(e ->
                    dispatchEvent(new WindowEvent(
                            StartScreenFrame.this,
                            WindowEvent.WINDOW_CLOSING)));

            add(startButton);
            add(historyButton);
            add(questionsButton);
            add(exitButton);
        }

        // Load background image from the Images folder
        private Image loadArtwork() {
            URL imageUrl = getClass().getResource(
                    "/View/Images/panda_menu_background.png");

            if (imageUrl == null) {
                throw new IllegalStateException(
                        "Missing image: "
                        + "/View/Images/panda_menu_background.png");
            }

            try {
                return ImageIO.read(imageUrl);
            } catch (IOException error) {
                throw new IllegalStateException(
                        "Could not load the background image", error);
            }
        }

        // Keep the illustration's aspect ratio
        private double getScale() {
            return Math.min(
                    getWidth() / (double) ART_WIDTH,
                    getHeight() / (double) ART_HEIGHT);
        }

        @Override
        public void doLayout() {
            double scale = getScale();

            int xOffset = (int) Math.round(
                    (getWidth() - ART_WIDTH * scale) / 2.0);

            int yOffset = (int) Math.round(
                    (getHeight() - ART_HEIGHT * scale) / 2.0);

            JButton[] controls = {
                    startButton,
                    historyButton,
                    questionsButton,
                    exitButton
            };

            for (int i = 0; i < controls.length; i++) {
                int[] r = buttonBounds[i];

                controls[i].setBounds(
                        xOffset + (int) Math.round(r[0] * scale),
                        yOffset + (int) Math.round(r[1] * scale),
                        Math.max(1, (int) Math.round(r[2] * scale)),
                        Math.max(1, (int) Math.round(r[3] * scale)));
            }
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            Graphics2D g2 = (Graphics2D) graphics.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            g2.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);

            double scale = getScale();

            int w = (int) Math.round(ART_WIDTH * scale);
            int h = (int) Math.round(ART_HEIGHT * scale);

            g2.drawImage(
                    artwork,
                    (getWidth() - w) / 2,
                    (getHeight() - h) / 2,
                    w,
                    h,
                    this);

            g2.dispose();
        }
    }

    private enum ButtonType {
        START, HISTORY, QUESTIONS, EXIT
    }

    // Custom rounded Swing button
    private static final class MenuButton extends JButton {

        private static final long serialVersionUID = 1L;

        private final ButtonType type;

        private final Color ink = new Color(31, 53, 85);
        private final Color red = new Color(207, 64, 82);

        MenuButton(String label, ButtonType type) {
            super(label);
            this.type = type;

            setOpaque(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setRolloverEnabled(true);

            setCursor(
                    type == ButtonType.START || type == ButtonType.EXIT
                    ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                    : Cursor.getDefaultCursor());

            getAccessibleContext().setAccessibleName(label);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();

            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);

            g.scale(
                    getWidth() / 684.0,
                    getHeight() / 100.0);

            boolean over =
                    isEnabled() && getModel().isRollover();

            boolean pressed =
                    isEnabled() && getModel().isPressed();

            boolean exit = type == ButtonType.EXIT;
            boolean start = type == ButtonType.START;

            Shape base = new RoundRectangle2D.Double(
                    4, 4, 676, 87, 62, 62);

            Shape shadow = new RoundRectangle2D.Double(
                    6, 10, 672, 86, 62, 62);

            if (!exit) {
                g.setColor(new Color(38, 75, 98, 45));
                g.fill(shadow);
            }

            Color top;
            Color bottom;
            Color outline;

            if (start) {
                top = over
                        ? new Color(89, 228, 103)
                        : new Color(60, 205, 82);

                bottom = pressed
                        ? new Color(3, 111, 56)
                        : over
                        ? new Color(4, 159, 72)
                        : new Color(3, 126, 64);

                outline = new Color(42, 145, 75);

            } else if (exit) {
                top = over
                        ? new Color(255, 241, 244)
                        : new Color(248, 251, 253);

                bottom = pressed
                        ? new Color(253, 214, 219)
                        : new Color(226, 235, 241);

                outline = red;

            } else {
                top = new Color(248, 251, 254);
                bottom = new Color(210, 224, 236);
                outline = new Color(204, 219, 232);
            }

            g.setPaint(new GradientPaint(
                    0, 5, top, 0, 91, bottom));

            g.fill(base);
            g.setColor(outline);

            g.setStroke(new BasicStroke(
                    exit ? 2.6f : 2.4f));

            g.draw(base);

            if (!exit) {
                g.setColor(new Color(
                        255, 255, 255,
                        start ? 105 : 170));

                g.setStroke(new BasicStroke(2));

                g.draw(new Arc2D.Double(
                        11, 8, 663, 75,
                        2, 176, Arc2D.OPEN));
            }

            if (start
                    || type == ButtonType.HISTORY
                    || type == ButtonType.QUESTIONS) {

                g.setColor(start
                        ? new Color(255, 255, 255, 210)
                        : new Color(139, 158, 179, 190));

                g.setStroke(new BasicStroke(
                        3.0f,
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND));

                g.drawLine(185, 24, 185, 72);
            }

            Color textColor = start
                    ? Color.WHITE
                    : exit ? red : ink;

            paintIcon(g, textColor);

            g.setFont(new Font(
                    Font.SANS_SERIF,
                    Font.BOLD,
                    start ? 34 : exit ? 34 : 30));

            FontMetrics fm = g.getFontMetrics();
            String caption = getText();

            int textCenter = exit ? 390 : 431;
            int textX = textCenter
                    - fm.stringWidth(caption) / 2;

            int baseline = (100 - fm.getHeight()) / 2
                    + fm.getAscent() + 1;

            if (start) {
                g.setColor(new Color(7, 91, 49, 90));
                g.drawString(caption, textX, baseline + 2);
            }

            g.setColor(textColor);
            g.drawString(caption, textX, baseline);

            if (hasFocus() && isEnabled()) {
                g.setColor(new Color(255, 255, 255, 165));
                g.setStroke(new BasicStroke(3));

                g.draw(new RoundRectangle2D.Double(
                        8, 7, 668, 81, 59, 59));
            }

            g.dispose();
        }

        private void paintIcon(Graphics2D g, Color color) {
            g.setColor(color);

            g.setStroke(new BasicStroke(
                    5.3f,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));

            if (type == ButtonType.START) {

                Path2D triangle = new Path2D.Double();

                triangle.moveTo(91, 25);
                triangle.quadTo(87, 22, 87, 31);
                triangle.lineTo(87, 72);
                triangle.quadTo(88, 79, 96, 75);
                triangle.lineTo(137, 52);
                triangle.quadTo(142, 49, 137, 45);
                triangle.closePath();

                g.fill(triangle);

            } else if (type == ButtonType.HISTORY) {

                g.draw(new Arc2D.Double(
                        79, 26, 47, 47,
                        -72, 308, Arc2D.OPEN));

                g.drawLine(102, 36, 102, 50);
                g.drawLine(102, 50, 116, 57);

                Path2D arrow = new Path2D.Double();
                arrow.moveTo(75, 33);
                arrow.lineTo(82, 29);
                arrow.lineTo(82, 39);
                arrow.closePath();

                g.fill(arrow);

            } else if (type == ButtonType.QUESTIONS) {

                Path2D gear = new Path2D.Double();

                for (int i = 0; i < 32; i++) {
                    double angle =
                            (i * Math.PI / 16.0) - Math.PI / 2;

                    double radius =
                            (i % 4 == 0 || i % 4 == 3)
                            ? 30 : 23;

                    double x = 105
                            + radius * Math.cos(angle);

                    double y = 50
                            + radius * Math.sin(angle);

                    if (i == 0) {
                        gear.moveTo(x, y);
                    } else {
                        gear.lineTo(x, y);
                    }
                }

                gear.closePath();
                g.fill(gear);

                g.setColor(new Color(231, 241, 249));
                g.fillOval(96, 41, 18, 18);

            } else {
                g.setColor(red);

                g.setStroke(new BasicStroke(
                        5,
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND));

                g.drawRoundRect(82, 28, 36, 48, 3, 3);

                g.drawLine(102, 52, 142, 52);
                g.drawLine(130, 41, 142, 52);
                g.drawLine(130, 63, 142, 52);
            }
        }
    }
}
