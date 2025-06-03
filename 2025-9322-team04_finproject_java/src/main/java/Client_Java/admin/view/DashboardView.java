package Client_Java.admin.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DashboardView {
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel contentPanel;
    private HeaderTemplate header;

    private JButton managePlayersBtn;
    private JButton searchPlayersBtn;
    private JButton timeConfigBtn;
    private JButton gameHistBtn;
    private JButton logOutBtn;

    public DashboardView() {
        initializeUI();
    }
    //forcommitting
    private void initializeUI() {
        frame = new JFrame();
        frame.setUndecorated(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Create and add header
        header = new HeaderTemplate(frame);
        mainPanel.add(header, BorderLayout.NORTH);

        // Create content panel
        createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private void createContentPanel() {
        contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));

        managePlayersBtn = createMenuButton("Manage Players");
        searchPlayersBtn = createMenuButton("Search Players");
        timeConfigBtn = createMenuButton("Time Configuration");
        gameHistBtn = createMenuButton("Game History");
        logOutBtn = createMenuButton("Log Out");

        buttonsPanel.add(managePlayersBtn);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        buttonsPanel.add(searchPlayersBtn);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        buttonsPanel.add(timeConfigBtn);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        buttonsPanel.add(gameHistBtn);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        buttonsPanel.add(logOutBtn);

        contentPanel.add(buttonsPanel);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(450, 60));
        button.setMaximumSize(new Dimension(450, 60));
        button.setFont(new Font("inter", Font.BOLD, 18));
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

    // Getters
    public JFrame getFrame() {
        return frame;
    }

    public HeaderTemplate getHeader() {
        return header;
    }

    public JButton getManagePlayersBtn() {
        return managePlayersBtn;
    }

    public JButton getSearchPlayersBtn() {
        return searchPlayersBtn;
    }

    public JButton getGameHistBtn() { return gameHistBtn; }

    public JButton getMinimizeBtn() {
        return header.getMinimizeBtn();
    }

    public JButton getMaximizeBtn() {
        return header.getMaximizeBtn();
    }

    public JButton getCloseBtn() {
        return header.getCloseBtn();
    }

    public JButton getTimeConfigBtn() {
        return timeConfigBtn;
    }

    public JButton getLogOutBtn() {
        return logOutBtn;
    }

    public void show() {
        frame.setVisible(true);
    }

    public void updateMaximizeButton(boolean isMaximized) {
        header.getMaximizeBtn().setText(isMaximized ? "❐" : "□");
    }
}