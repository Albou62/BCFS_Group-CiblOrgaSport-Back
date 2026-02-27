package com.ciblorgasport.notifications.serverHandlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ciblorgasport.notifications.models.GroupTableRow;
import com.ciblorgasport.notifications.services.DatabaseService;
import com.ciblorgasport.notifications.utils.Utils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class GroupHandler implements HttpHandler {
    private final DatabaseService dbService;

    public GroupHandler() {
        this(new DatabaseService());
    }

    public GroupHandler(DatabaseService dbService) {
        this.dbService = dbService;
    }

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
            String groupName = jsonObject.getString("name");
            GroupTableRow group = this.dbService.createGroupIfNotExists(groupName);
            Utils.sendResponse(exchange, group.toJson().toString().getBytes(StandardCharsets.UTF_8), 200);
        } catch(JSONException e) {
            System.out.println(e);
            byte[] bytes = "Bad request".getBytes(StandardCharsets.UTF_8);
            Utils.sendResponse(exchange, bytes, 400);
        } catch (IOException | SQLException e) {
            System.out.println(e);
            byte[] bytes = "Internal server error".getBytes(StandardCharsets.UTF_8);
            Utils.sendResponse(exchange, bytes, 500);
        }
    }

    public void handleGetRequest(HttpExchange exchange) {
        try {
            String query = exchange.getRequestURI().getQuery();
            if (query != null && !query.isEmpty()) {
                Map<String, String> params = Utils.parseQueryParams(query);
                String groupName = params.get("name");
                if (groupName != null && !groupName.isBlank()) {
                    GroupTableRow group = this.dbService.getGroupByName(groupName);
                    if (group == null) {
                        Utils.sendResponse(exchange, "{}".getBytes(StandardCharsets.UTF_8), 404);
                        return;
                    }
                    Utils.sendResponse(exchange, group.toJson().toString().getBytes(StandardCharsets.UTF_8), 200);
                    return;
                }
            }

            List<GroupTableRow> gtrs = this.dbService.getAllGroups();
            JSONArray arr = new JSONArray();
            for (GroupTableRow gtr : gtrs) {
                JSONObject gtrJson = gtr.toJson();
                arr.put(gtrJson);
            }
            Utils.sendResponse(exchange, arr.toString().getBytes(), 200);
        } catch(JSONException e) {
            e.printStackTrace();
            byte[] bytes = "Bad request".getBytes(StandardCharsets.UTF_8);
            Utils.sendResponse(exchange, bytes, 400);
        } catch (SQLException e) {
            e.printStackTrace();
            byte[] bytes = "Internal server error".getBytes(StandardCharsets.UTF_8);
            Utils.sendResponse(exchange, bytes, 500);
        }
    }
}
