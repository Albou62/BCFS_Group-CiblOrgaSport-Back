package com.ciblorgasport.geolocalisation.handlers;

import com.ciblorgasport.geolocalisation.models.UserLocation;
import com.ciblorgasport.geolocalisation.services.DatabaseService;
import com.ciblorgasport.geolocalisation.utils.JsonUtil;
import spark.Request;
import spark.Response;

public class LocationHandler {

    private final DatabaseService dbService = new DatabaseService();

    public String handlePostUserLocation(Request request, Response response) {
        String userId = request.params(":id");
        UserLocation location = JsonUtil.fromJson(request.body(), UserLocation.class);
        location.setUserId(userId);
        
        dbService.saveUserLocation(location);
        
        response.status(201);
        return "Location saved";
    }

    public String handleGetUserLocation(Request request, Response response) {
        String userId = request.params(":id");
        UserLocation location = dbService.getUserLocation(userId);
        
        if (location != null) {
            response.type("application/json");
            return JsonUtil.toJson(location);
        } else {
            response.status(404);
            return "Location not found";
        }
    }
}
