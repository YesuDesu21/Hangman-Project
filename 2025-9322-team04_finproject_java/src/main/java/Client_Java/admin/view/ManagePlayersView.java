package Client_Java.admin.view;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class ManagePlayersView {
    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton backButton;
    private HeaderTemplate header;

    public static final Color MAIN_COLOR = Color.WHITE;
    private static final Color BROWN_COLOR = new Color(0xA08F73);
    private static final Color BROWN_HOVER_COLOR = new Color(0x8A7B63);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    public ManagePlayersView() {
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("LETTRBOX - Manage Players");
        frame.setUndecorated(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(MAIN_COLOR);

        header = new HeaderTemplate(frame);
        mainPanel.add(header, BorderLayout.NORTH);

        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(MAIN_COLOR);
        contentWrapper.add(createNavigationBar(), BorderLayout.NORTH);
        contentWrapper.add(createContentPanel(), BorderLayout.CENTER);

        mainPanel.add(contentWrapper, BorderLayout.CENTER);
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JPanel createNavigationBar() {
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(MAIN_COLOR);
        navBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Back Button (already styled)
        backButton = new CustomComponentView.RoundedJButton("← Back", 15);
        styleBackButton(backButton);

        JLabel title = new JLabel("Manage Players", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.BLACK);

        // Updated Add Player Button
        addButton = new CustomComponentView.RoundedJButton("Add Player", 15);
        styleActionButton(addButton); // Same styling as Update/Delete

        navBar.add(backButton, BorderLayout.WEST);
        navBar.add(title, BorderLayout.CENTER);
        navBar.add(addButton, BorderLayout.EAST);

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
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(MAIN_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(MAIN_COLOR);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(100, 200, 100, 200));
        tablePanel.add(new JScrollPane(createTable()), BorderLayout.CENTER);
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        JPanel bottomButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomButtons.setBackground(MAIN_COLOR);
        bottomButtons.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // Updated Update and Delete buttons
        updateButton = new CustomComponentView.RoundedJButton("Update", 15);
        deleteButton = new CustomComponentView.RoundedJButton("Delete", 15);

        styleActionButton(updateButton);
        styleActionButton(deleteButton);

        bottomButtons.add(updateButton);
        bottomButtons.add(deleteButton);

        contentPanel.add(bottomButtons, BorderLayout.SOUTH);
        return contentPanel;
    }

    private void styleActionButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(BROWN_COLOR);
        button.setBorder(new CustomComponentView.RoundedBorder(15, BROWN_COLOR));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(160, 50)); // Slightly taller than back button
        button.setMaximumSize(new Dimension(160, 50));
        button.setMinimumSize(new Dimension(160, 50));
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
    private JTable createTable() {
        tableModel = new DefaultTableModel(new Object[]{"#", "Username", "Wins", "Password"}, 0);
        table = new JTable(tableModel);

        table.setRowHeight(50);
        table.setFont(new Font("Inter", Font.PLAIN, 24));
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Inter", Font.BOLD, 20));
        header.setBackground(BROWN_COLOR);
        header.setForeground(Color.WHITE);
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        return table;
    }

    private void styleButton(JButton button, Font font, Color foreground, Color background) {
        button.setFont(font);
        button.setForeground(foreground);
        button.setBackground(background);
        button.setFocusPainted(false);
    }

    // Getters
    public JFrame getFrame() { return frame; }
    public JTable getTable() { return table; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JButton getAddButton() { return addButton; }
    public JButton getUpdateButton() { return updateButton; }
    public JButton getDeleteButton() { return deleteButton; }
    public JButton getBackButton() { return backButton; }
    public HeaderTemplate getHeader() { return header; }
}