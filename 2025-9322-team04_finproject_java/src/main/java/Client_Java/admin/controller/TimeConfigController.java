package Client_Java.admin.controller;

import Client_Java.admin.model.DashboardModel;
import Client_Java.admin.model.TimeConfigModel;
import Client_Java.admin.view.DashboardView;
import Client_Java.admin.view.TimeConfigView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TimeConfigController {
    private final TimeConfigView view;
    private TimeConfigModel model;
    private final int DEFAULT_WAIT_TIME = 10;
    private final int DEFAULT_ROUND_DURATION = 30;

    public TimeConfigController(TimeConfigView view, TimeConfigModel model) {
        this.view = view;
        this.model = model;
        initController();
    }

    private void initController() {
        // Main functional buttons
        view.getDefaultButton().addActionListener(new DefaultButtonListener());
        view.getBackButton().addActionListener(new BackButtonListener());
        view.getSaveButton().addActionListener(new SaveButtonListener());

        // Window control from HeaderTemplate
        view.getHeader().getMinimizeBtn().addActionListener(e ->
                view.setExtendedState(JFrame.ICONIFIED));
        view.getHeader().getMaximizeBtn().addActionListener(e ->
                toggleMaximize());
        view.getHeader().getCloseBtn().addActionListener(e ->
                System.exit(0));
    }

    private void toggleMaximize() {
        if (view.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
            view.setExtendedState(JFrame.NORMAL);
            view.getHeader().getMaximizeBtn().setText("□");
        } else {
            view.setExtendedState(JFrame.MAXIMIZED_BOTH);
            view.getHeader().getMaximizeBtn().setText("❐");
        }
    }

    private class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int waitTime = Integer.parseInt(view.getJoinWaitField().getText().trim());
                int roundDuration = Integer.parseInt(view.getRoundDurationField().getText().trim());

                if (waitTime <= 0 || roundDuration <= 0) {
                    throw new NumberFormatException();
                }

                model.saveNewRoundDuration(roundDuration);
                model.saveNewWaitTime(waitTime);

                JOptionPane.showMessageDialog(view,
                        "Settings saved successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(view,
                        "Please enter valid positive integers for both fields.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class BackButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.dispose();
            // Return to dashboard
            new DashboardController(new DashboardModel(), new DashboardView());
        }
    }

    private class DefaultButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            model.saveNewRoundDuration(DEFAULT_ROUND_DURATION);
            model.saveNewWaitTime(DEFAULT_WAIT_TIME);

            view.getJoinWaitField().setText(String.valueOf(DEFAULT_WAIT_TIME));
            view.getRoundDurationField().setText(String.valueOf(DEFAULT_ROUND_DURATION));

            JOptionPane.showMessageDialog(view,
                    "Settings reset to default values",
                    "Defaults Restored",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}