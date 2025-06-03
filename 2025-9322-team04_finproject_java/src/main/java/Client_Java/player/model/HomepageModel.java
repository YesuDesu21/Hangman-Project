package Client_Java.player.model;

import Client_Java.player.PlayerCorbaManager;

public class HomepageModel {
    private String username;
    private int waitingTime;

    public HomepageModel() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public boolean isSessionValid() {
        String sessionId = PlayerCorbaManager.getInstance().getSessionId();
        return PlayerCorbaManager.getInstance().getLoginService().isSessionActive(sessionId);
    }
}
