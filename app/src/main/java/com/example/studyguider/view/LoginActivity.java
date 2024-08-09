package com.example.studyguider.view;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.studyguider.R;
import com.example.studyguider.models.UserLogin;
import com.example.studyguider.viewmodels.LoginViewModel;


public class LoginActivity extends AppCompatActivity {


    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        TextView forgetPassword = findViewById(R.id.lbl_forget_password);
        EditText editTextEMail = findViewById(R.id.txt_email);
        EditText editTextPassword = findViewById(R.id.txt_password);
        Button buttonMenu = findViewById(R.id.btn_menu);
        Button buttonSingUp = findViewById(R.id.btn_sing_up);
        View progressBar = findViewById(R.id.pgb_loading);

        loginViewModel.visibilidadeProgressBar.observe(this, isVisible -> {
            if (isVisible != null) {
                progressBar.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
            }
        });

        loginViewModel.loginSucesso.observe(this, isSuccess -> {
            if (isSuccess != null && isSuccess) {
                Toast.makeText(LoginActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                finish();
            }
        });

        loginViewModel.loginFalhou.observe(this, isFailed -> {
            if (isFailed != null && isFailed) {
                Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
            }
        });

        buttonMenu.setOnClickListener(v -> {
            String textEMail = editTextEMail.getText().toString();
            String textPassword = editTextPassword.getText().toString();
            UserLogin user = new UserLogin(textEMail, textPassword);
            loginViewModel.loginUser(user);
        });

        buttonSingUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SingUpActivity.class);
            startActivity(intent);
        });

        forgetPassword.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot_password, null);
            EditText emailBox = dialogView.findViewById(R.id.emailBox);

            builder.setView(dialogView);
            AlertDialog dialog = builder.create();

            dialogView.findViewById(R.id.btnReset).setOnClickListener(view -> {
                String userEmail = emailBox.getText().toString();
                loginViewModel.resetPassword(userEmail);
                dialog.dismiss();
            });

            dialogView.findViewById(R.id.btnCancel).setOnClickListener(view -> dialog.dismiss());

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            dialog.show();
        });
    }
}
