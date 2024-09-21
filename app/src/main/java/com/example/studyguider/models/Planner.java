package com.example.studyguider.models;

public class Planner {
    private String id;
    private String descricao;
    private String data;
    private int cor;

    public Planner(){

    }

    public Planner(String id, String descricao, String data, int cor) {
        this.id = id;
        this.descricao = descricao;
        this.data = data;
        this.cor = cor;
    }

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
