package com.example.studyguider.models;

public class UserProfile {
    private String name;
    private String email;
    private String dateOfBirth;
    private int absence;

    public UserProfile(String name, String email, String dateOfBirth, int absence) {
        this.name = name;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.absence = absence;
    }

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

    public void setAbsence(int absence) {
        this.absence = absence;
    }
}
