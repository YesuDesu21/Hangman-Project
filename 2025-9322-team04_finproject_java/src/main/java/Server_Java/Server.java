package Server_Java;

import Server_Java.implementation.*;
import compilations.*;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class Server {
    public static void main(String[] args) {
        try {
            Properties orbProps = new Properties();
            try (InputStream inputStream = Server.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (inputStream != null) {
                    orbProps.load(inputStream);
                } else {
                    log("File config.properties not found. Default ORB settings will be used.");
                }
            }

            ORB orb = ORB.init(args, orbProps);
            POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootPOA.the_POAManager().activate();

            WordServiceImpl wordImpl = new WordServiceImpl();
            org.omg.CORBA.Object wordRef = rootPOA.servant_to_reference(wordImpl);
            WordService wordService = WordServiceHelper.narrow(wordRef);

            GameServiceImpl gameImpl = new GameServiceImpl(wordService);
            org.omg.CORBA.Object gameRef = rootPOA.servant_to_reference(gameImpl);
            GameService gameService = GameServiceHelper.narrow(gameRef);

            LoginServiceImpl loginImpl = new LoginServiceImpl();
            org.omg.CORBA.Object loginRef = rootPOA.servant_to_reference(loginImpl);
            LoginService loginService = LoginServiceHelper.narrow(loginRef);

            AdminServiceImpl adminImpl = new AdminServiceImpl();
            org.omg.CORBA.Object adminRef = rootPOA.servant_to_reference(adminImpl);
            AdminService adminService = AdminServiceHelper.narrow(adminRef);

            LeaderboardServiceImpl leaderboardImpl = new LeaderboardServiceImpl();
            org.omg.CORBA.Object leaderboardRef = rootPOA.servant_to_reference(leaderboardImpl);
            LeaderboardService leaderboardService = LeaderboardServiceHelper.narrow(leaderboardRef);

            GameManagerServiceImpl gameManagerImpl = new GameManagerServiceImpl();
            org.omg.CORBA.Object gameManagerRef = rootPOA.servant_to_reference(gameManagerImpl);
            GameManagerService gameManagerService = GameManagerServiceHelper.narrow(gameManagerRef);

            // === Bind Services to Naming Service ===
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt namingContext = NamingContextExtHelper.narrow(objRef);

            namingContext.rebind(namingContext.to_name("LoginService"), loginService);
            namingContext.rebind(namingContext.to_name("GameService"), gameService);
            namingContext.rebind(namingContext.to_name("AdminService"), adminService);
            namingContext.rebind(namingContext.to_name("LeaderboardService"), leaderboardService);
            namingContext.rebind(namingContext.to_name("WordService"), wordService);
            namingContext.rebind(namingContext.to_name("GameManagerService"), gameManagerService);

            log("The Lettrbox Server is now running at " + getLocalIpAddress());
            orb.run();

        } catch (Exception e) {
            log("ERROR: " + e);
            e.printStackTrace();
        }

        log("The Lettrbox Server is now exiting...");
    }

    public static String getLocalIpAddress() {
        try {
            InetAddress localHost = null;
            for (NetworkInterface ni : java.util.Collections.list(NetworkInterface.getNetworkInterfaces())) {
                for (InetAddress address : java.util.Collections.list(ni.getInetAddresses())) {
                    if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
                        localHost = address;
                        break;
                    }
                }
                if (localHost != null) break;
            }
            return (localHost != null) ? localHost.getHostAddress() : "IP address not found.";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public static void log(String message) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("[" + timestamp + "] [SERVER] " + message);
    }
}
