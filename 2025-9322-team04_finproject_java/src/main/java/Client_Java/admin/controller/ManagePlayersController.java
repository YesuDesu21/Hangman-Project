package Client_Java.admin.controller;

import Client_Java.admin.model.AddPlayerModel;
import Client_Java.admin.model.DashboardModel;
import Client_Java.admin.model.ManagePlayersModel;
import Client_Java.admin.view.AddPlayerView;
import Client_Java.admin.view.DashboardView;
import Client_Java.admin.view.ManagePlayersView;
import Client_Java.admin.AdminCorbaManager;
import compilations.AdminService;
import compilations.PlayerAccount;
import compilations.PlayerAlreadyExistsException;
import compilations.PlayerNotFoundException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;

public class ManagePlayersController {
    private final ManagePlayersView view;
    private final ManagePlayersModel model;
    private final AdminCorbaManager corbaManager;

    public ManagePlayersController(ManagePlayersView view, ManagePlayersModel model) {
        this.view = view;
        this.model = model;
        this.corbaManager = AdminCorbaManager.getInstance();
        initializeController();
        initializeTableData();
    }

    private void initializeController() {
        view.getAddButton().addActionListener(e -> {
            view.getFrame().dispose();
            AddPlayerView addPlayerView = new AddPlayerView();
            AddPlayerModel addPlayerModel = new AddPlayerModel();
            new AddPlayerController(addPlayerModel, addPlayerView);
        });
//forcommitting
        view.getUpdateButton().addActionListener(this::handleUpdatePlayer);
        view.getDeleteButton().addActionListener(this::handleDeletePlayer);

        view.getHeader().getMinimizeBtn().addActionListener(e ->
                view.getFrame().setState(JFrame.ICONIFIED));
        view.getHeader().getMaximizeBtn().addActionListener(e ->
                toggleMaximize());
        view.getHeader().getCloseBtn().addActionListener(e ->
                System.exit(0));

        view.getBackButton().addActionListener(e -> {
            view.getFrame().dispose();
            DashboardView dashboardView = new DashboardView();
            DashboardModel dashboardModel = new DashboardModel();
            new DashboardController(dashboardModel, dashboardView);
        });
    }

    private void initializeTableData() {
        try {
            AdminService adminService = AdminCorbaManager.getInstance().getAdminService();

            // get list of players from the database
            PlayerAccount[] accounts = adminService.searchPlayerAccounts("");

            // clear data(sample data)
            model.getPlayers().clear();

            // add to the model
            for (PlayerAccount account : accounts) {
                model.addPlayer(new ManagePlayersModel.Player(
                        account.username,
                        account.gamesWon,
                        account.password
                ));
            }

            refreshTable();
        } catch (Exception e) {
            showError("Error loading players: " + e.getMessage());
            // if database didn't work, display this
            model.initializeSampleData();
            refreshTable();
        }
    }

    private void handleAddPlayer(ActionEvent e) {
        PlayerInputDialog dialog = new PlayerInputDialog(view.getFrame(), "Add New Player");
        if (dialog.showDialog()) {
            try {
                ManagePlayersModel.Player player = dialog.getPlayer();
                model.addPlayer(player);
                refreshTable();
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        }
    }

    private void handleUpdatePlayer(ActionEvent e) {
        int selectedRow = view.getTable().getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a player to update.");
            return;
        }

        ManagePlayersModel.Player currentPlayer = model.getPlayer(selectedRow);
        String currentUsername = currentPlayer.getUsername();

        PlayerInputDialog dialog = new PlayerInputDialog(
                view.getFrame(),
                "Update Player",
                currentPlayer.getUsername(),
                String.valueOf(currentPlayer.getWins()),
                currentPlayer.getPassword()
        );

        if (dialog.showDialog()) {
            try {
                ManagePlayersModel.Player updatedPlayer = dialog.getPlayer();
                String newUsername = updatedPlayer.getUsername();
                String newPassword = updatedPlayer.getPassword();

                AdminService adminService = AdminCorbaManager.getInstance().getAdminService();
                adminService.updatePlayerCredentials(currentUsername, newUsername, newPassword);

                model.updatePlayer(selectedRow, updatedPlayer);
                refreshTable();

                JOptionPane.showMessageDialog(
                        view.getFrame(),
                        "Player credentials updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } catch (PlayerNotFoundException ex) {
                showError("Player not found: " + ex.getMessage());
            } catch (PlayerAlreadyExistsException ex) {
                showError("Username already exists: " + ex.getMessage());
            } catch (Exception ex) {
                showError("Error updating player: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void handleDeletePlayer(ActionEvent e) {
        int selectedRow = view.getTable().getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a player to delete.");
            return;
        }

        String username = (String) view.getTable().getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
                view.getFrame(),
                "Are you sure you want to delete player '" + username + "'?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                AdminService adminService = AdminCorbaManager.getInstance().getAdminService();
                adminService.removePlayerAccount(username);

                model.deletePlayer(selectedRow);
                refreshTable();

                JOptionPane.showMessageDialog(
                        view.getFrame(),
                        "Player '" + username + "' was successfully deleted!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } catch (PlayerNotFoundException ex) {
                showError("Player not found in database: " + username);
            } catch (Exception ex) {
                showError("Error deleting player: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void refreshTable() {
        DefaultTableModel tableModel = view.getTableModel();
        tableModel.setRowCount(0); // Clear existing data

        // Get the table model from our data model
        DefaultTableModel modelTable = model.getTableModel();

        // Copy all rows from model to view
        for (int i = 0; i < modelTable.getRowCount(); i++) {
            tableModel.addRow(new Object[]{
                    modelTable.getValueAt(i, 0),  // #
                    modelTable.getValueAt(i, 1),  // Username
                    modelTable.getValueAt(i, 2),  // Wins
                    modelTable.getValueAt(i, 3)   // Password
            });
        }
    }

    private void toggleMaximize() {
        JFrame frame = view.getFrame();
        if (frame.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
            frame.setExtendedState(JFrame.NORMAL);
            view.getHeader().getMaximizeBtn().setText("□");
        } else {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            view.getHeader().getMaximizeBtn().setText("❐");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                view.getFrame(),
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private static class PlayerInputDialog {
        private final JTextField usernameField;
        private final JTextField winsField;
        private final JTextField passwordField;
        private final JFrame parent;
        private final String title;

        public PlayerInputDialog(JFrame parent, String title) {
            this(parent, title, "", "", "");
        }

        public PlayerInputDialog(JFrame parent, String title,
                                 String username, String wins, String password) {
            this.parent = parent;
            this.title = title;
            this.usernameField = new JTextField(username);
            this.winsField = new JTextField(wins);
            this.passwordField = new JPasswordField(password);
        }

        public boolean showDialog() {
            Object[] fields = {
                    "Username:", usernameField,
                    "Password:", passwordField
            };

            int option = JOptionPane.showConfirmDialog(
                    parent,
                    fields,
                    title,
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            return option == JOptionPane.OK_OPTION;
        }

        public ManagePlayersModel.Player getPlayer() throws IllegalArgumentException {
            String username = usernameField.getText().trim();
            String winsText = winsField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || winsText.isEmpty() || password.isEmpty()) {
                throw new IllegalArgumentException("All fields must be filled.");
            }

            try {
                int wins = Integer.parseInt(winsText);
                if (wins < 0) {
                    throw new IllegalArgumentException("Wins must be a non-negative integer.");
                }
                return new ManagePlayersModel.Player(username, wins, password);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Wins must be a valid number.");
            }
        }
    }
}