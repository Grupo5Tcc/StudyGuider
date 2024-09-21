package com.example.studyguider.models;

public class Notas {
    String id, nomeMateria, cred, list,trab, prec, prov;

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

    public String getPrec() {
        return prec;
    }

    public void setPrec(String prec) {
        this.prec = prec;
    }

    public String getProv() {
        return prov;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }
}
