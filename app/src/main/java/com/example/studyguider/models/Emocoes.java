package com.example.studyguider.models;

public class Emocoes {
    private int day;
    private int color;

    // Construtores, getters e setters
    public Emocoes(int day, int color) {
        this.day = day;
        this.color = color;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
