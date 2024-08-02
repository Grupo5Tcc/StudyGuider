package com.example.studyguider.view;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studyguider.R;

import java.util.Calendar;

public class EmotionalCalendarActivity extends AppCompatActivity {

    private GridLayout gridLayoutCalendar;
    private int selectedColor = Color.WHITE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_emotional_calendar);
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/

        gridLayoutCalendar = findViewById(R.id.gridLayoutCalendar);
        TextView monthTextView = findViewById(R.id.textViewMonth);

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
            TextView dayTextView = new TextView(this);
            dayTextView.setText(String.valueOf(day));
            dayTextView.setGravity(Gravity.CENTER);
            dayTextView.setBackgroundResource(R.drawable.ct_background_emotional_calendar);

            dayTextView.setOnClickListener(v -> dayTextView.setBackgroundColor(selectedColor));

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
    }

}