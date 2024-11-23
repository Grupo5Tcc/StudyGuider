package com.example.studyguider.models;

public class Absence {

    //Declaração de Variáveis
    private String day;
    private String motivo;
    private boolean atestado;
    private String nota;

    // Construtor vazio necessário para Firebase
    public Absence() {}

    // Construtor completo
    public Absence(String day, String motivo, boolean atestado, String nota) {
        this.day = day;
        this.motivo = motivo;
        this.atestado = atestado;
        this.nota = nota;
    }

    // Getters e Setters
    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public boolean getAtestado() {
        return atestado;
    }

    public void setAtestado(boolean atestado) {
        this.atestado = atestado;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }
}
