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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ShiftsEditActivity extends AppCompatActivity {

    private HeaderViewModel headerViewModel;
    private EditText professorET, materiaET, horaET, diaET;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Ativa o modo de tela cheia para uma experiência imersiva
        setContentView(R.layout.activity_shift_edit);

        // Define orientação da tela para retrato
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Configura o padding para evitar sobreposição com as barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializa o ViewModel para o cabeçalho e configura o cabeçalho da Activity
        headerViewModel = new ViewModelProvider(this).get(HeaderViewModel.class);
        View headerView = findViewById(R.id.header);
        HeaderActivity headerActivity = new HeaderActivity(headerView, headerViewModel, this);

        // Obtém o usuário atual e busca o nome de usuário
        FirebaseUser currentUser1 = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser1 != null) {
            headerViewModel.fetchUsername(currentUser1);
        }

        // Inicializa o Firestore para salvar as atualizações
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Inicializa os campos de entrada com os valores atuais do plantão
        professorET = findViewById(R.id.professorET);
        materiaET = findViewById(R.id.materiaET);
        horaET = findViewById(R.id.horaET);
        diaET = findViewById(R.id.diaET);
        save = findViewById(R.id.save);

        // Preenche os campos com os dados do plantão selecionado
        professorET.setText(App.shifts.getProfessor());
        materiaET.setText(App.shifts.getMateria());
        horaET.setText(App.shifts.getHora());
        diaET.setText(App.shifts.getDia());

        // Configura o botão de salvar para atualizar os dados no Firestore
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Cria um mapa com os dados atualizados do plantão
                Map<String, Object> updates = new HashMap<>();
                updates.put("professor", Objects.requireNonNull(professorET.getText()).toString());
                updates.put("materia", Objects.requireNonNull(materiaET.getText()).toString());
                updates.put("hora", Objects.requireNonNull(horaET.getText()).toString());
                updates.put("dia", Objects.requireNonNull(diaET.getText()).toString());

                // Obtém o usuário atual e o ID do plantão para salvar as atualizações no Firestore
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    db.collection("shifts").document(userId).collection("userShifts")
                            .document(App.shifts.getId()).update(updates)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Exibe uma mensagem de sucesso e finaliza a Activity
                                    Toast.makeText(ShiftsEditActivity.this, "Plantão salvo com sucesso", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Exibe uma mensagem de erro caso ocorra uma falha ao salvar
                                    Toast.makeText(ShiftsEditActivity.this, "Erro ao salvar plantão", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        // Configura o campo de horaET para abrir um seletor de horário quando clicado
        horaET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtém a hora atual para usar como padrão no TimePickerDialog
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                // Cria e exibe o TimePickerDialog para selecionar o horário do plantão
                TimePickerDialog timePickerDialog = new TimePickerDialog(ShiftsEditActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Formata e define o horário selecionado no campo horaET
                                String formattedTime = String.format("%02d:%02d", hourOfDay, minute);
                                horaET.setText(formattedTime);
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });
    }
}