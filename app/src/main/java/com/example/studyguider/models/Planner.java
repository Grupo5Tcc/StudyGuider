package com.example.studyguider.models;

public class Planner {
    private String name;
    private String time;
    private String info;
    private int color;

    public Planner(String name, String time, String info, int color) {
        this.name = name;
        this.time = time;
        this.info = info;
        this.color = color;
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
}
