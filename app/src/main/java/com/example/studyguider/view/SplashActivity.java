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

        splashViewModel.isUserAuthenticated().observe(this, isAuthenticated -> {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> {
                        if (isAuthenticated) {
                            gotoMenuActivity();
                        } else {
                            gotoMainActivity();
                        }
                    });
                }
            };
            timer.schedule(timerTask, 2500);
        });
    }

    private void gotoMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void gotoMenuActivity() {
        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
