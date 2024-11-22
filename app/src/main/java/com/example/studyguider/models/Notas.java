package com.example.studyguider.models;

public class Notas {

    // Criação de Variáveis
    String id, subjectName, creditScore, list,trab, pre, prova;

    // Construtor
    public Notas() {
    }

    // Getters e Setters
    public String getSubjectName() { return subjectName; }

    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(String cred) {
        this.creditScore = cred;
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }

    public String getTrab() {
        return trab;
    }

    public void setTrab(String trab) {
        this.trab = trab;
    }

    public String getPre() {
        return pre;
    }

    public void setPre(String pre) {
        this.pre = pre;
    }

    public String getProva() {
        return prova;
    }

    public void setProva(String prova) {
        this.prova = prova;
    }
}
