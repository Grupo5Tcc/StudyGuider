package com.example.studyguider.viewmodels;

import android.graphics.Color;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.studyguider.models.Planner;

import java.util.ArrayList;
import java.util.List;

public class PlannerViewModel extends ViewModel {

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
            currentEvents.add(new Planner(eventName, eventTime, additionalInfo, color, day)); // Passa o dia
            events.setValue(currentEvents);
        }
    }


}
