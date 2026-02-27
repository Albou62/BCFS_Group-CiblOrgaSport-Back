package com.ciblorgasport.notifications;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
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
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.common.TextFormat;

public class Server {
    private static final String DEFAULT_INCIDENT_GROUP_NAME = "Incidents";
    private static final AtomicLong INCIDENT_GROUP_BOOTSTRAP_SUCCESS = new AtomicLong(0);
    private static final Counter HTTP_REQUESTS_TOTAL = Counter.build()
        .name("notification_http_requests_total")
        .help("Total HTTP requests handled by notification-service")
        .labelNames("route", "method")
        .register();
    private static final Counter HTTP_ERRORS_TOTAL = Counter.build()
        .name("notification_http_errors_total")
        .help("Total unhandled HTTP request errors in notification-service")
        .labelNames("route", "method")
        .register();
    private static final Histogram HTTP_REQUEST_DURATION_SECONDS = Histogram.build()
        .name("notification_http_request_duration_seconds")
        .help("HTTP request duration in seconds for notification-service")
        .labelNames("route", "method")
        .register();
    
    public void start() {
        try {
            DatabaseService databaseService = new DatabaseService();
            bootstrapIncidentGroup(databaseService);

            HttpServer server = HttpServer.create(
                new InetSocketAddress("0.0.0.0", 8080), 0
            );
            server.createContext("/subscription", instrumented("/subscription", new SubscriptionHandler(databaseService)));
            server.createContext("/notification", instrumented("/notification", new NotificationHandler()));
            server.createContext("/group", instrumented("/group", new GroupHandler(databaseService)));

            server.createContext("/hello", instrumented("/hello", new HelloHandler()));
            server.createContext("/metrics", this::handleMetrics);

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

    private HttpHandler instrumented(String route, HttpHandler delegate) {
        return exchange -> {
            String method = exchange.getRequestMethod();
            HTTP_REQUESTS_TOTAL.labels(route, method).inc();
            Histogram.Timer timer = HTTP_REQUEST_DURATION_SECONDS.labels(route, method).startTimer();
            try {
                delegate.handle(exchange);
            } catch (Exception e) {
                HTTP_ERRORS_TOTAL.labels(route, method).inc();
                try {
                    byte[] body = "internal_error".getBytes();
                    exchange.sendResponseHeaders(500, body.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(body);
                    }
                } catch (IOException ignored) {
                    // If response was already sent by the delegate, keep original behavior.
                }
            } finally {
                timer.observeDuration();
            }
        };
    }

    private void handleMetrics(HttpExchange exchange) throws IOException {
        StringWriter writer = new StringWriter();
        TextFormat.write004(writer, CollectorRegistry.defaultRegistry.metricFamilySamples());
        byte[] response = writer.toString().getBytes();
        exchange.getResponseHeaders().set("Content-Type", TextFormat.CONTENT_TYPE_004);
        exchange.sendResponseHeaders(200, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
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
