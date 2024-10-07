package com.example.studyguider.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.studyguider.R;
import com.example.studyguider.viewmodels.HeaderViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ControleRec extends AppCompatActivity {

    private HeaderViewModel headerViewModel;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean salvarDadosAutomaticamente = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controle_rec);

        headerViewModel = new ViewModelProvider(this).get(HeaderViewModel.class);

        View headerView = findViewById(R.id.header);
        HeaderActivity headerActivity = new HeaderActivity(headerView, headerViewModel, this);

        FirebaseUser currentUser1 = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser1 != null) {
            headerViewModel.fetchUsername(currentUser1);
        }

        // Verifique se o campo "prova" é menor que o campo "pre" no banco de dados
        db.collection("grades").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    boolean possuiRecuperacao = false;

                    // Configura o LinearLayout para mostrar múltiplos containers
                    LinearLayout linearLayout = findViewById(R.id.linerec);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        // Recupere os valores dos campos "prova" e "pre"
                        String prova = document.getString("prova");
                        String pre = document.getString("pre");

                        // Verifique se o campo "prova" é menor que o campo "pre"
                        if (prova.compareTo(pre) < 0) {
                            possuiRecuperacao = true;

                            // Recupere os valores dos campos "nomeMateria" e "nota"
                            String nomeMateria = document.getString("nomeMateria");
                            String cred = document.getString("cred");
                            String trab = document.getString("trab");
                            String list = document.getString("list");
                            String provaNota = document.getString("prova");

                            // Calcule a nota
                            int nota = Integer.parseInt(cred) + Integer.parseInt(trab) + Integer.parseInt(list) + Integer.parseInt(provaNota);

                            // Verifique se um container com o ID de nomeMateria já existe
                            View existingContainer = linearLayout.findViewWithTag(nomeMateria);
                            if (existingContainer != null) {
                                // Atualize o container existente
                                atualizarContainer(existingContainer, nomeMateria, nota);
                            } else {
                                // Crie um novo container
                                View container = getLayoutInflater().inflate(R.layout.activity_add_rec, linearLayout, false); // Inflate com o LinearLayout como pai
                                container.setTag(nomeMateria); // Define o nomeMateria como tag do container

                                // Definir espaçamento entre os containers (margens)
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );
                                int marginInPixels = (int) (10 * getResources().getDisplayMetrics().density); // Converte 10dp para pixels
                                layoutParams.setMargins(0, marginInPixels, 0, marginInPixels); // Defina margem superior e inferior

                                container.setLayoutParams(layoutParams); // Aplique as margens no container
                                linearLayout.addView(container); // Adiciona o novo container ao LinearLayout

                                // Preenche os valores no novo container
                                atualizarContainer(container, nomeMateria, nota);
                            }
                        }
                    }

                    if (!possuiRecuperacao) {
                        // Se o campo "prova" não é menor que o campo "pre", não permita salvar
                        Toast.makeText(ControleRec.this, "Não possui nenhuma Recuperação", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Se houver um erro, mostre uma mensagem de erro
                    Toast.makeText(ControleRec.this, "Erro ao recuperar dados", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void atualizarContainer(View container, String nomeMateria, int nota) {
        // Atualiza os TextViews dentro do container
        TextView materiaTextView = container.findViewById(R.id.materiarec);
        materiaTextView.setText(nomeMateria);

        TextView notaTextView = container.findViewById(R.id.notarec);
        notaTextView.setText(String.valueOf(nota));

        // Adicione listeners aos checkboxes para salvar automaticamente
        CheckBox c1 = container.findViewById(R.id.checkSujes1);
        c1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (salvarDadosAutomaticamente) {
                    salvarDados(nomeMateria, container);
                }
            }
        });

        CheckBox c2 = container.findViewById(R.id.checkSujes2);
        c2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (salvarDadosAutomaticamente) {
                    salvarDados(nomeMateria, container);
                }
            }
        });

        CheckBox c3 = container.findViewById(R.id.checkSujes3);
        c3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (salvarDadosAutomaticamente) {
                    salvarDados(nomeMateria, container);
                }
            }
        });

        CheckBox c4 = container.findViewById(R.id.checkSujes4);
        c4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (salvarDadosAutomaticamente) {
                    salvarDados(nomeMateria, container);
                }
            }
        });

        EditText conteudosEditText = container.findViewById(R.id.conteuos_txt);
        conteudosEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (salvarDadosAutomaticamente) {
                    salvarDados(nomeMateria, container);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void salvarDados(String nomeMateria, View container) {
        salvarDadosAutomaticamente = false;

        // Encontre os elementos dentro do container
        CheckBox c1 = container.findViewById(R.id.checkSujes1);
        CheckBox c2 = container.findViewById(R.id.checkSujes2);
        CheckBox c3 = container.findViewById(R.id.checkSujes3);
        CheckBox c4 = container.findViewById(R.id.checkSujes4);
        EditText conteudos = container.findViewById(R.id.conteuos_txt);

        // Salvar os dados no Firestore usando nomeMateria como chave
        Map<String, Object> dados = new HashMap<>();
        dados.put("c1", c1.isChecked());
        dados.put("c2", c2.isChecked());
        dados.put("c3", c3.isChecked());
        dados.put("c4", c4.isChecked());
        dados.put("conteudos", conteudos.getText().toString()); // Acrescente isso aqui

        db.collection("recovery").document(nomeMateria).set(dados)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "Dados de " + nomeMateria + " gravados com sucesso!");
                        Toast.makeText(ControleRec.this, "Dados de " + nomeMateria + " salvos com sucesso!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Erro ao gravar dados: " + e.getMessage());
                        Toast.makeText(ControleRec.this, "Erro ao salvar dados de " + nomeMateria, Toast.LENGTH_SHORT).show();
                    }
                });

        salvarDadosAutomaticamente = true;
    }
}
