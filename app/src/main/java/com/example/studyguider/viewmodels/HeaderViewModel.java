package com.example.studyguider.viewmodels;

import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.studyguider.view.PerfilActivity;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class HeaderViewModel extends ViewModel {
    // LiveData que mantém o nome do usuário para ser observado
    private MutableLiveData<String> username = new MutableLiveData<>();
    // Instância do Firestore para operações no banco de dados
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Retorna o nome do usuário observável
    public LiveData<String> getUsername() {
        return username;
    }

    // Obtém o nome do usuário a partir do Firestore usando o ID do usuário autenticado
    public void fetchUsername(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            String userID = firebaseUser.getUid();
            DocumentReference documentReference = db.collection("user").document(userID);
            documentReference.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        // Define o nome do usuário se encontrado, senão define como "User"
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

    // Abre a tela de perfil ao clicar na imagem do perfil
    public void onProfileImageClicked(Context context) {
        Intent intent = new Intent(context, PerfilActivity.class);
        context.startActivity(intent);
    }
}
