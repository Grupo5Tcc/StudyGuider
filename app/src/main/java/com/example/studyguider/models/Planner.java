package com.example.studyguider.models;


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

import java.util.Calendar;


public class Planner extends AppCompatActivity {


    private GridLayout gridLayoutCalendar;
    private ScrollView informationScrollView;
    private LinearLayout savedEventsLayout;


    private EditText editTextEventName;
    private EditText editTextEventTime;
    private EditText editTextAdditionalInfo;
    private Button saveButton;
    private Button colorPickerButton;


    private TextView selectedDayTextView;
    private int selectedColor = Color.parseColor("#F68C0A"); // Default color


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);


        gridLayoutCalendar = findViewById(R.id.gridLayoutCalendar);
        TextView monthTextView = findViewById(R.id.textViewMonth);


        informationScrollView = findViewById(R.id.informationScrollView);
        savedEventsLayout = findViewById(R.id.savedEventsLayout);


        editTextEventName = findViewById(R.id.editTextEventName);
        editTextEventTime = findViewById(R.id.editTextEventTime);
        editTextAdditionalInfo = findViewById(R.id.editTextAdditionalInfo);
        saveButton = findViewById(R.id.saveButton);
        colorPickerButton = findViewById(R.id.colorPickerButton);


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
            dayTextView.setBackgroundColor(Color.WHITE);
            dayTextView.setPadding(16, 16, 16, 16); // Add padding for better appearance


            dayTextView.setOnClickListener(v -> handleDayClick(dayTextView));


            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            param.width = 0;
            param.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            dayTextView.setLayoutParams(param);


            gridLayoutCalendar.addView(dayTextView);
        }


        colorPickerButton.setOnClickListener(v -> showColorPickerDialog());


        saveButton.setOnClickListener(v -> {
            if (selectedDayTextView == null) {
                Toast.makeText(this, "Selecione um dia primeiro!", Toast.LENGTH_SHORT).show();
            } else {
                saveEvent();
            }
        });
    }


    private void handleDayClick(TextView dayTextView) {
        if (dayTextView.getBackground() instanceof ColorDrawable) {
            int backgroundColor = ((ColorDrawable) dayTextView.getBackground()).getColor();
            if (backgroundColor == Color.WHITE) {
                selectedDayTextView = dayTextView;
                dayTextView.setBackgroundColor(selectedColor);
                informationScrollView.setVisibility(View.VISIBLE);
                gridLayoutCalendar.setVisibility(View.GONE);
                savedEventsLayout.setVisibility(View.GONE); // Hide saved events
            } else {
                dayTextView.setBackgroundColor(Color.WHITE);
                selectedDayTextView = null;
                informationScrollView.setVisibility(View.GONE);
                gridLayoutCalendar.setVisibility(View.VISIBLE);
                savedEventsLayout.setVisibility(View.VISIBLE); // Show saved events
            }
        }
    }


    private void showColorPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escolha uma cor");


        String[] colors = {"Roxo", "Rosa", "Amarelo", "Azul"};
        int[] colorValues = {Color.parseColor("#CB9BDE"), Color.parseColor("#FFCBDB"), Color.parseColor("#FFFFCC"), Color.parseColor("#87CEEB")};


        builder.setItems(colors, (dialog, which) -> selectedColor = colorValues[which]);


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


        TextView eventTextView = new TextView(this);
        eventTextView.setText(String.format("Evento: %s\nHora: %s\nInformações: %s", eventName, eventTime, additionalInfo));
        eventTextView.setBackgroundColor(selectedColor);
        eventTextView.setPadding(16, 16, 16, 16);
        eventTextView.setTextColor(Color.BLACK);


        savedEventsLayout.addView(eventTextView);


        // Hide the scroll view and show the grid layout and saved events
        informationScrollView.setVisibility(View.GONE);
        gridLayoutCalendar.setVisibility(View.VISIBLE);
        savedEventsLayout.setVisibility(View.VISIBLE);


        // Reset the selected day color to default
        if (selectedDayTextView != null) {
            selectedDayTextView.setBackgroundColor(selectedColor);
        }


        selectedDayTextView = null;
        editTextEventName.setText("");
        editTextEventTime.setText("");
        editTextAdditionalInfo.setText("");
    }
}
