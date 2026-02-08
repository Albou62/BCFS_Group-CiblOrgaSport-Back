package com.ciblorgasport.notifications.models;

import org.json.JSONObject;

public class GroupTableRow {
    private Long id;
    private String name;
    
    public GroupTableRow(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() { return this.id; }
    public String getName() { return this.name; }
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }

    public JSONObject toJson() {
        String jsonObject = "{ \"groupId\": \"%s\",  \"groupName\": \"%s\" }";
        return new JSONObject(String.format(jsonObject, this.id, this.name));
    }
}
