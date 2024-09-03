package com.example.studyguider.viewmodels;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.studyguider.models.Planner;

import java.util.ArrayList;
import java.util.List;

public class PlannerViewModel extends ViewModel {
    private final MutableLiveData<List<Planner>> plannersLiveData = new MutableLiveData<>();

    public PlannerViewModel() {
        plannersLiveData.setValue(new ArrayList<>());
    }

    public LiveData<List<Planner>> getPlanners() {
        return plannersLiveData;
    }

    public void adicionarPlanner(Planner planner) {
        List<Planner> listaAtual = plannersLiveData.getValue();
        if (listaAtual != null) {
            listaAtual.add(planner);
            plannersLiveData.setValue(listaAtual);
        }
    }

    public void removerPlanner(Planner planner) {
        List<Planner> listaAtual = plannersLiveData.getValue();
        if (listaAtual != null) {
            listaAtual.remove(planner);
            plannersLiveData.setValue(listaAtual);
        }
    }

    public void atualizarCorDoPlanner(Planner planner, int novaCor) {
        List<Planner> listaAtual = plannersLiveData.getValue();
        if (listaAtual != null) {
            for (Planner p : listaAtual) {
                if (p.getId().equals(planner.getId())) {
                    p.setCor(novaCor);
                    break;
                }
            }
            plannersLiveData.setValue(listaAtual);
        }
    }
}

