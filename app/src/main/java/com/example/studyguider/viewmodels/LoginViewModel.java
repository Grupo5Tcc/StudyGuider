package com.example.studyguider.viewmodels;

import android.app.Application;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.studyguider.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;

public class LoginViewModel extends AndroidViewModel {
    private final FirebaseAuth auth;
    public MutableLiveData<Boolean> loginSucesso;
    public MutableLiveData<Boolean> loginFalhou;
    public MutableLiveData<Boolean> visibilidadeProgressBar;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        auth = FirebaseAuth.getInstance();
        loginSucesso = new MutableLiveData<>();
        loginFalhou = new MutableLiveData<>();
        visibilidadeProgressBar = new MutableLiveData<>();
    }

    public void loginUser(UserModel user) {
        if (isValidEmail(user.getEmail()) && isValidPassword(user.getPassword())) {
            visibilidadeProgressBar.setValue(true);
            auth.signInWithEmailAndPassword(user.getEmail(), user.getPassword())
                    .addOnSuccessListener(authResult -> loginSucesso.setValue(true))
                    .addOnFailureListener(e -> {
                        loginFalhou.setValue(true);
                        visibilidadeProgressBar.setValue(false);
                    });
        }
    }

    private boolean isValidEmail(String email) {
        if (email.isEmpty()) {
            Toast.makeText(getApplication(), "Por favor, insira seu email", Toast.LENGTH_LONG).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getApplication(), "Por favor, insira um email válido", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean isValidPassword(String password) {
        if (password.isEmpty()) {
            Toast.makeText(getApplication(), "Por favor, insira sua senha completa", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void resetPassword(String email) {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getApplication(), "Insira seu email registrado", Toast.LENGTH_SHORT).show();
            return;
        }
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplication(), "Verifique seu email", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplication(), "Falha, não foi possível enviar", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
