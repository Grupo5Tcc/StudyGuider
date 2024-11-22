package com.example.studyguider.view;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studyguider.R;
import com.example.studyguider.adapter.NotasAdapter;
import com.example.studyguider.models.Notas;
import com.example.studyguider.viewmodels.HeaderViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ControleNotasActivity extends AppCompatActivity {

    private HeaderViewModel headerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_grades);

        // Define orientação da tela para retrato
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        FirebaseApp.initializeApp(ControleNotasActivity.this);

        headerViewModel = new ViewModelProvider(this).get(HeaderViewModel.class);


        View headerView = findViewById(R.id.header);
        HeaderActivity headerActivity = new HeaderActivity(headerView, headerViewModel, this);

        // Verifica se o usuário está autenticado e busca o nome de usuário
        FirebaseUser currentUser1 = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser1 != null) {
            headerViewModel.fetchUsername(currentUser1);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = findViewById(R.id.recycler);

        FloatingActionButton add = findViewById(R.id.addNota);
        add.setOnClickListener(view -> startActivity(new Intent(ControleNotasActivity.this, ControleNotasAddActivity.class)));

        // Verifica o usuário autenticado para carregar seus dados específicos
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("grades").document(userId).collection("userGrades")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // Exibe mensagem de erro caso falhe ao carregar dados
                            Toast.makeText(ControleNotasActivity.this, "Falha ao carregar dados: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            // Processa e exibe as notas no RecyclerView
                            ArrayList<Notas> arrayList = new ArrayList<>();
                            assert value != null;
                            for (QueryDocumentSnapshot document : value) {
                                Notas notas = document.toObject(Notas.class);
                                notas.setId(document.getId());
                                arrayList.add(notas);
                            }
                            // Define o adapter para o RecyclerView e atualiza a lista de notas
                            NotasAdapter adapter = new NotasAdapter(ControleNotasActivity.this, arrayList);
                            recyclerView.setAdapter(adapter);

                            // Configura o evento de clique para editar uma nota
                            adapter.setOnItemClickListener(new NotasAdapter.OnItemClickListener() {
                                @Override
                                public void onClick(Notas notas) {
                                    App.notas = notas; // Define a nota selecionada
                                    startActivity(new Intent(ControleNotasActivity.this, ControleNotasEditActivity.class));
                                }
                            });
                        }
                    }
                });
        }
    }
}
