package com.example.studyguider.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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

public class ControleNotasAddActivity extends AppCompatActivity {

    private HeaderViewModel headerViewModel;
    private static final String TAG = "AddNotas";
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_controle_notas_add);

        headerViewModel = new ViewModelProvider(this).get(HeaderViewModel.class);


        View headerView = findViewById(R.id.header);
        HeaderActivity headerActivity = new HeaderActivity(headerView, headerViewModel, this);


        FirebaseUser currentUser1 = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser1 != null) {
            headerViewModel.fetchUsername(currentUser1);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Referência aos campos de entrada para inserir informações sobre a nota
        TextInputEditText nomeMateriaET = findViewById(R.id.nomeMateriaET);
        TextInputEditText notaCredET = findViewById(R.id.notaCredET);
        TextInputEditText notaTrabET = findViewById(R.id.notaTrabET);
        TextInputEditText notaListaET = findViewById(R.id.notaListaET);
        TextInputEditText notaProvaET = findViewById(R.id.notaProvaET);

        MaterialButton addNotas = findViewById(R.id.addNota);

        addNotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String notaPreciso; // Variável para armazenar a nota necessária para aprovação

                // Recupera e valida os valores inseridos nos campos de entrada
                String nomeMateria = Objects.requireNonNull(nomeMateriaET.getText()).toString().trim();
                String notaCred = Objects.requireNonNull(notaCredET.getText()).toString().trim();
                String notaTrab = Objects.requireNonNull(notaTrabET.getText()).toString().trim();
                String notaList = Objects.requireNonNull(notaListaET.getText()).toString().trim();
                String notaPro = Objects.requireNonNull(notaProvaET.getText()).toString().trim();

                // Verifica se todos os campos foram preenchidos
                if (nomeMateria.isEmpty() || notaCred.isEmpty() || notaTrab.isEmpty() || notaList.isEmpty() || notaPro.isEmpty()) {
                    Toast.makeText(ControleNotasAddActivity.this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    // Calcula se é necessário mais pontos para aprovação
                    float ntPreciso, ntCred, ntTrab, ntList;

                    ntCred = Float.parseFloat(notaCred);
                    ntTrab = Float.parseFloat(notaTrab);
                    ntList = Float.parseFloat(notaList);

                    float ntSoma = ntCred + ntTrab + ntList;

                    if(ntSoma < 6){
                        ntPreciso = 6 - ntSoma;
                        notaPreciso = String.valueOf(ntPreciso);
                        updateUserRecCount(1);
                    }else{
                        notaPreciso = "Não precisa de pontos para passar!!";
                        Toast.makeText(ControleNotasAddActivity.this, "Não está de recuperação!!", Toast.LENGTH_SHORT).show();
                    }

                }

                // Cria um mapa para armazenar as informações da nota a serem enviadas ao Firestore
                Map<String, Object> notas = new HashMap<>();
                notas.put("nomeMateria", Objects.requireNonNull(nomeMateriaET.getText()).toString());
                notas.put("cred", Objects.requireNonNull(notaCredET.getText()).toString());
                notas.put("trab", Objects.requireNonNull(notaTrabET.getText()).toString());
                notas.put("list", Objects.requireNonNull(notaListaET.getText()).toString());
                notas.put("pre", Objects.requireNonNull(notaPreciso));
                notas.put("prova", Objects.requireNonNull(notaProvaET.getText()).toString());

                // Adiciona a nova nota ao Firestore e define os callbacks para sucesso ou falha
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