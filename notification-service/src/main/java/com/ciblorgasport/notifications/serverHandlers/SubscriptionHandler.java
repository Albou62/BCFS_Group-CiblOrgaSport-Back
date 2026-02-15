package com.ciblorgasport.notifications.serverHandlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.ciblorgasport.notifications.models.SubscriptionTableRow;
import com.ciblorgasport.notifications.services.DatabaseService;
import com.ciblorgasport.notifications.utils.Utils;

public class SubscriptionHandler implements HttpHandler {
    DatabaseService dbService = new DatabaseService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            handlePostRequest(exchange);
        } else if ("GET".equals(exchange.getRequestMethod())) {
            handleGetRequest(exchange);
        } else if ("DELETE".equals(exchange.getRequestMethod())) {
            handleDeleteRequest(exchange);
        }
    }

    private void handlePostRequest(HttpExchange exchange) {
        try {
            String requestBody = Utils.getRequestBody(exchange);
            JSONObject jsonObject = new JSONObject(requestBody);
            Long groupId = jsonObject.getLong("groupId");
            Long userId = jsonObject.getLong("userId");
            String curTime = new Date().toString();
            this.dbService.insertUserInGroup(userId, groupId, curTime);
            Utils.sendResponse(exchange, "".getBytes(), 200);
        } catch(JSONException e) {
            e.printStackTrace();
            byte[] bytes = "Bad request".getBytes(StandardCharsets.UTF_8);
            Utils.sendResponse(exchange, bytes, 400);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            byte[] bytes = "Internal server error".getBytes(StandardCharsets.UTF_8);
            Utils.sendResponse(exchange, bytes, 500);
        }
    }

    private void handleGetRequest(HttpExchange exchange) {
        try {
            String requestBody = Utils.getRequestBody(exchange);
            JSONObject jsonObject = new JSONObject(requestBody);
            Long userId = jsonObject.getLong("userId");
            List<SubscriptionTableRow> groups = this.dbService.getGroupsByUser(userId);
            JSONArray responseArray = new JSONArray();
            for (SubscriptionTableRow curSub : groups) {
                JSONObject groupJson = curSub.toJson();
                String groupName = this.dbService.getGroupName(curSub.getGroupId());
                groupJson.put("groupName", groupName);
                responseArray.put(groupJson);
            }
            byte[] responseBytes = responseArray.toString().getBytes(StandardCharsets.UTF_8);
            Utils.sendResponse(exchange, responseBytes, 200);
        } catch(JSONException e) {
            e.printStackTrace();
            byte[] bytes = "Bad request".getBytes(StandardCharsets.UTF_8);
            Utils.sendResponse(exchange, bytes, 400);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            byte[] bytes = "Internal server error".getBytes(StandardCharsets.UTF_8);
            Utils.sendResponse(exchange, bytes, 500);
        }
    }

    private void handleDeleteRequest(HttpExchange exchange) {
        try {
            String requestBody = Utils.getRequestBody(exchange);
            JSONObject jsonObject = new JSONObject(requestBody);
            Long userId = jsonObject.getLong("userId");
            Long groupId = jsonObject.getLong("groupId");
            this.dbService.deleteUserFromGroup(groupId, userId);
            byte[] ok = "OK".getBytes();
            Utils.sendResponse(exchange, ok, 200);
        } catch(JSONException e) {
            e.printStackTrace();
            byte[] bytes = "Bad request".getBytes(StandardCharsets.UTF_8);
            Utils.sendResponse(exchange, bytes, 400);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            byte[] bytes = "Internal server error".getBytes(StandardCharsets.UTF_8);
            Utils.sendResponse(exchange, bytes, 500);
        }
    }
}
