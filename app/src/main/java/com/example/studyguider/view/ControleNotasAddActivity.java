package com.example.studyguider.view;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ControleNotasAddActivity extends AppCompatActivity {

    private HeaderViewModel headerViewModel;
    private static final String TAG = "AddNotas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_controle_notas_add);
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
        TextInputEditText notaCredET = findViewById(R.id.notaCredET);
        TextInputEditText notaTrabET = findViewById(R.id.notaTrabET);
        TextInputEditText notaListaET = findViewById(R.id.notaListaET);
        TextInputEditText notaPrecisoET = findViewById(R.id.notaPrecisoET);
        TextInputEditText notaProvaET = findViewById(R.id.notaProvaET);

        MaterialButton addNotas = findViewById(R.id.addNota);

        addNotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nomeMateria = Objects.requireNonNull(nomeMateriaET.getText()).toString().trim();
                String notaCred = Objects.requireNonNull(notaCredET.getText()).toString().trim();
                String notaTrab = Objects.requireNonNull(notaTrabET.getText()).toString().trim();
                String notaList = Objects.requireNonNull(notaListaET.getText()).toString().trim();
                String notaPro = Objects.requireNonNull(notaProvaET.getText()).toString().trim();

                if (nomeMateria.isEmpty() || notaCred.isEmpty() || notaTrab.isEmpty() || notaList.isEmpty() || notaPro.isEmpty()) {
                    Toast.makeText(ControleNotasAddActivity.this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> notas = new HashMap<>();
                notas.put("nomeMateria", Objects.requireNonNull(nomeMateriaET.getText()).toString());
                notas.put("cred", Objects.requireNonNull(notaCredET.getText()).toString());
                notas.put("trab", Objects.requireNonNull(notaTrabET.getText()).toString());
                notas.put("list", Objects.requireNonNull(notaListaET.getText()).toString());
                notas.put("pre", Objects.requireNonNull(notaPrecisoET.getText()).toString());
                notas.put("prova", Objects.requireNonNull(notaProvaET.getText()).toString());

                db.collection("grades").add(notas).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(ControleNotasAddActivity.this, "Notas adicionadas com sucesso!!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ControleNotasAddActivity.this, "Falha Ao Tentar Adicionar Matéria: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Erro ao adicionar matéria", e);
                    }
                });
            }
        });

    }

}