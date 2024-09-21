package com.example.studyguider.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.studyguider.models.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileViewModel extends ViewModel {
    private MutableLiveData<UserProfile> userProfileLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>(true);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<UserProfile> getUserProfileLiveData() {
        return userProfileLiveData;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchUserProfile() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            String userID = firebaseUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference documentReference = db.collection("user").document(userID);

            documentReference.get()
                    .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                // Extrai dados do Firestore
                                String name = documentSnapshot.getString("name");
                                String email = documentSnapshot.getString("e_mail");
                                String dateOfBirth = documentSnapshot.getString("date_of_birth");

                                // Verifica se o campo "absence" existe
                                String absence = documentSnapshot.contains("absence")
                                        ? documentSnapshot.getString("absence")
                                        : null;

                                // Atualiza o UserProfile no LiveData
                                UserProfile userProfile = new UserProfile(name, email, dateOfBirth, absence);
                                userProfileLiveData.setValue(userProfile);
                            } else {
                                errorMessage.setValue("Documento não encontrado.");
                            }
                            loading.setValue(false);
                        }
                    })
                    .addOnFailureListener(e -> {
                        errorMessage.setValue("Erro ao carregar os dados.");
                        Log.d("db_error", "Erro ao carregar dados: " + e.toString());
                        loading.setValue(false);
                    });
        } else {
            errorMessage.setValue("Usuário não autenticado.");
            loading.setValue(false);
        }
    }
}
