package com.example.studyguider.view;

import android.app.TimePickerDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.studyguider.R;
import com.example.studyguider.viewmodels.HeaderViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ShiftsAddActivity extends AppCompatActivity {

    private HeaderViewModel headerViewModel;
    private EditText professorET, materiaET, diaET, horaET;
    private Button addUser;
    private FirebaseFirestore db;
    private static final String TAG = "ShiftAddActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shift_add);

        // Define orientação da tela para retrato
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        headerViewModel = new ViewModelProvider(this).get(HeaderViewModel.class);

        View headerView = findViewById(R.id.header);
        HeaderActivity headerActivity = new HeaderActivity(headerView, headerViewModel, this);

        // Recupera o usuário atual e busca o nome de usuário se ele estiver autenticado
        FirebaseUser currentUser1 = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser1 != null) {
            headerViewModel.fetchUsername(currentUser1);
        }

        // Configura os campos de entrada e o botão para adicionar um novo "shift"
        professorET = findViewById(R.id.professorET);
        materiaET = findViewById(R.id.materiaET);
        diaET = findViewById(R.id.diaET);
        horaET = findViewById(R.id.horaET);
        addUser = findViewById(R.id.addUser);
        db = FirebaseFirestore.getInstance();

        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String professor = Objects.requireNonNull(professorET.getText()).toString().trim();
                String materia = Objects.requireNonNull(materiaET.getText()).toString().trim();
                String dia = Objects.requireNonNull(diaET.getText()).toString().trim();
                String hora = Objects.requireNonNull(horaET.getText()).toString().trim();

                // Verifica se todos os campos estão preenchidos antes de prosseguir
                if (professor.isEmpty() || materia.isEmpty() || dia.isEmpty() || hora.isEmpty()) {
                    Toast.makeText(ShiftsAddActivity.this, "Todos os campos devem ser preenchidos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Cria um novo documento com as informações do shift
                Map<String, Object> shift = new HashMap<>();
                shift.put("professor", professor);
                shift.put("materia", materia);
                shift.put("dia", dia);
                shift.put("hora", hora);

                // Adiciona o shift ao Firestore no caminho do usuário autenticado
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    db.collection("shifts").document(userId).collection("userShifts").add(shift)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    // Notifica o sucesso da operação e finaliza a atividade
                                    Toast.makeText(ShiftsAddActivity.this, "Shift adicionado com sucesso", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Notifica falha na operação
                                    Toast.makeText(ShiftsAddActivity.this, "Falha Ao Tentar Adicionar Shift: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        // Configura a seleção de horário ao clicar no campo horaET
        horaET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obter a hora atual
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                // Exibe um diálogo para seleção de horário e define o valor escolhido
                TimePickerDialog timePickerDialog = new TimePickerDialog(ShiftsAddActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Formatar a hora como String e definir no campo horaET
                                String formattedTime = String.format("%02d:%02d", hourOfDay, minute);
                                horaET.setText(formattedTime);
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });

    }

}