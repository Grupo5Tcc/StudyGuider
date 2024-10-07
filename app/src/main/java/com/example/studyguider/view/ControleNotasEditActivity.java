package com.example.studyguider.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ControleNotasEditActivity extends AppCompatActivity {

    private HeaderViewModel headerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_controle_notas_edit);
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

        MaterialButton save = findViewById(R.id.save);
        MaterialButton delete = findViewById(R.id.delete);

        nomeMateriaET.setText(App.notas.getNomeMateria());
        notaCredET.setText(App.notas.getCred());
        notaListaET.setText(App.notas.getList());
        notaTrabET.setText(App.notas.getTrab());
        notaPrecisoET.setText(App.notas.getPre());
        notaProvaET.setText(App.notas.getProva());

        Log.d("EditNotas", "nomeMateria: " + App.notas.getNomeMateria());
        Log.d("EditNotas", "notaCred: " + App.notas.getCred());
        Log.d("EditNotas", "notaLista: " + App.notas.getList());
        Log.d("EditNotas", "notaTrab: " + App.notas.getTrab());
        Log.d("EditNotas", "notaPreciso: " + App.notas.getPre());
        Log.d("EditNotas", "notaProva: " + App.notas.getProva());

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(ControleNotasEditActivity.this)
                        .setTitle("Confirmar Exclusão")
                        .setMessage("Tem certeza que deseja deletar essas notas?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                db.collection("grades").document(App.notas.getId()).delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(ControleNotasEditActivity.this, "Notas Deletada Com Sucesso!!", Toast.LENGTH_SHORT).show();
                                                Intent resultIntent = new Intent();
                                                setResult(RESULT_OK, resultIntent);
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("EditNotas", "Erro Ao Deletar As Notas!!", e);
                                                Toast.makeText(ControleNotasEditActivity.this, "Erro Ao Deletar As Notas!!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("Não", null)
                        .show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ControleNotasEditActivity.this)
                        .setTitle("Confirmar Alteração")
                        .setMessage("Tem certeza que deseja salvar as alterações?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                // Verificação de campos vazios
                                if (nomeMateriaET.getText().toString().isEmpty() ||
                                        notaCredET.getText().toString().isEmpty() ||
                                        notaTrabET.getText().toString().isEmpty() ||
                                        notaListaET.getText().toString().isEmpty() ||
                                        notaPrecisoET.getText().toString().isEmpty() ||
                                        notaProvaET.getText().toString().isEmpty()) {
                                    Toast.makeText(ControleNotasEditActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                Map<String, Object> notas = new HashMap<>();
                                notas.put("nomeMateria", Objects.requireNonNull(nomeMateriaET.getText()).toString());
                                notas.put("cred", Objects.requireNonNull(notaCredET.getText()).toString());
                                notas.put("trab", Objects.requireNonNull(notaTrabET.getText()).toString());
                                notas.put("list", Objects.requireNonNull(notaListaET.getText()).toString());
                                notas.put("pre", Objects.requireNonNull(notaPrecisoET.getText()).toString());
                                notas.put("prova", Objects.requireNonNull(notaProvaET.getText()).toString());

                                db.collection("grades").document(App.notas.getId()).update(notas)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(ControleNotasEditActivity.this, "Notas Alteradas Com Sucesso!!", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("EditNotas", "Erro ao salvar as notas", e);
                                                Toast.makeText(ControleNotasEditActivity.this, "Erro ao salvar as notas", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("Não", null)
                        .show();
            }
        });
        Log.d("EditNotas", "App.notas.getId(): " + App.notas.getId());

    }
}