package com.example.studyguider.view;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.example.studyguider.models.Login;
import com.example.studyguider.viewmodels.LoginViewModel;


public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Define orientação da tela para retrato
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Ajusta o padding para evitar sobreposição com as barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        TextView txtforgetPassword = findViewById(R.id.lblForgetPassword);
        EditText etEMail = findViewById(R.id.txtEmail);
        EditText etPassword = findViewById(R.id.txtPassword);
        Button btnMenu = findViewById(R.id.btnMenu);
        Button btnSingUp = findViewById(R.id.btnSingUp);
        View progressBar = findViewById(R.id.pgbLoading);
        CheckBox ckb_mostrarSenhaLogin  = findViewById(R.id.ckb_mostrarSenhaLogin);

        loginViewModel.visibilidadeProgressBar.observe(this, isVisible -> {
            if (isVisible != null) {
                progressBar.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
            }
        });

        // Observa o resultado do login e direciona para HomeActivity se for bem-sucedido
        loginViewModel.loginSucesso.observe(this, isSuccess -> {
            if (isSuccess != null && isSuccess) {
                Toast.makeText(LoginActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
            }
        });

        // Observa falhas de login e exibe uma mensagem de erro
        loginViewModel.loginFalhou.observe(this, isFailed -> {
            if (isFailed != null && isFailed) {
                Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
            }
        });

        // Configura o botão de login, pegando os dados do usuário e chamando o login no ViewModel
        btnMenu.setOnClickListener(v -> {
            String textEMail = etEMail.getText().toString();
            String textPassword = etPassword.getText().toString();
            Login user = new Login(textEMail, textPassword);
            loginViewModel.loginUser(user);
        });

        // Configura o botão de cadastro para redirecionar para a tela de CadastroActivity
        btnSingUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
            startActivity(intent);
        });

        // Configura a recuperação de senha ao clicar em "Esqueci minha senha"
        txtforgetPassword.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot_password, null);
            EditText emailBox = dialogView.findViewById(R.id.emailBox);

            builder.setView(dialogView);
            AlertDialog dialog = builder.create();

            // Configura o botão de resetar senha e envia o email ao ViewModel
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

        // Configura a opção para mostrar ou esconder a senha
        ckb_mostrarSenhaLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //se estiver marcado, chama o método e tira a máscara da "Senha"
                if (isChecked) {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

}