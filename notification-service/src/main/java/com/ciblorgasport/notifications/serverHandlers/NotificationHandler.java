package com.ciblorgasport.notifications.serverHandlers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import com.ciblorgasport.notifications.utils.Utils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class NotificationHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            handlePostRequest(exchange);
        } else if ("GET".equals(exchange.getRequestMethod())) {
            handleGetRequest(exchange);
        }
    }

    private void handlePostRequest(HttpExchange exchange) {
        try {
            String requestBody = Utils.getRequestBody(exchange);
            JSONObject jsonObject = new JSONObject(requestBody);
            String groupId = jsonObject.getString("groupId");
            String label = jsonObject.getString("label");
            String impactLevel = jsonObject.getString("impactLevel");
            // Get all subscribers in database
            // Kafka Producer Operation
        } catch(JSONException e) {
            byte[] bytes = "Bad request".getBytes(StandardCharsets.UTF_8);
            Utils.sendErrorResponse(exchange, bytes, 400);
        } catch (IOException e) {
            byte[] bytes = "Internal server error".getBytes(StandardCharsets.UTF_8);
            Utils.sendErrorResponse(exchange, bytes, 500);
        }
    }

    private void handleGetRequest(HttpExchange exchange) {
        try {
            String requestBody = Utils.getRequestBody(exchange);
            JSONObject jsonObject = new JSONObject(requestBody);
            String userId = jsonObject.getString("userId");
            // Kafka Consumer Operation
        } catch(JSONException e) {
            byte[] bytes = "Bad request".getBytes(StandardCharsets.UTF_8);
            Utils.sendErrorResponse(exchange, bytes, 400);
        } catch (IOException e) {
            byte[] bytes = "Internal server error".getBytes(StandardCharsets.UTF_8);
            Utils.sendErrorResponse(exchange, bytes, 500);
        }
    }
}
