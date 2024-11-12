package com.example.studyguider.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ControleNotasEditActivity extends AppCompatActivity {

    private HeaderViewModel headerViewModel;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controle_notas_edit);

        // Define orientação da tela para retrato
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        headerViewModel = new ViewModelProvider(this).get(HeaderViewModel.class);


        View headerView = findViewById(R.id.header);
        HeaderActivity headerActivity = new HeaderActivity(headerView, headerViewModel, this);


        FirebaseUser currentUser1 = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser1 != null) {
            headerViewModel.fetchUsername(currentUser1);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Inicializa os campos de entrada para edição das notas
        TextInputEditText nomeMateriaET = findViewById(R.id.nomeMateriaET);
        TextInputEditText notaCredET = findViewById(R.id.notaCredET);
        TextInputEditText notaTrabET = findViewById(R.id.notaTrabET);
        TextInputEditText notaListaET = findViewById(R.id.notaListaET);
        TextInputEditText notaProvaET = findViewById(R.id.notaProvaET);

        TextView notaPrecisoTxt = findViewById(R.id.notaPrecisoTxt);  // Campo para exibir a nota necessária

        MaterialButton save = findViewById(R.id.save); // Botão para salvar as alterações
        MaterialButton delete = findViewById(R.id.delete); // Botão para excluir as notas

        // Define os valores atuais das notas nos campos de entrada
        nomeMateriaET.setText(App.notas.getNomeMateria());
        notaCredET.setText(App.notas.getCred());
        notaListaET.setText(App.notas.getList());
        notaTrabET.setText(App.notas.getTrab());
        notaPrecisoTxt.setText(App.notas.getPre());
        notaProvaET.setText(App.notas.getProva());

        // Logs para ajudar no diagnóstico dos valores que estão sendo carregados
        Log.d("EditNotas", "nomeMateria: " + App.notas.getNomeMateria());
        Log.d("EditNotas", "notaCred: " + App.notas.getCred());
        Log.d("EditNotas", "notaLista: " + App.notas.getList());
        Log.d("EditNotas", "notaTrab: " + App.notas.getTrab());
        Log.d("EditNotas", "notaPreciso: " + App.notas.getPre());
        Log.d("EditNotas", "notaProva: " + App.notas.getProva());


        // Lógica de exclusão de notas
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(ControleNotasEditActivity.this)
                        .setTitle("Confirmar Exclusão")
                        .setMessage("Tem certeza que deseja deletar essas notas?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                db.collection("notas").document(App.notas.getId()).delete()
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
        // Lógica de salvamento de alterações
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ControleNotasEditActivity.this)
                        .setTitle("Confirmar Alteração")
                        .setMessage("Tem certeza que deseja salvar as alterações?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String notaPreciso;

                                // Recupera e valida os valores inseridos nos campos de entrada
                                String nomeMateria = Objects.requireNonNull(nomeMateriaET.getText()).toString().trim();
                                String notaCred = Objects.requireNonNull(notaCredET.getText()).toString().trim();
                                String notaTrab = Objects.requireNonNull(notaTrabET.getText()).toString().trim();
                                String notaList = Objects.requireNonNull(notaListaET.getText()).toString().trim();
                                String notaPro = Objects.requireNonNull(notaProvaET.getText()).toString().trim();

                                if (nomeMateria.isEmpty() || notaCred.isEmpty() || notaTrab.isEmpty() || notaList.isEmpty() || notaPro.isEmpty()) {
                                    Toast.makeText(ControleNotasEditActivity.this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show();
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
                                        notaPreciso = "Não precisa de pontos para passsar!!!" ;
                                        Toast.makeText(ControleNotasEditActivity.this, "Não está de recuperação!!", Toast.LENGTH_SHORT).show();
                                        updateUserRecCount(-1);
                                    }

                                }

                                // Cria um mapa para atualizar as informações da nota no Firestore
                                Map<String, Object> notas = new HashMap<>();
                                notas.put("nomeMateria", Objects.requireNonNull(nomeMateriaET.getText()).toString());
                                notas.put("cred", Objects.requireNonNull(notaCredET.getText()).toString());
                                notas.put("trab", Objects.requireNonNull(notaTrabET.getText()).toString());
                                notas.put("list", Objects.requireNonNull(notaListaET.getText()).toString());
                                notas.put("pre", Objects.requireNonNull(notaPreciso).toString());
                                notas.put("prova", Objects.requireNonNull(notaProvaET.getText()).toString());

                                db.collection("notas").document(App.notas.getId()).update(notas)
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
        // Log para o ID da nota sendo editada
        Log.d("EditNotas", "App.notas.getId(): " + App.notas.getId());

    }

    public void updateUserRecCount(int count) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            return;
        }

        DocumentReference userDocRef = db.collection("user").document(currentUser.getUid());

        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(userDocRef);
            long currentAbsenceCount = snapshot.contains("rec") ? snapshot.getLong("rec") : 0;
            long newAbsenceCount = currentAbsenceCount + count;
            transaction.update(userDocRef, "rec", newAbsenceCount);
            return null;
        }).addOnSuccessListener(aVoid -> {
            // Successfully updated
            Log.d("db", "Successful update of absence count");
        }).addOnFailureListener(e -> {
            Log.d("db_error", "Error updating absence count: " + e.toString());
        });
    }
}