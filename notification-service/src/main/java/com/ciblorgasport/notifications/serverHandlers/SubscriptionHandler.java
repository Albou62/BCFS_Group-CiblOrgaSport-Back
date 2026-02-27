package com.ciblorgasport.notifications.serverHandlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.ciblorgasport.notifications.models.SubscriptionTableRow;
import com.ciblorgasport.notifications.services.DatabaseService;
import com.ciblorgasport.notifications.utils.Utils;

public class SubscriptionHandler implements HttpHandler {
    private final DatabaseService dbService;

    public SubscriptionHandler() {
        this(new DatabaseService());
    }

    public SubscriptionHandler(DatabaseService dbService) {
        this.dbService = dbService;
    }

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
            boolean created = this.dbService.insertUserInGroupIfNotExists(userId, groupId, curTime);
            int status = created ? 201 : 204;
            Utils.sendResponse(exchange, "".getBytes(), status);
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
            // DON'T try to read the request body for GET requests!
            // String requestBody = Utils.getRequestBody(exchange); // REMOVE THIS LINE
            
            // Parse query parameters from the URL
            String query = exchange.getRequestURI().getQuery();
            if (query == null || query.isEmpty()) {
                Utils.sendResponse(exchange, "Missing query parameters".getBytes(StandardCharsets.UTF_8), 400);
                return;
            }
            
            Map<String, String> params = Utils.parseQueryParams(query);
            String userIdStr = params.get("userId");
            
            if (userIdStr == null) {
                Utils.sendResponse(exchange, "Missing userId parameter".getBytes(StandardCharsets.UTF_8), 400);
                return;
            }
            
            Long userId;
            try {
                userId = Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                Utils.sendResponse(exchange, "Invalid userId format".getBytes(StandardCharsets.UTF_8), 400);
                return;
            }
            
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
            
        } catch (SQLException e) {
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
