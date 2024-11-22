package com.example.studyguider.repository;

// Importações necessárias
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.studyguider.models.Perfil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser ;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

// Classe que gerencia as operações relacionadas ao usuário
public class UserRepository {
    private final FirebaseAuth firebaseAuth; // Instância do FirebaseAuth para autenticação
    private final FirebaseFirestore firebaseFirestore; // Instância do Firestore para acesso ao banco de dados

    // Construtor da classe
    public UserRepository() {
        firebaseAuth = FirebaseAuth.getInstance(); // Inicializa a instância do FirebaseAuth
        firebaseFirestore = FirebaseFirestore.getInstance(); // Inicializa a instância do Firestore
    }

    // Método para obter o usuário autenticado atual
    public FirebaseUser  getCurrentUser () {
        return firebaseAuth.getCurrentUser (); // Retorna o usuário autenticado
    }

    // Método para obter o perfil do usuário
    public LiveData<Perfil> getUserProfile() {
        MutableLiveData<Perfil> userProfileLiveData = new MutableLiveData<>(); // Cria um LiveData para o perfil do usuário

        FirebaseUser  currentUser  = getCurrentUser (); // Obtém o usuário autenticado
        if (currentUser  == null) { // Verifica se não há usuário autenticado
            Log.e("User Repository", "No authenticated user found"); // Log de erro
            return userProfileLiveData; // Retorna um LiveData vazio
        }

        String userID = currentUser .getUid(); // Obtém o ID do usuário
        DocumentReference documentReference = firebaseFirestore.collection("user").document(userID); // Referência ao documento do usuário

        // Obtém os dados do documento do usuário
        documentReference.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) { // Verifica se o documento existe
                        // Obtém os dados do documento
                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("e_mail");
                        String dob = documentSnapshot.getString("date_of_birth");

                        // Verifica a existência do campo "absence" e converte para int
                        int absence = documentSnapshot.contains("absence")
                                ? documentSnapshot.getLong("absence").intValue()
                                : 0; // Valor padrão

                        // Verifica a existência do campo "recovery" e converte para int
                        int recovery = documentSnapshot.contains("rec")
                                ? documentSnapshot.getLong("rec").intValue()
                                : 0; // Valor padrão

                        // Log dos dados do usuário
                        Log.d("User Repository", "Name: " + name + ", Email: " + email + ", DOB: " + dob + ", Absence: " + absence + ", Recovery: " + recovery);

                        // Cria um objeto Perfil com os dados obtidos
                        Perfil userProfile = new Perfil(name, email, dob, absence, recovery);
                        userProfileLiveData.setValue(userProfile); // Atualiza o LiveData com o perfil do usuário
                    } else {
                        Log.e("User Repository", "User  document not found"); // Log de erro se o documento não for encontrado
                    }
                })
                .addOnFailureListener(e -> Log.e("User Repository", "Error fetching user profile: " + e.getMessage())); // Log de erro ao buscar o perfil

        return userProfileLiveData; // Retorna o LiveData com o perfil do usuário
    }

    // Método para criar o perfil do usuário
    public void createUserProfile(String userID, String name, String email, String dob) {
        Map<String, Object> userProfileData = new HashMap<>(); // Cria um mapa para armazenar os dados do perfil
        userProfileData.put("name", name); // Adiciona o nome ao mapa
        userProfileData.put("e_mail", email); // Adiciona o e-mail ao mapa
        userProfileData.put("date_of_birth", dob); // Adiciona a data de nascimento ao mapa
        userProfileData.put("absence", 0); // Adiciona o campo de ausência com valor inicial
        userProfileData.put("rec", 0); // Adiciona o campo de recuperação com valor inicial

        // Cria o documento do usuário no Firestore
        firebaseFirestore.collection("user").document(userID).set(userProfileData)
                .addOnSuccessListener(aVoid -> Log.d("User Repository", "User  profile created successfully")) // Log de sucesso
                .addOnFailureListener(e -> Log.e("User Repository", "Error creating user profile : " + e.getMessage())); // Log de erro ao criar o perfil do usuário
    }

    // Método para atualizar o valor de recuperação do usuário
    public void updateRecovery(String userID, int newRecoveryValue) {
        DocumentReference documentReference = firebaseFirestore.collection("user").document(userID); // Referência ao documento do usuário

        // Atualiza o campo de recuperação no documento do usuário
        documentReference.update("rec", newRecoveryValue)
                .addOnSuccessListener(aVoid -> Log.d("User  Repository", "Recovery updated successfully")) // Log de sucesso
                .addOnFailureListener(e -> Log.e("User  Repository", "Error updating recovery: " + e.getMessage())); // Log de erro ao atualizar a recuperação
    }
}