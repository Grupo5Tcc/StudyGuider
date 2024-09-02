package com.example.studyguider.models;

import android.widget.GridLayout;
import android.widget.LinearLayout;

public class Planner {
    private GridLayout gridLayoutCalendar;
    private int selectedColor;
    private int daysInMonth;
    private LinearLayout containerTarefas;

    public LinearLayout getContainerTarefas() {
        return containerTarefas;
    }

    public void setContainerTarefas(LinearLayout containerTarefas) {
        this.containerTarefas = containerTarefas;
    }

    public int getDaysInMonth() {
        return daysInMonth;
    }

    public void setDaysInMonth(int daysInMonth) {
        this.daysInMonth = daysInMonth;
    }

    public int getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(int selectedColor) {
        this.selectedColor = selectedColor;
    }

    public GridLayout getGridLayoutCalendar() {
        return gridLayoutCalendar;
    }

    public void setGridLayoutCalendar(GridLayout gridLayoutCalendar) {
        this.gridLayoutCalendar = gridLayoutCalendar;
    }
}
