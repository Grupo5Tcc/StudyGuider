package com.example.studyguider.models;

public class Login {

    // Criação de Variáveis
    private String email;
    private String password;

    // Construtor
    public Login(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters e Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
