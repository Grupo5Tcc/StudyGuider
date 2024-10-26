package com.example.studyguider.models;


public class Agenda {
    private String id; // Add this field
    private String eventName;
    private String eventTime;
    private String additionalInfo;
    private int color;
    private String day;


    // Constructor, getters, and setters
    public Agenda(String eventName, String eventTime, String additionalInfo, int color, String day) {
        this.eventName = eventName;
        this.eventTime = eventTime;
        this.additionalInfo = additionalInfo;
        this.color = color;
        this.day = day;
    }


    // Add a constructor that takes an ID as a parameter
    public Agenda(String id, String eventName, String eventTime, String additionalInfo, int color, String day) {
        this.id = id;
        this.eventName = eventName;
        this.eventTime = eventTime;
        this.additionalInfo = additionalInfo;
        this.color = color;
        this.day = day;
    }


    // Getter for the ID
    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getEventName() {
        return eventName;
    }


    public void setEventName(String eventName) {
        this.eventName = eventName;
    }


    public String getEventTime() {
        return eventTime;
    }


    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }


    public String getAdditionalInfo() {
        return additionalInfo;
    }


    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }


    public int getColor() {
        return color;
    }


    public void setColor(int color) {
        this.color = color;
    }


    public String getDay() {
        return day;
    }


    public void setDay(String day) {
        this.day = day;
    }
}
