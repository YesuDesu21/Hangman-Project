package Client_Java.admin.model;

import Client_Java.admin.AdminCorbaManager;
import compilations.AdminService;

public class TimeConfigModel {
    private AdminService adminService;

    public TimeConfigModel() {
        this.adminService = AdminCorbaManager.getInstance().getAdminService();
    }
    public void saveNewWaitTime(int waitTime){
        adminService.updateGameWaitTime((short) waitTime);
    }
    public void saveNewRoundDuration(int roundDuration){
        adminService.updateRoundDuration((short) roundDuration);
    }
}
