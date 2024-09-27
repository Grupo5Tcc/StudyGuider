package com.example.studyguider.view;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.studyguider.R;
import com.example.studyguider.models.Planner;
import com.example.studyguider.viewmodels.PlannerViewModel;

import java.util.Calendar;
import java.util.List;

public class PlannerActivity extends AppCompatActivity {

    private GridLayout gridLayoutCalendar;
    private ScrollView informationScrollView;
    private LinearLayout savedEventsLayout;

    private EditText editTextEventName;
    private EditText editTextEventTime;
    private EditText editTextAdditionalInfo;
    private Button saveButton;
    private Button colorPickerButton;

    private TextView selectedDayTextView;
    private PlannerViewModel plannerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);

        gridLayoutCalendar = findViewById(R.id.gridLayoutCalendar);
        informationScrollView = findViewById(R.id.informationScrollView);
        savedEventsLayout = findViewById(R.id.savedEventsLayout);

        editTextEventName = findViewById(R.id.editTextEventName);
        editTextEventTime = findViewById(R.id.editTextEventTime);
        editTextAdditionalInfo = findViewById(R.id.editTextAdditionalInfo);
        saveButton = findViewById(R.id.saveButton);
        colorPickerButton = findViewById(R.id.colorPickerButton);

        plannerViewModel = new PlannerViewModel();

        setupCalendar();

        colorPickerButton.setOnClickListener(v -> showColorPickerDialog());

        saveButton.setOnClickListener(v -> {
            if (selectedDayTextView == null) {
                Toast.makeText(this, "Selecione um dia primeiro!", Toast.LENGTH_SHORT).show();
            } else {
                saveEvent();
            }
        });

        // Observe events
        plannerViewModel.getEvents().observe(this, this::updateSavedEvents);
    }

    private void setupCalendar() {
        Calendar calendar = Calendar.getInstance();
        String monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, getResources().getConfiguration().locale);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int day = 1; day <= daysInMonth; day++) {
            TextView dayTextView = new TextView(this);
            dayTextView.setText(String.valueOf(day));
            dayTextView.setGravity(Gravity.CENTER);
            dayTextView.setBackgroundColor(Color.WHITE);
            dayTextView.setPadding(16, 16, 16, 16);

            dayTextView.setOnClickListener(v -> handleDayClick(dayTextView));

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.width = 0;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            dayTextView.setLayoutParams(params);

            gridLayoutCalendar.addView(dayTextView);
        }
    }

    private void handleDayClick(TextView dayTextView) {
        if (dayTextView.getBackground() instanceof ColorDrawable) {
            int backgroundColor = ((ColorDrawable) dayTextView.getBackground()).getColor();
            if (backgroundColor == Color.WHITE) {
                selectedDayTextView = dayTextView;
                dayTextView.setBackgroundColor(plannerViewModel.getSelectedColor().getValue());
                informationScrollView.setVisibility(View.VISIBLE);
                gridLayoutCalendar.setVisibility(View.GONE);
                savedEventsLayout.setVisibility(View.GONE);
            } else {
                dayTextView.setBackgroundColor(Color.WHITE);
                selectedDayTextView = null;
                informationScrollView.setVisibility(View.GONE);
                gridLayoutCalendar.setVisibility(View.VISIBLE);
                savedEventsLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showColorPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escolha uma cor");

        String[] colors = {"Rosa", "Roxo", "Amarelo", "Azul"};
        int[] colorValues = {
                Color.parseColor("#FFCBDB"), // Rosa
                Color.parseColor("#CB9BDE"), // Roxo
                Color.parseColor("#FFFFCC"), // Amarelo
                Color.parseColor("#87CEEB")  // Azul
        };

        builder.setItems(colors, (dialog, which) -> {
            int color = colorValues[which];
            plannerViewModel.setColor(color);
            if (selectedDayTextView != null) {
                selectedDayTextView.setBackgroundColor(color);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveEvent() {
        String day = selectedDayTextView.getText().toString();
        String eventName = editTextEventName.getText().toString();
        String eventTime = editTextEventTime.getText().toString();
        String additionalInfo = editTextAdditionalInfo.getText().toString();

        if (eventName.isEmpty() || eventTime.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show();
            return;
        }

        plannerViewModel.addEvent(eventName, eventTime, additionalInfo, plannerViewModel.getSelectedColor().getValue());

        // Reset the selected day color to the chosen color
        if (selectedDayTextView != null) {
            selectedDayTextView.setBackgroundColor(plannerViewModel.getSelectedColor().getValue());
        }

        selectedDayTextView = null;
        editTextEventName.setText("");
        editTextEventTime.setText("");
        editTextAdditionalInfo.setText("");
        informationScrollView.setVisibility(View.GONE);
        gridLayoutCalendar.setVisibility(View.VISIBLE);
        savedEventsLayout.setVisibility(View.VISIBLE);
    }

    private void updateSavedEvents(List<Planner> events) {
        savedEventsLayout.removeAllViews();
        for (Planner event : events) {
            TextView eventTextView = new TextView(this);
            eventTextView.setText(String.format("Evento: %s\nHora: %s\nInformações: %s", event.getName(), event.getTime(), event.getInfo()));
            eventTextView.setBackgroundColor(event.getColor());
            eventTextView.setPadding(16, 16, 16, 16);
            eventTextView.setTextColor(Color.BLACK);
            savedEventsLayout.addView(eventTextView);
        }
    }
}
