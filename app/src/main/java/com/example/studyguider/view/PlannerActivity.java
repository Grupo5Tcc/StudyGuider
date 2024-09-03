package com.example.studyguider.view;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.studyguider.R;
import com.example.studyguider.models.Planner;
import com.example.studyguider.viewmodels.PlannerViewModel;

import java.util.List;

public class PlannerActivity extends AppCompatActivity {

    private PlannerViewModel plannerViewModel;
    private GridLayout gridLayoutCalendar;
    private int selectedColor = Color.WHITE;
    private int daysInMonth;
    private LinearLayout containerTarefas;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_planner);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        plannerViewModel = new ViewModelProvider(this).get(PlannerViewModel.class);


        plannerViewModel.getPlanners().observe(this, new Observer<List<Planner>>() {
            @Override
            public void onChanged(List<Planner> planners) {
                atualizarUI(planners);
            }
        });


        plannerViewModel.adicionarPlanner(new Planner("1", "Exemplo de Tarefa", "01/01/2024", Color.RED));


        CalendarView calendarView = findViewById(R.id.calendarView);
        containerTarefas = findViewById(R.id.container_tarefas);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                containerTarefas.setVisibility(View.VISIBLE);
            }
        });
    }

    private void atualizarUI(List<Planner> planners) {

    }
}
