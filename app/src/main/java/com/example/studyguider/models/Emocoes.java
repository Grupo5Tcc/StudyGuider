package com.example.studyguider.models;

public class Emocoes {

   // Criação de Variáveis
    private int day;
    private int color;

    // Construtor
    public Emocoes(int day, int color) {
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
