package com.ciblorgasport.geolocalisation.services;

import com.ciblorgasport.geolocalisation.models.UserLocation;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseService {

    private Connection connect() throws SQLException {
        String url = System.getenv().getOrDefault("JDBC_DATABASE_URL", "jdbc:postgresql://localhost:5432/postgres");
        String user = System.getenv().getOrDefault("JDBC_DATABASE_USERNAME", "postgres");
        String password = System.getenv().getOrDefault("JDBC_DATABASE_PASSWORD", "postgres");
        return DriverManager.getConnection(url, user, password);
    }

    public void saveUserLocation(UserLocation location) {
        String sql = "INSERT INTO user_localisation(user_id, latitude, longitude, altitude) VALUES(?,?,?,?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, location.getUserId());
            pstmt.setDouble(2, location.getLatitude());
            pstmt.setDouble(3, location.getLongitude());
            if (location.getAltitude() != null) {
                pstmt.setDouble(4, location.getAltitude());
            } else {
                pstmt.setNull(4, java.sql.Types.DOUBLE);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public UserLocation getUserLocation(String userId) {
        String sql = "SELECT id, user_id, latitude, longitude, altitude, timestamp FROM user_localisation WHERE user_id = ? ORDER BY timestamp DESC LIMIT 1";
        UserLocation location = null;

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                location = new UserLocation();
                location.setUserId(rs.getString("user_id"));
                location.setLatitude(rs.getDouble("latitude"));
                location.setLongitude(rs.getDouble("longitude"));
                location.setAltitude(rs.getDouble("altitude"));
                location.setTimestamp(rs.getTimestamp("timestamp"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return location;
    }
}
