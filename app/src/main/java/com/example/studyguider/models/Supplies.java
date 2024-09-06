package com.example.studyguider.models;

public class Supplies {
    public String materia;
    public String professor;
    public String conteudos;
    public String media;
    public String situaçao;

    public Supplies(String materia, String professor, String conteudos, String media, String situaçao) {
        this.materia = materia;
        this.professor = professor;
        this.conteudos = conteudos;
        this.media = media;
        this.situaçao = situaçao;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public String getConteudos() {
        return conteudos;
    }

    public void setConteudos(String conteudos) {
        this.conteudos = conteudos;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getSituaçao() {
        return situaçao;
    }

    public void setSituaçao(String situaçao) {
        this.situaçao = situaçao;
    }
}
