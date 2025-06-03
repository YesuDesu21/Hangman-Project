package Client_Java.player.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class LeaderboardsView extends JFrame {
    private JTable leaderboardTable;
    private HeaderTemplate headerTemplate;
    private DefaultTableModel tableModel;
    private JButton backButton;

    public LeaderboardsView(List<Map<String, Object>> leaderboardData) {
        setTitle("LETTRBOX - Leaderboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        getContentPane().setBackground(Color.WHITE);

        Color primaryColor = new Color(0xA08F73);
        Font titleFont = new Font("Segoe UI", Font.BOLD, 32);
        Font tableFont = new Font("Segoe UI", Font.PLAIN, 18);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        add(mainPanel);

        // Initialize HeaderTemplate
        headerTemplate = new HeaderTemplate(this);
        mainPanel.add(headerTemplate, BorderLayout.NORTH);

        // Create navigation panel with back button
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBackground(Color.WHITE);
        navPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        backButton = createBackButton();
        navPanel.add(backButton, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Leaderboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        navPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel bodyPanel = new JPanel();
        bodyPanel.setBackground(Color.WHITE);
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        JScrollPane scrollPane = new JScrollPane(createLeaderboardTable());
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        bodyPanel.add(scrollPane);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(navPanel, BorderLayout.NORTH);
        contentPanel.add(bodyPanel, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        populateLeaderboard(leaderboardData);

        // Pack and center the window
        pack();
        setLocationRelativeTo(null);
    }

    private JButton createBackButton() {
        JButton button = new JButton("← Back");
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0xA08F73));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        // Add hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0x8A7B63));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0xA08F73));
            }
        });

        return button;
    }

    private JTable createLeaderboardTable() {
        String[] columnNames = {"Rank", "Player Name", "Score"};
        tableModel = new DefaultTableModel(columnNames, 0);
        leaderboardTable = new JTable(tableModel);

        leaderboardTable.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        leaderboardTable.setRowHeight(40);
        leaderboardTable.setFillsViewportHeight(true);
        leaderboardTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 20));
        leaderboardTable.getTableHeader().setBackground(new Color(0xA08F73));
        leaderboardTable.getTableHeader().setForeground(Color.WHITE);
        leaderboardTable.setGridColor(Color.LIGHT_GRAY);
        leaderboardTable.setShowVerticalLines(false);

        return leaderboardTable;
    }

    public void updateMaximizeButton(boolean isMaximized) {
        headerTemplate.getMaximizeBtn().setText(isMaximized ? "❐" : "□");
    }

    private void populateLeaderboard(List<Map<String, Object>> leaderboardData) {
        tableModel.setRowCount(0);
        int rank = 1;
        for (Map<String, Object> entry : leaderboardData) {
            String name = (String) entry.get("name");
            int score = (int) entry.get("score");
            tableModel.addRow(new Object[]{rank++, name, score});
        }
    }

    public JTable getLeaderboardTable() {
        return leaderboardTable;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

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

    public void showView() {
        headerTemplate.updateHeaderSize(getWidth());
        super.setVisible(true);
    }
}