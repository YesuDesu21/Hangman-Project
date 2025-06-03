package Client_Java.admin.controller;

import Client_Java.admin.model.AddPlayerModel;
import Client_Java.admin.view.AddPlayerView;
import Client_Java.admin.view.ManagePlayersView;
import Client_Java.admin.model.ManagePlayersModel;
import Client_Java.admin.AdminCorbaManager;
import compilations.PlayerAlreadyExistsException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class AddPlayerController {
    private final AddPlayerModel model;
    private final AddPlayerView view;
    private boolean isMaximized = true;

    public AddPlayerController(AddPlayerModel model, AddPlayerView view) {
        this.model = model;
        this.view = view;
        initController();
        setupBackButton();
    }

    private void initController() {
        setupWindowControls();
        setupAddButton();
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

    private void setupAddButton() {
        view.getAddButton().addActionListener(new AddButtonListener());
        setupButtonHoverEffects();
    }

    private void setupButtonHoverEffects() {
        Color BUTTON_BROWN = new Color(0xA08F73);
        Color BUTTON_BROWN_HOVER = new Color(0x8A7B63);

        view.getAddButton().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                view.getAddButton().setBackground(BUTTON_BROWN_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                view.getAddButton().setBackground(BUTTON_BROWN);
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

    private void setupBackButton() {
        view.getBackButton().addActionListener(e -> {
            view.getFrame().dispose();
            new ManagePlayersController(new ManagePlayersView(), new ManagePlayersModel());
        });

        Color BUTTON_BROWN = new Color(0xA08F73);
        Color BUTTON_BROWN_HOVER = new Color(0x8A7B63);

        view.getBackButton().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                view.getBackButton().setBackground(BUTTON_BROWN_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                view.getBackButton().setBackground(BUTTON_BROWN);
            }
        });
    }

    private class AddButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = view.getUsername().trim();
            char[] passwordChars = view.getPassword().toCharArray();

            try {
                if (username.isEmpty() || passwordChars.length == 0) {
                    showError("Please fill in all fields.");
                    return;
                }

                if (passwordChars.length < 8) {
                    showError("Password must be at least 8 characters long.");
                    return;
                }

                String password = new String(passwordChars);
                savePlayer(username, password);
            } finally {
                Arrays.fill(passwordChars, '\0');
            }
        }

        private void showError(String message) {
            JOptionPane.showMessageDialog(
                    view.getFrame(),
                    message,
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        private void savePlayer(String username, String password) {
            try {
                AdminCorbaManager.getInstance().getAdminService().createPlayerAccount(username, password);
                JOptionPane.showMessageDialog(
                        view.getFrame(),
                        "Player added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
                view.getFrame().dispose();
                new ManagePlayersController(new ManagePlayersView(), new ManagePlayersModel());
            } catch (PlayerAlreadyExistsException e) {
                showError("Player already exists. Please choose a different username.");
            } catch (Exception e) {
                showError("An error occurred while adding the player.");
            }
        }
    }
}