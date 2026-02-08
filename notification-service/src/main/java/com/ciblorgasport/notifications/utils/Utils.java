package com.ciblorgasport.notifications.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.json.JSONArray;

import com.sun.net.httpserver.HttpExchange;

public class Utils {
    public static String getRequestBody(HttpExchange exchange) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
        String requestBody = bufferedReader.lines().collect(Collectors.joining("\n"));
        return requestBody;
    }

    public static void sendResponse(HttpExchange exchange, byte[] bytes, int statusCode) {
        try {
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(statusCode, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.getResponseBody().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
