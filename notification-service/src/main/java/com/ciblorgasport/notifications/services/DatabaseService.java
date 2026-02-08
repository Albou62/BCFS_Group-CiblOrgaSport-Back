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
    public static final String SELECT_ALL_GROUPS = "SELECT * FROM groups";
    public static final String SELECT_GROUP_NAME = "SELECT groupName FROM groups WHERE groupId == ?;";
    public static final String SELECT_USER_BY_GROUP  = "SELECT userId FROM abonnements WHERE groupId == ?;";
    public static final String SELECT_GROUPS_BY_USER = "SELECT * FROM abonnements WHERE userId == ?;";
    public static final String INSERT_NEW_GROUP = "INSERT INTO groups VALUES (?, ?)";
    public static final String INSERT_USER_GROUP_ABONNEMENTS = "INSERT INTO abonnements VALUES (?, ?, ?);";
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
             PreparedStatement stmt = conn.prepareStatement(SELECT_USER_BY_GROUP)) {

            stmt.setLong(1, groupId);
            ResultSet rs = stmt.executeQuery();
            String groupName = "";

            while (rs.next()) {
                groupName = rs.getString("groupName");
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
                String subscriptionDate = rs.getString("subscriptionDate");
                SubscriptionTableRow gtr = new SubscriptionTableRow(groupId, userId, subscriptionDate);
                groups.add(gtr);
            }
        }

        return groups;
    }

    public void insertNewGroup(Long groupId, String groupName) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_NEW_GROUP)) {

            stmt.setLong(1, groupId);
            stmt.setString(2, groupName);
            stmt.executeQuery();
        }
    }

    public void insertUserInGroup(Long userId, Long groupId, String timestamp) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_USER_GROUP_ABONNEMENTS)) {

            stmt.setLong(1, userId);
            stmt.setLong(2, groupId);
            stmt.setString(3, timestamp);
            stmt.executeQuery();
        }
    }

    public void deleteUserFromGroup(Long groupId, Long userId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_USER_FROM_GROUP)) {

            stmt.setLong(1, groupId);
            stmt.setLong(2, userId);
            stmt.executeQuery();
        }
    }
}
