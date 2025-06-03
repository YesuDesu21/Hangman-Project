package Client_Java.player.model;

public class WaitingRoomModel {
    private final String username;
    private int countdownTime;

    public WaitingRoomModel(String username, int initialCountdownTime) {
        this.username = username;
        this.countdownTime = initialCountdownTime;
    }

    public String getUsername() {
        return username;
    }

    public int getCountdownTime() {
        return countdownTime;
    }

    public void setCountdownTime(int countdownTime) {
        this.countdownTime = countdownTime;
    }

    public void decrementCountdownTime() {
        if (countdownTime > 0) {
            countdownTime--;
        }
    }
}
