package com.example.studyguider.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

public class ControleNotasAddActivity extends AppCompatActivity {

    private static final String TAG = "AddNotas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_controle_notas_add);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        TextInputEditText nomeMateriaET = findViewById(R.id.nomeMateriaET);
        TextInputEditText notaCredET = findViewById(R.id.notaCredET);
        TextInputEditText notaTrabET = findViewById(R.id.notaTrabET);
        TextInputEditText notaListaET = findViewById(R.id.notaListaET);
        TextInputEditText notaProvaET = findViewById(R.id.notaProvaET);

        MaterialButton addNotas = findViewById(R.id.addNota);

        addNotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String notaPreciso;

                String nomeMateria = Objects.requireNonNull(nomeMateriaET.getText()).toString().trim();
                String notaCred = Objects.requireNonNull(notaCredET.getText()).toString().trim();
                String notaTrab = Objects.requireNonNull(notaTrabET.getText()).toString().trim();
                String notaList = Objects.requireNonNull(notaListaET.getText()).toString().trim();
                String notaPro = Objects.requireNonNull(notaProvaET.getText()).toString().trim();

                if (nomeMateria.isEmpty() || notaCred.isEmpty() || notaTrab.isEmpty() || notaList.isEmpty() || notaPro.isEmpty()) {
                    Toast.makeText(ControleNotasAddActivity.this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    float ntPreciso, ntCred, ntTrab, ntList;

                    ntCred = Float.parseFloat(notaCred);
                    ntTrab = Float.parseFloat(notaTrab);
                    ntList = Float.parseFloat(notaList);

                    float ntSoma = ntCred + ntTrab + ntList;

                    if(ntSoma < 6){
                        ntPreciso = 6 - ntSoma;
                        notaPreciso = String.valueOf(ntPreciso);
                    }else{
                        notaPreciso = "Não precisa de pontos para passar!!";
                        Toast.makeText(ControleNotasAddActivity.this, "Não está de recuperação!!", Toast.LENGTH_SHORT).show();
                    }

                }

                Map<String, Object> notas = new HashMap<>();
                notas.put("nomeMateria", Objects.requireNonNull(nomeMateriaET.getText()).toString());
                notas.put("cred", Objects.requireNonNull(notaCredET.getText()).toString());
                notas.put("trab", Objects.requireNonNull(notaTrabET.getText()).toString());
                notas.put("list", Objects.requireNonNull(notaListaET.getText()).toString());
                notas.put("pre", Objects.requireNonNull(notaPreciso));
                notas.put("prova", Objects.requireNonNull(notaProvaET.getText()).toString());

                db.collection("notas").add(notas).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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