package com.example.studyguider.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.studyguider.R;
import com.example.studyguider.viewmodels.HeaderViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GradesEditActivity extends AppCompatActivity {
    private HeaderViewModel headerViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        headerViewModel = new ViewModelProvider(this).get(HeaderViewModel.class);

        View headerView = findViewById(R.id.header);
        HeaderActivity headerActivity = new HeaderActivity(headerView, headerViewModel, this);

        // Configurações do cabeçalho
        FirebaseUser currentUser1 = FirebaseAuth.getInstance().getCurrentUser ();
        if (currentUser1 != null) {
            headerViewModel.fetchUsername(currentUser1);
        }

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

        nomeMateriaTxt.setText(App.grades.getNomeMateria());
        notaCredET.setText(App.grades.getCred());
        notaListaET.setText(App.grades.getList());
        notaTrabET.setText(App.grades.getTrab());
        notaPrecisoTxt.setText(App.grades.getPre());
        notaProvaET.setText(App.grades.getProva());

        Log.d("EditNotas", "nomeMateria: " + App.grades.getNomeMateria());

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(GradesEditActivity.this)
                        .setTitle("Confirmar Exclusão")
                        .setMessage("Tem certeza que deseja deletar essas grades?")
                        .setPositiveButton("Sim", (dialog, which) -> {
                            db.collection("grades").document(App.grades.getId()).delete()
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(GradesEditActivity.this, "Grades Deletada Com Sucesso!!", Toast.LENGTH_SHORT).show();
                                        Intent resultIntent = new Intent();
                                        setResult(RESULT_OK, resultIntent);
                                        finish();
                                    }).addOnFailureListener(e -> {
                                        Log.e("EditNotas", "Erro Ao Deletar As Grades!!", e);
                                        Toast.makeText(GradesEditActivity.this, "Erro Ao Deletar As Grades!!", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .setNegativeButton("Não", null)
                        .show();
            }
        });

        save.setOnClickListener(v -> {
            new AlertDialog.Builder(GradesEditActivity.this)
                    .setTitle("Confirmar Alteração")
                    .setMessage("Tem certeza que deseja salvar as alterações?")
                    .setPositiveButton("Sim", (dialog, which) -> {

                        String nomeMateria = App.grades.getNomeMateria(); // Nome da matéria já definido
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

                                    db.collection("grades").document(App.grades.getId()).update(notas)
                                            .addOnSuccessListener(unused -> {
                                                Toast.makeText(this, "Grades Alteradas Com Sucesso!!", Toast.LENGTH_SHORT).show();
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e("EditNotas", "Erro ao salvar as grades", e);
                                                Toast.makeText(this, "Erro ao salvar as grades", Toast.LENGTH_SHORT).show();
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

        Log.d("EditNotas", "App.grades.getId(): " + App.grades.getId());
    }
}
