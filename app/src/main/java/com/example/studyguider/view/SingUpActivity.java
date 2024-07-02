package com.example.studyguider.view;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.example.studyguider.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.Calendar;

import models.ReadWriteUserDetails;

public class SingUpActivity extends AppCompatActivity {


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

        EditText editTextUsername = findViewById(R.id.txt_name);
        EditText editTextEMail = findViewById(R.id.txt_email);
        EditText editTextPassword = findViewById(R.id.txt_password);
        EditText editTextDateOfBirth = findViewById(R.id.txt_date);

        /*Form Date*/
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
                        editTextDateOfBirth.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                },year,month,day);
                picker.show();
            }
        });


        /*Form */
        Button buttonMenu = findViewById(R.id.btn_menu);
        View progressBar = findViewById(R.id.pgb_loading);
        buttonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textUsername = editTextUsername.getText().toString();
                String textEMail = editTextEMail.getText().toString();
                String textDateOfBirth = editTextDateOfBirth.getText().toString();
                String textPassword = editTextPassword.getText().toString();

                if(TextUtils.isEmpty(textUsername)){
                    Toast.makeText(SingUpActivity.this,"Please enter your full username",Toast.LENGTH_LONG).show();
                    editTextUsername.setError("Full name is required");
                    editTextUsername.requestFocus();
                } else if(TextUtils.isEmpty(textEMail)){
                    Toast.makeText(SingUpActivity.this,"Please enter your email",Toast.LENGTH_LONG).show();
                    editTextEMail.setError("Email name is required");
                    editTextEMail.requestFocus();
                } else if(!Patterns.EMAIL_ADDRESS.matcher(textEMail).matches()){
                    Toast.makeText(SingUpActivity.this,"Please re-enter your email",Toast.LENGTH_LONG).show();
                    editTextEMail.setError("Valid email is required");
                    editTextEMail.requestFocus();
                } else if(textPassword.length()<8){
                    Toast.makeText(SingUpActivity.this,"Password should be at least 8 digits",Toast.LENGTH_LONG).show();
                    editTextPassword.setError("Password confirmation is required");
                    editTextPassword.requestFocus();
                } else if(TextUtils.isEmpty(textDateOfBirth)){
                    Toast.makeText(SingUpActivity.this,"Please enter your date of birth",Toast.LENGTH_LONG).show();
                    editTextDateOfBirth.setError("date of birth is required");
                    editTextDateOfBirth.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textUsername,textEMail,textPassword,textDateOfBirth);
                }
            }
        });
    }

    private void registerUser(String txtUsername, String txtEMail, String txtPassword, String txtDateOfBirth) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(txtEMail, txtPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {
                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(SingUpActivity.this, "Registered user. Check your email.", Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(SingUpActivity.this, "Failed to send verification email.", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }

                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(txtUsername,txtDateOfBirth);


                            user.sendEmailVerification();

                            Intent intent = new Intent(SingUpActivity.this, MenuActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(SingUpActivity.this, "Failed to register user: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}