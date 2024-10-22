package com.example.studyguider.view;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import com.example.studyguider.R;
import com.example.studyguider.models.Faltas;
import com.example.studyguider.viewmodels.AbsenceCalendarViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.studyguider.viewmodels.HeaderViewModel;

import java.util.Calendar;


public class FaltasActivity extends AppCompatActivity {



    private GridLayout gridLayoutCalendar;
    private int selectedColor = Color.parseColor("#F68C0A"); // Selected color
    private int defaultColor = Color.WHITE; // Default background color

    private ScrollView informationScrollView;
    private LinearLayout informationLayout;
    private LinearLayout savedFaltasLayout;

    private TextView selectedDayTextView;
    private EditText editTextMotivo;
    private CheckBox checkBoxAtestado;
    private EditText editTextNota;
    private Button saveButton;
    private Button backButton;
    private Button  buttonPreviousMonth;
    private Button buttonNextMonth;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;

    private Calendar calendar;
    private TextView monthTextView;

    private String currentEditingDay; // Day currently being edited

    private HeaderViewModel headerViewModel;
    private AbsenceCalendarViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faltas);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        viewModel = new ViewModelProvider(this).get(AbsenceCalendarViewModel.class);

        headerViewModel = new ViewModelProvider(this).get(HeaderViewModel.class);

        View headerView = findViewById(R.id.header);
        HeaderActivity headerActivity = new HeaderActivity(headerView, headerViewModel, this);

        FirebaseUser currentUser1 = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser1 != null) {
            headerViewModel.fetchUsername(currentUser1);
        }

        gridLayoutCalendar = findViewById(R.id.gridLayoutCalendar);
        monthTextView = findViewById(R.id.textViewMonth);

        informationScrollView = findViewById(R.id.informationScrollView);
        informationLayout = findViewById(R.id.informationLayout);
        savedFaltasLayout = findViewById(R.id.savedFaltasLayout);

        editTextMotivo = findViewById(R.id.editTextMotivo);
        checkBoxAtestado = findViewById(R.id.checkBoxAtestado);
        editTextNota = findViewById(R.id.editTextNota);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton);

        buttonPreviousMonth = findViewById(R.id.buttonPreviousMonth);
        buttonNextMonth = findViewById(R.id.buttonNextMonth);

        buttonPreviousMonth.setText("<");
        buttonNextMonth.setText(">");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        userId = currentUser.getUid();

        calendar = Calendar.getInstance();

        findViewById(R.id.buttonPreviousMonth).setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1);
            updateCalendar();
        });

        findViewById(R.id.buttonNextMonth).setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, 1);
            updateCalendar();
        });


        saveButton.setOnClickListener(v -> saveFalta());

        backButton.setOnClickListener(v -> {
            clearForm();  // Limpa o formulário
            informationScrollView.setVisibility(View.GONE); // Esconde o formulário
            gridLayoutCalendar.setVisibility(View.VISIBLE); // Mostra o calendário
            savedFaltasLayout.setVisibility(View.VISIBLE);  // Mostra as faltas salvas

        });

        updateCalendar();
    }

    private String getMonthYearKey() {
        int month = calendar.get(Calendar.MONTH) + 1; // Months are 0-indexed
        int year = calendar.get(Calendar.YEAR);
        return String.format("%04d-%02d", year, month);
    }

    private void updateCalendar() {
        Button buttonPreviousMonth = findViewById(R.id.buttonPreviousMonth);
        Button buttonNextMonth = findViewById(R.id.buttonNextMonth);

        gridLayoutCalendar.removeAllViews();

        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        String monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, getResources().getConfiguration().locale);
        monthTextView.setText(String.format("%s %d", monthName, year));

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int day = 1; day <= daysInMonth; day++) {
            final int dayCopy = day;
            TextView dayTextView = new TextView(this);
            dayTextView.setText(String.valueOf(day));
            dayTextView.setGravity(Gravity.CENTER);
            dayTextView.setBackgroundColor(defaultColor);
            dayTextView.setPadding(16, 16, 16, 16);

            dayTextView.setOnClickListener(v -> handleDayClick(dayCopy, dayTextView));

            monthTextView.setVisibility(View.VISIBLE);
            buttonPreviousMonth.setText("<");
            buttonNextMonth.setText(">");
            buttonNextMonth.setEnabled(true);
            buttonPreviousMonth.setEnabled(true);

            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            param.width = 0;
            param.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            dayTextView.setLayoutParams(param);

            gridLayoutCalendar.addView(dayTextView);
        }

        loadFaltas();
    }

    private void handleDayClick(int day, TextView dayTextView) {
        Button buttonPreviousMonth = findViewById(R.id.buttonPreviousMonth);
        Button buttonNextMonth = findViewById(R.id.buttonNextMonth);

        ColorDrawable background = (ColorDrawable) dayTextView.getBackground();
        if (background.getColor() == defaultColor) {
            selectedDayTextView = dayTextView;
            currentEditingDay = String.valueOf(day); // Set the day to edit
            loadFormForDay(day); // Load existing data into the form
            informationScrollView.setVisibility(View.VISIBLE);
            gridLayoutCalendar.setVisibility(View.GONE);
            savedFaltasLayout.setVisibility(View.GONE);
            monthTextView.setVisibility(View.INVISIBLE);
            buttonPreviousMonth.setVisibility(View.VISIBLE);
            buttonNextMonth.setVisibility(View.VISIBLE);
            buttonPreviousMonth.setText("");
            buttonNextMonth.setText("");
            buttonNextMonth.setEnabled(false);
            buttonPreviousMonth.setEnabled(false);

            dayTextView.setBackgroundColor(selectedColor);
        } else {
            removeFalta(day);
            dayTextView.setBackgroundColor(defaultColor);
            Toast.makeText(this, "Anotação removida", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadFormForDay(int day) {
        viewModel.loadFaltas(userId, getMonthYearKey());
    }

    private void saveFalta() {
        if (currentEditingDay == null) {
            Toast.makeText(this, "No day selected", Toast.LENGTH_SHORT).show();
            return;
        }

        String day = currentEditingDay;
        String motivo = editTextMotivo.getText().toString();
        boolean atestado = checkBoxAtestado.isChecked();
        String nota = editTextNota.getText().toString();

        Faltas falta = new Faltas(day, motivo, atestado, nota);

        // Verificar se já existe uma falta para esse dia antes de incrementar a contagem
        if (!dayAlreadyExists(day)) {
            int countChange = 1;  // Incrementa a contagem de ausências
            viewModel.updateUserAbsenceCount(countChange);
        }

        // Salvar a falta usando o ViewModel
        viewModel.saveFalta(userId, getMonthYearKey(), day, falta);
        updateFaltaInLayout(day, motivo, atestado, nota);
        updateCalendar();
        clearForm();
        if (selectedDayTextView != null) {
            selectedDayTextView.setBackgroundColor(selectedColor);
        }
    }

    private void addFaltaToLayout(String day, String motivo, boolean atestado, String nota) {
        LinearLayout faltaInfoLayout = new LinearLayout(this);
        faltaInfoLayout.setOrientation(LinearLayout.VERTICAL);
        faltaInfoLayout.setPadding(32, 32, 32, 32);

        GradientDrawable roundedBackground = new GradientDrawable();
        roundedBackground.setColor(Color.parseColor("#FFBC62"));
        roundedBackground.setCornerRadius(20);
        faltaInfoLayout.setBackground(roundedBackground);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(24, 24, 24, 24);
        faltaInfoLayout.setLayoutParams(layoutParams);

        addTextViewToLayout(faltaInfoLayout, String.format("Dia: %s", day));
        addTextViewToLayout(faltaInfoLayout, String.format("Motivo: %s", motivo));
        addTextViewToLayout(faltaInfoLayout, String.format("Atestado: %s", atestado ? "Sim" : "Não"));
        addTextViewToLayout(faltaInfoLayout, String.format("Perdeu nota: %s", nota));

        // Adicionar botão de deletar
        Button deleteButton = new Button(this);
        deleteButton.setBackgroundColor(Color.TRANSPARENT);
        deleteButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_delete, 0, 0, 0);

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                70, // Largura fixa em pixels (ajuste conforme necessário)
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.gravity = Gravity.END;
        deleteButton.setLayoutParams(buttonParams);

        // Listener para deletar a falta
        deleteButton.setOnClickListener(v -> {
            removeFalta(Integer.parseInt(day)); // Remove do banco de dados
            savedFaltasLayout.removeView(faltaInfoLayout); // Remove do layout sem recarregar
            Toast.makeText(this, "Falta deletada", Toast.LENGTH_SHORT).show();
        });

        faltaInfoLayout.addView(deleteButton); // Adiciona o botão ao layout

        // Set a click listener to edit this entry
        faltaInfoLayout.setOnClickListener(v -> {
            currentEditingDay = day;
            loadFormForDay(Integer.parseInt(day));
            informationScrollView.setVisibility(View.VISIBLE);
            gridLayoutCalendar.setVisibility(View.GONE);
            savedFaltasLayout.setVisibility(View.GONE);
            monthTextView.setVisibility(View.INVISIBLE);
            buttonPreviousMonth.setVisibility(View.INVISIBLE);
            buttonNextMonth.setVisibility(View.INVISIBLE);
        });

        savedFaltasLayout.addView(faltaInfoLayout);
    }

    private void addTextViewToLayout(LinearLayout layout, String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(16);

        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textViewParams.setMargins(0, 0, 0, 16);
        textView.setLayoutParams(textViewParams);

        layout.addView(textView);
    }

    private void clearForm() {
        editTextMotivo.setText("");
        checkBoxAtestado.setChecked(false);
        editTextNota.setText("");
        informationScrollView.setVisibility(View.GONE);
        gridLayoutCalendar.setVisibility(View.VISIBLE);
        savedFaltasLayout.setVisibility(View.VISIBLE);
        buttonPreviousMonth.setVisibility(View.VISIBLE);
        buttonNextMonth.setVisibility(View.VISIBLE);
        currentEditingDay = null; // Redefinir o dia na edição
    }

    private void removeFalta(int day) {
        String monthYearKey = getMonthYearKey();
        viewModel.removeFalta(userId, monthYearKey, day);
        int countChange = -1; // Incrementa a contagem de ausências
        viewModel.updateUserAbsenceCount(countChange);
        updateCalendar();
        clearForm();
        if (selectedDayTextView != null) {
            selectedDayTextView.setBackgroundColor(Color.WHITE); // Redefinir plano de fundo do dia
        }
    }

    private void removeFaltaFromLayout(int day) {
        String dayStr = String.valueOf(day);
        for (int i = 0; i < savedFaltasLayout.getChildCount(); i++) {
            LinearLayout faltaLayout = (LinearLayout) savedFaltasLayout.getChildAt(i);
            TextView dayTextView = (TextView) faltaLayout.getChildAt(0);
            if (dayTextView.getText().toString().contains(dayStr)) {
                savedFaltasLayout.removeView(faltaLayout);
                break;
            }
        }
    }

    private void updateFaltaInLayout(String day, String motivo, boolean atestado, String nota) {
        removeFaltaFromLayout(Integer.parseInt(day));
        addFaltaToLayout(day, motivo, atestado, nota);
    }

    private void loadFaltas() {
        savedFaltasLayout.removeAllViews();

        String monthYearKey = getMonthYearKey();

        db.collection("absence_calendar").document(userId).collection(monthYearKey)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String day = document.getString("day");
                            String motivo = document.getString("motivo");
                            boolean atestado = document.getBoolean("atestado");
                            String nota = document.getString("nota");
                            addFaltaToLayout(day, motivo, atestado, nota);

                            //Destaque o dia no calendário
                            for (int i = 0; i < gridLayoutCalendar.getChildCount(); i++) {
                                TextView dayView = (TextView) gridLayoutCalendar.getChildAt(i);
                                if (dayView.getText().toString().equals(day)) {
                                    dayView.setBackgroundColor(selectedColor);
                                    break;
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this, "Failed to load entries", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private boolean dayAlreadyExists(String day) {
        for (int i = 0; i < savedFaltasLayout.getChildCount(); i++) {
            LinearLayout faltaLayout = (LinearLayout) savedFaltasLayout.getChildAt(i);
            TextView dayTextView = (TextView) faltaLayout.getChildAt(0);
            if (dayTextView.getText().toString().contains(day)) {
                return true;  // Já existe
            }
        }
        return false;
    }
}