package com.ciblorgasport.geolocalisation;

import static spark.Spark.*;

import com.ciblorgasport.geolocalisation.handlers.LocationHandler;

public class Server {

    public Server() {
        port(8080);
        LocationHandler handler = new LocationHandler();
        
        post("/localisation/user/:id", handler::handlePostUserLocation);
        get("/localisation/user/:id", handler::handleGetUserLocation);
    }
}
