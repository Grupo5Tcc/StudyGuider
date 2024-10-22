package com.example.studyguider.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EmocoesViewModel extends ViewModel {
    private final MutableLiveData<Map<Integer, Integer>> moodData = new MutableLiveData<>();
    private final MutableLiveData<String> userId = new MutableLiveData<>();
    private final MutableLiveData<String> monthYearDisplayName = new MutableLiveData<>();
    private final Calendar calendar;
    private final FirebaseFirestore db;

    public EmocoesViewModel() {
        calendar = Calendar.getInstance();
        db = FirebaseFirestore.getInstance();
        checkUserAuthentication();
        updateMonthYearDisplayName();
    }

    private void checkUserAuthentication() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId.setValue(user.getUid());
            loadMoodData();
        } else {
            userId.setValue(null);
        }
    }

    public LiveData<String> getUserId() {
        return userId;
    }

    public LiveData<String> getMonthYearDisplayName() {
        return monthYearDisplayName;
    }

    public LiveData<Map<Integer, Integer>> getMoodData() {
        return moodData;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void goToPreviousMonth() {
        calendar.add(Calendar.MONTH, -1);
        updateMonthYearDisplayName();
        loadMoodData();
    }

    public void goToNextMonth() {
        calendar.add(Calendar.MONTH, 1);
        updateMonthYearDisplayName();
        loadMoodData();
    }

    public void saveMoodData(int day, int color) {
        String userId = this.userId.getValue();
        if (userId == null) return;

        String monthYear = getMonthYearKey();

        Map<String, Object> moodDataMap = new HashMap<>();
        moodDataMap.put("day", day);
        moodDataMap.put("color", color);

        db.collection("emotional_calendar")
                .document(userId)
                .collection(monthYear)
                .document(String.valueOf(day))
                .set(moodDataMap)
                .addOnSuccessListener(aVoid -> {
                    // Dados salvos com sucesso
                })
                .addOnFailureListener(e -> {
                    // Falha ao salvar os dados
                });
    }

    public void loadMoodData() {
        String userId = this.userId.getValue();
        if (userId == null) return;

        String monthYear = getMonthYearKey();

        db.collection("emotional_calendar")
                .document(userId)
                .collection(monthYear)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Map<Integer, Integer> moodEntries = new HashMap<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            if (document.exists()) {
                                int day = document.getLong("day").intValue();
                                int color = document.getLong("color").intValue();
                                moodEntries.put(day, color);
                            }
                        }
                        moodData.setValue(moodEntries);
                    } else {
                        // Falha ao carregar os dados
                    }
                });
    }

    private void updateMonthYearDisplayName() {
        String monthName = new DateFormatSymbols().getMonths()[calendar.get(Calendar.MONTH)];
        int year = calendar.get(Calendar.YEAR);
        monthYearDisplayName.setValue(String.format("%s %d", monthName, year));
    }

    private String getMonthYearKey() {
        int month = calendar.get(Calendar.MONTH) + 1; // Meses são 0-indexados
        int year = calendar.get(Calendar.YEAR);
        return String.format("%04d-%02d", year, month);
    }
}
