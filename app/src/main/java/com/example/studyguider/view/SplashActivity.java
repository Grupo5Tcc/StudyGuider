package com.example.studyguider.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.studyguider.R;
import com.example.studyguider.viewmodels.SplashViewModel;


import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    private final Timer timer = new Timer();
    TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SplashViewModel splashViewModel = new ViewModelProvider(this).get(SplashViewModel.class);

        // Observa a autenticação do usuário e direciona para a tela apropriada após o tempo da splash
        splashViewModel.isUserAuthenticated().observe(this, isAuthenticated -> {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> {
                        // Direciona para a Home se autenticado; caso contrário, para o login
                        if (isAuthenticated) {
                            gotoMenuActivity();
                        } else {
                            gotoMainActivity();
                        }
                    });
                }
            };
            timer.schedule(timerTask, 2500); // Splash é exibida por 2,5 segundos antes do redirecionamento

        });
    }

    // Direciona para a tela de login (MainActivity)
    private void gotoMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    // Direciona para a tela inicial do app (HomeActivity) após autenticação
    private void gotoMenuActivity() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
