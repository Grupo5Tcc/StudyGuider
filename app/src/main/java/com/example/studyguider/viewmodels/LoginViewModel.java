package com.example.studyguider.viewmodels;

import android.app.Application;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.studyguider.models.Login;
import com.google.firebase.auth.FirebaseAuth;

public class LoginViewModel extends AndroidViewModel {
    // Instância do FirebaseAuth para autenticação
    private final FirebaseAuth auth;

    // LiveData para acompanhar o status do login e a visibilidade da progress bar
    public MutableLiveData<Boolean> loginSuccess;
    public MutableLiveData<Boolean> loginFailed;
    public MutableLiveData<Boolean> progressBarVisibility;

    // Construtor que inicializa a autenticação e os LiveData
    public LoginViewModel(@NonNull Application application) {
        super(application);
        auth = FirebaseAuth.getInstance();
        loginSuccess = new MutableLiveData<>();
        loginFailed = new MutableLiveData<>();
        progressBarVisibility = new MutableLiveData<>();
    }

    // Método para realizar o login do usuário
    public void loginUser(Login user) {
        if (isValidEmail(user.getEmail()) && isValidPassword(user.getPassword())) {
            progressBarVisibility.setValue(true); // Mostra a progress bar
            // Tenta fazer o login com email e senha
            auth.signInWithEmailAndPassword(user.getEmail(), user.getPassword())
                    .addOnSuccessListener(authResult -> loginSuccess.setValue(true)) // Login bem-sucedido
                    .addOnFailureListener(e -> {
                        loginFailed.setValue(true);  // Login falhou
                        progressBarVisibility.setValue(false);  // Esconde a progress bar
                    });
        }
    }

    // Verifica se o email fornecido é válido
    private boolean isValidEmail(String email) {
        if (email.isEmpty()) {
            Toast.makeText(getApplication(), "Entre com seu e-mail", Toast.LENGTH_LONG).show();
            return false; // Email vazio
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getApplication(), "Entre com um e-mail valido", Toast.LENGTH_LONG).show();
            return false; // Email inválido
        }
        return true;  // Email válido
    }

    // Verifica se a senha fornecida não está vazia
    private boolean isValidPassword(String password) {
        if (password.isEmpty()) {
            Toast.makeText(getApplication(), "Please enter your full password", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    // Envia um email de redefinição de senha
    public void resetPassword(String email) {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getApplication(), "Enter your registered email", Toast.LENGTH_SHORT).show();
            return;
        }
        // Tenta enviar o email de redefinição
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplication(), "Check your email", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplication(), "Failed, unable to send", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
