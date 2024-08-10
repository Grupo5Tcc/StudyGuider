package com.example.studyguider.view;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
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
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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
    private Button cancelButton;

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
            // Se o dia não estiver selecionado, selecione-o e mostre o formulário
            dayTextView.setBackgroundColor(selectedColor);
            selectedDayTextView = dayTextView;
            informationScrollView.setVisibility(View.VISIBLE);
            gridLayoutCalendar.setVisibility(View.GONE);
            savedFaltasLayout.setVisibility(View.GONE);
        } else {
            // Se o dia estiver selecionado, remova a anotação
            removeFalta(dayTextView.getText().toString());
            dayTextView.setBackgroundColor(defaultColor);
            Toast.makeText(this, "Anotação removida", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeFalta(String day) {
        int childCount = savedFaltasLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = savedFaltasLayout.getChildAt(i);
            if (view instanceof LinearLayout) {
                LinearLayout faltaLayout = (LinearLayout) view;
                TextView dayTextView = (TextView) faltaLayout.getChildAt(0);
                if (dayTextView.getText().toString().contains(day)) {
                    // Remove a falta correspondente ao dia
                    savedFaltasLayout.removeView(faltaLayout);
                    break;
                }
            }
        }

        // Hide information layout and show calendar
        informationScrollView.setVisibility(View.GONE);
        gridLayoutCalendar.setVisibility(View.VISIBLE);
        savedFaltasLayout.setVisibility(View.VISIBLE);
    }

    private void saveFalta() {
        String day = selectedDayTextView.getText().toString();
        String motivo = editTextMotivo.getText().toString();
        boolean atestado = checkBoxAtestado.isChecked();
        String nota = editTextNota.getText().toString();

        // Create a container for the falta information
        LinearLayout faltaInfoLayout = new LinearLayout(this);
        faltaInfoLayout.setOrientation(LinearLayout.VERTICAL);
        faltaInfoLayout.setPadding(32, 32, 32, 32);

        // Create a rounded background with a color
        int backgroundColor = Color.parseColor("#FFBC62");
        int cornerRadius = 20;

        GradientDrawable roundedBackground = new GradientDrawable();
        roundedBackground.setColor(backgroundColor);
        roundedBackground.setCornerRadius(cornerRadius);

        faltaInfoLayout.setBackground(roundedBackground);

        // Set layout parameters with rounded margins
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(24, 24, 24, 24); // Adding rounded margins around the entire block
        faltaInfoLayout.setLayoutParams(layoutParams);

        // Create and add the day information with margins
        TextView dayTextView = new TextView(this);
        dayTextView.setText(String.format("Dia: %s", day));
        dayTextView.setTextColor(Color.BLACK);
        dayTextView.setTextSize(16);

        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textViewParams.setMargins(0, 0, 0, 16); // Adding bottom margin
        dayTextView.setLayoutParams(textViewParams);
        faltaInfoLayout.addView(dayTextView);

        // Create and add the motivo information with margins
        TextView motivoTextView = new TextView(this);
        motivoTextView.setText(String.format("Motivo: %s", motivo));
        motivoTextView.setTextColor(Color.BLACK);
        motivoTextView.setTextSize(16);
        motivoTextView.setLayoutParams(textViewParams);
        faltaInfoLayout.addView(motivoTextView);

        // Create and add the atestado information with margins
        TextView atestadoTextView = new TextView(this);
        atestadoTextView.setText(String.format("Atestado: %s", atestado ? "Sim" : "Não"));
        atestadoTextView.setTextColor(Color.BLACK);
        atestadoTextView.setTextSize(16);
        atestadoTextView.setLayoutParams(textViewParams);
        faltaInfoLayout.addView(atestadoTextView);

        // Create and add the nota information with margins
        TextView notaTextView = new TextView(this);
        notaTextView.setText(String.format("Perdeu nota: %s", nota));
        notaTextView.setTextColor(Color.BLACK);
        notaTextView.setTextSize(16);
        notaTextView.setLayoutParams(textViewParams);
        faltaInfoLayout.addView(notaTextView);

        // Add the faltaInfoLayout to the savedFaltasLayout
        savedFaltasLayout.addView(faltaInfoLayout);

        // Reset input fields
        editTextMotivo.setText("");
        checkBoxAtestado.setChecked(false);
        editTextNota.setText("");

        // Hide information layout and show calendar
        informationScrollView.setVisibility(View.GONE);
        gridLayoutCalendar.setVisibility(View.VISIBLE);
        savedFaltasLayout.setVisibility(View.VISIBLE);
    }

    private void cancelForm() {
        // Reset input fields and hide the form
        clearForm();
        informationScrollView.setVisibility(View.GONE);
        gridLayoutCalendar.setVisibility(View.VISIBLE);
        savedFaltasLayout.setVisibility(View.VISIBLE);
    }

    private void clearForm() {
        editTextMotivo.setText("");
        checkBoxAtestado.setChecked(false);
        editTextNota.setText("");
    }

}