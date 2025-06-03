package Client_Java.admin;

import Server_Java.manager.DatabaseManager;
import compilations.*;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

public class AdminCorbaManager {
    private static AdminCorbaManager instance;
    private static Connection connection;

    private ORB orb;
    private NamingContextExt namingContext;

    // Service references
    private LoginService loginService;
    private GameService gameService;
    private AdminService adminService;
    private LeaderboardService leaderboardService;
    private WordService wordService;
    private GameManagerService gameManagerService;

    private AdminCorbaManager(String[] orbArgs) {
        try {
            // Initialize ORB
            Properties orbProps = new Properties();
            try (InputStream input = AdminCorbaManager.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (input != null) {
                    orbProps.load(input);
                } else {
                    System.err.println("AdminCorbaManager: config.properties not found. Using default ORB settings.");
                }
            } catch (Exception e) {
                System.err.println("AdminCorbaManager: Failed to load config.properties: " + e.getMessage());
            }

            orb = ORB.init(orbArgs, orbProps);

            // Resolve naming service
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            namingContext = NamingContextExtHelper.narrow(objRef);

            // Connect to services
            loginService = LoginServiceHelper.narrow(namingContext.resolve_str("LoginService"));
            gameService = GameServiceHelper.narrow(namingContext.resolve_str("GameService"));
            adminService = AdminServiceHelper.narrow(namingContext.resolve_str("AdminService"));
            leaderboardService = LeaderboardServiceHelper.narrow(namingContext.resolve_str("LeaderboardService"));
            wordService = WordServiceHelper.narrow(namingContext.resolve_str("WordService"));
            gameManagerService = GameManagerServiceHelper.narrow(namingContext.resolve_str("GameManagerService"));
            // Database connection
            connection = DatabaseManager.getConnection();

            System.out.println("AdminCORBAManager: Connected to all services.");
        } catch (Exception e) {
            System.err.println("AdminCORBAManager: Failed to connect to services.");
            e.printStackTrace();
        }
    }

    // First-time initializer
    public static AdminCorbaManager getInstance(String[] orbArgs) {
        if (instance == null) {
            synchronized (AdminCorbaManager.class) {
                if (instance == null) {
                    instance = new AdminCorbaManager(orbArgs);
                }
            }
        }
        return instance;
    }

    // Getter (after initialized)
    public static AdminCorbaManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("CORBAConnector not initialized. Call getInstance(String[] args) first.");
        }
        return instance;
    }

    public Connection getDbConnection() {
        return connection;
    }

    private String currentAdminUsername;
    private String sessionId;

    public String getCurrentAdminUsername() {
        return currentAdminUsername;
    }

    public void setCurrentAdminUsername(String username) {
        this.currentAdminUsername = username;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }


    // Service accessors
    public LoginService getLoginService() {
        return loginService;
    }

    public GameService getGameService() {
        return gameService;
    }

    public AdminService getAdminService() {
        return adminService;
    }

    public LeaderboardService getLeaderboardService() {
        return leaderboardService;
    }

    public WordService getWordService() {
        return wordService;
    }

    public GameManagerService getGameManagerService() {
        return gameManagerService;
    }
}
