package com.example.studyguider.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studyguider.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddMateriaActivity extends AppCompatActivity {

    private static final String TAG = "AddMateriaActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_materia);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        TextInputEditText nomeMateriaET = findViewById(R.id.nomeMateriaET);
        TextInputEditText professorET = findViewById(R.id.professorET);
        TextInputEditText conteudosET = findViewById(R.id.conteudosET);
        TextInputEditText mediaET = findViewById(R.id.mediaET);
        MaterialButton addMateria = findViewById(R.id.addMateria);

        addMateria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nomeMateria = Objects.requireNonNull(nomeMateriaET.getText()).toString().trim();
                String professor = Objects.requireNonNull(professorET.getText()).toString().trim();
                String conteudos = Objects.requireNonNull(conteudosET.getText()).toString().trim();
                String media = Objects.requireNonNull(mediaET.getText()).toString().trim();

                if (nomeMateria.isEmpty() || professor.isEmpty() || conteudos.isEmpty() || media.isEmpty()) {
                    Toast.makeText(AddMateriaActivity.this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Criar o mapa de dados para a matéria
                Map<String, Object> materia = new HashMap<>();
                materia.put("nomeMateria", nomeMateria);
                materia.put("professor", professor);
                materia.put("conteudos", conteudos);
                materia.put("media", media);

                // Adicionar a matéria ao Firestore
                db.collection("materia").add(materia)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(AddMateriaActivity.this, "Matéria Adicionada Com Sucesso!!", Toast.LENGTH_SHORT).show();

                                // Chamar função para criar um novo "database" (nova coleção com base no campo adicionado)
                                criarNovoDatabase(db, nomeMateria);

                                // Fechar a atividade
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddMateriaActivity.this, "Falha Ao Tentar Adicionar Matéria: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Erro ao adicionar matéria", e);
                            }
                        });
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
