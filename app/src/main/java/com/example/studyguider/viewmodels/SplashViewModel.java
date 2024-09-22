package com.example.studyguider.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

public class SplashViewModel extends ViewModel {

    private final MutableLiveData<Boolean> userAuthenticated = new MutableLiveData<>();

    public SplashViewModel() {
        checkUserAuthentication();
    }

    private void checkUserAuthentication() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            userAuthenticated.setValue(false); // Usuário não autenticado
        } else {
            userAuthenticated.setValue(true);  // Usuário autenticado
        }
    }

    public LiveData<Boolean> isUserAuthenticated() {
        return userAuthenticated;
    }

}
