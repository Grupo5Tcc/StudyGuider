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
import com.example.studyguider.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class AbsenceCalendarActivity extends AppCompatActivity {

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

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;

    private Calendar calendar;
    private TextView monthTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absence_calendar);

        gridLayoutCalendar = findViewById(R.id.gridLayoutCalendar);
        monthTextView = findViewById(R.id.textViewMonth);

        informationScrollView = findViewById(R.id.informationScrollView);
        informationLayout = findViewById(R.id.informationLayout);
        savedFaltasLayout = findViewById(R.id.savedFaltasLayout);

        editTextMotivo = findViewById(R.id.editTextMotivo);
        checkBoxAtestado = findViewById(R.id.checkBoxAtestado);
        editTextNota = findViewById(R.id.editTextNota);
        saveButton = findViewById(R.id.saveButton);

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
        updateCalendar();
    }

    private String getMonthYearKey() {
        int month = calendar.get(Calendar.MONTH) + 1; // Months are 0-indexed
        int year = calendar.get(Calendar.YEAR);
        return String.format("%04d-%02d", year, month);
    }

    private void updateCalendar() {
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
            dayTextView.setTextSize(16);
            dayTextView.setPadding(16, 16, 16, 16);

            dayTextView.setOnClickListener(v -> handleDayClick(dayCopy, dayTextView));

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
        ColorDrawable background = (ColorDrawable) dayTextView.getBackground();
        if (background.getColor() == defaultColor) {
            selectedDayTextView = dayTextView;
            informationScrollView.setVisibility(View.VISIBLE);
            gridLayoutCalendar.setVisibility(View.GONE);
            savedFaltasLayout.setVisibility(View.GONE);
            dayTextView.setBackgroundColor(selectedColor);
        } else {
            removeFalta(day);
            dayTextView.setBackgroundColor(defaultColor);
            Toast.makeText(this, "Anotação removida", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveFalta() {
        String day = selectedDayTextView.getText().toString();
        String motivo = editTextMotivo.getText().toString();
        boolean atestado = checkBoxAtestado.isChecked();
        String nota = editTextNota.getText().toString();

        Map<String, Object> falta = new HashMap<>();
        falta.put("day", day);
        falta.put("motivo", motivo);
        falta.put("atestado", atestado);
        falta.put("nota", nota);

        String monthYearKey = getMonthYearKey();

        db.collection("absence_calendar").document(userId).collection(monthYearKey).document(day)
                .set(falta)
                .addOnSuccessListener(aVoid -> {
                    addFaltaToLayout(day, motivo, atestado, nota);
                    clearForm();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save the entry", Toast.LENGTH_SHORT).show());
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
    }

    private void removeFalta(int day) {
        String monthYearKey = getMonthYearKey();

        db.collection("absence_calendar").document(userId).collection(monthYearKey)
                .whereEqualTo("day", String.valueOf(day))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("absence_calendar").document(userId)
                                    .collection(monthYearKey).document(document.getId()).delete()
                                    .addOnSuccessListener(aVoid -> removeFaltaFromLayout(day));
                        }
                    } else {
                        Toast.makeText(this, "Failed to remove the entry", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeFaltaFromLayout(int day) {
        int childCount = savedFaltasLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            LinearLayout faltaLayout = (LinearLayout) savedFaltasLayout.getChildAt(i);
            TextView dayTextView = (TextView) faltaLayout.getChildAt(0);
            if (dayTextView.getText().toString().contains(String.valueOf(day))) {
                savedFaltasLayout.removeViewAt(i);
                break;
            }
        }
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

                            // Colorir o dia no calendário
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

}