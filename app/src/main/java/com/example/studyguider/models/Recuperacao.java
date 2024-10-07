package com.example.studyguider.models;

public class Recuperacao {
    private String containerId;
    private boolean checkbox1;
    private boolean checkbox2;
    private boolean checkbox3;
    private boolean checkbox4;
    private String conteudos;

    public Recuperacao(String containerId, boolean checkbox1, boolean checkbox2, boolean checkbox3, boolean checkbox4, String conteudos) {
        this.containerId = containerId;
        this.checkbox1 = checkbox1;
        this.checkbox2 = checkbox2;
        this.checkbox3 = checkbox3;
        this.checkbox4 = checkbox4;
        this.conteudos = conteudos;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public boolean isCheckbox1() {
        return checkbox1;
    }

    public void setCheckbox1(boolean checkbox1) {
        this.checkbox1 = checkbox1;
    }

    public boolean isCheckbox2() {
        return checkbox2;
    }

    public void setCheckbox2(boolean checkbox2) {
        this.checkbox2 = checkbox2;
    }

    public boolean isCheckbox3() {
        return checkbox3;
    }

    public void setCheckbox3(boolean checkbox3) {
        this.checkbox3 = checkbox3;
    }

    public boolean isCheckbox4() {
        return checkbox4;
    }

    public void setCheckbox4(boolean checkbox4) {
        this.checkbox4 = checkbox4;
    }

    public String getConteudos() {
        return conteudos;
    }

    public void setConteudos(String conteudos) {
        this.conteudos = conteudos;
    }
}