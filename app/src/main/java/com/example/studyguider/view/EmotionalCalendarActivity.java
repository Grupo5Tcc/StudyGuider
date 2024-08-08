package com.example.studyguider.view;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
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

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EmotionalCalendarActivity extends AppCompatActivity {

    private GridLayout gridLayoutCalendar;
    private int selectedColor = Color.WHITE;
    private Calendar calendar;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String userId;
    private TextView monthTextView;
    private int currentMonth;
    private int currentYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_emotional_calendar);

        gridLayoutCalendar = findViewById(R.id.gridLayoutCalendar);
        monthTextView = findViewById(R.id.textViewMonth);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Unauthenticated user. Please log in.", Toast.LENGTH_LONG).show();
            return;
        }
        userId = user.getUid();

        calendar = Calendar.getInstance();

        currentMonth = calendar.get(Calendar.MONTH);
        currentYear = calendar.get(Calendar.YEAR);

        Button buttonPreviousMonth = findViewById(R.id.buttonPreviousMonth);
        buttonPreviousMonth.setText("<");

        Button buttonNextMonth = findViewById(R.id.buttonNextMonth);
        buttonNextMonth.setText(">");

        updateCalendar();

        findViewById(R.id.buttonPreviousMonth).setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1);
            updateCalendar();
        });

        findViewById(R.id.buttonNextMonth).setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, 1);
            updateCalendar();
        });

        findViewById(R.id.colorOtimo).setOnClickListener(v -> selectedColor = Color.parseColor("#2196F3"));
        findViewById(R.id.colorBom).setOnClickListener(v -> selectedColor = Color.parseColor("#4CAF50"));
        findViewById(R.id.colorNormal).setOnClickListener(v -> selectedColor = Color.parseColor("#FFEB3B"));
        findViewById(R.id.colorRuim).setOnClickListener(v -> selectedColor = Color.parseColor("#FF9800"));
        findViewById(R.id.colorPessimo).setOnClickListener(v -> selectedColor = Color.parseColor("#F44336"));

        loadMoodData();

    }

    private void updateCalendar() {
        try {
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
                dayTextView.setBackgroundResource(R.drawable.ct_background_emotional_calendar);

                dayTextView.setOnClickListener(v -> {
                    dayTextView.setBackgroundColor(selectedColor);
                    saveMoodData(dayCopy, selectedColor);
                });

                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.height = GridLayout.LayoutParams.WRAP_CONTENT;
                param.width = 0;
                param.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                dayTextView.setLayoutParams(param);

                gridLayoutCalendar.addView(dayTextView);
            }

            loadMoodData();

        } catch (Exception e) {
            Log.e("EmotionalCalendar", "Error updating calendar: " + e.getMessage(), e);
            Toast.makeText(this, "Error updating the calendar.", Toast.LENGTH_LONG).show();
        }
    }

    private void saveMoodData(int day, int color) {
        Map<String, Object> moodData = new HashMap<>();
        moodData.put("day", day);
        moodData.put("color", color);

        String monthYear = getMonthYearKey();

        db.collection("emotional_calendar")
                .document(userId)
                .collection(monthYear)
                .document(String.valueOf(day))
                .set(moodData)
                .addOnSuccessListener(aVoid -> {
                    // Sucesso ao salvar
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void loadMoodData() {

        String monthYear = getMonthYearKey();

        db.collection("emotional_calendar")
                .document(userId)
                .collection(monthYear)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document != null && document.exists()) {
                                    int day = document.getLong("day").intValue();
                                    int color = document.getLong("color").intValue();

                                    if (gridLayoutCalendar != null && day > 0 && day <= gridLayoutCalendar.getChildCount()) {
                                        TextView dayTextView = (TextView) gridLayoutCalendar.getChildAt(day - 1);
                                        dayTextView.setBackgroundColor(color);
                                    } else {
                                        // Log se o gridLayoutCalendar for nulo ou day for inválido
                                        Log.e("EmotionalCalendar", "GridLayout is null or the day is invalid.");
                                    }
                                } else {
                                    // Log se o documento for nulo ou não existir
                                    Log.e("EmotionalCalendar", "Null document or does not exist.");
                                }
                            }
                        } else {
                            // Log se task.getResult() for nulo
                            Log.e("EmotionalCalendar", "Task result is null.");
                        }
                    } else {
                        // Falha ao carregar os dados
                        Toast.makeText(this, "Failed to load data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("EmotionalCalendar", "Error loading data: " + task.getException().getMessage());
                    }
                });
    }

    private String getMonthName() {
        return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, getResources().getConfiguration().locale);
    }

    private int getCurrentYear() {
        return calendar.get(Calendar.YEAR);
    }

    private String getMonthYearKey() {
        int month = calendar.get(Calendar.MONTH) + 1; // Meses são 0-indexados
        int year = calendar.get(Calendar.YEAR);
        return String.format("%04d-%02d", year, month);
    }

}