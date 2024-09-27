package com.example.studyguider.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class EditMateriaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_materia);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        TextInputEditText nomeMateriaET = findViewById(R.id.nomeMateriaET);
        TextInputEditText professorET = findViewById(R.id.professorET);
        TextInputEditText conteudosET = findViewById(R.id.conteudosET);
        TextInputEditText mediaET = findViewById(R.id.mediaET);
        MaterialButton save = findViewById(R.id.save);
        MaterialButton delete = findViewById(R.id.delete);

        nomeMateriaET.setText(App.materia.getNomeMateria());
        professorET.setText(App.materia.getProfessor());
        conteudosET.setText(App.materia.getConteudos());
        mediaET.setText(App.materia.getMedia());

        // Ação para deletar a matéria com confirmação
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Exibe o diálogo de confirmação
                new AlertDialog.Builder(EditMateriaActivity.this)
                        .setTitle("Confirmar Exclusão")
                        .setMessage("Tem certeza que deseja deletar esta matéria?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Confirmado: Deletar a matéria
                                db.collection("materia").document(App.materia.getId()).delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(EditMateriaActivity.this, "Matéria Deletada Com Sucesso!!", Toast.LENGTH_SHORT).show();
                                                Intent resultIntent = new Intent();
                                                setResult(RESULT_OK, resultIntent);
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(EditMateriaActivity.this, "Erro Ao Deletar A Matéria!!", Toast.LENGTH_SHORT).show();
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
                new AlertDialog.Builder(EditMateriaActivity.this)
                        .setTitle("Confirmar Alteração")
                        .setMessage("Tem certeza que deseja salvar as alterações?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Map<String, Object> materia = new HashMap<>();
                                materia.put("nomeMateria", Objects.requireNonNull(nomeMateriaET.getText().toString()));
                                materia.put("professor", Objects.requireNonNull(professorET.getText().toString()));
                                materia.put("conteudos", Objects.requireNonNull(conteudosET.getText().toString()));
                                materia.put("media", Objects.requireNonNull(mediaET.getText().toString()));

                                db.collection("materia").document(App.materia.getId()).set(materia)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(EditMateriaActivity.this, "Matéria Alterada Com Sucesso!!", Toast.LENGTH_SHORT).show();
                                                Intent resultIntent = new Intent();
                                                setResult(RESULT_OK, resultIntent);
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(EditMateriaActivity.this, "Erro Ao Alterar Matéria!!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("Não", null)
                        .show();
            }
        });
    }
}
