package com.example.studyguider.view;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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
    private EditText editTextAdditionalInfo;
    private Button saveButton;
    private Button colorPickerButton;

    private TextView selectedDayTextView;
    private TextView textViewEventTime;

    private PlannerViewModel plannerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);

        plannerViewModel = new ViewModelProvider(this).get(PlannerViewModel.class);

        gridLayoutCalendar = findViewById(R.id.gridLayoutCalendar);
        informationScrollView = findViewById(R.id.informationScrollView);
        savedEventsLayout = findViewById(R.id.savedEventsLayout);

        editTextEventName = findViewById(R.id.editTextEventName);
        textViewEventTime = findViewById(R.id.textViewEventTime);
        editTextAdditionalInfo = findViewById(R.id.editTextAdditionalInfo);
        saveButton = findViewById(R.id.saveButton);
        colorPickerButton = findViewById(R.id.colorPickerButton);

        setupCalendar();
        setupObservers();

        colorPickerButton.setOnClickListener(v -> showColorPickerDialog());

        saveButton.setOnClickListener(v -> {
            if (selectedDayTextView == null) {
                Toast.makeText(this, "Selecione um dia primeiro!", Toast.LENGTH_SHORT).show();
            } else {
                saveEvent();
            }
        });

        textViewEventTime.setOnClickListener(v -> showTimePickerDialog());
    }

    private void setupCalendar() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        String monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, getResources().getConfiguration().locale);
        TextView monthTextView = findViewById(R.id.textViewMonth);
        monthTextView.setText(String.format("%s %d", monthName, year));

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int day = 1; day <= daysInMonth; day++) {
            TextView dayTextView = new TextView(this);
            dayTextView.setText(String.valueOf(day));
            dayTextView.setGravity(Gravity.CENTER);
            dayTextView.setBackgroundColor(Color.WHITE);
            dayTextView.setPadding(16, 16, 16, 16);

            int finalDay = day;
            dayTextView.setOnClickListener(v -> handleDayClick(dayTextView, finalDay));

            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            param.width = 0;
            param.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            dayTextView.setLayoutParams(param);

            gridLayoutCalendar.addView(dayTextView);
        }
    }

    private void handleDayClick(TextView dayTextView, int day) {
        if (dayTextView.getBackground() instanceof ColorDrawable) {
            int backgroundColor = ((ColorDrawable) dayTextView.getBackground()).getColor();
            if (backgroundColor == Color.WHITE) {
                selectedDayTextView = dayTextView;
                dayTextView.setBackgroundColor(plannerViewModel.getSelectedColor().getValue());
                plannerViewModel.selectDay(String.valueOf(day));
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

        String[] colors = {"Roxo", "Rosa", "Amarelo", "Azul"};
        int[] colorValues = {Color.parseColor("#CB9BDE"), Color.parseColor("#FFCBDB"), Color.parseColor("#FFFFCC"), Color.parseColor("#87CEEB")};

        builder.setItems(colors, (dialog, which) -> {
            plannerViewModel.setColor(colorValues[which]);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showTimePickerDialog() {
        // Implementação do TimePickerDialog
        int hour = 12; // Hora padrão
        int minute = 0; // Minuto padrão

        new android.app.TimePickerDialog(this, (view, selectedHour, selectedMinute) -> {
            String time = String.format("%02d:%02d", selectedHour, selectedMinute);
            textViewEventTime.setText(time);
        }, hour, minute, true).show();
    }

    private void saveEvent() {
        String day = plannerViewModel.getSelectedDay().getValue();
        String eventName = editTextEventName.getText().toString();
        String eventTime = textViewEventTime.getText().toString();
        String additionalInfo = editTextAdditionalInfo.getText().toString();

        if (eventName.isEmpty() || eventTime.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show();
            return;
        }

        plannerViewModel.addEvent(eventName, eventTime, additionalInfo, plannerViewModel.getSelectedColor().getValue(), day); // Passa o dia

        informationScrollView.setVisibility(View.GONE);
        gridLayoutCalendar.setVisibility(View.VISIBLE);
        savedEventsLayout.setVisibility(View.VISIBLE);

        if (selectedDayTextView != null) {
            selectedDayTextView.setBackgroundColor(plannerViewModel.getSelectedColor().getValue());
        }

        selectedDayTextView = null;
        editTextEventName.setText("");
        textViewEventTime.setText("Selecione o horário");
        editTextAdditionalInfo.setText("");
    }

    private void setupObservers() {
        plannerViewModel.getEvents().observe(this, this::updateSavedEvents);
    }

    private void updateSavedEvents(List<Planner> events) {
        savedEventsLayout.removeAllViews();
        for (Planner event : events) {
            TextView eventTextView = new TextView(this);
            eventTextView.setText(String.format("Evento: %s\nDia: %s\nHora: %s\nInformações: %s",
                    event.getName(), event.getDay(), event.getTime(), event.getInfo())); // Inclui o dia
            eventTextView.setBackgroundColor(event.getColor());
            eventTextView.setPadding(32, 32, 32, 32);
            eventTextView.setTextColor(Color.BLACK);
            GradientDrawable roundedBackground = new GradientDrawable();
            roundedBackground.setCornerRadius(20);

            // Adiciona espaçamento entre eventos
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(24, 24, 24, 24); // Define margens superior e inferior
            eventTextView.setLayoutParams(params);



            savedEventsLayout.addView(eventTextView);
        }
    }
}
