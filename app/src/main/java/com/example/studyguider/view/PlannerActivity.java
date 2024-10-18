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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import com.example.studyguider.R;
import com.example.studyguider.models.Planner;
import com.example.studyguider.viewmodels.PlannerViewModel;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<String, Integer> dayColors = new HashMap<>();

    private int currentMonth;
    private int currentYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Button buttonPreviousMonth = findViewById(R.id.buttonPreviousMonth);
        buttonPreviousMonth.setText("<");

        Button buttonNextMonth = findViewById(R.id.buttonNextMonth);
        buttonNextMonth.setText(">");

        plannerViewModel = new ViewModelProvider(this).get(PlannerViewModel.class);

        gridLayoutCalendar = findViewById(R.id.gridLayoutCalendar);
        informationScrollView = findViewById(R.id.informationScrollView);
        savedEventsLayout = findViewById(R.id.savedEventsLayout);

        editTextEventName = findViewById(R.id.editTextEventName);
        textViewEventTime = findViewById(R.id.textViewEventTime);
        editTextAdditionalInfo = findViewById(R.id.editTextAdditionalInfo);
        saveButton = findViewById(R.id.saveButton);
        colorPickerButton = findViewById(R.id.colorPickerButton);

        Calendar calendar = Calendar.getInstance();
        currentMonth = calendar.get(Calendar.MONTH);
        currentYear = calendar.get(Calendar.YEAR);

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

        buttonPreviousMonth.setOnClickListener(v -> {
            currentMonth--;
            if (currentMonth < 0) {
                currentMonth = 11;
                currentYear--;
            }
            setupCalendar();
            updateSavedEvents(plannerViewModel.getEvents().getValue());
        });

        buttonNextMonth.setOnClickListener(v -> {
            currentMonth++;
            if (currentMonth > 11) {
                currentMonth = 0;
                currentYear++;
            }
            setupCalendar();
            updateSavedEvents(plannerViewModel.getEvents().getValue());
        });
    }

    private void setupCalendar() {
        gridLayoutCalendar.removeAllViews();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, currentMonth);
        calendar.set(Calendar.YEAR, currentYear);

        String monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, getResources().getConfiguration().locale);
        TextView monthTextView = findViewById(R.id.textViewMonth);
        monthTextView.setText(String.format("%s %d", monthName, currentYear));

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int day = 1; day <= daysInMonth; day++) {
            TextView dayTextView = new TextView(this);
            dayTextView.setText(String.valueOf(day));
            dayTextView.setGravity(Gravity.CENTER);
            dayTextView.setBackgroundColor(dayColors.getOrDefault(getDayKey(day), Color.WHITE));
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

        TextView monthTextView = findViewById(R.id.textViewMonth);
        Button buttonPreviousMonth = findViewById(R.id.buttonPreviousMonth);
        Button buttonNextMonth = findViewById(R.id.buttonNextMonth);

        monthTextView.setVisibility(View.INVISIBLE);
        buttonPreviousMonth.setText("");
        buttonNextMonth.setText("");
        buttonNextMonth.setEnabled(false);
        buttonPreviousMonth.setEnabled(false);

        if (dayTextView.getBackground() instanceof ColorDrawable) {
            int backgroundColor = ((ColorDrawable) dayTextView.getBackground()).getColor();
            if (backgroundColor == Color.WHITE) {
                selectedDayTextView = dayTextView;
                dayTextView.setBackgroundColor(plannerViewModel.getSelectedColor().getValue());
                dayColors.put(getDayKey(day), plannerViewModel.getSelectedColor().getValue());
                plannerViewModel.selectDay(String.valueOf(day));
                informationScrollView.setVisibility(View.VISIBLE);
                gridLayoutCalendar.setVisibility(View.GONE);
                savedEventsLayout.setVisibility(View.GONE);
            } else {
                dayTextView.setBackgroundColor(Color.WHITE);
                dayColors.put(getDayKey(day), Color.WHITE);
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
        int hour = 12;
        int minute = 0;

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

        TextView monthTextView = findViewById(R.id.textViewMonth);
        Button buttonPreviousMonth = findViewById(R.id.buttonPreviousMonth);
        Button buttonNextMonth = findViewById(R.id.buttonNextMonth);

        if (eventName.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the event time is empty
        if (eventTime.equals("Clique Aqui")) {
            Toast.makeText(this, "Selecione um horário!", Toast.LENGTH_SHORT).show();
            return;
        }

        Integer selectedColor = plannerViewModel.getSelectedColor().getValue();
        if (selectedColor == null || selectedColor == Color.WHITE) {
            Toast.makeText(this, "Selecione uma cor para o evento!", Toast.LENGTH_SHORT).show();
            return;
        }

        plannerViewModel.addEvent(eventName, eventTime, additionalInfo, plannerViewModel.getSelectedColor().getValue(), getDayKey(Integer.parseInt(day)));

        if (selectedDayTextView != null) {
            Integer newColor = plannerViewModel.getSelectedColor().getValue();
            dayColors.put(getDayKey(Integer.parseInt(day)), newColor);
            selectedDayTextView.setBackgroundColor(newColor);
        }

        informationScrollView.setVisibility(View.GONE);
        gridLayoutCalendar.setVisibility(View.VISIBLE);
        savedEventsLayout.setVisibility(View.VISIBLE);

        selectedDayTextView = null;
        editTextEventName.setText("");
        textViewEventTime.setText("Selecione o horário");
        editTextAdditionalInfo.setText("");

        monthTextView.setVisibility(View.VISIBLE);
        buttonPreviousMonth.setText("<");
        buttonNextMonth.setText(">");
        buttonNextMonth.setEnabled(true);
        buttonPreviousMonth.setEnabled(true);
    }

    private void setupObservers() {
        plannerViewModel.getEvents().observe(this, this::updateSavedEvents);
    }

    private void updateSavedEvents(List<Planner> events) {
        savedEventsLayout.removeAllViews();

        // Mapeia os dias que possuem eventos
        Map<String, Boolean> daysWithEvents = new HashMap<>();

        for (Planner event : events) {
            String[] eventDateParts = event.getDay().split("-");
            int eventDay = Integer.parseInt(eventDateParts[0]);
            int eventMonth = Integer.parseInt(eventDateParts[1]);
            int eventYear = Integer.parseInt(eventDateParts[2]);

            if (eventMonth == currentMonth && eventYear == currentYear) {
                daysWithEvents.put(getDayKey(eventDay), true); // Marca o dia com evento

                LinearLayout eventLayout = new LinearLayout(this);
                eventLayout.setOrientation(LinearLayout.HORIZONTAL);
                eventLayout.setPadding(16, 16, 16, 16);

                TextView eventTextView = new TextView(this);
                eventTextView.setText(String.format("Evento: %s\nDia: %d\nHora: %s\nInformações: %s",
                        event.getName(), eventDay, event.getTime(), event.getInfo()));
                eventTextView.setBackgroundColor(event.getColor());
                eventTextView.setTextColor(Color.BLACK);
                eventTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                ImageView deleteIcon = new ImageView(this);
                deleteIcon.setImageResource(R.drawable.ic_delete);
                deleteIcon.setLayoutParams(new LinearLayout.LayoutParams(80, 80));
                deleteIcon.setPadding(16, 0, 0, 0);
                deleteIcon.setOnClickListener(v -> {
                    plannerViewModel.removeEvent(event);
                    Toast.makeText(this, "Evento removido", Toast.LENGTH_SHORT).show();
                    updateCalendarDayColor(event.getDay());
                    updateSavedEvents(plannerViewModel.getEvents().getValue()); // Atualiza a lista de eventos
                });

                eventLayout.addView(eventTextView);
                eventLayout.addView(deleteIcon);
                savedEventsLayout.addView(eventLayout);
            }
        }

        // Atualiza as cores dos dias no calendário
        for (int i = 0; i < gridLayoutCalendar.getChildCount(); i++) {
            TextView dayTextView = (TextView) gridLayoutCalendar.getChildAt(i);
            String dayKey = getDayKey(Integer.parseInt(dayTextView.getText().toString()));

            // Se o dia não tiver evento, define a cor como branco
            if (!daysWithEvents.containsKey(dayKey)) {
                dayColors.put(dayKey, Color.WHITE);
            }

            dayTextView.setBackgroundColor(dayColors.getOrDefault(dayKey, Color.WHITE));
        }
    }

    private void updateCalendarDayColor(String dayKey) {
        dayColors.put(dayKey, Color.WHITE); // Define a cor como branco ao remover o evento

        for (int i = 0; i < gridLayoutCalendar.getChildCount(); i++) {
            TextView dayTextView = (TextView) gridLayoutCalendar.getChildAt(i);
            String currentDayKey = getDayKey(Integer.parseInt(dayTextView.getText().toString()));
            dayTextView.setBackgroundColor(dayColors.getOrDefault(currentDayKey, Color.WHITE));
        }
    }

    private String getDayKey(int day) {
        return day + "-" + currentMonth + "-" + currentYear;
    }

}
