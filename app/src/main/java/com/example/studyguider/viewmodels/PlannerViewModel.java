package com.example.studyguider.viewmodels;


import android.graphics.Color;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.example.studyguider.models.Planner;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.List;


public class PlannerViewModel extends ViewModel {


    private final MutableLiveData<List<Planner>> events = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> selectedDay = new MutableLiveData<>();
    private final MutableLiveData<Integer> selectedColor = new MutableLiveData<>(Color.WHITE);


    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference eventsCollection = db.collection("events");


    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String userId = user.getUid();


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


    public void addEvent(String userId, String eventName, String eventTime, String additionalInfo, int color, String day) {
        Planner newEvent = new Planner(userId, eventName, eventTime, additionalInfo, color, day);
        eventsCollection.add(newEvent)
                .addOnSuccessListener(documentReference -> {
                    newEvent.setId(documentReference.getId());
                    List<Planner> currentEvents = events.getValue();
                    if (currentEvents != null) {
                        currentEvents.add(newEvent);
                        events.setValue(currentEvents);
                    }
                })
                .addOnFailureListener(e -> {
                    // Lidar com erro
                });
    }


    public void removeEvent(Planner event) {
        // Checa se o evento é nulo
        if (event == null) {
            return;
        }


        // Remove o evento no Firestore
        eventsCollection.document(event.getId()) // Assuming you have a method getId() in your Planner class
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Removido com Sucesso
                    List<Planner> currentEvents = events.getValue();
                    if (currentEvents != null) {
                        currentEvents.remove(event);
                        events.setValue(currentEvents);
                    }
                })
                .addOnFailureListener(e -> {
                    // Lidar com erro ao tentar excluir do Firestore
                });
    }


    public void removeEventByDay(String day) {
        List<Planner> currentEvents = events.getValue();
        if (currentEvents != null) {
            List<Planner> updatedEvents = new ArrayList<>(currentEvents);
            for (Planner event : currentEvents) {
                if (event.getDay().equals(day)) {
                    // Delete from Firestore
                    eventsCollection.document(event.getId()).delete()
                            .addOnSuccessListener(aVoid -> {
                                // Successfully deleted from Firestore
                                updatedEvents.remove(event); // Remove from local list after successful delete
                            })
                            .addOnFailureListener(e -> {
                                // Handle error when trying to delete from Firestore
                            });
                }
            }
            // Update the list of events locally after processing all deletions
            events.setValue(updatedEvents);
        }
    }


    public void loadEventsByUserId(String userId) {
        List<Planner> currentEvents = new ArrayList<>();
        events.setValue(currentEvents); // Limpar eventos atuais

        eventsCollection.whereEqualTo("userId", userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            Planner planner = document.toObject(Planner.class);
                            if (planner != null) {
                                planner.setId(document.getId());
                                currentEvents.add(planner);
                            }
                        }
                        events.setValue(currentEvents); // Atualizar a lista de eventos
                    } else {
                        Log.e("PlannerViewModel", "Erro ao obter documentos: ", task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("PlannerViewModel", "Erro ao carregar eventos: ", e);
                });
    }

    public void updateEvent(Planner event) {
        if (event == null || event.getId() == null) {
            Log.e("PlannerViewModel", "Event or ID is null, cannot update.");
            return;
        }

        eventsCollection.document(event.getId()).set(event)
                .addOnSuccessListener(aVoid -> {
                    // Atualiza a lista local de eventos
                    List<Planner> currentEvents = events.getValue();
                    if (currentEvents != null) {
                        int index = currentEvents.indexOf(event);
                        if (index >= 0) {
                            currentEvents.set(index, event);
                            events.setValue(currentEvents);
                        }
                    }
                    Log.d("PlannerViewModel", "Event updated successfully.");
                })
                .addOnFailureListener(e -> {
                    Log.e("PlannerViewModel", "Error updating event: ", e);
                });
    }
}
