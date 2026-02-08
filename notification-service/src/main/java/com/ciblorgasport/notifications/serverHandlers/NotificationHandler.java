package main.java.com.ciblorgasport.notifications.serverHandlers;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import main.java.com.ciblorgasport.notifications.utils.Utils;

import com.sun.net.httpserver.HttpExchange;

public class NotificationHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            handleOptions(exchange);
        } else if ("POST".equals(exchange.getRequestMethod())) {
            handlePostRequest(exchange);
        } else if ("GET".equals(exchange.getRequestMethod())) {
            handleGetRequest(exchange);
        }
    }

    private void handlePostRequest(HttpExchange exchange) {
        String requestBody = Utils.getRequestBody(exchange);
        JSONObject jsonObject = new JSONObject(requestBody);
        try {
            String groupId = jsonObject.getString("groupId");
            String label = jsonObject.getString("label");
            String impactLevel = jsonObject.getString("impactLevel");
            // Get all subscribers in database
            // Kafka Producer Operation
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
            // Kafka Consumer Operation
        } catch(JSONException e) {
            byte[] bytes = "Bad request".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(400, bytes);
            exchange.getResponseBody().write(bytes);
            exchange.getResponseBody().close();
        }
    }
}
