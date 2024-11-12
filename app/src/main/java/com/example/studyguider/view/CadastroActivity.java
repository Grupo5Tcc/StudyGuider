package com.example.studyguider.view;

import android.app.DatePickerDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.studyguider.R;
import com.example.studyguider.models.Usuario;
import com.example.studyguider.viewmodels.RegisterViewModel;


import java.util.Calendar;


public class CadastroActivity extends AppCompatActivity {

    private RegisterViewModel registerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro);

        // Define orientação da tela para retrato
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Define padding automático para evitar sobreposição com barras de sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializa ViewModel para gerenciar lógica de registro
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        EditText etUsername = findViewById(R.id.txtName);
        EditText etEmail = findViewById(R.id.txtEmail);
        EditText etPassword = findViewById(R.id.txtPassword);
        EditText etDateOfBirth = findViewById(R.id.txtDate);
        CheckBox ckb_mostrarSenhaCadastro  = findViewById(R.id.ckb_mostrarSenhaCadastro);

        // Configura o campo de data de nascimento com um DatePickerDialog
        etDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog picker;
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                picker = new DatePickerDialog(CadastroActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        etDateOfBirth.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        // Botão de registro: valida campos e exibe barra de progresso
        Button buttonMenu = findViewById(R.id.btnMenu);
        View progressBar = findViewById(R.id.pgbLoading);
        buttonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtUsername = etUsername.getText().toString();
                String txtEmail = etEmail.getText().toString();
                String txtDateOfBirth = etDateOfBirth.getText().toString();
                String txtPassword = etPassword.getText().toString();

                if (validateInput(txtUsername, txtEmail, txtPassword, txtDateOfBirth)) {
                    progressBar.setVisibility(View.VISIBLE);
                    Usuario user = new Usuario(txtUsername, txtEmail, txtDateOfBirth);
                    registerViewModel.registerUser(user, txtPassword, CadastroActivity.this);
                }
            }
        });

        // Checkbox para exibir/ocultar senha
        ckb_mostrarSenhaCadastro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
    // Valida os campos obrigatórios e formatações de entrada
    private boolean validateInput(String username, String email, String password, String dateOfBirth) {
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(CadastroActivity.this, "Please enter your full username", Toast.LENGTH_LONG).show();
            return false;
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(CadastroActivity.this, "Please enter your email", Toast.LENGTH_LONG).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(CadastroActivity.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
            return false;
        } else if (password.length() < 8) {
            Toast.makeText(CadastroActivity.this, "Password should be at least 8 digits", Toast.LENGTH_LONG).show();
            return false;
        } else if (TextUtils.isEmpty(dateOfBirth)) {
            Toast.makeText(CadastroActivity.this, "Please enter your date of birth", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}