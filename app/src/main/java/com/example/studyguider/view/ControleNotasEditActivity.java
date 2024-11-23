package com.example.studyguider.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studyguider.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ControleNotasEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_notas);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        TextView nomeMateriaTxt = findViewById(R.id.nomeMateriaTxt); // Alterado para TextView
        TextInputEditText notaCredET = findViewById(R.id.notaCredET);
        TextInputEditText notaTrabET = findViewById(R.id.notaTrabET);
        TextInputEditText notaListaET = findViewById(R.id.notaListaET);
        TextInputEditText notaProvaET = findViewById(R.id.notaProvaET);

        TextView notaPrecisoTxt = findViewById(R.id.notaPrecisoTxt);

        MaterialButton save = findViewById(R.id.save);
        MaterialButton delete = findViewById(R.id.delete);

        nomeMateriaTxt.setText(App.notas.getNomeMateria());
        notaCredET.setText(App.notas.getCred());
        notaListaET.setText(App.notas.getList());
        notaTrabET.setText(App.notas.getTrab());
        notaPrecisoTxt.setText(App.notas.getPre());
        notaProvaET.setText(App.notas.getProva());

        Log.d("EditNotas", "nomeMateria: " + App.notas.getNomeMateria());

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ControleNotasEditActivity.this)
                        .setTitle("Confirmar Exclusão")
                        .setMessage("Tem certeza que deseja deletar essas notas?")
                        .setPositiveButton("Sim", (dialog, which) -> {
                            db.collection("notas").document(App.notas.getId()).delete()
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(ControleNotasEditActivity.this, "Notas Deletada Com Sucesso!!", Toast.LENGTH_SHORT).show();
                                        Intent resultIntent = new Intent();
                                        setResult(RESULT_OK, resultIntent);
                                        finish();
                                    }).addOnFailureListener(e -> {
                                        Log.e("EditNotas", "Erro Ao Deletar As Notas!!", e);
                                        Toast.makeText(ControleNotasEditActivity.this, "Erro Ao Deletar As Notas!!", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .setNegativeButton("Não", null)
                        .show();
            }
        });

        save.setOnClickListener(v -> {
            new AlertDialog.Builder(ControleNotasEditActivity.this)
                    .setTitle("Confirmar Alteração")
                    .setMessage("Tem certeza que deseja salvar as alterações?")
                    .setPositiveButton("Sim", (dialog, which) -> {

                        String nomeMateria = App.notas.getNomeMateria(); // Nome da matéria já definido
                        String notaCred = Objects.requireNonNull(notaCredET.getText()).toString().trim();
                        String notaTrab = Objects.requireNonNull(notaTrabET.getText()).toString().trim();
                        String notaList = Objects.requireNonNull(notaListaET.getText()).toString().trim();
                        String notaPro = Objects.requireNonNull(notaProvaET.getText()).toString().trim();

                        if (notaCred.isEmpty() || notaTrab.isEmpty() || notaList.isEmpty() || notaPro.isEmpty()) {
                            Toast.makeText(this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        float ntCred = Float.parseFloat(notaCred);
                        float ntTrab = Float.parseFloat(notaTrab);
                        float ntList = Float.parseFloat(notaList);
                        float ntSoma = ntCred + ntTrab + ntList;

                        String notaPreciso = (ntSoma < 6) ? String.valueOf(6 - ntSoma) : "Não precisa de pontos para passar!!!";

                        db.collection("subjects").whereEqualTo("userSubjects", nomeMateria).get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    String nomeProf = "Desconhecido"; // Valor padrão
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        nomeProf = queryDocumentSnapshots.getDocuments().get(0).getString("nomeProf");
                                        if (nomeProf == null || nomeProf.isEmpty()) {
                                            nomeProf = "Desconhecido";
                                        }
                                    }

                                    Map<String, Object> notas = new HashMap<>();
                                    notas.put("nomeMateria", nomeMateria);
                                    notas.put("cred", notaCred);
                                    notas.put("trab", notaTrab);
                                    notas.put("list", notaList);
                                    notas.put("pre", notaPreciso);
                                    notas.put("prova", notaPro);
                                    notas.put("nomeProf", nomeProf);

                                    db.collection("notas").document(App.notas.getId()).update(notas)
                                            .addOnSuccessListener(unused -> {
                                                Toast.makeText(this, "Notas Alteradas Com Sucesso!!", Toast.LENGTH_SHORT).show();
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e("EditNotas", "Erro ao salvar as notas", e);
                                                Toast.makeText(this, "Erro ao salvar as notas", Toast.LENGTH_SHORT).show();
                                            });

                                })
                                .addOnFailureListener(e -> {
                                    Log.e("EditNotas", "Erro ao buscar o professor", e);
                                    Toast.makeText(this, "Erro ao buscar o professor", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("Não", null)
                    .show();
        });

        Log.d("EditNotas", "App.notas.getId(): " + App.notas.getId());
    }
}
