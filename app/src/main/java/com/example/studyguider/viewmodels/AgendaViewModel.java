package com.example.studyguider.viewmodels;

import android.graphics.Color;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.studyguider.models.Planner;

import java.util.ArrayList;
import java.util.List;

public class AgendaViewModel extends ViewModel {

    private final MutableLiveData<List<Planner>> events = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> selectedDay = new MutableLiveData<>();
    private final MutableLiveData<Integer> selectedColor = new MutableLiveData<>(Color.WHITE);

    public LiveData<List<Planner>> getEvents() {
        return events;
    }

    public LiveData<String> getSelectedDay() {
        return selectedDay;
    }

    public void selectDay(String day) {
        selectedDay.setValue(day);
    }

    public LiveData<Integer> getSelectedColor() {
        return selectedColor;
    }

    public void setColor(int color) {
        selectedColor.setValue(color);
    }

    public void addEvent(String eventName, String eventTime, String additionalInfo, int color, String day) {
        List<Planner> currentEvents = events.getValue();
        if (currentEvents != null) {
            currentEvents.add(new Planner(eventName, eventTime, additionalInfo, color, day));
            events.setValue(currentEvents);
        }
    }

    public void removeEvent(Planner event) {
        List<Planner> currentEvents = events.getValue();
        if (currentEvents != null) {
            currentEvents.remove(event);
            events.setValue(currentEvents);
        }
    }

    public void removeEventByDay(String day) {
        List<Planner> currentEvents = events.getValue();
        if (currentEvents != null) {
            List<Planner> updatedEvents = new ArrayList<>(currentEvents);
            for (Planner event : currentEvents) {
                if (event.getDay().equals(day)) {
                    updatedEvents.remove(event); // Remove o evento do dia
                }
            }
            events.setValue(updatedEvents); // Atualiza a lista de eventos
        }
    }
}
