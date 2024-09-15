package com.example.studyguider.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.studyguider.R;
import com.example.studyguider.viewmodels.EmotionalCalendarViewModel;
import com.example.studyguider.viewmodels.HeaderViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.Map;

public class EmotionalCalendarActivity extends AppCompatActivity {

    private GridLayout gridLayoutCalendar;
    private TextView monthTextView;
    private int selectedColor = Color.WHITE;
    private EmotionalCalendarViewModel viewModel;
    private HeaderViewModel headerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotional_calendar);

        headerViewModel = new ViewModelProvider(this).get(HeaderViewModel.class);

        View headerView = findViewById(R.id.header);
        HeaderActivity headerActivity = new HeaderActivity(headerView, headerViewModel, this);

        FirebaseUser currentUser1 = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser1 != null) {
            headerViewModel.fetchUsername(currentUser1);
        }

        Button buttonPreviousMonth = findViewById(R.id.buttonPreviousMonth);
        buttonPreviousMonth.setText("<");

        Button buttonNextMonth = findViewById(R.id.buttonNextMonth);
        buttonNextMonth.setText(">");

        viewModel = new ViewModelProvider(this).get(EmotionalCalendarViewModel.class);

        gridLayoutCalendar = findViewById(R.id.gridLayoutCalendar);
        monthTextView = findViewById(R.id.textViewMonth);
        findViewById(R.id.buttonPreviousMonth).setOnClickListener(v -> viewModel.goToPreviousMonth());
        findViewById(R.id.buttonNextMonth).setOnClickListener(v -> viewModel.goToNextMonth());

        setupColorButtons();

        viewModel.getMonthYearDisplayName().observe(this, this::updateMonthTextView);
        viewModel.getMoodData().observe(this, moodEntries -> {
            if (moodEntries != null) {
                updateCalendar(moodEntries);
            }
        });

        viewModel.getUserId().observe(this, userId -> {
            if (userId == null) {
                // Redirecionar para login ou mostrar mensagem de erro
                Toast.makeText(this, "User not authenticated", Toast.LENGTH_LONG).show();
            } else {
                viewModel.loadMoodData(); // Carrega os dados ao iniciar ou mudar de mês
            }
        });
    }

    private void setupColorButtons() {
        findViewById(R.id.colorOtimo).setOnClickListener(v -> selectedColor = Color.parseColor("#2196F3"));
        findViewById(R.id.colorBom).setOnClickListener(v -> selectedColor = Color.parseColor("#4CAF50"));
        findViewById(R.id.colorNormal).setOnClickListener(v -> selectedColor = Color.parseColor("#FFEB3B"));
        findViewById(R.id.colorRuim).setOnClickListener(v -> selectedColor = Color.parseColor("#FF9800"));
        findViewById(R.id.colorPessimo).setOnClickListener(v -> selectedColor = Color.parseColor("#F44336"));
    }

    private void updateMonthTextView(String monthYear) {
        monthTextView.setText(monthYear);
    }

    private void updateCalendar(Map<Integer, Integer> moodEntries) {
        try {
            gridLayoutCalendar.removeAllViews();

            Calendar calendar = viewModel.getCalendar();
            int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            for (int day = 1; day <= daysInMonth; day++) {
                final int dayCopy = day;
                TextView dayTextView = new TextView(this);
                dayTextView.setText(String.valueOf(day));
                dayTextView.setGravity(Gravity.CENTER);
                dayTextView.setTextSize(16);
                dayTextView.setPadding(8, 8, 8, 8);
                dayTextView.setBackgroundColor(moodEntries.getOrDefault(day, Color.WHITE)); // Define cor a partir dos dados carregados

                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.height = GridLayout.LayoutParams.WRAP_CONTENT;
                param.width = 0;
                param.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                param.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                dayTextView.setLayoutParams(param);

                dayTextView.setOnClickListener(v -> {
                    dayTextView.setBackgroundColor(selectedColor);
                    viewModel.saveMoodData(dayCopy, selectedColor);
                });

                gridLayoutCalendar.addView(dayTextView);
            }
        } catch (Exception e) {
            Log.e("EmotionalCalendar", "Error updating calendar: " + e.getMessage(), e);
            Toast.makeText(this, "Error updating the calendar.", Toast.LENGTH_LONG).show();
        }

    }

}