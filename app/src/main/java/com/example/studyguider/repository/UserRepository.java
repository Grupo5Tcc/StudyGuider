package com.example.studyguider.repository;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.studyguider.models.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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

    public LiveData<UserProfile> getUserProfile() {
        MutableLiveData<UserProfile> userProfileLiveData = new MutableLiveData<>();

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
                                ? documentSnapshot.getLong("absence").intValue()  // Conversão para int
                                : 0;  // Valor padrão, se o campo não existir

                        UserProfile userProfile = new UserProfile(name, email, dob, absence);
                        userProfileLiveData.setValue(userProfile);
                    } else {
                        Log.e("UserRepository", "User document not found");
                    }
                })
                .addOnFailureListener(e -> Log.e("UserRepository", "Error fetching user profile: " + e.getMessage()));

        return userProfileLiveData;
    }
}
