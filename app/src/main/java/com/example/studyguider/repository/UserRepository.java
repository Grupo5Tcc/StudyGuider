package com.example.studyguider.repository;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.studyguider.models.Perfil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firebaseFirestore;

    public UserRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public LiveData<Perfil> getUserProfile() {
        MutableLiveData<Perfil> userProfileLiveData = new MutableLiveData<>();

        FirebaseUser currentUser = getCurrentUser();
        if (currentUser == null) {
            Log.e("UserRepository", "No authenticated user found");
            return userProfileLiveData;
        }

        String userID = currentUser.getUid();
        DocumentReference documentReference = firebaseFirestore.collection("user").document(userID);

        documentReference.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("e_mail");
                        String dob = documentSnapshot.getString("date_of_birth");

                        // Verificando a existência do campo "absence" e convertendo para int
                        int absence = documentSnapshot.contains("absence")
                                ? documentSnapshot.getLong("absence").intValue()
                                : 0; // Valor padrão

                        // Verificando a existência do campo "recovery" e convertendo para int
                        int recovery = documentSnapshot.contains("rec")
                                ? documentSnapshot.getLong("rec").intValue()
                                : 0; // Valor padrão

                        Log.d("UserRepository", "Name: " + name + ", Email: " + email + ", DOB: " + dob + ", Absence: " + absence + ", Recovery: " + recovery);

                        Perfil userProfile = new Perfil(name, email, dob, absence, recovery); // Adiciona recuperação
                        userProfileLiveData.setValue(userProfile);
                    } else {
                        Log.e("UserRepository", "User document not found");
                    }
                })
                .addOnFailureListener(e -> Log.e("UserRepository", "Error fetching user profile: " + e.getMessage()));

        return userProfileLiveData;
    }

    public void createUserProfile(String userID, String name, String email, String dob) {
        Map<String, Object> userProfileData = new HashMap<>();
        userProfileData.put("name", name);
        userProfileData.put("e_mail", email);
        userProfileData.put("date_of_birth", dob);
        userProfileData.put("absence", 0); // ou outro valor inicial
        userProfileData.put("rec", 0); // Adiciona o campo de recuperação com valor inicial

        firebaseFirestore.collection("user").document(userID).set(userProfileData)
                .addOnSuccessListener(aVoid -> Log.d("UserRepository", "User profile created successfully"))
                .addOnFailureListener(e -> Log.e("UserRepository", "Error creating user profile: " + e.getMessage()));
    }

    public void updateRecovery(String userID, int newRecoveryValue) {
        DocumentReference documentReference = firebaseFirestore.collection("user").document(userID);

        documentReference.update("rec", newRecoveryValue)
                .addOnSuccessListener(aVoid -> Log.d("UserRepository", "Recovery updated successfully"))
                .addOnFailureListener(e -> Log.e("UserRepository", "Error updating recovery: " + e.getMessage()));
    }
}
