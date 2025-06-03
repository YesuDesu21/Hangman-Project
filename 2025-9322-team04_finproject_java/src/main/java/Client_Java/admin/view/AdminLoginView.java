package Client_Java.admin.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class AdminLoginView {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton minimizeBtn;
    private JButton maximizeBtn;
    private JButton closeBtn;
    public static final Color MAIN_COLOR = Color.WHITE;

    public AdminLoginView() {
        initializeUI();
    }//for committing

    private void initializeUI() {
        frame = new JFrame("LETTRBOX - Admin Login");
        configureFrame();

        JPanel mainPanel = new JPanel(new BorderLayout());
        addControlButtons(mainPanel);
        mainPanel.add(createLoginPanel(), BorderLayout.CENTER);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private void configureFrame() {
        frame.setUndecorated(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void addControlButtons(JPanel mainPanel) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setPreferredSize(new Dimension(frame.getWidth(), 50));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setOpaque(false);

        minimizeBtn = createControlButton("—");
        maximizeBtn = createControlButton("❐");
        closeBtn = createControlButton("X");

        buttonPanel.add(minimizeBtn);
        buttonPanel.add(maximizeBtn);
        buttonPanel.add(closeBtn);

        headerPanel.add(buttonPanel, BorderLayout.EAST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }

    private JButton createControlButton(String text) {
        JButton button = new JButton(text);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBackground(MAIN_COLOR);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Inter", Font.BOLD, 16));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setFocusPainted(false);
        return button;
    }

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JPanel centerContainer = new JPanel();
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));
        centerContainer.setBackground(Color.WHITE);
        centerContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Logo Image
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/Screenshot 2025-04-30 213220.png"));
            if (originalIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                Image scaledImage = originalIcon.getImage().getScaledInstance(500, 100, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                centerContainer.add(imageLabel);
                centerContainer.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        } catch (Exception e) {
            System.err.println("Image load error: " + e.getMessage());
        }

        // Separator Line
        JPanel separator = new JPanel();
        separator.setMaximumSize(new Dimension(400, 3));
        separator.setPreferredSize(new Dimension(300, 2));
        separator.setBackground(Color.BLACK);
        separator.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerContainer.add(separator);
        centerContainer.add(Box.createRigidArea(new Dimension(0, 35)));

        // Username Label
        JLabel usernameLabel = new JLabel("Username");
        styleLabel(usernameLabel);
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerContainer.add(usernameLabel);

        // Username Field
        usernameField = new CustomComponentView.RoundedJTextField(15);
        styleTextField(usernameField);
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerContainer.add(usernameField);
        centerContainer.add(Box.createRigidArea(new Dimension(0, 35)));

        // Password Label
        JLabel passwordLabel = new JLabel("Password");
        styleLabel(passwordLabel);
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerContainer.add(passwordLabel);

        // Password Field
        passwordField = new CustomComponentView.RoundedJPasswordField(15);
        styleTextField(passwordField);
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerContainer.add(passwordField);
        centerContainer.add(Box.createRigidArea(new Dimension(0, 35)));

        // Login Button
        loginButton = new CustomComponentView.RoundedJButton("Log In", 15);
        styleLoginButton(loginButton);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerContainer.add(loginButton);
        centerContainer.add(Box.createRigidArea(new Dimension(0, 35)));

        // Bottom Separator
        JPanel bottomSeparator = new JPanel();
        bottomSeparator.setMaximumSize(new Dimension(400, 3));
        bottomSeparator.setPreferredSize(new Dimension(300, 2));
        bottomSeparator.setBackground(Color.BLACK);
        bottomSeparator.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerContainer.add(bottomSeparator);

        loginPanel.add(Box.createVerticalGlue());
        loginPanel.add(centerContainer);
        loginPanel.add(Box.createVerticalGlue());

        return loginPanel;
    }

    private void styleLabel(JLabel label) {
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(Color.BLACK);
        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void styleTextField(JTextField textField) {
        textField.setMaximumSize(new Dimension(300, 50));
        textField.setPreferredSize(new Dimension(300, 50));
        textField.setMinimumSize(new Dimension(300, 50));
        textField.setBorder(new CustomComponentView.RoundedBorder(15, new Color(170, 150, 120)));
        textField.setBackground(new Color(245, 245, 245));
        textField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);
        textField.setOpaque(false);
    }

    private void styleLoginButton(JButton button) {
        Color mainButtonColor = new Color(0xA08F73);
        Color hoverButtonColor = new Color(0x8A7B63);

        button.setMaximumSize(new Dimension(300, 40));
        button.setPreferredSize(new Dimension(300, 40));
        button.setMinimumSize(new Dimension(300, 40));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(mainButtonColor);
        button.setForeground(Color.WHITE);
        button.setBorder(new CustomComponentView.RoundedBorder(15, mainButtonColor));
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setOpaque(false);
    }
    public JButton getMinimizeButton() {
        return minimizeBtn;
    }

    public JButton getMaximizeButton() {
        return maximizeBtn;
    }

    public JButton getCloseButton() {
        return closeBtn;
    }

    public JButton getLoginButton() {
        return loginButton;
    }

    // Listener registration methods
    public void addLoginButtonListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }

    public void addMinimizeButtonListener(ActionListener listener) {
        minimizeBtn.addActionListener(listener);
    }

    public void addMaximizeButtonListener(ActionListener listener) {
        maximizeBtn.addActionListener(listener);
    }

    public void addCloseButtonListener(ActionListener listener) {
        closeBtn.addActionListener(listener);
    }

    // Getters for controller
    public JFrame getFrame() {
        return frame;
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }
}