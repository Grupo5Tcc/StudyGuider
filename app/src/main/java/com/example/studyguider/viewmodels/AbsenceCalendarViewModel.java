package com.example.studyguider.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AbsenceCalendarViewModel extends ViewModel {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final MutableLiveData<Map<String, Object>> absenceData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public void loadFaltas(String userId, String monthYearKey) {
        isLoading.setValue(true);
        db.collection("absence_calendar").document(userId).collection(monthYearKey)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        errorMessage.setValue("Failed to load entries");
                        isLoading.setValue(false);
                        return;
                    }
                    Map<String, Object> result = new HashMap<>();
                    if (querySnapshot != null) {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            result.put(document.getId(), document.getData());
                        }
                        absenceData.setValue(result);
                    }
                    isLoading.setValue(false);
                });
    }

    public LiveData<Map<String, Object>> getAbsenceData() {
        return absenceData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

   public void saveFalta(String userId, String monthYearKey, String day, Map<String, Object> falta) {
        DocumentReference docRef = db.collection("absence_calendar").document(userId)
                .collection(monthYearKey).document(day);

        docRef.set(falta).addOnSuccessListener(aVoid -> {
            // Successfully saved
        }).addOnFailureListener(e -> {
            errorMessage.setValue("Failed to save the entry");
            Log.w("TAG", "Error saving document", e);
        });
    }

    public void removeFalta(String userId, String monthYearKey, int day) {
        isLoading.setValue(true);

        DocumentReference docRef = db.collection("absence_calendar")
                .document(userId)
                .collection(monthYearKey)
                .document(String.valueOf(day));

        docRef.delete()
                .addOnSuccessListener(aVoid -> {
                    // Successfully deleted
                    loadFaltas(userId, monthYearKey); // Reload data to reflect changes
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    errorMessage.setValue("Failed to delete the entry");
                    Log.w("AbsenceCalendarViewModel", "Error deleting document", e);
                })
                .addOnCompleteListener(task -> isLoading.setValue(false));
    }

    /**********************************************/

    public void updateUserAbsenceCount(int count) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        DocumentReference userDocRef = db.collection("user").document(currentUser.getUid());
        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(userDocRef);
            long currentAbsenceCount = snapshot.contains("absence") ? snapshot.getLong("absence") : 0;
            long newAbsenceCount = currentAbsenceCount + count;
            transaction.update(userDocRef, "absence", newAbsenceCount);
            return null;
        }).addOnSuccessListener(aVoid -> {
            // Successful update
        }).addOnFailureListener(e -> {
            Log.d("db_error", "Error updating absence count: " + e.toString());
        });
    }
}
