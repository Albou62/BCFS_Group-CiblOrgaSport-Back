package com.ciblorgasport.notifications.models;

public class Notification {

    private long groupId;
    private String date;
    private String label;
    private String impactLevel;
    public void setGroupId(long groupId) { this.groupId = groupId; }
    public void setDate(String date) { this.date = date; }
    public void setLabel(String label) { this.label = label; }
    public void setImpactLevel(String impactLevel) { this.impactLevel = impactLevel; }
    public long getgroupId() { return this.groupId; }
    public String getDate() { return this.date; }
    public String getLabel() { return this.label; }
    public String getImpactLevel() { return this.impactLevel; }
}
