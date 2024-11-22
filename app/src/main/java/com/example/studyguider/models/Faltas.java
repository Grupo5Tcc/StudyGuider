package com.example.studyguider.models;

public class Faltas {
    private String day;
    private String reason;
    private boolean medicalCertificate;
    private String grade;

    // Empty constructor needed for Firebase
    public Faltas() {}

    // Full constructor
    public Faltas(String day, String reason, boolean medicalCertificate, String grade) {
        this.day = day;
        this.reason = reason;
        this.medicalCertificate = medicalCertificate;
        this.grade = grade;
    }

    // Getters and Setters
    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean getMedicalCertificate() {
        return medicalCertificate;
    }

    public void setMedicalCertificate(boolean medicalCertificate) {
        this.medicalCertificate = medicalCertificate;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

}
