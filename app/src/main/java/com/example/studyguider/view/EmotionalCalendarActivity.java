package com.example.studyguider.view;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studyguider.R;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EmotionalCalendarActivity extends AppCompatActivity {

    private GridLayout gridLayoutCalendar;
    private int selectedColor = Color.WHITE;
    private Calendar calendar;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_emotional_calendar);

        gridLayoutCalendar = findViewById(R.id.gridLayoutCalendar);
        TextView monthTextView = findViewById(R.id.textViewMonth);
        calendar = Calendar.getInstance(); // Define o calendário para o mês atual

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            // Redirecionar para a tela de login ou mostrar uma mensagem
            Toast.makeText(this, "Usuário não autenticado. Por favor, faça login.", Toast.LENGTH_LONG).show();
            return;
        }
        userId = user.getUid();

        gridLayoutCalendar = findViewById(R.id.gridLayoutCalendar);
        monthTextView = findViewById(R.id.textViewMonth);

        // Set the current month and year
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        String monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, getResources().getConfiguration().locale);
        monthTextView.setText(String.format("%s %d", monthName, year));

        // Get the number of days in the current month
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Add day views to the calendar
        for (int day = 1; day <= daysInMonth; day++) {
            final int dayCopy = day;
            TextView dayTextView = new TextView(this);
            dayTextView.setText(String.valueOf(day));
            dayTextView.setGravity(Gravity.CENTER);
            dayTextView.setBackgroundResource(R.drawable.ct_background_emotional_calendar);

            dayTextView.setOnClickListener(v -> {
                dayTextView.setBackgroundColor(selectedColor);
                saveMoodData(dayCopy, selectedColor);});

            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            param.width = 0;
            param.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            dayTextView.setLayoutParams(param);

            gridLayoutCalendar.addView(dayTextView);

        }
        findViewById(R.id.colorOtimo).setOnClickListener(v -> selectedColor = Color.parseColor("#2196F3"));
        findViewById(R.id.colorBom).setOnClickListener(v -> selectedColor = Color.parseColor("#4CAF50"));
        findViewById(R.id.colorNormal).setOnClickListener(v -> selectedColor = Color.parseColor("#FFEB3B"));
        findViewById(R.id.colorRuim).setOnClickListener(v -> selectedColor = Color.parseColor("#FF9800"));
        findViewById(R.id.colorPessimo).setOnClickListener(v -> selectedColor = Color.parseColor("#F44336"));

        loadMoodData();

    }

    private void saveMoodData(int day, int color) {
        Map<String, Object> moodData = new HashMap<>();
        moodData.put("day", day);
        moodData.put("color", color);

        String monthYear = String.format("%s_%d", getMonthName(), getCurrentYear());

        db.collection("emotional_calendar")
                .document(userId)
                .collection(monthYear)
                .document(String.valueOf(day))
                .set(moodData)
                .addOnSuccessListener(aVoid -> {
                    // Sucesso ao salvar
                })
                .addOnFailureListener(e -> {
                    // Falha ao salvar
                    Toast.makeText(this, "Falha ao salvar dados: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void loadMoodData() {
        String monthYear = String.format("%s_%d", getMonthName(), getCurrentYear());

        db.collection("emotional_calendar")
                .document(userId)
                .collection(monthYear)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            int day = document.getLong("day").intValue();
                            int color = document.getLong("color").intValue();

                            TextView dayTextView = (TextView) gridLayoutCalendar.getChildAt(day - 1);
                            dayTextView.setBackgroundColor(color);
                        }
                    } else {
                        // Falha ao carregar os dados
                        Toast.makeText(this, "Falha ao carregar dados: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private String getMonthName() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, getResources().getConfiguration().locale);
    }

    private int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

}