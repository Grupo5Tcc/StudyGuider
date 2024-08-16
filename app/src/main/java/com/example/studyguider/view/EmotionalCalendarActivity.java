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
import androidx.lifecycle.ViewModelProvider;

import com.example.studyguider.R;
import com.example.studyguider.viewmodels.EmotionalCalendarViewModel;
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
    private EmotionalCalendarViewModel viewModel;
    private TextView monthTextView;
    private int daysInMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_emotional_calendar);

        gridLayoutCalendar = findViewById(R.id.gridLayoutCalendar);
        monthTextView = findViewById(R.id.textViewMonth);

        viewModel = new ViewModelProvider(this).get(EmotionalCalendarViewModel.class);

        observeViewModel();

        Button buttonPreviousMonth = findViewById(R.id.buttonPreviousMonth);
        buttonPreviousMonth.setText("<");

        Button buttonNextMonth = findViewById(R.id.buttonNextMonth);
        buttonNextMonth.setText(">");

        buttonPreviousMonth.setOnClickListener(v -> viewModel.changeMonth(-1));
        buttonNextMonth.setOnClickListener(v -> viewModel.changeMonth(1));

        findViewById(R.id.colorOtimo).setOnClickListener(v -> viewModel.setSelectedColor(Color.parseColor("#2196F3")));
        findViewById(R.id.colorBom).setOnClickListener(v -> viewModel.setSelectedColor(Color.parseColor("#4CAF50")));
        findViewById(R.id.colorNormal).setOnClickListener(v -> viewModel.setSelectedColor(Color.parseColor("#FFEB3B")));
        findViewById(R.id.colorRuim).setOnClickListener(v -> viewModel.setSelectedColor(Color.parseColor("#FF9800")));
        findViewById(R.id.colorPessimo).setOnClickListener(v -> viewModel.setSelectedColor(Color.parseColor("#F44336")));

        viewModel.loadMoodData();
    }

    private void observeViewModel() {
        viewModel.getMonthYear().observe(this, this::updateCalendar);
        viewModel.getMoodData().observe(this, this::updateCalendarMoodData);
    }

    private void updateCalendar(String monthYear) {
        monthTextView.setText(monthYear);

        gridLayoutCalendar.removeAllViews();
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int day = 1; day <= daysInMonth; day++) {
            final int dayCopy = day;
            TextView dayTextView = new TextView(this);
            dayTextView.setText(String.valueOf(day));
            dayTextView.setGravity(Gravity.CENTER);
            dayTextView.setBackgroundResource(R.drawable.ct_background_emotional_calendar);

            dayTextView.setOnClickListener(v -> {
                viewModel.getSelectedColor().observe(this, color -> {
                    dayTextView.setBackgroundColor(color);
                    viewModel.saveMoodData(dayCopy, color);
                });
            });

            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            param.width = 0;
            param.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            dayTextView.setLayoutParams(param);

            gridLayoutCalendar.addView(dayTextView);
        }
    }

    private void updateCalendarMoodData(Map<Integer, Integer> moodData) {
        if (moodData != null) {
            for (Map.Entry<Integer, Integer> entry : moodData.entrySet()) {
                int day = entry.getKey();
                int color = entry.getValue();

                if (gridLayoutCalendar != null && day > 0 && day <= gridLayoutCalendar.getChildCount()) {
                    TextView dayTextView = (TextView) gridLayoutCalendar.getChildAt(day - 1);
                    dayTextView.setBackgroundColor(color);
                } else {
                    Log.e("EmotionalCalendar", "GridLayout is null or the day is invalid.");
                }
            }
        }
    }

}