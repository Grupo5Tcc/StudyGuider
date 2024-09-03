package com.example.studyguider.models;

import android.widget.GridLayout;
import android.widget.LinearLayout;

public class Planner {
    private String id;
    private String descricao;
    private String data;
    private int cor;

    // Construtor
    public Planner(String id, String descricao, String data, int cor) {
        this.id = id;
        this.descricao = descricao;
        this.data = data;
        this.cor = cor;
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getCor() {
        return cor;
    }

    public void setCor(int cor) {
        this.cor = cor;
    }
}

