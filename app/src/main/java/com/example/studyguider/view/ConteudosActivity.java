package com.example.studyguider.view;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studyguider.R;
import com.google.firebase.firestore.FirebaseFirestore;

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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_conteudos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        primeiroBimestre = findViewById(R.id.primeiro_bimestre);
        btnAdd1 = findViewById(R.id.btn_add1);

        btnAdd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // cria um novo campo de texto
                EditText conteudos_1_bim = new EditText(ConteudosActivity.this);
                conteudos_1_bim.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

                // adiciona o campo dentro do container
                primeiroBimestre.addView(conteudos_1_bim);
            }
        });

        segundoBimestre = findViewById(R.id.segundo_bimestre);
        btnAdd2 = findViewById(R.id.btn_add2);

        btnAdd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // cria um novo campo de texto
                EditText conteudos_2_bim = new EditText(ConteudosActivity.this);
                conteudos_2_bim.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

                // adiciona o campo dentro do container
                segundoBimestre.addView(conteudos_2_bim);
            }
        });

        terceiroBimestre = findViewById(R.id.terceiro_bimestre);
        btnAdd3 = findViewById(R.id.btn_add3);

        btnAdd3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // cria um novo campo de texto
                EditText conteudos_3_bim = new EditText(ConteudosActivity.this);
                conteudos_3_bim.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

                // adiciona o campo dentro do container
                terceiroBimestre.addView(conteudos_3_bim);
            }
        });

        quartoBimestre = findViewById(R.id.quarto_bimestre);
        btnAdd4 = findViewById(R.id.btn_add4);

        btnAdd4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // cria um novo campo de texto
                EditText conteudos_4_bim = new EditText(ConteudosActivity.this);
                conteudos_4_bim.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

                // adiciona o campo dentro do container
                quartoBimestre.addView(conteudos_4_bim);
            }
        });
    }
}
