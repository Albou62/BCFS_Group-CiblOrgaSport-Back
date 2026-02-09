package com.ciblorgasport.notifications.serverHandlers;

import java.io.IOException;

import com.ciblorgasport.notifications.utils.Utils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HelloHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            handleGetRequest(exchange);
        }
    }

    public void handleGetRequest(HttpExchange exchange) {
        byte[] msg = "Hello from notifications".getBytes();
        Utils.sendResponse(exchange, msg, 200);
    }
}
