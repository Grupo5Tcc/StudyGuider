package com.example.studyguider.viewmodels;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.studyguider.models.User;
import com.example.studyguider.view.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterViewModel extends ViewModel {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance(); // Inicializa o FirebaseAuth para gerenciar a autenticação

    // Método para registrar um novo usuário
    public void registerUser(User user, String password, Context context) {
        // Cria um usuário com email e senha
        mAuth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { // Verifica se o registro foi bem-sucedido
                            FirebaseUser firebaseUser = mAuth.getCurrentUser(); // Obtém o usuário autenticado

                            if (firebaseUser != null) { // Verifica se o usuário não é nulo
                                // Envia um email de verificação
                                firebaseUser.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                // Notifica o usuário sobre o envio do email de verificação
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(context, "Registered user. Check your email.", Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(context, "Failed to send verification email.", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }

                            // Salva os detalhes do usuário no Firestore
                            saveUserDetails(user);
                            // Navega para a HomeActivity após o registro
                            navigateToMenu(context);
                        } else {
                            // Exibe mensagem de erro em caso de falha no registro
                            Toast.makeText(context, "Failed to register user: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // Método para salvar os detalhes do usuário no Firestore
    private void saveUserDetails(User user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance(); // Obtém a instância do Firestore
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Obtém o ID do usuário autenticado

        Map<String, Object> userMap = new HashMap<>(); // Cria um mapa para armazenar os detalhes do usuário
        userMap.put("name", user.getName());
        userMap.put("e_mail", user.getEmail());
        userMap.put("date_of_birth", user.getDateOfBirth());

        DocumentReference documentReference = db.collection("user").document(userID); // Referência ao documento do usuário
        documentReference.set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("db", "Successful saving data"); // Log de sucesso ao salvar os dados
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("db_error", "Error saving data" + e.toString()); // Log de erro ao salvar os dados
            }
        });
    }

    // Método para navegar para a HomeActivity
    private void navigateToMenu(Context context) {
        Intent intent = new Intent(context, HomeActivity.class); // Cria um Intent para iniciar a HomeActivity
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Define flags para limpar a pilha de atividades
        context.startActivity(intent); // Inicia a HomeActivity
    }
}


