package com.example.studyguider.view;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studyguider.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class ConteudosActivity extends AppCompatActivity {
    private ConstraintLayout primeiroBimestre;
    private TextView primBimestre;
    private ImageButton btnAdd1;
    private ConstraintLayout segundoBimestre;
    private ImageButton btnAdd2;
    private ConstraintLayout terceiroBimestre;
    private ImageButton btnAdd3;
    private ConstraintLayout quartoBimestre;
    private ImageButton btnAdd4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_conteudos);

        // Define orientação da tela para retrato
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        primeiroBimestre = findViewById(R.id.primeiro_bimestre);
        btnAdd1 = findViewById(R.id.btn_add1);
        primBimestre = findViewById(R.id.primBimestre);

        btnAdd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* cria um novo campo de texto
                EditText conteudos_1_bim = new EditText(ConteudosActivity.this);
                conteudos_1_bim.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

                primeiroBimestre.addView(conteudos_1_bim);
                */

                EditText conteudos_1_bim = new EditText(ConteudosActivity.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                params.setMargins(0, 0, 0, 0);

                conteudos_1_bim.setLayoutParams(params);

                // Adiciona o EditText ao container primeiroBimestre
                primeiroBimestre.addView(conteudos_1_bim);

                conteudos_1_bim.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        // Adiciona um OnClickListener no container "primeiroBimestre"
                        primeiroBimestre.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Pega o texto digitado no EditText
                                String textoDigitado = conteudos_1_bim.getText().toString();

                                // Desabilita o EditText para que não seja mais editável
                                conteudos_1_bim.setEnabled(false);

                                // Cria um TextView para exibir o texto digitado
                                TextView textoExibido = new TextView(ConteudosActivity.this);
                                textoExibido.setText(textoDigitado);
                                textoExibido.setLayoutParams(conteudos_1_bim.getLayoutParams()); // Aplica o mesmo layout do EditText ao TextView

                                // Substitui o EditText pelo TextView
                                primeiroBimestre.removeView(conteudos_1_bim); // Remove o EditText
                                primeiroBimestre.addView(textoExibido); // Adiciona o TextView com o texto
                            }
                        });
                        return false;
                    }
                });

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
