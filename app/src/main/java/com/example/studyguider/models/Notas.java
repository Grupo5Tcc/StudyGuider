package com.example.studyguider.models;

public class Notas {
    String id, nomeMateria, cred, list,trab, pre, prova;

    public Notas() {
    }

    public String getNomeMateria() { return nomeMateria; }

    public void setNomeMateria(String nomeMateria) { this.nomeMateria = nomeMateria; }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCred() {
        return cred;
    }

    public void setCred(String cred) {
        this.cred = cred;
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
