package com.example.studyguider.models;

public class Perfil {
    private String name;
    private String email;
    private String dateOfBirth;
    private int absence;
    private int recovery; // Novo campo para recuperações

    public Perfil(String name, String email, String dateOfBirth, int absence, int recovery) {
        this.name = name;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.absence = absence;
        this.recovery = recovery; // Inicialização do campo
    }

    // Getters e Setters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public int getAbsence() {
        return absence;
    }

    public int getRecovery() {
        return recovery; // Novo getter
    }
}
