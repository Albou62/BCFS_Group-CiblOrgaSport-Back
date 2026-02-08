package com.ciblorgasport.notifications.serverHandlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ciblorgasport.notifications.models.GroupTableRow;
import com.ciblorgasport.notifications.services.DatabaseService;
import com.ciblorgasport.notifications.utils.Utils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class GroupHandler implements HttpHandler {
    DatabaseService dbService = new DatabaseService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            handlePostRequest(exchange);
        } else if ("GET".equals(exchange.getRequestMethod())) {
            handleGetRequest(exchange);
        }
    }

    public void handlePostRequest(HttpExchange exchange) {
        try {
            String requestBody = Utils.getRequestBody(exchange);
            JSONObject jsonObject = new JSONObject(requestBody);
            Long groupId = jsonObject.getLong("id");
            String groupName = jsonObject.getString("name");
            this.dbService.insertNewGroup(groupId, groupName);
            Utils.sendResponse(exchange, "".getBytes(), 200);
        } catch(JSONException e) {
            byte[] bytes = "Bad request".getBytes(StandardCharsets.UTF_8);
            Utils.sendResponse(exchange, bytes, 400);
        } catch (IOException | SQLException e) {
            byte[] bytes = "Internal server error".getBytes(StandardCharsets.UTF_8);
            Utils.sendResponse(exchange, bytes, 500);
        }
    }

    public void handleGetRequest(HttpExchange exchange) {
        try {
            List<GroupTableRow> gtrs = this.dbService.getAllGroups();
            JSONArray arr = new JSONArray();
            for (GroupTableRow gtr : gtrs) {
                JSONObject gtrJson = gtr.toJson();
                arr.put(gtrJson);
            }
            Utils.sendResponse(exchange, arr.toString().getBytes(), 200);
        } catch(JSONException e) {
            byte[] bytes = "Bad request".getBytes(StandardCharsets.UTF_8);
            Utils.sendResponse(exchange, bytes, 400);
        } catch (SQLException e) {
            byte[] bytes = "Internal server error".getBytes(StandardCharsets.UTF_8);
            Utils.sendResponse(exchange, bytes, 500);
        }
    }
}
