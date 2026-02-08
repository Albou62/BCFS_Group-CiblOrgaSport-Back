package com.ciblorgasport.notifications.models;

import org.json.JSONObject;

public class SubscriptionTableRow {
    private Long groupId;
    private Long userId;
    private String dateInscription;

    public SubscriptionTableRow(Long groupId, Long userId, String dateInscription) {
        this.groupId = groupId;
        this.userId = userId;
        this.dateInscription = dateInscription;
    }

    public Long getGroupId() { return groupId; }
    public Long getUserId() { return userId; }
    public String dateInscription() { return dateInscription; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setDateInscription(String dateInscription) { this.dateInscription = dateInscription; }

    public JSONObject toJson() {
        String jsonObject = "{ \"groupId\": \"%s\",  \"userId\": \"%s\", \"dateInscription\": \"%s\" }";
        return new JSONObject(String.format(jsonObject, this.groupId, this.userId, this.dateInscription));
    }
}
