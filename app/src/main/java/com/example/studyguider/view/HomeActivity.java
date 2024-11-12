package com.example.studyguider.view;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.studyguider.R;
import com.example.studyguider.viewmodels.HeaderViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    private HeaderViewModel headerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Define orientação da tela para retrato
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        headerViewModel = new ViewModelProvider(this).get(HeaderViewModel.class);

        View headerView = findViewById(R.id.header);
        HeaderActivity headerActivity = new HeaderActivity(headerView, headerViewModel, this);

        // Verifica se o usuário está logado e busca o nome de usuário
        FirebaseUser currentUser1 = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser1 != null) {
            headerViewModel.fetchUsername(currentUser1);
        }

        CardView cardViewSobreNos = findViewById(R.id.cardAboutUs);
        CardView cardViewAgenda = findViewById(R.id.cardPlanner);
        CardView cardViewAfazeres = findViewById(R.id.cardToDoList);
        CardView cardViewFaltas = findViewById(R.id.cardAbsence);
        CardView cardViewEmocoes = findViewById(R.id.cardEmotionalCalendar);
        CardView cardViewNotas = findViewById(R.id.cardGrades);
        CardView cardViewRecuperacoes = findViewById(R.id.cardRecovery);
        CardView cardViewMaterias = findViewById(R.id.cardSupplies);
        CardView cardViewPlantoes = findViewById(R.id.cardSchoolDuty);

        ImageView imageProfile = findViewById(R.id.imgPerfil);

// Configura os listeners dos CardViews para abrir atividades correspondentes
        cardViewSobreNos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SobreNosActivity.class);
                startActivity(intent);
            }
        });

        // Configurações semelhantes para os outros CardViews
        cardViewAgenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AgendaActivity.class);
                startActivity(intent);
            }
        });
// Código similar repetido para os outros CardViews (cardViewAbsence, cardViewToDoList, etc.)

        cardViewFaltas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, FaltasActivity.class);
                startActivity(intent);
            }
        });

        cardViewAfazeres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AfazeresActivity.class);
                startActivity(intent);
            }
        });

        cardViewEmocoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, EmotionalCalendarActivity.class);
                startActivity(intent);
            }
        });

        cardViewNotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ControleNotasActivity.class);
                startActivity(intent);
            }
        });

        cardViewPlantoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PlantoesActivity.class);
                startActivity(intent);
            }
        });

        cardViewRecuperacoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ControleRecuperacoes.class);
                startActivity(intent);
            }
        });

        cardViewMaterias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MateriasActivity.class);
                startActivity(intent);
            }
        });

        // Configura o listener de clique para a imagem de perfil, abrindo a tela de Perfil
        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PerfilActivity.class);
                startActivity(intent);  // Inicia a atividade PerfilActivity
            }
        });

    }
}