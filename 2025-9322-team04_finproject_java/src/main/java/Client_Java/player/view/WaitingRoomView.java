package Client_Java.player.view;

import javax.swing.*;
import java.awt.*;

public class WaitingRoomView extends JFrame {
    private HeaderTemplate headerTemplate;
    private JLabel statusLabel;
    private JLabel countdownLabel;
    private JLabel playerCountLabel;
    private JButton backButton;

    public WaitingRoomView() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("LETTRBOX - Waiting Room");
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Initialize header with control buttons
        headerTemplate = new HeaderTemplate(this);
        mainPanel.add(headerTemplate, BorderLayout.NORTH);

        // Create navigation panel with back button
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBackground(Color.WHITE);
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Create and style the back button (same as ManagePlayersView)
        backButton = new CustomComponentView.RoundedJButton("← Back", 15);
        styleBackButton(backButton);
        navPanel.add(backButton, BorderLayout.WEST);

        // Title label (centered)
        JLabel titleLabel = new JLabel("Waiting Room", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        navPanel.add(titleLabel, BorderLayout.CENTER);

        // Create center content panel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));

        // Create labels panel with vertical layout
        JPanel labelsPanel = new JPanel();
        labelsPanel.setLayout(new BoxLayout(labelsPanel, BoxLayout.Y_AXIS));
        labelsPanel.setBackground(Color.WHITE);

        // Initialize and style labels
        statusLabel = new JLabel("Waiting for opponent...", SwingConstants.CENTER);
        countdownLabel = new JLabel("Time left: 10 seconds", SwingConstants.CENTER);
        playerCountLabel = new JLabel("Players joined: 0", SwingConstants.CENTER);

        // Set fonts
        Font labelFont = new Font("Arial", Font.BOLD, 24);
        statusLabel.setFont(labelFont);
        countdownLabel.setFont(labelFont);
        playerCountLabel.setFont(new Font("Arial", Font.PLAIN, 22));

        // Add labels with proper spacing
        labelsPanel.add(statusLabel);
        labelsPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        labelsPanel.add(countdownLabel);
        labelsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        labelsPanel.add(playerCountLabel);

        // Add labels panel to center panel
        centerPanel.add(labelsPanel);

        // Create content wrapper
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(Color.WHITE);
        contentWrapper.add(navPanel, BorderLayout.NORTH);
        contentWrapper.add(centerPanel, BorderLayout.CENTER);

        // Add to main panel
        mainPanel.add(contentWrapper, BorderLayout.CENTER);

        // Set content pane
        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(null);
    }

    // Same back button styling as in ManagePlayersView
    private void styleBackButton(JButton button) {
        Color BROWN_COLOR = new Color(0xA08F73);
        Color BROWN_HOVER_COLOR = new Color(0x8A7B63);

        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(BROWN_COLOR);
        button.setBorder(new CustomComponentView.RoundedBorder(15, BROWN_COLOR));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
        button.setMaximumSize(new Dimension(120, 35));
        button.setMinimumSize(new Dimension(120, 35));
        button.setOpaque(false);

        // Add hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BROWN_HOVER_COLOR);
                button.setBorder(new CustomComponentView.RoundedBorder(15, BROWN_HOVER_COLOR));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BROWN_COLOR);
                button.setBorder(new CustomComponentView.RoundedBorder(15, BROWN_COLOR));
            }
        });
    }

    // Getters
    public JButton getBackButton() {
        return backButton;
    }

    public JButton getMinimizeBtn() {
        return headerTemplate.getMinimizeBtn();
    }

    public JButton getMaximizeBtn() {
        return headerTemplate.getMaximizeBtn();
    }

    public JButton getCloseBtn() {
        return headerTemplate.getCloseBtn();
    }

    public void updateStatus(String status) {
        statusLabel.setText(status);
    }

    public void updateCountdownLabel(int seconds) {
        countdownLabel.setText("Time left: " + seconds + " seconds");
    }

    public void updatePlayerCount(int count) {
        playerCountLabel.setText("Players joined: " + (count >= 0 ? count : "?"));
    }

    public void showView() {
        setVisible(true);
    }
}