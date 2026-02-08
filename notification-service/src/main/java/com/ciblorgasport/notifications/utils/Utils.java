package main.java.com.ciblorgasport.notifications.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;

public class Utils {
    public static String getRequestBody(HttpExchange exchange) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(exchange.getResponseBody(), StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String requestBody = bufferedReader.lines().collect(Collectors.joining("\n"));
        return requestBody;
    }
}
