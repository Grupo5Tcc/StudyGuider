package com.example.studyguider.view;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.studyguider.R;
import com.example.studyguider.viewmodels.PerfilViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {

    private PerfilViewModel profileViewModel;
    private ProgressBar progressBar;
    private TextView textViewName, textViewEmail, textViewDateOfBirth, textViewAbsence, textViewRecovery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Define orientação da tela para retrato
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializa os elementos da interface
        initUI();

        // Configura o ViewModel para gerenciar e observar dados do perfil do usuário
        profileViewModel = new ViewModelProvider(this).get(PerfilViewModel.class);
        observeViewModel();

        // Solicita o carregamento dos dados do perfil do usuário
        profileViewModel.fetchUserProfile();

        // Configura o botão de logout para sair da conta do Firebase e retornar à MainActivity
        Button buttonLogOut = findViewById(R.id.btnLogOut);
        buttonLogOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();  // Encerra a Activity atual
        });
    }

    private void initUI() {
        // Inicializa os componentes de interface para exibir os dados do perfil
        progressBar = findViewById(R.id.pgbLoading);
        textViewName = findViewById(R.id.lblNameLoading);
        textViewEmail = findViewById(R.id.lblEmailLoading);
        textViewDateOfBirth = findViewById(R.id.lblDateOfBirthLoading);
        textViewAbsence = findViewById(R.id.lblAbsenceLoading);
    }

    private void observeViewModel() {
        // Observa mudanças no perfil do usuário para atualizar a interface
        profileViewModel.getUserProfile().observe(this, userProfile -> {
            if (userProfile != null) {
                textViewName.setText(userProfile.getName());
                textViewEmail.setText(userProfile.getEmail());
                textViewDateOfBirth.setText(userProfile.getDateOfBirth());
                textViewAbsence.setText(String.valueOf(userProfile.getAbsence()));

            }
        });

        // Observa o estado de carregamento para exibir ou ocultar a progressBar
        profileViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
