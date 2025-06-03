package Server_Java.dao;

import Server_Java.manager.DatabaseManager;
import compilations.InvalidCredentialsException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginServiceDAO {
    private static final Connection connection = DatabaseManager.getConnection();

    // Used by admin login (optional - you may remove if not used)
    public static void validateAdminCredentials(String username, String password, String table) throws InvalidCredentialsException {
        String sql = "SELECT * FROM " + table + " WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new InvalidCredentialsException(); // No match found
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InvalidCredentialsException(); // Treat DB error as invalid login
        }
    }

    //Used by CORBA loginPlayer() — for player login
    public static void validatePlayerCredentials(String username, String password) throws InvalidCredentialsException {
        String sql = "SELECT * FROM players WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new InvalidCredentialsException(); // No match found
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InvalidCredentialsException(); // Treat DB error like invalid login
        }
    }
}