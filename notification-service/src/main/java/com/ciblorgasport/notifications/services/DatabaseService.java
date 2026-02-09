package com.ciblorgasport.notifications.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ciblorgasport.notifications.models.GroupTableRow;
import com.ciblorgasport.notifications.models.SubscriptionTableRow;

public class DatabaseService {
    public static final String SELECT_ALL_GROUPS = "SELECT * FROM groups;";
    public static final String SELECT_GROUP_NAME = "SELECT name FROM groups WHERE id = ?;";
    public static final String SELECT_USER_BY_GROUP  = "SELECT userId FROM abonnements WHERE groupId = ?;";
    public static final String SELECT_GROUPS_BY_USER = "SELECT * FROM abonnements WHERE userId = ?;";
    public static final String INSERT_NEW_GROUP = "INSERT INTO groups (name) VALUES (?);";
    public static final String INSERT_USER_GROUP_ABONNEMENTS = "INSERT INTO abonnements (userId, groupId, timestamp) VALUES (?, ?, ?);";
    public static final String DELETE_USER_FROM_GROUP = "DELETE FROM abonnements WHERE groupId = ? AND userId = ?;";
    public static final String ADD_NOTIFICATION_TO_HISTORY = "INSERT INTO notifs (groupId, label, impactLevel, timestamp) VALUES (?, ?, ?, ?);";

    public static final String CREATE_GROUPS_TABLE = "CREATE TABLE IF NOT EXISTS groups (id SERIAL PRIMARY KEY, name VARCHAR(255));";
    public static final String CREATE_ABONNEMENTS_TABLE = "CREATE TABLE IF NOT EXISTS abonnements (id SERIAL PRIMARY KEY, userId INTEGER, groupId INTEGER, timestamp VARCHAR(255));";
    public static final String CREATE_NOTIFS_TABLE = "CREATE TABLE IF NOT EXISTS notifs (id SERIAL PRIMARY KEY, groupId INTEGER, label VARCHAR(255), impactLevel VARCHAR(255), timestamp VARCHAR(255));";

    private String url;
    private String username;
    private String password;

    public DatabaseService() {
        this.url = System.getenv("DATASOURCE_URL");
        this.username = System.getenv("DATASOURCE_USERNAME");
        this.password = System.getenv("DATASOURCE_PASSWORD");
        try {
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.url, this.username, this.password);
    }

    public void createTables() throws SQLException {
        try (Connection conn = getConnection();
            PreparedStatement cat = conn.prepareStatement(CREATE_ABONNEMENTS_TABLE);
            PreparedStatement cgt = conn.prepareStatement(CREATE_GROUPS_TABLE);
            PreparedStatement cnt = conn.prepareStatement(CREATE_NOTIFS_TABLE)) {
            cat.execute();
            cgt.execute();
            cnt.execute();
        }
    }

    public List<GroupTableRow> getAllGroups() throws SQLException {
        List<GroupTableRow> groups = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_GROUPS)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Long groupId = rs.getLong("id");
                String groupName = rs.getString("name");
                groups.add(new GroupTableRow(groupId, groupName));
            }
        }

        return groups;
    }

    public String getGroupName(Long groupId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_GROUP_NAME)) {

            stmt.setLong(1, groupId);
            ResultSet rs = stmt.executeQuery();
            String groupName = "";

            while (rs.next()) {
                groupName = rs.getString("name");
            }
            return groupName;
        }
    }

    public List<Long> getUsersByGroup(Long groupId) throws SQLException {
        List<Long> users = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_USER_BY_GROUP)) {

            stmt.setLong(1, groupId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                users.add(rs.getLong("userId"));
            }
        }

        return users;
    }

    public List<SubscriptionTableRow> getGroupsByUser(Long userId) throws SQLException {
        List<SubscriptionTableRow> groups = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_GROUPS_BY_USER)) {

            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Long groupId = rs.getLong("groupId");
                String subscriptionDate = rs.getString("timestamp");
                SubscriptionTableRow gtr = new SubscriptionTableRow(groupId, userId, subscriptionDate);
                groups.add(gtr);
            }
        }

        return groups;
    }

    public void insertNewGroup(String groupName) throws SQLException {
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(INSERT_NEW_GROUP)) {

            stmt.setString(1, groupName);
            stmt.execute();
        }
    }

    public void insertUserInGroup(Long userId, Long groupId, String timestamp) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_USER_GROUP_ABONNEMENTS)) {

            stmt.setLong(1, userId);
            stmt.setLong(2, groupId);
            stmt.setString(3, timestamp);
            stmt.execute();
        }
    }

    public void deleteUserFromGroup(Long groupId, Long userId) throws SQLException {
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(DELETE_USER_FROM_GROUP)) {

            stmt.setLong(1, groupId);
            stmt.setLong(2, userId);
            stmt.execute();
        }
    }

    public void addNotificationToHistory(Long groupId, String label, String impactLevel, String date) throws SQLException {
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(ADD_NOTIFICATION_TO_HISTORY)) {
            
            stmt.setLong(1, groupId);
            stmt.setString(2, label);
            stmt.setString(3, impactLevel);
            stmt.setString(4, date);

            stmt.execute();
        }
    }
}
