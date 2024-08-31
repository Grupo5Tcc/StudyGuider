package com.example.studyguider.view;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studyguider.R;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        CardView cardViewAboutUs = findViewById(R.id.cardAboutUs);
        CardView cardViewPlanner = findViewById(R.id.cardPlanner);
        CardView cardViewToDoList = findViewById(R.id.cardToDoList);
        CardView cardViewAbsence = findViewById(R.id.cardAbsence);
        CardView cardViewEmotionalCalendar = findViewById(R.id.cardEmotionalCalendar);
        CardView cardViewGrades = findViewById(R.id.cardGrades);
        CardView cardViewRecovery = findViewById(R.id.cardRecovery);
        CardView cardViewSupplies = findViewById(R.id.cardSupplies);
        CardView cardViewSchoolDuty = findViewById(R.id.cardSchoolDuty);

        ImageView imageProfile = findViewById(R.id.imgPerfil);


        cardViewAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, AboutUsActivity.class);
                startActivity(intent);
            }
        });

        cardViewPlanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, PlannerActivity.class);
                startActivity(intent);
            }
        });

        cardViewAbsence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, AbsenceCalendarActivity.class);
                startActivity(intent);
            }
        });

        cardViewToDoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this,ToDoListActivity.class);
                startActivity(intent);
            }
        });

        cardViewEmotionalCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, EmotionalCalendarActivity.class);
                startActivity(intent);
            }
        });

        cardViewGrades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, GradesActivity.class);
                startActivity(intent);
            }
        });

        cardViewSchoolDuty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, SchoolDutyActivity.class);
                startActivity(intent);
            }
        });

        cardViewRecovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, RecoveryActivity.class);
                startActivity(intent);
            }
        });

        cardViewSupplies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, SuppliesActivity.class);
                startActivity(intent);
            }
        });

        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

    }
}