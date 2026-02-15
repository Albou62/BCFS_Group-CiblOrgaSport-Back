package com.ciblorgasport.notifications.serverHandlers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.ciblorgasport.notifications.services.DatabaseService;
import com.ciblorgasport.notifications.services.KafkaConsumerService;
import com.ciblorgasport.notifications.services.KafkaProducerService;
import com.ciblorgasport.notifications.utils.Utils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class NotificationHandler implements HttpHandler {
    DatabaseService dbService = new DatabaseService();

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
            Long groupId = jsonObject.getLong("groupId");
            String label = jsonObject.getString("label");
            String impactLevel = jsonObject.getString("impactLevel");
            List<Long> subscribedUsers = this.dbService.getUsersByGroup(groupId);
            KafkaProducerService producerService = new KafkaProducerService();
            for(Long subscriber : subscribedUsers) {
                System.out.println(subscriber);
                producerService.sendMessage(subscriber.toString(), groupId, label);
            }
            Date curDate = new Date();
            this.dbService.addNotificationToHistory(groupId, label, impactLevel, curDate.toString());
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

    private void handleGetRequest(HttpExchange exchange) {
        try {
            String requestBody = Utils.getRequestBody(exchange);
            JSONObject jsonObject = new JSONObject(requestBody);
            Long userId = jsonObject.getLong("userId");
            
            KafkaConsumerService consumerService = new KafkaConsumerService(userId.toString());
            
            // Wait up to 5 seconds for messages
            JSONArray notifs = consumerService.getNotifs(5000);
            
            byte[] response = notifs.toString().getBytes(StandardCharsets.UTF_8);
            Utils.sendResponse(exchange, response, 200);
            
        } catch(JSONException e) {
            e.printStackTrace();
            byte[] bytes = "Bad request".getBytes(StandardCharsets.UTF_8);
            Utils.sendResponse(exchange, bytes, 400);
        } catch (IOException e) {
            e.printStackTrace();
            byte[] bytes = "Internal server error".getBytes(StandardCharsets.UTF_8);
            Utils.sendResponse(exchange, bytes, 500);
        }
    }
}
