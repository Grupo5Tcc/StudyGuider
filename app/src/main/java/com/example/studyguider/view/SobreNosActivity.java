package com.example.studyguider.view;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.studyguider.R;
import com.example.studyguider.viewmodels.HeaderViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SobreNosActivity extends AppCompatActivity {

    private HeaderViewModel headerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ativa o modo de bordas para uma experiência em tela cheia
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sobre_nos);
        // Define orientação da tela para retrato
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Ajusta o layout para considerar as barras de sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        headerViewModel = new ViewModelProvider(this).get(HeaderViewModel.class);

        View headerView = findViewById(R.id.header);
        HeaderActivity headerActivity = new HeaderActivity(headerView, headerViewModel, this);

        // Obtém o usuário autenticado e busca o nome do usuário, se ele estiver logado
        FirebaseUser currentUser1 = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser1 != null) {
            headerViewModel.fetchUsername(currentUser1);
        }

    }
}