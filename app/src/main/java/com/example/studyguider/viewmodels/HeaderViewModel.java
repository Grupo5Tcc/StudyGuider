package com.example.studyguider.viewmodels;

import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.studyguider.view.ProfileActivity;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class HeaderViewModel extends ViewModel {
    private MutableLiveData<String> username = new MutableLiveData<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<String> getUsername() {
        return username;
    }

    public void fetchUsername(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            String userID = firebaseUser.getUid();
            DocumentReference documentReference = db.collection("user").document(userID);
            documentReference.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            username.setValue(name);
                        } else {
                            username.setValue("User");
                        }
                    })
                    .addOnFailureListener(e -> {
                        username.setValue("Error");
                    });
        }
    }

    public void onProfileImageClicked(Context context) {
        Intent intent = new Intent(context, ProfileActivity.class);
        context.startActivity(intent);
    }
}
