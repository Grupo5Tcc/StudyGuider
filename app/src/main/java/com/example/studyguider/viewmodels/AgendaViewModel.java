package com.example.studyguider.viewmodels;


import android.graphics.Color;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.example.studyguider.models.Agenda;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.List;


public class AgendaViewModel extends ViewModel {


    private final MutableLiveData<List<Agenda>> events = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> selectedDay = new MutableLiveData<>();
    private final MutableLiveData<Integer> selectedColor = new MutableLiveData<>(Color.WHITE);


    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference eventsCollection = db.collection("events");


    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String userId = user.getUid();


    public LiveData<List<Agenda>> getEvents() {
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


    public void addEvent(String userId,String eventName, String eventTime, String additionalInfo, int color, String day) {
        Agenda newEvent = new Agenda(userId,eventName, eventTime, additionalInfo, color, day);


        // Save the event to Firestore
        eventsCollection.add(newEvent)
                .addOnSuccessListener(documentReference -> {
                    // Set the ID of the event
                    newEvent.setId(documentReference.getId());
                    List<Agenda> currentEvents = events.getValue();
                    if (currentEvents != null) {
                        currentEvents.add(newEvent);
                        events.setValue(currentEvents);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }


    public void removeEvent(Agenda event) {
        // Check if the event is null
        if (event == null) {
            return;
        }


        // Remove the event from Firestore
        eventsCollection.document(event.getId()) // Assuming you have a method getId() in your Agenda class
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Successfully deleted from Firestore, now remove from the local list
                    List<Agenda> currentEvents = events.getValue();
                    if (currentEvents != null) {
                        currentEvents.remove(event);
                        events.setValue(currentEvents);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error when trying to delete from Firestore
                });
    }


    public void removeEventByDay(String day) {
        List<Agenda> currentEvents = events.getValue();
        if (currentEvents != null) {
            List<Agenda> updatedEvents = new ArrayList<>(currentEvents);
            for (Agenda event : currentEvents) {
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
        // Clear the current list of events before loading new ones
        List<Agenda> currentEvents = new ArrayList<>();
        events.setValue(currentEvents); // Avoid showing an empty screen while loading


        // Query Firestore to get events for the specific user ID
        eventsCollection.whereEqualTo("userId", userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            // Convert Firestore document to Agenda object
                            Agenda agenda = document.toObject(Agenda.class);
                            if (agenda != null) {
                                agenda.setId(document.getId()); // Set the document ID
                                currentEvents.add(agenda); // Add the event to the list
                            }
                        }
                        events.setValue(currentEvents); // Update LiveData with the new list
                    } else {
                        // Handle failure in retrieving events
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors in the Firestore query
                });
    }
}
