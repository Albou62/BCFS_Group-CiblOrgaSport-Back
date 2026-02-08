package com.ciblorgasport.notifications.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {
    public static final String SELECT_GROUP_NAME = "SELECT groupName FROM notification_groups WHERE groupId == ?;";
    public static final String SELECT_USER_BY_GROUP  = "SELECT userId FROM abonnements WHERE groupId == ?;";
    public static final String SELECT_GROUPS_BY_USER = "SELECT * FROM abonnements WHERE userId == ?;";
    public static final String INSERT_USER_IN_GROUP = "INSERT INTO abonnements VALUES (?, ?, ?);";
    public static final String DELETE_USER_FROM_GROUP = "DELETE FROM abonnements WHERE groupId == ? AND userId == ?;";

    private String url;
    private String username;
    private String password;

    public DatabaseService() {
        this.url = System.getenv("DATASOURCE_URL");
        this.username = System.getenv("DATASOURCE_USERNAME");
        this.password = System.getenv("DATASOURCE_PASSWORD");
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.url, this.username, this.password);
    }

    public String getGroupName(String groupId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_USER_BY_GROUP)) {

            stmt.setString(1, groupId);
            ResultSet rs = stmt.executeQuery();
            String groupName = "";

            while (rs.next()) {
                groupName = rs.getString("groupName");
            }
            return groupName;
        }
    }

    public List<String> getUsersByGroup(String groupId) throws SQLException {
        List<String> users = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_USER_BY_GROUP)) {

            stmt.setString(1, groupId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                users.add(rs.getString("userId"));
            }
        }

        return users;
    }

    public List<String> getGroupsByUser(String userId) throws SQLException {
        List<String> groups = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_GROUPS_BY_USER)) {

            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String groupName = rs.getString("groupName");
                String groupId = rs.getString("groupId");
                String subscriptionDate = rs.getString("subscriptionDate");
                String jsonObject = "{ \"groupId\": \"%s\",  \"subscriptionDate\": \"%s\" }";
                groups.add(String.format(jsonObject, groupName, groupId, subscriptionDate));
            }
        }

        return groups;
    }

    public void insertUserInGroup(String userId, String groupId, String groupName, String timestamp) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_GROUPS_BY_USER)) {

            stmt.setString(1, userId);
            stmt.setString(2, groupId);
            stmt.setString(3, groupName);
            stmt.setString(4, timestamp);
            stmt.executeQuery();
        }
    }

    public void deleteUserFromGroup(String groupId, String userId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_USER_FROM_GROUP)) {

            stmt.setString(1, groupId);
            stmt.setString(2, userId);
            stmt.executeQuery();
        }
    }
}
