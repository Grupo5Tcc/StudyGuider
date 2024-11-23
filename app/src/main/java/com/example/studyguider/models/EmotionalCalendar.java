package com.example.studyguider.models;

public class EmotionalCalendar {

   // Criação de Variáveis
    private int day;
    private int color;

    // Construtor
    public EmotionalCalendar(int day, int color) {
        this.day = day;
        this.color = color;
    }

    // Getters e Setters
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
