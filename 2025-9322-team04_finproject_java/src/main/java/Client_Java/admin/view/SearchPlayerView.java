package Client_Java.admin.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SearchPlayerView {
    private JFrame frame;
    private JTextField keywordField;
    private JButton searchButton;
    private JButton backButton;
    private HeaderTemplate header;

    public static final Color MAIN_COLOR = Color.WHITE;
    private static final Color BUTTON_BROWN = new Color(0xA08F73);
    private static final Color BUTTON_BROWN_HOVER = new Color(0x8A7B63);
    private static final Color TEXTFIELD_BORDER = new Color(170, 150, 120);
    private static final Color TEXTFIELD_BG = new Color(245, 245, 245);

    public SearchPlayerView() {
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("LETTRBOX - Search Player");
        configureFrame();

        JPanel mainPanel = new JPanel(new BorderLayout());
        header = new HeaderTemplate(frame);
        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(createFormPanel(), BorderLayout.CENTER);
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JPanel createFormPanel() {
        // Main form panel using BorderLayout for proper structure
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(MAIN_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // Top panel with back button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(MAIN_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        backButton = new CustomComponentView.RoundedJButton("← Back", 15);
        styleBackButton(backButton);
        topPanel.add(backButton, BorderLayout.WEST);
        formPanel.add(topPanel, BorderLayout.NORTH);

        // Center content panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(MAIN_COLOR);
        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        JLabel titleLabel = new JLabel("Search Player");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Keyword Field
        JLabel keywordLabel = new JLabel("Enter Keyword");
        styleLabel(keywordLabel);
        centerPanel.add(keywordLabel);

        keywordField = new CustomComponentView.RoundedJTextField(15);
        styleTextField(keywordField);
        centerPanel.add(keywordField);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Search Button in original placement
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(MAIN_COLOR);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        searchButton = new CustomComponentView.RoundedJButton("Search", 15);
        styleSearchButton(searchButton);
        buttonPanel.add(searchButton);

        centerPanel.add(buttonPanel);
        formPanel.add(centerPanel, BorderLayout.CENTER);

        return formPanel;
    }

    private void styleBackButton(JButton button) {

        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(BUTTON_BROWN);
        button.setBorder(new CustomComponentView.RoundedBorder(15, BUTTON_BROWN));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
        button.setMaximumSize(new Dimension(120, 35));
        button.setMinimumSize(new Dimension(120, 35));
        button.setOpaque(false);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_BROWN_HOVER);
                button.setBorder(new CustomComponentView.RoundedBorder(15, BUTTON_BROWN_HOVER));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_BROWN);
                button.setBorder(new CustomComponentView.RoundedBorder(15, BUTTON_BROWN));
            }
        });
    }

    private void styleSearchButton(JButton button) {

        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(BUTTON_BROWN);
        button.setBorder(new CustomComponentView.RoundedBorder(15, BUTTON_BROWN));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 40));
        button.setMaximumSize(new Dimension(150, 40));
        button.setMinimumSize(new Dimension(150, 40));
        button.setOpaque(false);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_BROWN_HOVER);
                button.setBorder(new CustomComponentView.RoundedBorder(15, BUTTON_BROWN_HOVER));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_BROWN);
                button.setBorder(new CustomComponentView.RoundedBorder(15, BUTTON_BROWN));
            }
        });
    }

    private void styleLabel(JLabel label) {
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(Color.BLACK);
        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void styleTextField(JTextField textField) {
        textField.setMaximumSize(new Dimension(300, 35)); // Slightly more compact
        textField.setPreferredSize(new Dimension(300, 35));
        textField.setMinimumSize(new Dimension(300, 35));
        textField.setBorder(new CustomComponentView.RoundedBorder(15, TEXTFIELD_BORDER));
        textField.setBackground(TEXTFIELD_BG);
        textField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);
        textField.setOpaque(false);
    }


    // Getters
    public String getKeyword() {
        return keywordField.getText();
    }

    public JFrame getFrame() {
        return frame;
    }

    public JButton getSearchButton() {
        return searchButton;
    }

    public JButton getBackButton() {
        return backButton;
    }

    public HeaderTemplate getHeader() {
        return header;
    }

    // Listener registration
    public void addSearchButtonListener(ActionListener listener) {
        searchButton.addActionListener(listener);
    }

    public void addBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }

    public void show() {
        frame.setVisible(true);
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