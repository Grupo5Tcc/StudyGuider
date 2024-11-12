package com.example.studyguider.view;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studyguider.R;
import com.example.studyguider.adapter.PlantoesAdapter;
import com.example.studyguider.models.Plantoes;
import com.example.studyguider.viewmodels.HeaderViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PlantoesActivity extends AppCompatActivity {

    private HeaderViewModel headerViewModel;
    private RecyclerView recyclerView;
    private FloatingActionButton add, refresh;
    private PlantoesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);   // Ativa o modo de tela cheia para uma experiência imersiva
        setContentView(R.layout.activity_shift);

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

        FirebaseUser currentUser1 = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser1 != null) {
            headerViewModel.fetchUsername(currentUser1);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = findViewById(R.id.recycler);

        // Configura o botão para adicionar um novo plantão, redirecionando para a tela de adição
        FloatingActionButton add = findViewById(R.id.addShift);
        add.setOnClickListener(view -> startActivity(new Intent(PlantoesActivity.this, PlantoesAddActivity.class)));

        // Verifica o usuário autenticado para carregar seus dados específicos
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Define uma consulta ao Firestore para buscar os plantões do usuário atual
            db.collection("shifts").document(userId).collection("userShifts")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            // Tratamento de erro ao buscar dados
                            if (error != null) {
                                Toast.makeText(PlantoesActivity.this, "Falha ao carregar dados: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                return;
                            }

                            ArrayList<Plantoes> arrayList = new ArrayList<>();
                            assert value != null;
                            for (QueryDocumentSnapshot document : value) {
                                Plantoes shifts = document.toObject(Plantoes.class);
                                shifts.setId(document.getId());
                                arrayList.add(shifts);
                            }

                            // Configura o adapter para exibir a lista de plantões no RecyclerView
                            PlantoesAdapter adapter = new PlantoesAdapter(PlantoesActivity.this, arrayList);
                            recyclerView.setAdapter(adapter);

                            // Define um listener para clique nos itens do RecyclerView, redirecionando para edição do plantão
                            adapter.setOnItemClickListener(new PlantoesAdapter.OnItemClickListener() {
                                @Override
                                public void onClick(Plantoes shifts) {
                                    App.plantoes = shifts;
                                    startActivity(new Intent(PlantoesActivity.this, PlantoesEditActivity.class));
                                }
                            });
                        }
                    });
        }

    }
}