package com.example.studyguider.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Planner {

    // Criação de Variáveis
    private String id;
    private String userId;
    private String eventName;
    private String eventTime;
    private String additionalInfo;
    private int color;
    private String day;

    // Construtor vazio necessário para o Firestore
    public Planner() {}

    // Construtor com parâmetros
    public Planner(String userId, String eventName, String eventTime, String additionalInfo, int color, String day) {
        this.userId = userId;
        this.eventName = eventName;
        this.eventTime = eventTime;
        this.additionalInfo = additionalInfo;
        this.color = color;
        this.day = day;
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    // Método para serializar uma lista de Planner em JSON
    public static String toJson(List<Planner> planners) {
        Gson gson = new Gson();
        return gson.toJson(planners);
    }

    // Método para desserializar uma string JSON para uma lista de Planner
    public static List<Planner> fromJson(String json) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Planner>>() {}.getType();
        return gson.fromJson(json, listType);
    }
}
