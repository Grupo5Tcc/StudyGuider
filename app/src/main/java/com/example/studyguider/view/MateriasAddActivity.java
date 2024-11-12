package com.example.studyguider.view;


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;


import com.example.studyguider.R;
import com.example.studyguider.viewmodels.HeaderViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class MateriasAddActivity extends AppCompatActivity {

    private HeaderViewModel headerViewModel;
    private static final String TAG = "SubjectsAddActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_materias_add);

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

        TextInputEditText nomeMateriaET = findViewById(R.id.nomeMateriaET);
        TextInputEditText professorET = findViewById(R.id.professorET);
        MaterialButton addMateria = findViewById(R.id.addMateria);


        addMateria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String nomeMateria = Objects.requireNonNull(nomeMateriaET.getText()).toString().trim();
                String professor = Objects.requireNonNull(professorET.getText()).toString().trim();
                //String conteudos = Objects.requireNonNull(conteudosET.getText()).toString().trim();
                //String media = Objects.requireNonNull(mediaET.getText()).toString().trim();


                if (nomeMateria.isEmpty() || professor.isEmpty()) {
                    Toast.makeText(MateriasAddActivity.this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show();
                    return;
                }


                // Criar o mapa de dados para a matéria
                Map<String, Object> materia = new HashMap<>();
                materia.put("nomeMateria", nomeMateria);
                materia.put("professor", professor);
                //materia.put("conteudos", conteudos);
                //materia.put("media", media);


                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    db.collection("subjects").document(userId).collection("userSubjects").add(materia)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(MateriasAddActivity.this, "Materia adicionada com sucesso", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MateriasAddActivity.this, "Falha Ao Tentar Adicionar Materia: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }


    /**
     * Função para criar um novo "database" (na verdade, uma nova coleção ou documento)
     * com base em um campo específico adicionado em matérias.
     */
    private void criarNovoDatabase(FirebaseFirestore db, String nomeMateria) {
        // Novo documento ou coleção com base no campo adicionado
        Map<String, Object> newDatabaseData = new HashMap<>();
        newDatabaseData.put("nomeMateria", nomeMateria);
        newDatabaseData.put("timestamp", System.currentTimeMillis());


        // Cria uma nova coleção chamada "novoDatabase" com os dados baseados na matéria
        db.collection(nomeMateria).add(newDatabaseData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Novo banco de dados criado com sucesso baseado na matéria: " + nomeMateria);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Erro ao criar novo banco de dados baseado na matéria: " + nomeMateria, e);
                    }
                });
    }
}
