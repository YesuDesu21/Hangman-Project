package Client_Java.admin.controller;

import Client_Java.admin.model.AdminLoginModel;
import Client_Java.admin.model.DashboardModel;
import Client_Java.admin.view.AdminLoginView;
import Client_Java.admin.view.CustomComponentView;
import Client_Java.admin.view.DashboardView;
import Client_Java.admin.AdminCorbaManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdminLoginController {
    private AdminLoginModel model;
    private AdminLoginView view;
    private boolean isMaximized = true;

    public AdminLoginController(AdminLoginModel model, AdminLoginView view) {
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
                button.setBackground(AdminLoginView.MAIN_COLOR);
            }
        });
    }

    private void addHoverEffect(JButton button, Color hoverColor, Color pressedColor) {
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(AdminLoginView.MAIN_COLOR);
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
                if (model.verifyAdminCredentials(username, password)) {
                    if (!model.isSessionValid()) {
                        JOptionPane.showMessageDialog(view.getFrame(),
                                "Your session has expired or you have been logged out elsewhere.",
                                "Session Expired",
                                JOptionPane.WARNING_MESSAGE);
                        System.exit(0);
                    }

                    JOptionPane.showMessageDialog(view.getFrame(),
                            "Login successful!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);


                    view.getFrame().dispose();

                    DashboardView dashboardView = new DashboardView();
                    DashboardModel dashboardModel = new DashboardModel();
                    new DashboardController(dashboardModel, dashboardView);

                } else {
                    JOptionPane.showMessageDialog(view.getFrame(),
                            "Invalid username or password",
                            "Login Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view.getFrame(),
                        "Login error. Connection failed.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
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
        AdminCorbaManager.getInstance(args);

        // Create MVC components
        AdminLoginModel model = new AdminLoginModel();
        AdminLoginView view = new AdminLoginView();
        new AdminLoginController(model, view);
    }
}
