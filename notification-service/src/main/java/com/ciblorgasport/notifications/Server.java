package com.ciblorgasport.notifications;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import com.ciblorgasport.notifications.models.GroupTableRow;
import com.ciblorgasport.notifications.serverHandlers.GroupHandler;
import com.ciblorgasport.notifications.serverHandlers.HelloHandler;
import com.ciblorgasport.notifications.serverHandlers.NotificationHandler;
import com.ciblorgasport.notifications.serverHandlers.SubscriptionHandler;
import com.ciblorgasport.notifications.services.DatabaseService;
import com.sun.net.httpserver.HttpServer;

public class Server {
    private static final String DEFAULT_INCIDENT_GROUP_NAME = "Incidents";
    private static final AtomicLong INCIDENT_GROUP_BOOTSTRAP_SUCCESS = new AtomicLong(0);
    
    public void start() {
        try {
            DatabaseService databaseService = new DatabaseService();
            bootstrapIncidentGroup(databaseService);

            HttpServer server = HttpServer.create(
                new InetSocketAddress("0.0.0.0", 8080), 0
            );
            server.createContext("/subscription", new SubscriptionHandler(databaseService));
            server.createContext("/notification", new NotificationHandler());
            server.createContext("/group", new GroupHandler(databaseService));

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

    void bootstrapIncidentGroup(DatabaseService databaseService) {
        String incidentGroupName = System.getenv("INCIDENT_GROUP_NAME");
        if (incidentGroupName == null || incidentGroupName.isBlank()) {
            incidentGroupName = DEFAULT_INCIDENT_GROUP_NAME;
        }

        try {
            GroupTableRow existingGroup = databaseService.getGroupByName(incidentGroupName);
            GroupTableRow group = databaseService.createGroupIfNotExists(incidentGroupName);
            INCIDENT_GROUP_BOOTSTRAP_SUCCESS.incrementAndGet();
            if (existingGroup == null) {
                System.out.printf(
                    "incident_group_bootstrap_created incident_group_bootstrap_success=%d groupId=%d groupName=%s%n",
                    INCIDENT_GROUP_BOOTSTRAP_SUCCESS.get(),
                    group.getId(),
                    group.getName()
                );
            } else {
                System.out.printf(
                    "incident_group_bootstrap_already_exists incident_group_bootstrap_success=%d groupId=%d groupName=%s%n",
                    INCIDENT_GROUP_BOOTSTRAP_SUCCESS.get(),
                    group.getId(),
                    group.getName()
                );
            }
        } catch (SQLException e) {
            System.out.printf(
                "incident_group_bootstrap_error groupName=%s error=%s%n",
                incidentGroupName,
                e.getMessage()
            );
        } catch (Exception e) {
            System.out.printf("incident_group_bootstrap_error groupName=%s error=%s%n", incidentGroupName, e.getMessage());
        }
    }

}
