package com.ciblorgasport.notifications;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ciblorgasport.notifications.serverHandlers.GroupHandler;
import com.ciblorgasport.notifications.serverHandlers.HelloHandler;
import com.ciblorgasport.notifications.serverHandlers.NotificationHandler;
import com.ciblorgasport.notifications.serverHandlers.SubscriptionHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {
    
    public void start() {
        try {
            HttpServer server = HttpServer.create(
                new InetSocketAddress("0.0.0.0", 8080), 0
            );
            server.createContext("/subscription", new SubscriptionHandler());
            server.createContext("/notification", new NotificationHandler());
            server.createContext("/group", new GroupHandler());

            server.createContext("/hello", new HelloHandler());

            // Create a thread pool with a fixed number of threads
            ExecutorService executor = Executors.newFixedThreadPool(10);
            server.setExecutor(executor);
            
            server.start();
            System.out.println("Server is running on port 8080");
            
            // Add shutdown hook to clean up the executor
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down server...");
                executor.shutdown();
            }));
        } catch (IOException e) {
            System.out.println("Failed to start server: " + e);
        }
    }

}
