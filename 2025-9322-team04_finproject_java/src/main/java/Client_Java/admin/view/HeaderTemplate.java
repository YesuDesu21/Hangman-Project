package Client_Java.admin.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HeaderTemplate extends JPanel {
    private final JButton minimizeBtn;
    private final JButton maximizeBtn;
    private final JButton closeBtn;
    private final JPanel buttonPanel;
    private final JLabel logoLabel;

    private final int HEADER_HEIGHT = 150;
    private final int IMAGE_WIDTH = 500;
    private final int IMAGE_HEIGHT = 120;

    private Point initialClick;
    private final JFrame parent;

    public HeaderTemplate(JFrame frame) {
        this.parent = frame;

        setLayout(null);
        setBackground(new Color(0xA08F73));
        setPreferredSize(new Dimension(frame.getWidth(), HEADER_HEIGHT));

        // Logo image center
        logoLabel = createLogoLabel();
        add(logoLabel);

        // Top-right buttons
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBounds(frame.getWidth() - 180, 0, 180, HEADER_HEIGHT - 20);

        minimizeBtn = createButton("—");
        maximizeBtn = createButton("□");
        closeBtn = createButton("X");

        addHoverEffect(minimizeBtn, new Color(0x8A7B63));
        addHoverEffect(maximizeBtn, new Color(0x8A7B63));
        addHoverEffect(closeBtn, Color.RED, new Color(0xCC0000));

        buttonPanel.add(minimizeBtn);
        buttonPanel.add(maximizeBtn);
        buttonPanel.add(closeBtn);
        add(buttonPanel);

        // Dragging functionality
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                int thisX = parent.getLocation().x;
                int thisY = parent.getLocation().y;

                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;

                parent.setLocation(thisX + xMoved, thisY + yMoved);
            }
        });

        // Re-center logo and adjust buttons when resized
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                centerLogo();
                buttonPanel.setBounds(getWidth() - 180, 0, 180, HEADER_HEIGHT - 20);
            }
        });
    }
    public void updateHeaderSize(int windowWidth) {
        setPreferredSize(new Dimension(windowWidth, HEADER_HEIGHT));
        buttonPanel.setBounds(windowWidth - 180, 0, 180, HEADER_HEIGHT - 20);
        centerLogo();
    }
    //for committing
    private JLabel createLogoLabel() {
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/Screenshot 2025-04-30 213212.png"));
            if (originalIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                Image scaledImage = originalIcon.getImage().getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH);
                JLabel label = new JLabel(new ImageIcon(scaledImage));
                label.setSize(IMAGE_WIDTH, IMAGE_HEIGHT);
                return label;
            }
        } catch (Exception e) {
            System.err.println("Image load error: " + e.getMessage());
        }

        // Fallback
        JLabel fallback = new JLabel("LettRBox", SwingConstants.CENTER);
        fallback.setFont(new Font("inter", Font.BOLD, 48));
        fallback.setForeground(Color.WHITE);
        fallback.setSize(300, 60);
        return fallback;
    }

    private void centerLogo() {
        int centerX = (getWidth() - logoLabel.getWidth()) / 2;
        int centerY = (HEADER_HEIGHT - logoLabel.getHeight()) / 2 + 10;
        logoLabel.setLocation(centerX, centerY);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBackground(new Color(0xA08F73));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("inter", Font.BOLD, 16));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setFocusPainted(false);
        return button;
    }

    private void addHoverEffect(JButton button, Color hoverColor) {
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(0xA08F73));
            }
        });
    }

    private void addHoverEffect(JButton button, Color hoverColor, Color pressedColor) {
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(0xA08F73));
            }

            public void mousePressed(MouseEvent e) {
                button.setBackground(pressedColor);
            }

            public void mouseReleased(MouseEvent e) {
                button.setBackground(hoverColor);
            }
        });
    }

    public JButton getMinimizeBtn() {
        return minimizeBtn;
    }

    public JButton getMaximizeBtn() {
        return maximizeBtn;
    }

    public JButton getCloseBtn() {
        return closeBtn;
    }
}
