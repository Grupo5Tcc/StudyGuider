package com.example.studyguider.models;

public class Subjects {
    String id, nomeMateria, professor, conteudos, media, situacao;

    public Subjects(String id, String nomeMateria, String professor, String conteudos, String media, String situacao) {
        this.id = id;
        this.nomeMateria = nomeMateria;
        this.professor = professor;
        this.conteudos = conteudos;
        this.media = media;
        this.situacao = situacao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomeMateria() {
        return nomeMateria;
    }

    public void setNomeMateria(String nomeMateria) {
        this.nomeMateria = nomeMateria;
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

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }
}
