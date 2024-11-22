package com.example.studyguider.view;

import android.content.pm.ActivityInfo;
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

public class ControleRecuperacoes extends AppCompatActivity {

    private HeaderViewModel headerViewModel;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean salvarDadosAutomaticamente = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controle_rec);

        // Define orientação da tela para retrato
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        headerViewModel = new ViewModelProvider(this).get(HeaderViewModel.class);


        View headerView = findViewById(R.id.header);
        HeaderActivity headerActivity = new HeaderActivity(headerView, headerViewModel, this);


        FirebaseUser currentUser1 = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser1 != null) {
            headerViewModel.fetchUsername(currentUser1);
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Obtém coleção "notas" do Firestore e verifica se o aluno está em recuperação
            db.collection("grades").document(userId).collection("userGrades").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        boolean possuiRecuperacao = false;

                        LinearLayout linearLayout = findViewById(R.id.linerec);
                        linearLayout.setOrientation(LinearLayout.VERTICAL);

                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            String po = document.getString("prova");
                            String pe = document.getString("pre");
                            float prova, pre;

                            prova = Float.parseFloat(po);
                            pre = Float.parseFloat(pe);


                            if ((prova - pre) < 0) {
                                possuiRecuperacao = true;

                                String nomeMateria = document.getString("nomeMateria");
                                float nota = calcularNota(document);

                                View existingContainer = linearLayout.findViewWithTag(nomeMateria);
                                if (existingContainer != null) {
                                    atualizarContainer(existingContainer, nomeMateria, nota);
                                } else {
                                    View container = getLayoutInflater().inflate(R.layout.activity_add_rec, linearLayout, false);
                                    container.setTag(nomeMateria);

                                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );
                                    int marginInPixels = (int) (10 * getResources().getDisplayMetrics().density);
                                    layoutParams.setMargins(0, marginInPixels, 0, marginInPixels);
                                    container.setLayoutParams(layoutParams);

                                    linearLayout.addView(container);
                                    atualizarContainer(container, nomeMateria, nota);

                                    preencherDadosDoFirestore(nomeMateria, container);
                                }
                            }
                        }

                        // Se nenhuma recuperação é encontrada, exibe um aviso
                        if (!possuiRecuperacao) {
                            Toast.makeText(ControleRecuperacoes.this, "Não possui nenhuma Recuperação", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ControleRecuperacoes.this, "Erro ao recuperar dados", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    // Método que carrega os dados da recuperação de Firestore para preencher o container correspondente
    private void preencherDadosDoFirestore(String nomeMateria, View container) {
        db.collection("rec").document(nomeMateria).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        CheckBox c1 = container.findViewById(R.id.checkSujes1);
                        CheckBox c2 = container.findViewById(R.id.checkSujes2);
                        CheckBox c3 = container.findViewById(R.id.checkSujes3);
                        CheckBox c4 = container.findViewById(R.id.checkSujes4);
                        EditText conteudos = container.findViewById(R.id.conteuos_txt);

                        // Define o status de cada checkbox e o conteúdo do campo de texto
                        c1.setChecked(document.getBoolean("c1"));
                        c2.setChecked(document.getBoolean("c2"));
                        c3.setChecked(document.getBoolean("c3"));
                        c4.setChecked(document.getBoolean("c4"));
                        conteudos.setText(document.getString("conteudos"));
                    }
                } else {
                    Toast.makeText(ControleRecuperacoes.this, "Erro ao carregar dados", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Método para calcular a nota total da matéria
    private float calcularNota(DocumentSnapshot document) {
        float cred = Float.parseFloat(document.getString("cred"));
        float trab = Float.parseFloat(document.getString("trab"));
        float list = Float.parseFloat(document.getString("list"));
        float prova = Float.parseFloat(document.getString("prova"));
        return cred + trab + list + prova;
    }

    // Atualiza visualmente o container com informações de matéria e nota, incluindo checkboxes e campo de texto
    private void atualizarContainer(View container, String nomeMateria, float nota) {

        TextView materiaTextView = container.findViewById(R.id.materiarec);
        materiaTextView.setText(nomeMateria);

        TextView notaTextView = container.findViewById(R.id.notarec);
        notaTextView.setText(String.valueOf(nota));


        CheckBox c1 = container.findViewById(R.id.checkSujes1);
        CheckBox c2 = container.findViewById(R.id.checkSujes2);
        CheckBox c3 = container.findViewById(R.id.checkSujes3);
        CheckBox c4 = container.findViewById(R.id.checkSujes4);

        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (salvarDadosAutomaticamente) {
                    salvarDados(nomeMateria, container);
                }
            }
        };

        c1.setOnCheckedChangeListener(checkedChangeListener);
        c2.setOnCheckedChangeListener(checkedChangeListener);
        c3.setOnCheckedChangeListener(checkedChangeListener);
        c4.setOnCheckedChangeListener(checkedChangeListener);

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

    // Exclui dados de recuperação do Firestore e remove o container do layout
    private void excluirDados(String nomeMateria, View container) {
        db.collection("rec").document(nomeMateria)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "Dados de " + nomeMateria + " excluídos com sucesso!");
                        Toast.makeText(ControleRecuperacoes.this, "Dados de " + nomeMateria + " excluídos com sucesso!", Toast.LENGTH_SHORT).show();

                        LinearLayout linearLayout = findViewById(R.id.linerec);
                        linearLayout.removeView(container);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Erro ao excluir dados: " + e.getMessage());
                        Toast.makeText(ControleRecuperacoes.this, "Erro ao excluir dados de " + nomeMateria, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Salva dados de checkboxes e conteúdo no Firestore
    private void salvarDados(String nomeMateria, View container) {
        salvarDadosAutomaticamente = false; // Desativa o salvamento automático enquanto salva dados manualmente

        CheckBox c1 = container.findViewById(R.id.checkSujes1);
        CheckBox c2 = container.findViewById(R.id.checkSujes2);
        CheckBox c3 = container.findViewById(R.id.checkSujes3);
        CheckBox c4 = container.findViewById(R.id.checkSujes4);
        EditText conteudos = container.findViewById(R.id.conteuos_txt);

        Map<String, Object> dados = new HashMap<>();
        dados.put("c1", c1.isChecked());
        dados.put("c2", c2.isChecked());
        dados.put("c3", c3.isChecked());
        dados.put("c4", c4.isChecked());
        dados.put("conteudos", conteudos.getText().toString());

        db.collection("rec").document(nomeMateria).set(dados)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "Dados de " + nomeMateria + " gravados com sucesso!");
                        Toast.makeText(ControleRecuperacoes.this, "Dados de " + nomeMateria + " salvos com sucesso!", Toast.LENGTH_SHORT);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Erro ao gravar dados: " + e.getMessage());
                        Toast.makeText(ControleRecuperacoes.this, "Erro ao salvar dados de " + nomeMateria, Toast.LENGTH_SHORT);
                    }
                });

        salvarDadosAutomaticamente = true; // Reativa o salvamento automático

    }

}