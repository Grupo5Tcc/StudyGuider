package com.example.studyguider.models;

public class UserProfile {
    private String name;
    private String email;
    private String dateOfBirth;
    private String absence;

    public UserProfile() {

    }

    public UserProfile(String name, String email, String dateOfBirth, String absence) {
        this.name = name;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.absence = absence;
    }

    // Getters e Setters para todos os campos
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAbsence() {
        return absence;
    }

    public void setAbsence(String absence) {
        this.absence = absence;
    }
}
