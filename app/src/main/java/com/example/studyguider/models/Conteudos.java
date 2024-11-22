package com.example.studyguider.models;

public class Conteudos {

    // Criação de Variáveis
    private String contents;
    private String term;


    // Construtores
    public Conteudos() {
    }

    public Conteudos(String contents, String term) {
        this.contents = contents;
        this.term = term;
    }

    // Getters e Setters
    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

}
