package com.ciblorgasport.notifications;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.ciblorgasport.notifications.serverHandlers.GroupHandler;
import com.ciblorgasport.notifications.serverHandlers.NotificationHandler;
import com.ciblorgasport.notifications.serverHandlers.SubscriptionHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {
    
    public void start() {
        try {
            HttpServer server = HttpServer.create(
                new InetSocketAddress("localhost", 8080), 0
            );
            server.createContext("/subscription", new SubscriptionHandler());
            server.createContext("/notification", new NotificationHandler());
            server.createContext("/group", new GroupHandler());

            server.setExecutor(null);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
