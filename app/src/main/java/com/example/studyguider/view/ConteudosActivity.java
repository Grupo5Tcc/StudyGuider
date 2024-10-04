package com.example.studyguider.view;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studyguider.R;

public class ConteudosActivity extends AppCompatActivity {
    private LinearLayout primeiroBimestre;
    private ImageButton btnAdd1;
    private LinearLayout segundoBimestre;
    private ImageButton btnAdd2;
    private LinearLayout terceiroBimestre;
    private ImageButton btnAdd3;
    private LinearLayout quartoBimestre;
    private ImageButton btnAdd4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conteudos);

        primeiroBimestre = findViewById(R.id.primeiro_bimestre);
        btnAdd1 = findViewById(R.id.btn_add1);

        btnAdd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cria um novo campo de texto
                EditText editText = new EditText(ConteudosActivity.this);
                editText.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

                // adiciona o campo dentro do container
                primeiroBimestre.addView(editText);
            }
        });

        segundoBimestre = findViewById(R.id.segundo_bimestre);
        btnAdd2 = findViewById(R.id.btn_add2);

        btnAdd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // cria um novo campo de texto
                EditText editText = new EditText(ConteudosActivity.this);
                editText.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

                // adiciona o campo dentro do container
                segundoBimestre.addView(editText);
            }
        });

        terceiroBimestre = findViewById(R.id.terceiro_bimestre);
        btnAdd3 = findViewById(R.id.btn_add3);

        btnAdd3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // cria um novo campo de texto
                EditText editText = new EditText(ConteudosActivity.this);
                editText.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

                // adiciona o campo dentro do container
                terceiroBimestre.addView(editText);
            }
        });

        quartoBimestre = findViewById(R.id.quarto_bimestre);
        btnAdd4 = findViewById(R.id.btn_add4);

        btnAdd4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // cria um novo campo de texto
                EditText editText = new EditText(ConteudosActivity.this);
                editText.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

                // adiciona o campo dentro do container
                quartoBimestre.addView(editText);
            }
        });
    }
}
