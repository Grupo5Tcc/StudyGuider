package com.example.studyguider.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.studyguider.R;
import com.example.studyguider.viewmodels.EmotionalCalendarViewModel;
import com.example.studyguider.viewmodels.HeaderViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EmotionalCalendarActivity extends AppCompatActivity {

    private HeaderViewModel headerViewModel;
    private GridLayout gridLayoutCalendar;
    private TextView monthTextView;
    private int selectedColor = Color.WHITE;
    private EmotionalCalendarViewModel viewModel;
    private boolean isEmotionSelected = false; // Para controlar a seleção de emoção

    // Mapeia cores para listas de frases motivacionais
    private final Map<Integer, List<String>> colorMessages = Map.of(
            Color.parseColor("#2196F3"), List.of("Keep going, you're doing great!", "Stay positive and strong!", "You are amazing!"),
            Color.parseColor("#4CAF50"), List.of("Fantastic job! Keep it up!", "Great work today!", "You're on fire!"),
            Color.parseColor("#FFEB3B"), List.of("Things are looking good!", "Keep up the good vibes!", "You're making progress!"),
            Color.parseColor("#FF9800"), List.of("Keep pushing, you're almost there!", "Stay focused and motivated!", "You got this!"),
            Color.parseColor("#F44336"), List.of("Don't give up, you're almost there!", "Stay strong and resilient!", "Believe in yourself!")
    );

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
                Toast.makeText(this, "User not authenticated", Toast.LENGTH_LONG).show();
            } else {
                viewModel.loadMoodData();
            }
        });
    }

    private void setupColorButtons() {
        findViewById(R.id.colorOtimo).setOnClickListener(v -> {
            selectedColor = Color.parseColor("#2196F3");
            isEmotionSelected = true; // Emoção foi selecionada
        });
        findViewById(R.id.colorBom).setOnClickListener(v -> {
            selectedColor = Color.parseColor("#4CAF50");
            isEmotionSelected = true; // Emoção foi selecionada
        });
        findViewById(R.id.colorNormal).setOnClickListener(v -> {
            selectedColor = Color.parseColor("#FFEB3B");
            isEmotionSelected = true; // Emoção foi selecionada
        });
        findViewById(R.id.colorRuim).setOnClickListener(v -> {
            selectedColor = Color.parseColor("#FF9800");
            isEmotionSelected = true; // Emoção foi selecionada
        });
        findViewById(R.id.colorPessimo).setOnClickListener(v -> {
            selectedColor = Color.parseColor("#F44336");
            isEmotionSelected = true; // Emoção foi selecionada
        });
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
                dayTextView.setBackgroundColor(moodEntries.getOrDefault(day, Color.WHITE));

                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.height = GridLayout.LayoutParams.WRAP_CONTENT;
                param.width = 0;
                param.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                param.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                dayTextView.setLayoutParams(param);

                dayTextView.setOnClickListener(v -> {
                    if (isEmotionSelected) {
                        dayTextView.setBackgroundColor(selectedColor);
                        viewModel.saveMoodData(dayCopy, selectedColor);
                        showMotivationalMessage(selectedColor);
                    } else {
                        showEmotionSelectionMessage();
                    }
                });

                gridLayoutCalendar.addView(dayTextView);
            }
        } catch (Exception e) {
            Log.e("EmotionalCalendar", "Error updating calendar: " + e.getMessage(), e);
            Toast.makeText(this, "Error updating the calendar.", Toast.LENGTH_LONG).show();
        }
    }

    private void showMotivationalMessage(int color) {
        List<String> messages = colorMessages.getOrDefault(color, List.of("Stay positive!"));
        String message = messages.get(new Random().nextInt(messages.size()));

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_motivational_message);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(600, 400);

        TextView messageTextView = dialog.findViewById(R.id.textViewMessage);
        messageTextView.setText(message);

        View dialogView = dialog.findViewById(R.id.dialogRoot);
        dialogView.setBackgroundColor(Color.WHITE);
        dialogView.setPadding(24, 24, 24, 24);
        dialogView.setElevation(16);

        dialog.show();
    }

    private void showEmotionSelectionMessage() {
        Toast.makeText(this, "Por favor, escolha uma emoção antes de selecionar um dia!", Toast.LENGTH_SHORT).show();
    }
}
