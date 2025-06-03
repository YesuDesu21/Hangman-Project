package Client_Java.player;

import Server_Java.manager.DatabaseManager;
import compilations.*;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

public class PlayerCorbaManager {
    private static PlayerCorbaManager instance;
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

    private PlayerCorbaManager(String[] orbArgs) {
        try {
            Properties orbProps = new Properties();
            try (InputStream input = PlayerCorbaManager.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (input != null) {
                    orbProps.load(input);
                } else {
                    System.err.println("PlayerCorbaManager: config.properties not found. Using default ORB settings.");
                }
            } catch (Exception e) {
                System.err.println("PlayerCorbaManager: Failed to load config.properties: " + e.getMessage());
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

            System.out.println("PlayerCORBAManager: Connected to all services.");
        } catch (Exception e) {
            System.err.println("PlayerCORBAManager: Failed to connect to services.");
            e.printStackTrace();
        }
    }

    // First-time initializer
    public static PlayerCorbaManager getInstance(String[] orbArgs) {
        if (instance == null) {
            synchronized (PlayerCorbaManager.class) {
                if (instance == null) {
                    instance = new PlayerCorbaManager(orbArgs);
                }
            }
        }
        return instance;
    }

    // Getter (after initialized)
    public static PlayerCorbaManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("CORBAConnector not initialized. Call getInstance(String[] args) first.");
        }
        return instance;
    }

    public Connection getDbConnection() {
        return connection;
    }

    private String currentPlayerUsername;
    private String sessionId;

    public String getCurrentPlayerUsername() {
        return currentPlayerUsername;
    }

    public void setCurrentPlayerUsername(String username) {
        this.currentPlayerUsername = username;
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

    /**
     * Re-establishes CORBA connection and reinitializes all services
     */
    public synchronized void reconnect(String[] orbArgs) {
        try {
            System.out.println("PlayerCORBAManager: Attempting to reconnect CORBA services...");

            // Reinitialize ORB with provided parameters
            orb = ORB.init(orbArgs, null);

            // Re-resolve naming service
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            namingContext = NamingContextExtHelper.narrow(objRef);

            // Reconnect to all services
            loginService = LoginServiceHelper.narrow(namingContext.resolve_str("LoginService"));
            gameService = GameServiceHelper.narrow(namingContext.resolve_str("GameService"));
            adminService = AdminServiceHelper.narrow(namingContext.resolve_str("AdminService"));
            leaderboardService = LeaderboardServiceHelper.narrow(namingContext.resolve_str("LeaderboardService"));
            wordService = WordServiceHelper.narrow(namingContext.resolve_str("WordService"));
            gameManagerService = GameManagerServiceHelper.narrow(namingContext.resolve_str("GameManagerService"));

            System.out.println("PlayerCORBAManager: CORBA services reconnected successfully");
        } catch (Exception e) {
            System.err.println("PlayerCORBAManager: CORBA reconnection failed: " + e);
            e.printStackTrace();
        }
    }

    /**
     * Force a fresh initialization of all services
     * @param orbArgs ORB initialization arguments
     */
    public synchronized void reinitialize(String[] orbArgs) {
        try {
            System.out.println("PlayerCORBAManager: Performing full CORBA reinitialization...");

            // Shutdown existing ORB if present
            if (orb != null) {
                try {
                    orb.shutdown(false);
                } catch (Exception e) {
                    System.err.println("PlayerCORBAManager: Error shutting down existing ORB: " + e);
                }
            }

            // Create completely new instance
            instance = new PlayerCorbaManager(orbArgs);
            System.out.println("PlayerCORBAManager: CORBA services fully reinitialized");
        } catch (Exception e) {
            System.err.println("PlayerCORBAManager: Full reinitialization failed: " + e);
            e.printStackTrace();
        }
    }
}