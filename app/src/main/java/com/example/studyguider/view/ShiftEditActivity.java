package com.example.studyguider.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.studyguider.R;
import com.example.studyguider.models.Shift;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ShiftEditActivity extends AppCompatActivity {

    private EditText professorET, materiaET, diaET, horaET;
    private Button save;
    private Shift shiftEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shift_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Inicialização dos campos
        professorET = findViewById(R.id.professorET);
        materiaET = findViewById(R.id.materiaET);
        diaET = findViewById(R.id.diaET);
        horaET = findViewById(R.id.horaET);
        save = findViewById(R.id.save);

        professorET.setText(App.plantoes.getProfessor());
        materiaET.setText(App.plantoes.getMateria());
        diaET.setText(App.plantoes.getDia());
        horaET.setText(App.plantoes.getHora());

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> updates = new HashMap<>();
                updates.put("professor", Objects.requireNonNull(professorET.getText()).toString());
                updates.put("materia", Objects.requireNonNull(materiaET.getText()).toString());
                updates.put("dia", Objects.requireNonNull(diaET.getText()).toString());
                updates.put("hora", Objects.requireNonNull(horaET.getText()).toString());


                db.collection("shifts").document(App.plantoes.getId()).set(shiftEdit).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ShiftEditActivity.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShiftEditActivity.this, "Error while saving users", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
}