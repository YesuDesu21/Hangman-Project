package Client_Java.admin.controller;

import Client_Java.admin.model.DashboardModel;
import Client_Java.admin.model.SearchPlayerModel;
import Client_Java.admin.view.DashboardView;
import Client_Java.admin.view.SearchPlayerView;
import Client_Java.admin.AdminCorbaManager;
import compilations.AdminService;
import compilations.PlayerAccount;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.awt.event.*;
import java.util.stream.Collectors;

public class SearchPlayerController {
    private final SearchPlayerModel model;
    private final SearchPlayerView view;
    private boolean isMaximized = true;

    public SearchPlayerController(SearchPlayerModel model, SearchPlayerView view) {
        this.model = model;
        this.view = view;
        initController();
    }
    //forcommitting
    private void initController() {
        setupWindowControls();
        setupSearchButton();
        setupBackButton();
        setupWindowStateListener();
    }

    private void setupWindowControls() {
        view.getHeader().getMinimizeBtn().addActionListener(e ->
                view.getFrame().setExtendedState(Frame.ICONIFIED));

        view.getHeader().getMaximizeBtn().addActionListener(e ->
                toggleMaximizeRestore());

        view.getHeader().getCloseBtn().addActionListener(e ->
                view.getFrame().dispose());
    }

    private void setupSearchButton() {
        view.getSearchButton().addActionListener(e -> performSearch());
        setupButtonHoverEffect(view.getSearchButton());
    }

    private void setupBackButton() {
        view.getBackButton().addActionListener(e -> {
            view.getFrame().dispose();
            DashboardView dashboardView = new DashboardView();
            DashboardModel dashboardModel = new DashboardModel();
            new DashboardController(dashboardModel, dashboardView);
        });

        setupButtonHoverEffect(view.getBackButton());
    }

    private void setupButtonHoverEffect(JButton button) {
        Color BUTTON_BROWN = new Color(0xA08F73);
        Color BUTTON_BROWN_HOVER = new Color(0x8A7B63);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_BROWN_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_BROWN);
            }
        });
    }

    private void setupWindowStateListener() {
        view.getFrame().addWindowStateListener(e -> {
            isMaximized = (e.getNewState() & Frame.MAXIMIZED_BOTH) != 0;
            view.updateWindowState(isMaximized);
        });
    }

    private void toggleMaximizeRestore() {
        if (isMaximized) {
            view.getFrame().setExtendedState(Frame.NORMAL);
            view.getFrame().setSize(1000, 700);
            view.getFrame().setLocationRelativeTo(null);
        } else {
            view.getFrame().setExtendedState(Frame.MAXIMIZED_BOTH);
        }
        isMaximized = !isMaximized;
        view.updateWindowState(isMaximized);
    }

    private void performSearch() {
        String keyword = view.getKeyword().trim();

        if (keyword.isEmpty()) {
            showErrorDialog("Please enter a search keyword");
            return;
        }

        try {
            AdminService adminService = AdminCorbaManager.getInstance().getAdminService();
            PlayerAccount[] allPlayers = adminService.searchPlayerAccounts("");

            List<PlayerAccount> filteredPlayers = Arrays.stream(allPlayers)
                    .filter(player -> player.username.toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());

            if (filteredPlayers.isEmpty()) {
                showInfoDialog("No players found matching: " + keyword);
            } else {
                displayResultsInTable(filteredPlayers, keyword);
            }
        } catch (Exception e) {
            showErrorDialog("Search failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // helper method for displaying results
    private void displayResultsInTable(List<PlayerAccount> players, String keyword) {
        String[] columnNames = {"Username", "Games Won"};
        Object[][] rowData = players.stream()
                .map(player -> new Object[]{player.username, player.gamesWon})
                .toArray(Object[][]::new);

        JTable table = new JTable(rowData, columnNames);
        JOptionPane.showMessageDialog(
                view.getFrame(),
                new JScrollPane(table),
                "Search Results for: " + keyword,
                JOptionPane.PLAIN_MESSAGE
        );
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(
                view.getFrame(),
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void showInfoDialog(String message) {
        JOptionPane.showMessageDialog(
                view.getFrame(),
                message,
                "Results for: " + view.getKeyword() + "",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}