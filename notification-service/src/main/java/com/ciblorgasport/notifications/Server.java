package main.java.com.ciblorgasport.notifications;

import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {
    
    public void start() {
        HttpServer server = HttpServer.create(
            new InetSocketAddress("localhost", 8080), 0
        );
        server.createContext("/subscription", new SubscriptionHandler());
        server.createContext("/notification", new NotificationHandler());

        server.setExecutor(null);
        server.start();
    }

}
