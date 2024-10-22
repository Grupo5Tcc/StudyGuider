package com.example.studyguider.models;

public class Plantoes {
    String id, professor, materia, dia, hora;

    public Plantoes() {
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getProfessor() {
        return professor;
    }

    public String getMateria() {
        return materia;
    }

    public String getDia() {
        return dia;
    }

    public String getHora() {
        return hora;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }
}
