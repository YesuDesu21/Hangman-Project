package Client_Java.player.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HomepageView {
    private JFrame frame;
    private HeaderTemplate headerTemplate;


    private JPanel contentPanel;

    private JButton startGameBtn;
    private JButton viewLeaderboardBtn;
    private JButton logoutBtn;

    public static final int HEADER_HEIGHT = 150;

    public HomepageView() {
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame();
        headerTemplate = new HeaderTemplate(frame);
        frame.setUndecorated(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(headerTemplate, BorderLayout.NORTH);

        createContentPanel();

      //  updateMaximizeButton(false); might caused something
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        frame.add(mainPanel);
    }

    private void createContentPanel() {
        contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));

        startGameBtn = createMenuButton("Start Game");
        viewLeaderboardBtn = createMenuButton("View Leaderboard");
        logoutBtn = createMenuButton("Log Out");

        buttonsPanel.add(startGameBtn);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        buttonsPanel.add(viewLeaderboardBtn);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        buttonsPanel.add(logoutBtn);

        contentPanel.add(buttonsPanel);
    }

    private JButton createHeaderButton(String text) {
        JButton button = new JButton(text);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBackground(new Color(0xA08F73));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Inter", Font.BOLD, 16));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setFocusPainted(false);
        return button;
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(450, 60));
        button.setMaximumSize(new Dimension(450, 60));
        button.setFont(new Font("Inter", Font.BOLD, 18));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xA08F73), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0xA08F73));
                button.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
                button.setForeground(Color.BLACK);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(new Color(0x8A7B63));
                button.setForeground(Color.WHITE);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(new Color(0xA08F73));
                button.setForeground(Color.WHITE);
            }
        });

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
    public void updateMaximizeButton(boolean isMaximized) {
        headerTemplate.getMaximizeBtn().setText(isMaximized ? "❐" : "□");
    }

    // Getters for Controller
    public JButton getMinimizeBtn() {
        return headerTemplate.getMinimizeBtn();
    }

    public JButton getMaximizeBtn() {
        return headerTemplate.getMaximizeBtn();
    }

    public JButton getCloseBtn() {
        return headerTemplate.getCloseBtn();
    }
    public JFrame getFrame() {
        return frame;
    }

    public JButton getStartGameBtn() {
        return startGameBtn;
    }

    public JButton getViewLeaderboardBtn() {
        return viewLeaderboardBtn;
    }

    public JButton getLogoutBtn() {
        return logoutBtn;
    }

    public void show() {
        headerTemplate.updateHeaderSize(frame.getWidth());
        frame.setVisible(true);
        frame.toFront();
        frame.requestFocus();
    }
}