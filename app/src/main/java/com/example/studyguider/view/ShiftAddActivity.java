package com.example.studyguider.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studyguider.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ShiftAddActivity extends AppCompatActivity {

    private EditText professorET, materiaET, diaET, horaET;
    private Button addUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shift_add);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicialização dos campos e do Firestore
        professorET = findViewById(R.id.professorET);
        materiaET = findViewById(R.id.materiaET);
        diaET = findViewById(R.id.diaET);
        horaET = findViewById(R.id.horaET);
        addUser = findViewById(R.id.addUser);
        db = FirebaseFirestore.getInstance();

        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String professor = Objects.requireNonNull(professorET.getText()).toString().trim();
                String materia = Objects.requireNonNull(materiaET.getText()).toString().trim();
                String dia = Objects.requireNonNull(diaET.getText()).toString().trim();
                String hora = Objects.requireNonNull(horaET.getText()).toString().trim();

                if (professor.isEmpty() || materia.isEmpty() || dia.isEmpty() || hora.isEmpty()) {
                    Toast.makeText(ShiftAddActivity.this, "Todos os campos devem ser preenchidos", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> user = new HashMap<>();
                user.put("professor", professor);
                user.put("materia", materia);
                user.put("dia", dia);
                user.put("hora", hora);

                db.collection("shifts").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(ShiftAddActivity.this, "Usuário adicionado com sucesso", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShiftAddActivity.this, "Erro ao adicionar usuário: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
}