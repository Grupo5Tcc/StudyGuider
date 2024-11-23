package com.example.studyguider.view;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studyguider.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GradesAddActivity extends AppCompatActivity {

    private static final String TAG = "AddNotas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_add_notas);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Spinner nomeMateriaSp = findViewById(R.id.nomeMateriaTxt);
        TextInputEditText notaCredET = findViewById(R.id.notaCredET);
        TextInputEditText notaTrabET = findViewById(R.id.notaTrabET);
        TextInputEditText notaListaET = findViewById(R.id.notaListaET);
        TextInputEditText notaProvaET = findViewById(R.id.notaProvaET);

        MaterialButton addNotas = findViewById(R.id.addNota);

        carregarMaterias(nomeMateriaSp);

        addNotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomeMateria = nomeMateriaSp.getSelectedItem().toString().trim();
                if (nomeMateria.equals("Selecione uma matéria")) {
                    Toast.makeText(GradesAddActivity.this, "Por favor, selecione uma matéria válida!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String notaCred = Objects.requireNonNull(notaCredET.getText()).toString().trim();
                String notaTrab = Objects.requireNonNull(notaTrabET.getText()).toString().trim();
                String notaList = Objects.requireNonNull(notaListaET.getText()).toString().trim();
                String notaPro = Objects.requireNonNull(notaProvaET.getText()).toString().trim();

                if (notaCred.isEmpty() || notaTrab.isEmpty() || notaList.isEmpty() || notaPro.isEmpty()) {
                    Toast.makeText(GradesAddActivity.this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show();
                    return;
                }

                float ntCred = Float.parseFloat(notaCred);
                float ntTrab = Float.parseFloat(notaTrab);
                float ntList = Float.parseFloat(notaList);
                float ntSoma = ntCred + ntTrab + ntList;
                String notaPreciso = ntSoma < 6 ? String.valueOf(6 - ntSoma) : "Não precisa de pontos para passar!!";

                // Aqui você deve buscar na subcoleção correta
                FirebaseUser  currentUser  = FirebaseAuth.getInstance().getCurrentUser ();
                if (currentUser  != null) {
                    String userId = currentUser .getUid();

                    db.collection("subjects").document(userId).collection("userSubjects")
                            .whereEqualTo("nomeMateria", nomeMateria) // Verifique se o campo é realmente "nomeMateria"
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    String nomeProf = queryDocumentSnapshots.getDocuments().get(0).getString("nomeProf");
                                    if (nomeProf == null) {
                                        nomeProf = "Desconhecido"; // Caso nomeProf esteja ausente.
                                    }

                                    Map<String, Object> notas = new HashMap<>();
                                    notas.put("nomeMateria", nomeMateria);
                                    notas.put("cred", notaCred);
                                    notas.put("trab", notaTrab);
                                    notas.put("list", notaList);
                                    notas.put("pre", notaPreciso);
                                    notas.put("prova", notaPro);
                                    notas.put("nomeProf", nomeProf);

                                    db.collection("grades").add(notas)
                                            .addOnSuccessListener(documentReference -> {
                                                Toast.makeText(GradesAddActivity.this, "Grades adicionadas com sucesso!!", Toast.LENGTH_SHORT).show();
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(GradesAddActivity.this, "Falha ao adicionar grades: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                Log.e(TAG, "Erro ao adicionar grades", e);
                                            });
                                } else {
                                    Toast.makeText(GradesAddActivity.this, "Matéria não encontrada no banco!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(GradesAddActivity.this, "Erro ao buscar professor: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Erro ao buscar professor", e);
                            });
                } else {
                    Toast.makeText(GradesAddActivity.this, "Usuário não autenticado", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void carregarMaterias(Spinner spinner) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Lista para armazenar as matérias
        ArrayList<String> listaMaterias = new ArrayList<>();
        listaMaterias.add("Selecione uma matéria"); // Opção neutra

        // Recupera as matérias do usuário atual, pela coleção correta
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("subjects").document(userId).collection("userSubjects").get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String nomeMateria = document.getString("nomeMateria");
                                if (nomeMateria != null) {
                                    listaMaterias.add(nomeMateria);
                                }
                            }

                            // Prepara o adapter para o Spinner
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaMaterias) {
                                @Override
                                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                                    View view = super.getDropDownView(position, convertView, parent);
                                    TextView textView = (TextView) view;
                                    textView.setTextColor(Color.BLACK);
                                    view.setBackgroundColor(Color.WHITE);
                                    return view;
                                }

                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View view = super.getView(position, convertView, parent);
                                    TextView textView = (TextView) view;
                                    textView.setTextColor(Color.BLACK);
                                    return view;
                                }
                            };

                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(adapter);
                        } else {
                            Toast.makeText(this, "Erro ao carregar matérias: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show();
        }
    }


}
