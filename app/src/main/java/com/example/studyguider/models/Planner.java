package com.example.studyguider.models;

public class Planner {
    private String name;
    private String time;
    private String info;
    private int color;
    private String day; // Novo campo para o dia do evento

    public Planner(String name, String time, String info, int color, String day) {
        this.name = name;
        this.time = time;
        this.info = info;
        this.color = color;
        this.day = day; // Inicializa o dia
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getInfo() {
        return info;
    }

    public int getColor() {
        return color;
    }

    public String getDay() {
        return day; // Getter para o dia
    }
}