package com.example.studyguider.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.studyguider.R;
import com.example.studyguider.viewmodels.ProfileViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class ProfileActivity extends AppCompatActivity {

    private ProfileViewModel profileViewModel;
    private ProgressBar progressBar;
    private TextView textViewName, textViewEmail, textViewDateOfBirth, textViewAbsence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initUI();

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        observeViewModel();

        profileViewModel.fetchUserProfile();

        Button buttonLogOut = findViewById(R.id.btnLogOut);
        buttonLogOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        ImageView myButton = findViewById(R.id.myButton);
        myButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void initUI() {
        progressBar = findViewById(R.id.pgbLoading);
        textViewName = findViewById(R.id.lblNameLoading);
        textViewEmail = findViewById(R.id.lblEmailLoading);
        textViewDateOfBirth = findViewById(R.id.lblDateOfBirthLoading);
        textViewAbsence = findViewById(R.id.lblAbsenceLoading); // Campo de ausência
    }

    private void observeViewModel() {
        profileViewModel.getUserProfile().observe(this, userProfile -> {
            if (userProfile != null) {
                textViewName.setText(userProfile.getName());
                textViewEmail.setText(userProfile.getEmail());
                textViewDateOfBirth.setText(userProfile.getDateOfBirth());

                // Exibe o campo absence como int
                textViewAbsence.setText(String.valueOf(userProfile.getAbsence()));
            }
        });

        profileViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}