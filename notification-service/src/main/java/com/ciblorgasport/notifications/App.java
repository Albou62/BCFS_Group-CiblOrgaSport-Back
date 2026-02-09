package com.ciblorgasport.notifications;

public class App {
    public static void main(String[] args) {
        System.out.println("Starting Notification Service...");
        Server server = new Server();
        server.start();

        try {
            Thread.currentThread().join();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
