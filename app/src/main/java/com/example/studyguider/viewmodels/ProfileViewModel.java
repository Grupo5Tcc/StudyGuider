package com.example.studyguider.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.studyguider.models.UserProfile;
import com.example.studyguider.repository.UserRepository;

public class ProfileViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public ProfileViewModel() {
        userRepository = new UserRepository();
    }

    public LiveData<UserProfile> getUserProfile() {
        return userRepository.getUserProfile(); // Assumindo que o repositório busca o perfil
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void fetchUserProfile() {
        isLoading.setValue(true);

        LiveData<UserProfile> userProfileLiveData = getUserProfile();
        userProfileLiveData.observeForever(userProfile -> {
            if (userProfile != null) {
                // Dados do usuário atualizados
            }
            isLoading.setValue(false);
        });
    }
}
