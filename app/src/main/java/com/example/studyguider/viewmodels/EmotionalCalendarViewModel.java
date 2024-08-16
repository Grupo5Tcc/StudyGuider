package com.example.studyguider.viewmodels;

import android.app.Application;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EmotionalCalendarViewModel extends AndroidViewModel {
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private final MutableLiveData<Map<Integer, Integer>> moodData = new MutableLiveData<>();
    private final MutableLiveData<String> monthYear = new MutableLiveData<>();
    private final MutableLiveData<Integer> selectedColor = new MutableLiveData<>(Color.WHITE);
    private final Calendar calendar = Calendar.getInstance();
    private String userId;

    public EmotionalCalendarViewModel(@NonNull Application application) {
        super(application);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }
        updateMonthYear();
    }

    public LiveData<Map<Integer, Integer>> getMoodData() {
        return moodData;
    }

    public LiveData<String> getMonthYear() {
        return monthYear;
    }

    public LiveData<Integer> getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(int color) {
        selectedColor.setValue(color);
    }

    public void loadMoodData() {
        String monthYearKey = getMonthYearKey();

        db.collection("emotional_calendar")
                .document(userId)
                .collection(monthYearKey)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<Integer, Integer> data = new HashMap<>();
                        if (task.getResult() != null) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document != null && document.exists()) {
                                    int day = document.getLong("day").intValue();
                                    int color = document.getLong("color").intValue();
                                    data.put(day, color);
                                }
                            }
                            moodData.setValue(data);
                        }
                    } else {
                        Log.e("EmotionalCalendarVM", "Error loading data: " + task.getException().getMessage());
                    }
                });
    }

    public void saveMoodData(int day, int color) {
        Map<String, Object> moodData = new HashMap<>();
        moodData.put("day", day);
        moodData.put("color", color);

        String monthYearKey = getMonthYearKey();

        db.collection("emotional_calendar")
                .document(userId)
                .collection(monthYearKey)
                .document(String.valueOf(day))
                .set(moodData)
                .addOnSuccessListener(aVoid -> {
                    // Success callback
                })
                .addOnFailureListener(e -> {
                    Log.e("EmotionalCalendarVM", "Failed to save data: " + e.getMessage());
                });
    }

    public void changeMonth(int offset) {
        calendar.add(Calendar.MONTH, offset);
        updateMonthYear();
        loadMoodData();
    }

    private void updateMonthYear() {
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        String monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        monthYear.setValue(String.format("%s %d", monthName, year));
    }

    private String getMonthYearKey() {
        int month = calendar.get(Calendar.MONTH) + 1; // Meses são 0-indexados
        int year = calendar.get(Calendar.YEAR);
        return String.format("%04d-%02d", year, month);
    }
}
