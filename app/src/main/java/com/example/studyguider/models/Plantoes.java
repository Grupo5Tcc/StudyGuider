package com.example.studyguider.models;

public class Plantoes {
    private String id, teacher, subject, day, time;

    public Plantoes() {
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getSubject() {
        return subject;
    }

    public String getDay() {
        return day;
    }

    public String getTime() {
        return time;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
