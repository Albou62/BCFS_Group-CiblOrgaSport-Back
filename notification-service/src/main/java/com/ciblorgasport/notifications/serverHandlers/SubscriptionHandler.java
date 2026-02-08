package main.java.com.ciblorgasport.notifications.serverHandlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;

import main.java.com.ciblorgasport.notifications.utils.Utils;

public class SubscriptionHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            handleOptions(exchange);
        } else if ("POST".equals(exchange.getRequestMethod())) {
            handlePostRequest(exchange);
        } else if ("GET".equals(exchange.getRequestMethod())) {
            handleGetRequest(exchange);
        } else if ("DELETE".equals(exchange.getRequestMethod())) {
            handleDeleteRequest(exchange);
        }
    }

    private void handlePostRequest(HttpExchange exchange) {
        String requestBody = Utils.getRequestBody(exchange);
        JSONObject jsonObject = new JSONObject(requestBody);
        try {
            String groupId = jsonObject.getString("groupId");
            String userId = jsonObject.getString("userId");
            // Add subscription to database
        } catch(JSONException e) {
            byte[] bytes = "Bad request".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(400, bytes);
            exchange.getResponseBody().write(bytes);
            exchange.getResponseBody().close();
        }
    }

    private void handleGetRequest(HttpExchange exchange) {
        String requestBody = Utils.getRequestBody(exchange);
        JSONObject jsonObject = new JSONObject(requestBody);
        try {
            String userId = jsonObject.getString("userId");
            // Check Database for subscriptions
        } catch(JSONException e) {
            byte[] bytes = "Bad request".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(400, bytes);
            exchange.getResponseBody().write(bytes);
            exchange.getResponseBody().close();
        }
    }

    private void handleDeleteRequest(HttpExchange exchange) {
        String requestBody = Utils.getRequestBody(exchange);
        JSONObject jsonObject = new JSONObject(requestBody);
        try {
            String userId = jsonObject.getString("userId");
            String groupId = jsonObject.getString("groupId");
            // Delete subscriptions in database
        } catch(JSONException e) {
            byte[] bytes = "Bad request".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(400, bytes);
            exchange.getResponseBody().write(bytes);
            exchange.getResponseBody().close();
        }
    }
}
