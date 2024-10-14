package com.example.studyguider.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

public class SubjectsEditActivity extends AppCompatActivity {

    private HeaderViewModel headerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subjects_edit);
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
        TextInputEditText professorET = findViewById(R.id.professorET);
        MaterialButton save = findViewById(R.id.save);
        MaterialButton delete = findViewById(R.id.delete);

        nomeMateriaET.setText(App.materia.getNomeMateria());
        professorET.setText(App.materia.getProfessor());

        // Ação para deletar a matéria com confirmação
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Exibe o diálogo de confirmação
                new AlertDialog.Builder(SubjectsEditActivity.this)
                        .setTitle("Confirmar Exclusão")
                        .setMessage("Tem certeza que deseja deletar esta matéria?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Confirmado: Deletar a matéria
                                db.collection("subjects").document(App.materia.getId()).delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(SubjectsEditActivity.this, "Matéria Deletada Com Sucesso!!", Toast.LENGTH_SHORT).show();
                                                Intent resultIntent = new Intent();
                                                setResult(RESULT_OK, resultIntent);
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(SubjectsEditActivity.this, "Erro Ao Deletar A Matéria!!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("Não", null)
                        .show();
            }
        });

        // Ação para salvar as alterações com confirmação
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Exibe o diálogo de confirmação
                new AlertDialog.Builder(SubjectsEditActivity.this)
                        .setTitle("Confirmar Alteração")
                        .setMessage("Tem certeza que deseja salvar as alterações?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Map<String, Object> materia = new HashMap<>();
                                materia.put("nomeMateria", Objects.requireNonNull(nomeMateriaET.getText().toString()));
                                materia.put("professor", Objects.requireNonNull(professorET.getText().toString()));

                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                if (currentUser != null) {
                                    String userId = currentUser.getUid();
                                    db.collection("subjects").document(userId).collection("userSubjects")
                                            .document(App.materia.getId()).update(materia)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(SubjectsEditActivity.this, "Matéria Alterada Com Sucesso!!", Toast.LENGTH_SHORT).show();
                                                    Intent resultIntent = new Intent();
                                                    setResult(RESULT_OK, resultIntent);
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(SubjectsEditActivity.this, "Erro Ao Alterar Matéria!!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        })
                        .setNegativeButton("Não", null)
                        .show();
            }
        });
    }
}
