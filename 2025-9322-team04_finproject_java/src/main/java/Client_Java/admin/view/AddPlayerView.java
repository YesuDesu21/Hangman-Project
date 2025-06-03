package Client_Java.admin.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class AddPlayerView {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton addButton;
    private HeaderTemplate header;
    private JButton backButton;

    public static final Color MAIN_COLOR = Color.WHITE;

    public AddPlayerView() {
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("LETTRBOX - Add Player");
        configureFrame();

        JPanel mainPanel = new JPanel(new BorderLayout());
        header = new HeaderTemplate(frame);
        mainPanel.add(header, BorderLayout.NORTH);

        // Top panel with back button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(MAIN_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 0));

        backButton = new CustomComponentView.RoundedJButton("← Back", 15);
        styleMainButton(backButton);
        backButton.setPreferredSize(new Dimension(120, 35));
        topPanel.add(backButton, BorderLayout.WEST);

        // Form panel
        JPanel formPanel = createFormPanel();

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(MAIN_COLOR);
        centerWrapper.add(topPanel, BorderLayout.NORTH);
        centerWrapper.add(formPanel, BorderLayout.CENTER);

        mainPanel.add(centerWrapper, BorderLayout.CENTER);
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(MAIN_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));

        JPanel centerContainer = new JPanel();
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));
        centerContainer.setBackground(MAIN_COLOR);
        centerContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        JLabel titleLabel = new JLabel("Add New Player");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerContainer.add(titleLabel);
        centerContainer.add(Box.createRigidArea(new Dimension(0, 20)));

        // Username
        JLabel usernameLabel = new JLabel("Username");
        styleLabel(usernameLabel);
        centerContainer.add(usernameLabel);

        usernameField = new CustomComponentView.RoundedJTextField(15);
        styleTextField(usernameField);
        centerContainer.add(usernameField);
        centerContainer.add(Box.createRigidArea(new Dimension(0, 25)));

        // Password
        JLabel passwordLabel = new JLabel("Password");
        styleLabel(passwordLabel);
        centerContainer.add(passwordLabel);

        passwordField = new CustomComponentView.RoundedJPasswordField(15);
        styleTextField(passwordField);
        centerContainer.add(passwordField);
        centerContainer.add(Box.createRigidArea(new Dimension(0, 35)));

        // Add Button
        addButton = new CustomComponentView.RoundedJButton("Add Player", 15);
        styleMainButton(addButton);
        centerContainer.add(addButton);

        formPanel.add(Box.createVerticalGlue());
        formPanel.add(centerContainer);
        formPanel.add(Box.createVerticalGlue());

        return formPanel;
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

    private void styleMainButton(JButton button) {
        Color mainColor = new Color(0xA08F73);
        button.setMaximumSize(new Dimension(300, 40));
        button.setPreferredSize(new Dimension(300, 40));
        button.setMinimumSize(new Dimension(300, 40));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(mainColor);
        button.setForeground(Color.WHITE);
        button.setBorder(new CustomComponentView.RoundedBorder(15, mainColor));
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setOpaque(false);
    }

    // Getters
    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public JFrame getFrame() {
        return frame;
    }

    public JButton getAddButton() {
        return addButton;
    }

    public JButton getBackButton() {
        return backButton;
    }

    public HeaderTemplate getHeader() {
        return header;
    }

    public void addAddButtonListener(ActionListener listener) {
        addButton.addActionListener(listener);
    }

    public void updateWindowState(boolean isMaximized) {
        header.getMaximizeBtn().setText(isMaximized ? "❐" : "□");
    }

    private void configureFrame() {
        frame.setUndecorated(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}