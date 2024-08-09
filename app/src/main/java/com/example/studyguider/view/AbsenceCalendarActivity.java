package com.example.studyguider.view;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studyguider.R;

import java.util.Calendar;

public class AbsenceCalendarActivity extends AppCompatActivity {

    private GridLayout gridLayoutCalendar;
    private int selectedColor = Color.parseColor("#F68C0A"); // Selected color
    private int defaultColor = Color.WHITE; // Default background color

    private ScrollView informationScrollView;
    private LinearLayout informationLayout;
    private LinearLayout savedFaltasLayout;

    private TextView selectedDayTextView;
    private EditText editTextMotivo;
    private CheckBox checkBoxAtestado;
    private EditText editTextNota;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absence_calendar);

        gridLayoutCalendar = findViewById(R.id.gridLayoutCalendar);
        TextView monthTextView = findViewById(R.id.textViewMonth);

        informationScrollView = findViewById(R.id.informationScrollView);
        informationLayout = findViewById(R.id.informationLayout);
        savedFaltasLayout = findViewById(R.id.savedFaltasLayout);

        editTextMotivo = findViewById(R.id.editTextMotivo);
        checkBoxAtestado = findViewById(R.id.checkBoxAtestado);
        editTextNota = findViewById(R.id.editTextNota);
        saveButton = findViewById(R.id.saveButton);

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
            dayTextView.setBackgroundColor(defaultColor);
            dayTextView.setPadding(16, 16, 16, 16); // Adding padding for better appearance

            dayTextView.setOnClickListener(v -> handleDayClick(dayTextView));

            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            param.width = 0;
            param.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            dayTextView.setLayoutParams(param);

            gridLayoutCalendar.addView(dayTextView);
        }

        saveButton.setOnClickListener(v -> saveFalta());

    }

    private void handleDayClick(TextView dayTextView) {
        ColorDrawable background = (ColorDrawable) dayTextView.getBackground();
        if (background.getColor() == defaultColor) {
            dayTextView.setBackgroundColor(selectedColor);
            selectedDayTextView = dayTextView;
            informationScrollView.setVisibility(View.VISIBLE);
            gridLayoutCalendar.setVisibility(View.GONE);
            savedFaltasLayout.setVisibility(View.GONE); // Ocultar faltas salvas
        } else {
            dayTextView.setBackgroundColor(defaultColor);
            informationScrollView.setVisibility(View.GONE);
            gridLayoutCalendar.setVisibility(View.VISIBLE);
            savedFaltasLayout.setVisibility(View.VISIBLE); // Mostrar faltas salvas
        }
    }

    private void saveFalta() {
        String day = selectedDayTextView.getText().toString();
        String motivo = editTextMotivo.getText().toString();
        boolean atestado = checkBoxAtestado.isChecked();
        String nota = editTextNota.getText().toString();

        TextView faltaInfoTextView = new TextView(this);
        faltaInfoTextView.setText(String.format("Dia %s\nMotivo: %s\nAtestado: %s\nPerdeu nota: %s",
                day, motivo, atestado ? "Sim" : "Não", nota));
        faltaInfoTextView.setBackgroundColor(Color.parseColor("#FFBC62"));
        faltaInfoTextView.setPadding(16, 16, 16, 16);

        savedFaltasLayout.addView(faltaInfoTextView);

        // Reset input fields
        editTextMotivo.setText("");
        checkBoxAtestado.setChecked(false);
        editTextNota.setText("");

        // Hide information layout and show calendar
        informationScrollView.setVisibility(View.GONE);
        gridLayoutCalendar.setVisibility(View.VISIBLE);
        savedFaltasLayout.setVisibility(View.VISIBLE); // Mostrar faltas salvas
    }

}