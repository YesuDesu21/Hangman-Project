package Client_Java.admin.model;

import Client_Java.admin.AdminCorbaManager;

public class DashboardModel {
    private boolean isMaximized = true;
    private String imagePath = "java_server/resources/lettrbox_logo3.jpg";

    public boolean isMaximized() {
        return isMaximized;
    }

    public boolean isSessionValid() {
        String sessionId = AdminCorbaManager.getInstance().getSessionId();
        return AdminCorbaManager.getInstance().getLoginService().isSessionActive(sessionId);
    }

    public void setMaximized(boolean maximized) {
        isMaximized = maximized;
    }

    public String getImagePath() {
        return imagePath;
    }
    //forcommitting
    public void setImagePath(String path) {
        imagePath = path;
    }
}