package com.example.studyguider.view;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.studyguider.R;
import com.example.studyguider.models.User;
import com.example.studyguider.viewmodels.RegisterViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SingUpActivity extends AppCompatActivity {

    private RegisterViewModel registerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sing_up);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        EditText editTextUsername = findViewById(R.id.txt_name);
        EditText editTextEMail = findViewById(R.id.txt_email);
        EditText editTextPassword = findViewById(R.id.txt_password);
        EditText editTextDateOfBirth = findViewById(R.id.txt_date);

        editTextDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog picker;
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                picker = new DatePickerDialog(SingUpActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextDateOfBirth.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        Button buttonMenu = findViewById(R.id.btn_menu);
        View progressBar = findViewById(R.id.pgb_loading);
        buttonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textUsername = editTextUsername.getText().toString();
                String textEMail = editTextEMail.getText().toString();
                String textDateOfBirth = editTextDateOfBirth.getText().toString();
                String textPassword = editTextPassword.getText().toString();

                if (validateInput(textUsername, textEMail, textPassword, textDateOfBirth)) {
                    progressBar.setVisibility(View.VISIBLE);
                    User user = new User(textUsername, textEMail, textDateOfBirth);
                    registerViewModel.registerUser(user, textPassword, SingUpActivity.this);
                }
            }
        });
    }

    private boolean validateInput(String username, String email, String password, String dateOfBirth) {
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(SingUpActivity.this, "Please enter your full username", Toast.LENGTH_LONG).show();
            return false;
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(SingUpActivity.this, "Please enter your email", Toast.LENGTH_LONG).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(SingUpActivity.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
            return false;
        } else if (password.length() < 8) {
            Toast.makeText(SingUpActivity.this, "Password should be at least 8 digits", Toast.LENGTH_LONG).show();
            return false;
        } else if (TextUtils.isEmpty(dateOfBirth)) {
            Toast.makeText(SingUpActivity.this, "Please enter your date of birth", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}