package com.ciblorgasport.notifications.serverHandlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
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
            String groupId = jsonObject.getString("groupId");
            String userId = jsonObject.getString("userId");
            // Add subscription to database
        } catch(JSONException e) {
            byte[] bytes = "Bad request".getBytes(StandardCharsets.UTF_8);
            Utils.sendErrorResponse(exchange, bytes, 400);
        } catch (IOException e) {
            e.printStackTrace();
            byte[] bytes = "Internal server error".getBytes(StandardCharsets.UTF_8);
            Utils.sendErrorResponse(exchange, bytes, 500);
        }
    }

    private void handleGetRequest(HttpExchange exchange) {
        try {
            String requestBody = Utils.getRequestBody(exchange);
            JSONObject jsonObject = new JSONObject(requestBody);
            String userId = jsonObject.getString("userId");
            List<String> groups = this.dbService.getGroupsByUser(userId);
            JSONArray responseArray = new JSONArray();
            for (String groupJson : groups) {
                responseArray.put(new JSONObject(groupJson));
            }
            byte[] responseBytes = responseArray.toString().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, responseBytes.length);
            exchange.getResponseBody().write(responseBytes);
            exchange.close();
        } catch(JSONException e) {
            byte[] bytes = "Bad request".getBytes(StandardCharsets.UTF_8);
            Utils.sendErrorResponse(exchange, bytes, 400);
        } catch (IOException | SQLException e) {
            byte[] bytes = "Internal server error".getBytes(StandardCharsets.UTF_8);
            Utils.sendErrorResponse(exchange, bytes, 500);
        }
    }

    private void handleDeleteRequest(HttpExchange exchange) {
        try {
            String requestBody = Utils.getRequestBody(exchange);
            JSONObject jsonObject = new JSONObject(requestBody);
            String userId = jsonObject.getString("userId");
            String groupId = jsonObject.getString("groupId");
            // Delete subscriptions in database
        } catch(JSONException e) {
            byte[] bytes = "Bad request".getBytes(StandardCharsets.UTF_8);
            Utils.sendErrorResponse(exchange, bytes, 400);
        } catch (IOException e) {
            byte[] bytes = "Internal server error".getBytes(StandardCharsets.UTF_8);
            Utils.sendErrorResponse(exchange, bytes, 500);
        }
    }
}
