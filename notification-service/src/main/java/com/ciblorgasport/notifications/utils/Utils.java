package com.ciblorgasport.notifications.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

    public static Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query != null && !query.isEmpty()) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx > 0) {
                    String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                    params.put(key, value);
                } else {
                    // Handle keys without values (e.g., "flag" instead of "flag=true")
                    String key = URLDecoder.decode(pair, StandardCharsets.UTF_8);
                    params.put(key, "");
                }
            }
        }
        return params;
    }
}
