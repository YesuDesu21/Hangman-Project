package Client_Java.player.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class GameProperView extends Component {
    private final JFrame frame;
    private final JLabel[] letterLabels;
    private JLabel roundLabel;
    private final JLabel hiddenWordLabel;
    private final JLabel[] heartLabels;
    private final JPanel keyboardPanel;
    private final Map<Character, JButton> keyButtons;
    private JLabel timerLabel;

    public static final int TIMER_START = 30;
    private static final Color MAIN_COLOR = Color.WHITE;
    private static final Color HEADER_COLOR = new Color(0xA08F73);
    public static final int HEADER_HEIGHT = 100;
    public static final int IMAGE_WIDTH = 300;
    public static final int IMAGE_HEIGHT = 70;

    private JButton minimizeBtn;
    private JButton maximizeBtn;
    private JButton closeBtn;
    private JDialog waitingDialog;

    public GameProperView(String word) {
        frame = new JFrame("LETTRBOX - Hangman");
        frame.setUndecorated(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        keyButtons = new HashMap<>();
        letterLabels = new JLabel[word.length()];
        heartLabels = new JLabel[5];

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(MAIN_COLOR);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(HEADER_COLOR);
        JPanel headerPanel = createHeaderPanel();
        topPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Display of which round the game is in
        roundLabel = new JLabel("ROUND 1", SwingConstants.CENTER);
        roundLabel.setFont(new Font("Arial", Font.BOLD, 26));
        roundLabel.setForeground(Color.BLACK);
        roundLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Display of the time remaining for the current round
        timerLabel = new JLabel("Time Remaining: " + TIMER_START);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        timerLabel.setForeground(Color.BLACK);
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel wordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        wordPanel.setBackground(MAIN_COLOR);
        for (int i = 0; i < word.length(); i++) {
            JLabel lbl = new JLabel("_");
            lbl.setFont(new Font("SansSerif", Font.BOLD, 36));
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setPreferredSize(new Dimension(40, 50));
            letterLabels[i] = lbl;
            wordPanel.add(lbl);
        }

        // Display a hidden word
        hiddenWordLabel = new JLabel(word.replaceAll(".", "*"), SwingConstants.CENTER);
        hiddenWordLabel.setFont(new Font("Arial", Font.BOLD, 28));
        hiddenWordLabel.setForeground(Color.BLACK);

        // A display of your remaining number of attempts using a heart image
        JPanel heartsPanel = new JPanel();
        heartsPanel.setBackground(MAIN_COLOR);
        for (int i = 0; i < 5; i++) {
            JLabel heart;
            java.net.URL imgUrl = getClass().getResource("/heart.png");
            if (imgUrl != null) {
                heart = new JLabel(new ImageIcon(imgUrl));
            } else {
                heart = new JLabel("❤️");
                heart.setFont(new Font("Arial", Font.PLAIN, 24));
            }
            heartLabels[i] = heart;
            heartsPanel.add(heart);
        }

        // A keyboard in QWERTY format to use
        keyboardPanel = new JPanel();
        keyboardPanel.setLayout(new GridLayout(3, 1, 0, 0));
        keyboardPanel.setBackground(MAIN_COLOR);

        String[] rows = {
                "QWERTYUIOP",
                "ASDFGHJKL",
                "ZXCVBNM"
        };

        for (String row : rows) {
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            rowPanel.setBackground(MAIN_COLOR);

            for (char c : row.toCharArray()) {
                JButton btn = new JButton(String.valueOf(c));
                btn.setOpaque(true);
                btn.setContentAreaFilled(false);
                btn.setBorderPainted(true);
                btn.setFocusPainted(false);
                btn.setBackground(new Color(245, 245, 245, 100));
                btn.setForeground(Color.BLACK);
                btn.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                btn.setFont(new Font("Arial", Font.BOLD, 20));
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btn.setPreferredSize(new Dimension(90, 90));
                btn.setMargin(new Insets(0, 0, 0, 0));

                keyButtons.put(c, btn);
                addHoverEffect(btn);
                rowPanel.add(btn);
            }

            keyboardPanel.add(rowPanel);
        }

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(MAIN_COLOR);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        hiddenWordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        wordPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        keyboardPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        heartsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(roundLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(timerLabel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(wordPanel);
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(keyboardPanel);
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(heartsPanel);
        centerPanel.add(Box.createVerticalStrut(30));

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    // Naccaw header from Admin MVC :3
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setPreferredSize(new Dimension(0, HEADER_HEIGHT));

        JPanel logoWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        logoWrapper.setOpaque(false);
        java.net.URL imgUrl = getClass().getResource("/img.png");
        if (imgUrl != null) {
            ImageIcon icon = new ImageIcon(imgUrl);
            Image scaledImage = icon.getImage().getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            logoWrapper.add(imageLabel);
        } else {
            JLabel textLabel = new JLabel("LETTRBOX");
            textLabel.setFont(new Font("Arial", Font.BOLD, 24));
            textLabel.setForeground(Color.WHITE);
            logoWrapper.add(textLabel);
        }

        headerPanel.add(logoWrapper, BorderLayout.CENTER);
        headerPanel.add(createControlButtons(), BorderLayout.EAST);
        return headerPanel;
    }

    private JPanel createControlButtons() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setPreferredSize(new Dimension(150, 40));

        minimizeBtn = createControlButton("—", 30);
        maximizeBtn = createControlButton("□", 30);
        closeBtn = createControlButton("X", 30);
        closeBtn.setForeground(new Color(255, 230, 230));

        buttonPanel.add(minimizeBtn);
        buttonPanel.add(maximizeBtn);
        buttonPanel.add(closeBtn);
        return buttonPanel;
    }

    private JButton createControlButton(String text, int size) {
        JButton button = new JButton(text);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBackground(HEADER_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Inter", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setPreferredSize(new Dimension(size + 10, size + 10));
        button.setFocusPainted(false);
        return button;
    }

    private void toggleMaximize() {
        if (frame.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
            frame.setExtendedState(JFrame.NORMAL);
            maximizeBtn.setText("□");
        } else {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            maximizeBtn.setText("❐");
        }
    }

    // Naccaw Hover :3
    private void addHoverEffect(JButton btn) {
        Color defaultBg = new Color(245, 245, 245);
        Color hoverBg = new Color(220, 220, 220);

        btn.setBackground(defaultBg);

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverBg);
                btn.setContentAreaFilled(true);
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(defaultBg);
                btn.setContentAreaFilled(false);
            }
        });
    }

    public void showWaitingForOthersDialog() {
        if (waitingDialog != null && waitingDialog.isShowing()) return;

        // Create the JDialog to display the message
        waitingDialog = new JDialog(frame, "Waiting", false); // non-modal
        waitingDialog.setUndecorated(true);
        waitingDialog.setSize(300, 100);
        waitingDialog.setLocationRelativeTo(this); // Center on the current window

        // Label to show the message
        JLabel message = new JLabel("You have used all attempts.\nWaiting for others...", SwingConstants.CENTER);
        message.setFont(new Font("Arial", Font.PLAIN, 14));
        waitingDialog.add(message);

        // Make the dialog always on top of other windows
        waitingDialog.setAlwaysOnTop(true);
        waitingDialog.setVisible(true);

    }

    public void hideWaitingDialogAndEnableInput() {
        if (waitingDialog != null) {
            waitingDialog.dispose();
            waitingDialog = null;
        }
    }


    public JFrame getFrame() {
        return frame;
    }

    public Map<Character, JButton> getKeyButtons() {
        return keyButtons;
    }

    public JLabel[] getLetterLabels() {
        return letterLabels;
    }

    public JLabel getTimerLabel() {
        return timerLabel;
    }

    public JLabel[] getHeartLabels() {
        return heartLabels;
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

    public JButton getKeyButton(char key) {
        return keyButtons.get(Character.toUpperCase(key));
    }

    public void setRound(int roundNumber) {
        roundLabel.setText("ROUND " + roundNumber);
    }


}
