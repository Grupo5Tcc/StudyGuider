package com.example.studyguider.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studyguider.R;
import com.example.studyguider.models.Shift;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditShiftActivity extends AppCompatActivity {

    private EditText professorET, materiaET, diaET, horaET;
    private Button save;
    private FirebaseFirestore db;
    private Shift user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shift);

        // Inicialização dos campos e do Firestore
        professorET = findViewById(R.id.professorET);
        materiaET = findViewById(R.id.materiaET);
        diaET = findViewById(R.id.diaET);
        horaET = findViewById(R.id.horaET);
        save = findViewById(R.id.save);
        db = FirebaseFirestore.getInstance();

        // Obtendo usuário passado pela ShiftsActivity
        user = App.plantoes;
        if (user != null) {
            professorET.setText(user.getProfessor());
            materiaET.setText(user.getMateria());
            diaET.setText(user.getDia());
            horaET.setText(user.getHora());
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String professor = Objects.requireNonNull(professorET.getText()).toString().trim();
                String materia = Objects.requireNonNull(materiaET.getText()).toString().trim();
                String dia = Objects.requireNonNull(diaET.getText()).toString().trim();
                String hora = Objects.requireNonNull(horaET.getText()).toString().trim();

                if (professor.isEmpty() || materia.isEmpty() || dia.isEmpty() || hora.isEmpty()) {
                    Toast.makeText(EditShiftActivity.this, "Todos os campos devem ser preenchidos", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> updates = new HashMap<>();
                updates.put("professor", professor);
                updates.put("materia", materia);
                updates.put("dia", dia);
                updates.put("hora", hora);

                db.collection("shifts").document(user.getId()).update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditShiftActivity.this, "Dados atualizados com sucesso", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditShiftActivity.this, "Erro ao atualizar dados: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
