package Client_Java.admin.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TimeConfigView extends JFrame {
    private JPanel mainPanel;
    private JTextField joinWaitField;
    private JTextField roundDurationField;
    private JButton applyButton;
    private JButton resetButton;
    private JButton backButton;
    private HeaderTemplate header;

    public static final Color MAIN_COLOR = Color.WHITE;
    private static final Color BUTTON_BROWN = new Color(0xA08F73);
    private static final Color BUTTON_BROWN_HOVER = new Color(0x8A7B63);
    private static final Color TEXTFIELD_BORDER = new Color(170, 150, 120);
    private static final Color TEXTFIELD_BG = new Color(245, 245, 245);

    // Fixed component sizes
    private static final Dimension FIELD_SIZE = new Dimension(400, 40);
    private static final Dimension BUTTON_SIZE = new Dimension(120, 35);
    private static final Dimension LARGE_BUTTON_SIZE = new Dimension(400, 40);

    public TimeConfigView() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("LETTRBOX - Time Configuration");
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(MAIN_COLOR);

        // Header setup
        header = new HeaderTemplate(this);
        mainPanel.add(header, BorderLayout.NORTH);

        // Content setup
        mainPanel.add(createContentPanel(), BorderLayout.CENTER);
        add(mainPanel);

        setVisible(true);
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(MAIN_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));

        // Top bar with back button (matches AddPlayerView)
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        backButton = createStyledButton("← Back", BUTTON_SIZE, 14);
        topBar.add(backButton, BorderLayout.WEST);

        applyButton = createStyledButton("Apply", BUTTON_SIZE, 14);
        topBar.add(applyButton, BorderLayout.EAST);

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        // Join Wait Time
        JLabel waitLabel = createFormLabel("Join Wait Time (seconds):");
        formPanel.add(waitLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        joinWaitField = createFormTextField();
        formPanel.add(joinWaitField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Round Duration
        JLabel durationLabel = createFormLabel("Round Duration (seconds):");
        formPanel.add(durationLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        roundDurationField = createFormTextField();
        formPanel.add(roundDurationField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Reset Button
        resetButton = createStyledButton("Reset to Default", LARGE_BUTTON_SIZE, 16);
        formPanel.add(resetButton);

        // Center the form
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(formPanel);

        contentPanel.add(topBar, BorderLayout.NORTH);
        contentPanel.add(centerPanel, BorderLayout.CENTER);

        return contentPanel;
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createFormTextField() {
        JTextField textField = new CustomComponentView.RoundedJTextField(10);
        textField.setPreferredSize(FIELD_SIZE);
        textField.setMaximumSize(FIELD_SIZE);
        textField.setMinimumSize(FIELD_SIZE);
        textField.setBorder(new CustomComponentView.RoundedBorder(10, TEXTFIELD_BORDER));
        textField.setBackground(TEXTFIELD_BG);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        return textField;
    }

    private JButton createStyledButton(String text, Dimension size, int fontSize) {
        JButton button = new CustomComponentView.RoundedJButton(text, 10);
        button.setFont(new Font("Arial", Font.BOLD, fontSize));
        button.setForeground(Color.WHITE);
        button.setBackground(BUTTON_BROWN);
        button.setBorder(new CustomComponentView.RoundedBorder(10, BUTTON_BROWN));
        button.setFocusPainted(false);
        button.setPreferredSize(size);
        button.setMaximumSize(size);
        button.setMinimumSize(size);
        button.setOpaque(false);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_BROWN_HOVER);
                button.setBorder(new CustomComponentView.RoundedBorder(10, BUTTON_BROWN_HOVER));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_BROWN);
                button.setBorder(new CustomComponentView.RoundedBorder(10, BUTTON_BROWN));
            }
        });
        return button;
    }

    // Getters
    public JButton getSaveButton() {
        return applyButton;
    }

    public JButton getDefaultButton() {
        return resetButton;
    }

    public JButton getBackButton() {
        return backButton;
    }

    public JTextField getJoinWaitField() {
        return joinWaitField;
    }

    public JTextField getRoundDurationField() {
        return roundDurationField;
    }

    public HeaderTemplate getHeader() {
        return header;
    }
}