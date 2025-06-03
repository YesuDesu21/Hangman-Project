package Client_Java.player.controller;

import Client_Java.player.model.HomepageModel;
import Client_Java.player.model.PlayerLoginModel;
import Client_Java.player.view.HomepageView;
import Client_Java.player.view.PlayerLoginView;
import Client_Java.player.view.CustomComponentView;
import Client_Java.player.PlayerCorbaManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PlayerLoginController {
    private PlayerLoginModel model;
    private PlayerLoginView view;
    private boolean isMaximized = true;
    public PlayerLoginController(PlayerLoginModel model, PlayerLoginView view) {
        this.model = model;
        this.view = view;

        // Set up listeners
        this.view.addLoginButtonListener(new LoginButtonListener());
        this.view.addMinimizeButtonListener(new MinimizeButtonListener());
        this.view.addMaximizeButtonListener(new MaximizeButtonListener());
        this.view.addCloseButtonListener(new CloseButtonListener());

        // Set up hover effects
        setupHoverEffects();
    }

    private void setupHoverEffects() {
        // For minimize/maximize buttons
        addHoverEffect(view.getMinimizeButton(), new Color(240, 240, 240));
        addHoverEffect(view.getMaximizeButton(), new Color(240, 240, 240));

        // For close button (special red hover)
        addHoverEffect(view.getCloseButton(), Color.RED, new Color(0xCC0000));

        // For login button
        addLoginButtonHoverEffect();
    }

    private void addHoverEffect(JButton button, Color hoverColor) {
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(PlayerLoginView.MAIN_COLOR);
            }
        });
    }

    private void addHoverEffect(JButton button, Color hoverColor, Color pressedColor) {
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(PlayerLoginView.MAIN_COLOR);
            }

            public void mousePressed(MouseEvent e) {
                button.setBackground(pressedColor);
            }

            public void mouseReleased(MouseEvent e) {
                button.setBackground(hoverColor);
            }
        });
    }


    private void addLoginButtonHoverEffect() {
        JButton loginButton = view.getLoginButton();
        Color mainButtonColor = new Color(0xA08F73);
        Color hoverButtonColor = new Color(0x8A7B63);

        loginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(hoverButtonColor);
                loginButton.setBorder(new CustomComponentView.RoundedBorder(15, hoverButtonColor));
            }

            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(mainButtonColor);
                loginButton.setBorder(new CustomComponentView.RoundedBorder(15, mainButtonColor));
            }
        });
    }

    class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = view.getUsername().trim();
            String password = new String(view.getPassword()).trim();

            model.setUsername(username);
            model.setPassword(password);

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(view.getFrame(),
                        "Please enter both username and password",
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                if (model.verifyPlayerCredentials(username, password)) {
                    if (!model.isSessionValid()) {
                        JOptionPane.showMessageDialog(view.getFrame(),
                                "Your session has expired or you have been logged out elsewhere.",
                                "Session Expired",
                                JOptionPane.WARNING_MESSAGE);
                        System.exit(0); // Or navigate back to login
                    }

                    JOptionPane.showMessageDialog(view.getFrame(),
                            "Login successful!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);

                    view.getFrame().dispose();

                    HomepageView homepageView = new HomepageView();
                    HomepageModel homepageModel = new HomepageModel();
                    homepageModel.setUsername(username);  // <<< Pass username here!
                    new HomepageController(homepageModel, homepageView);

                    System.out.println("showing...");
                    homepageView.show();

                } else {
                    JOptionPane.showMessageDialog(view.getFrame(),
                            "Invalid username or password",
                            "Login Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view.getFrame(),
                        "Error: " + e,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }


    class MinimizeButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.getFrame().setState(JFrame.ICONIFIED);
        }
    }

    class MaximizeButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (isMaximized) {
                view.getFrame().setExtendedState(JFrame.NORMAL);
                view.getFrame().setSize(800, 600);
                view.getFrame().setLocationRelativeTo(null);
            } else {
                view.getFrame().setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
            isMaximized = !isMaximized;
        }
    }

    class CloseButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        PlayerCorbaManager.getInstance(args);

        // Create MVC components
        PlayerLoginModel model = new PlayerLoginModel();
        PlayerLoginView view = new PlayerLoginView();
        new PlayerLoginController(model, view);
    }
    private void showHomepage(String username) {
        HomepageModel homepageModel = new HomepageModel();
        homepageModel.setUsername(username);

        HomepageView homepageView = new HomepageView();

        HomepageController homepageController = new HomepageController(homepageModel, homepageView);

        homepageView.show();
    }
}