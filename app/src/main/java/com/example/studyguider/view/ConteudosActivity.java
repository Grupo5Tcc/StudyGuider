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
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studyguider.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class ConteudosActivity extends AppCompatActivity {
    private String materia;
    private TextView nomeMateria;
    private ConstraintLayout primeiroBimestre;
    private TextView primBimestre;
    private ImageButton btnAdd1;
    private ConstraintLayout segundoBimestre;
    private TextView segBimestre;
    private ImageButton btnAdd2;
    private ConstraintLayout terceiroBimestre;
    private TextView tercBimestre;
    private ImageButton btnAdd3;
    private ConstraintLayout quartoBimestre;
    private TextView quarBimestre;
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

        materia = getIntent().getStringExtra("materia");
        nomeMateria = findViewById(R.id.nome_materia);

        nomeMateria.setText(null);
        nomeMateria.setText(materia);


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        primeiroBimestre = findViewById(R.id.primeiro_bimestre);
        btnAdd1 = findViewById(R.id.btn_add1);
        primBimestre = findViewById(R.id.primBimestre);

        btnAdd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText conteudos_1_bim = new EditText(ConteudosActivity.this);
                conteudos_1_bim.setId(View.generateViewId()); // Gere um ID único
                conteudos_1_bim.setHint("Digite o conteúdo");

                // Configura a ação do botão Enter
                conteudos_1_bim.setImeOptions(EditorInfo.IME_ACTION_DONE);
                conteudos_1_bim.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE ||
                                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                            conteudos_1_bim.clearFocus(); // Remove o foco do EditText
                            return true; // Indica que o evento foi tratado
                        }
                        return false;
                    }
                });

                // Adiciona o EditText ao ConstraintLayout
                primeiroBimestre.addView(conteudos_1_bim);

                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                );
                conteudos_1_bim.setLayoutParams(params);

                // Define as restrições do ConstraintLayout
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(primeiroBimestre);

                // Conecta o EditText ao TextView primBimestre ou ao último filho
                if (primeiroBimestre.getChildCount() > 1) {
                    View lastChild = primeiroBimestre.getChildAt(primeiroBimestre.getChildCount() - 2);
                    constraintSet.connect(conteudos_1_bim.getId(), ConstraintSet.TOP, lastChild.getId(), ConstraintSet.BOTTOM, 10);
                } else {
                    // Conecta ao título do bimestre, caso seja o primeiro EditText
                    constraintSet.connect(conteudos_1_bim.getId(), ConstraintSet.TOP, primBimestre.getId(), ConstraintSet.BOTTOM, 10);
                }

                constraintSet.connect(conteudos_1_bim.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16);
                constraintSet.connect(conteudos_1_bim.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16);

                constraintSet.applyTo(primeiroBimestre);
            }

        });

        segundoBimestre = findViewById(R.id.segundo_bimestre);
        btnAdd2 = findViewById(R.id.btn_add2);
        segBimestre = findViewById(R.id.segBimestre);

        btnAdd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText conteudos_2_bim = new EditText(ConteudosActivity.this);
                conteudos_2_bim.setId(View.generateViewId()); // Gere um ID único
                conteudos_2_bim.setHint("Digite o conteúdo");

                // Configura a ação do botão Enter
                conteudos_2_bim.setImeOptions(EditorInfo.IME_ACTION_DONE);
                conteudos_2_bim.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE ||
                                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                            conteudos_2_bim.clearFocus(); // Remove o foco do EditText
                            return true; // Indica que o evento foi tratado
                        }
                        return false;
                    }
                });

                // Adiciona o EditText ao ConstraintLayout
                segundoBimestre.addView(conteudos_2_bim);

                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                );
                conteudos_2_bim.setLayoutParams(params);

                // Define as restrições do ConstraintLayout
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(segundoBimestre);

                if (primeiroBimestre.getChildCount() > 1) {
                    View lastChild = segundoBimestre.getChildAt(segundoBimestre.getChildCount() - 2);
                    constraintSet.connect(conteudos_2_bim.getId(), ConstraintSet.TOP, lastChild.getId(), ConstraintSet.BOTTOM, 10);
                } else {
                    // Conecta ao título do bimestre, caso seja o primeiro EditText
                    constraintSet.connect(conteudos_2_bim.getId(), ConstraintSet.TOP, segBimestre.getId(), ConstraintSet.BOTTOM, 10);
                }

                constraintSet.connect(conteudos_2_bim.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16);
                constraintSet.connect(conteudos_2_bim.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16);

                constraintSet.applyTo(segundoBimestre);
            }
        });

        terceiroBimestre = findViewById(R.id.terceiro_bimestre);
        btnAdd3 = findViewById(R.id.btn_add3);
        tercBimestre = findViewById(R.id.tercBimestre);

        btnAdd3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText conteudos_3_bim = new EditText(ConteudosActivity.this);
                conteudos_3_bim.setId(View.generateViewId()); // Gere um ID único
                conteudos_3_bim.setHint("Digite o conteúdo");

                // Configura a ação do botão Enter
                conteudos_3_bim.setImeOptions(EditorInfo.IME_ACTION_DONE);
                conteudos_3_bim.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE ||
                                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                            conteudos_3_bim.clearFocus(); // Remove o foco do EditText
                            return true; // Indica que o evento foi tratado
                        }
                        return false;
                    }
                });

                // Adiciona o EditText ao ConstraintLayout
                terceiroBimestre.addView(conteudos_3_bim);

                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                );
                conteudos_3_bim.setLayoutParams(params);

                // Define as restrições do ConstraintLayout
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(terceiroBimestre);

                // Conecta o EditText ao TextView primBimestre ou ao último filho
                if (terceiroBimestre.getChildCount() > 1) {
                    View lastChild = terceiroBimestre.getChildAt(terceiroBimestre.getChildCount() - 2);
                    constraintSet.connect(conteudos_3_bim.getId(), ConstraintSet.TOP, lastChild.getId(), ConstraintSet.BOTTOM, 10);
                } else {
                    // Conecta ao título do bimestre, caso seja o primeiro EditText
                    constraintSet.connect(conteudos_3_bim.getId(), ConstraintSet.TOP, tercBimestre.getId(), ConstraintSet.BOTTOM, 10);
                }

                constraintSet.connect(conteudos_3_bim.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16);
                constraintSet.connect(conteudos_3_bim.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16);

                constraintSet.applyTo(terceiroBimestre);
            }
        });

        quartoBimestre = findViewById(R.id.quarto_bimestre);
        btnAdd4 = findViewById(R.id.btn_add4);
        quarBimestre = findViewById(R.id.quarBimestre);

        btnAdd4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText conteudos_4_bim = new EditText(ConteudosActivity.this);
                conteudos_4_bim.setId(View.generateViewId()); // Gere um ID único
                conteudos_4_bim.setHint("Digite o conteúdo");

                // Configura a ação do botão Enter
                conteudos_4_bim.setImeOptions(EditorInfo.IME_ACTION_DONE);
                conteudos_4_bim.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE ||
                                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                            conteudos_4_bim.clearFocus(); // Remove o foco do EditText
                            return true; // Indica que o evento foi tratado
                        }
                        return false;
                    }
                });

                // Adiciona o EditText ao ConstraintLayout
                primeiroBimestre.addView(conteudos_4_bim);

                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                );
                conteudos_4_bim.setLayoutParams(params);

                // Define as restrições do ConstraintLayout
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(quartoBimestre);

                // Conecta o EditText ao TextView primBimestre ou ao último filho
                if (quartoBimestre.getChildCount() > 1) {
                    View lastChild = quartoBimestre.getChildAt(quartoBimestre.getChildCount() - 2);
                    constraintSet.connect(conteudos_4_bim.getId(), ConstraintSet.TOP, lastChild.getId(), ConstraintSet.BOTTOM, 10);
                } else {
                    // Conecta ao título do bimestre, caso seja o primeiro EditText
                    constraintSet.connect(conteudos_4_bim.getId(), ConstraintSet.TOP, quarBimestre.getId(), ConstraintSet.BOTTOM, 10);
                }

                constraintSet.connect(conteudos_4_bim.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16);
                constraintSet.connect(conteudos_4_bim.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16);

                constraintSet.applyTo(quartoBimestre);
            }
        });
    }
}
