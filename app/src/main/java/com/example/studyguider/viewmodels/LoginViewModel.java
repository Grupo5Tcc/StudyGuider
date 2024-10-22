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

    public void loginUser(Login user) {
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
            Toast.makeText(getApplication(), "Please enter your email", Toast.LENGTH_LONG).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getApplication(), "Please enter a valid email", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean isValidPassword(String password) {
        if (password.isEmpty()) {
            Toast.makeText(getApplication(), "Please enter your full password", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void resetPassword(String email) {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getApplication(), "Enter your registered email", Toast.LENGTH_SHORT).show();
            return;
        }
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
