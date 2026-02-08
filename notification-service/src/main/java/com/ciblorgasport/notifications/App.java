package com.ciblorgasport.notifications;

public class App {
    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.start();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
