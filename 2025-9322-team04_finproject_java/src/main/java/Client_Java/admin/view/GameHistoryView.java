package Client_Java.admin.view;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class GameHistoryView extends JFrame {
    private JTable gameTable;
    private JButton backButton;
    private HeaderTemplate header;

    public static final Color MAIN_COLOR = Color.WHITE;
    private static final Color BROWN_COLOR = new Color(0xA08F73);
    private static final Color BROWN_HOVER_COLOR = new Color(0x8A7B63);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);

    public GameHistoryView() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("LETTRBOX - Game History");
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(MAIN_COLOR);

        // Use the same HeaderTemplate as other views
        header = new HeaderTemplate(this);
        mainPanel.add(header, BorderLayout.NORTH);

        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(MAIN_COLOR);
        contentWrapper.add(createNavigationBar(), BorderLayout.NORTH);
        contentWrapper.add(createContentPanel(), BorderLayout.CENTER);

        mainPanel.add(contentWrapper, BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);
    }

    private JPanel createNavigationBar() {
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(MAIN_COLOR);
        navBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Back Button with same styling as ManagePlayersView
        backButton = new CustomComponentView.RoundedJButton("← Back", 15);
        styleBackButton(backButton);

        JLabel title = new JLabel("Game History", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(Color.BLACK);

        navBar.add(backButton, BorderLayout.WEST);
        navBar.add(title, BorderLayout.CENTER);

        return navBar;
    }

    private void styleBackButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(BROWN_COLOR);
        button.setBorder(new CustomComponentView.RoundedBorder(15, BROWN_COLOR));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
        button.setMaximumSize(new Dimension(120, 35));
        button.setMinimumSize(new Dimension(120, 35));
        button.setOpaque(false);

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

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(MAIN_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(MAIN_COLOR);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(50, 150, 50, 150));

        // Create table with original columns
        String[] columns = {"#", "Game ID", "Time Created", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        gameTable = new JTable(model);

        // Apply ManagePlayersView table styling
        styleGameTable();

        tablePanel.add(new JScrollPane(gameTable), BorderLayout.CENTER);
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        return contentPanel;
    }

    private void styleGameTable() {
        gameTable.setRowHeight(50);
        gameTable.setFont(new Font("Inter", Font.PLAIN, 20));
        gameTable.setFillsViewportHeight(true);
        gameTable.setShowGrid(false);
        gameTable.setIntercellSpacing(new Dimension(0, 0));
        gameTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < gameTable.getColumnCount(); i++) {
            gameTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JTableHeader tableHeader = gameTable.getTableHeader();
        tableHeader.setFont(new Font("Inter", Font.BOLD, 18));
        tableHeader.setBackground(BROWN_COLOR);
        tableHeader.setForeground(Color.WHITE);
        ((DefaultTableCellRenderer) tableHeader.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
    }

    public void populateGameTable(List<Map<String, Object>> gameSessions) {
        DefaultTableModel tableModel = (DefaultTableModel) gameTable.getModel();
        tableModel.setRowCount(0);  // Clear existing rows

        if (gameSessions == null || gameSessions.isEmpty()) {
            System.out.println("No game sessions available");
            return;
        }

        int index = 1;
        for (Map<String, Object> session : gameSessions) {
            String gameId = (String) session.get("gameId");
            String timeCreated = (String) session.get("timeCreated");
            String status = (String) session.get("status");

            tableModel.addRow(new Object[]{
                    String.format("%02d", index),
                    gameId,
                    timeCreated,
                    status
            });
            index++;
        }
    }

    // Getters
    public JFrame getFrame() { return this; }
    public JTable getGameTable() { return gameTable; }
    public JButton getBackButton() { return backButton; }
    public HeaderTemplate getHeader() { return header; }
}