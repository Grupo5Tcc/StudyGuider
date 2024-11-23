package com.example.studyguider.view;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
import androidx.lifecycle.ViewModelProvider;

import com.example.studyguider.R;
import com.example.studyguider.viewmodels.HeaderViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private HeaderViewModel headerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_conteudos);

        headerViewModel = new ViewModelProvider(this).get(HeaderViewModel.class);

        View headerView = findViewById(R.id.header);
        HeaderActivity headerActivity = new HeaderActivity(headerView, headerViewModel, this);

        FirebaseUser currentUser1 = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser1 != null) {
            headerViewModel.fetchUsername(currentUser1);
        }

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

        btnAdd1.setOnClickListener(v -> {
            EditText conteudos_1_bim = new EditText(ConteudosActivity.this);
            conteudos_1_bim.setId(View.generateViewId());
            conteudos_1_bim.setHint("Digite o conteúdo");
            conteudos_1_bim.setImeOptions(EditorInfo.IME_ACTION_DONE);
            conteudos_1_bim.setOnEditorActionListener((TextView v1, int actionId, KeyEvent event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    conteudos_1_bim.clearFocus();
                    return true;
                }
                return false;
            });

            ImageButton btnDelete = new ImageButton(ConteudosActivity.this);
            btnDelete.setImageResource(R.drawable.ic_delete);
            btnDelete.setBackground(null);
            btnDelete.setId(View.generateViewId());

            btnDelete.setOnClickListener(v12 -> {
                primeiroBimestre.removeView(conteudos_1_bim);
                primeiroBimestre.removeView(btnDelete);
            });

            primeiroBimestre.addView(conteudos_1_bim);
            primeiroBimestre.addView(btnDelete);

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
            );
            conteudos_1_bim.setLayoutParams(params);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(primeiroBimestre);

            if (primeiroBimestre.getChildCount() > 2) {
                View lastChild = primeiroBimestre.getChildAt(primeiroBimestre.getChildCount() - 3); // Último EditText
                constraintSet.connect(conteudos_1_bim.getId(), ConstraintSet.TOP, lastChild.getId(), ConstraintSet.BOTTOM, 10);
            } else {
                constraintSet.connect(conteudos_1_bim.getId(), ConstraintSet.TOP, primBimestre.getId(), ConstraintSet.BOTTOM, 10);
            }

            constraintSet.connect(conteudos_1_bim.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16);
            constraintSet.connect(conteudos_1_bim.getId(), ConstraintSet.END, btnDelete.getId(), ConstraintSet.START, 8);
            constraintSet.connect(btnDelete.getId(), ConstraintSet.TOP, conteudos_1_bim.getId(), ConstraintSet.TOP);
            constraintSet.connect(btnDelete.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16);

            constraintSet.applyTo(primeiroBimestre);
            conteudos_1_bim.requestFocus();

            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(conteudos_1_bim, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        segundoBimestre = findViewById(R.id.segundo_bimestre);
        btnAdd2 = findViewById(R.id.btn_add2);
        segBimestre = findViewById(R.id.segBimestre);

        btnAdd2.setOnClickListener(v -> {
            EditText conteudos_2_bim = new EditText(ConteudosActivity.this);
            conteudos_2_bim.setId(View.generateViewId());
            conteudos_2_bim.setHint("Digite o conteúdo");
            conteudos_2_bim.setImeOptions(EditorInfo.IME_ACTION_DONE);
            conteudos_2_bim.setOnEditorActionListener((TextView v1, int actionId, KeyEvent event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    conteudos_2_bim.clearFocus();
                    return true;
                }
                return false;
            });

            ImageButton btnDelete = new ImageButton(ConteudosActivity.this);
            btnDelete.setImageResource(R.drawable.ic_delete);
            btnDelete.setBackground(null);
            btnDelete.setId(View.generateViewId());

            btnDelete.setOnClickListener(v12 -> {
                segundoBimestre.removeView(conteudos_2_bim);
                segundoBimestre.removeView(btnDelete);
            });

            segundoBimestre.addView(conteudos_2_bim);
            segundoBimestre.addView(btnDelete);

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
            );
            conteudos_2_bim.setLayoutParams(params);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(segundoBimestre);

            if (segundoBimestre.getChildCount() > 2) {
                View lastChild = segundoBimestre.getChildAt(segundoBimestre.getChildCount() - 3); // Último EditText
                constraintSet.connect(conteudos_2_bim.getId(), ConstraintSet.TOP, lastChild.getId(), ConstraintSet.BOTTOM, 10);
            } else {
                constraintSet.connect(conteudos_2_bim.getId(), ConstraintSet.TOP, segBimestre.getId(), ConstraintSet.BOTTOM, 10);
            }

            constraintSet.connect(conteudos_2_bim.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16);
            constraintSet.connect(conteudos_2_bim.getId(), ConstraintSet.END, btnDelete.getId(), ConstraintSet.START, 8);
            constraintSet.connect(btnDelete.getId(), ConstraintSet.TOP, conteudos_2_bim.getId(), ConstraintSet.TOP);
            constraintSet.connect(btnDelete.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16);

            constraintSet.applyTo(segundoBimestre);
            conteudos_2_bim.requestFocus();

            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(conteudos_2_bim, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        terceiroBimestre = findViewById(R.id.terceiro_bimestre);
        btnAdd3 = findViewById(R.id.btn_add3);
        tercBimestre = findViewById(R.id.tercBimestre);

        btnAdd3.setOnClickListener(v -> {
            EditText conteudos_3_bim = new EditText(ConteudosActivity.this);
            conteudos_3_bim.setId(View.generateViewId());
            conteudos_3_bim.setHint("Digite o conteúdo");
            conteudos_3_bim.setImeOptions(EditorInfo.IME_ACTION_DONE);
            conteudos_3_bim.setOnEditorActionListener((TextView v1, int actionId, KeyEvent event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    conteudos_3_bim.clearFocus();
                    return true;
                }
                return false;
            });

            ImageButton btnDelete = new ImageButton(ConteudosActivity.this);
            btnDelete.setImageResource(R.drawable.ic_delete);
            btnDelete.setBackground(null);
            btnDelete.setId(View.generateViewId());

            btnDelete.setOnClickListener(v12 -> {
                terceiroBimestre.removeView(conteudos_3_bim);
                terceiroBimestre.removeView(btnDelete);
            });

            terceiroBimestre.addView(conteudos_3_bim);
            terceiroBimestre.addView(btnDelete);

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
            );
            conteudos_3_bim.setLayoutParams(params);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(terceiroBimestre);

            if (terceiroBimestre.getChildCount() > 2) {
                View lastChild = terceiroBimestre.getChildAt(terceiroBimestre.getChildCount() - 3); // Último EditText
                constraintSet.connect(conteudos_3_bim.getId(), ConstraintSet.TOP, lastChild.getId(), ConstraintSet.BOTTOM, 10);
            } else {
                constraintSet.connect(conteudos_3_bim.getId(), ConstraintSet.TOP, tercBimestre.getId(), ConstraintSet.BOTTOM, 10);
            }

            constraintSet.connect(conteudos_3_bim.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16);
            constraintSet.connect(conteudos_3_bim.getId(), ConstraintSet.END, btnDelete.getId(), ConstraintSet.START, 8);
            constraintSet.connect(btnDelete.getId(), ConstraintSet.TOP, conteudos_3_bim.getId(), ConstraintSet.TOP);
            constraintSet.connect(btnDelete.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16);

            constraintSet.applyTo(terceiroBimestre);
            conteudos_3_bim.requestFocus();

            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(conteudos_3_bim, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        quartoBimestre = findViewById(R.id.quarto_bimestre);
        btnAdd4 = findViewById(R.id.btn_add4);
        quarBimestre = findViewById(R.id.quarBimestre);

        btnAdd4.setOnClickListener(v -> {
            EditText conteudos_4_bim = new EditText(ConteudosActivity.this);
            conteudos_4_bim.setId(View.generateViewId());
            conteudos_4_bim.setHint("Digite o conteúdo");
            conteudos_4_bim.setImeOptions(EditorInfo.IME_ACTION_DONE);
            conteudos_4_bim.setOnEditorActionListener((TextView v1, int actionId, KeyEvent event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    conteudos_4_bim.clearFocus();
                    return true;
                }
                return false;
            });

            ImageButton btnDelete = new ImageButton(ConteudosActivity.this);
            btnDelete.setImageResource(R.drawable.ic_delete);
            btnDelete.setBackground(null);
            btnDelete.setId(View.generateViewId());

            btnDelete.setOnClickListener(v12 -> {
                quartoBimestre.removeView(conteudos_4_bim);
                quartoBimestre.removeView(btnDelete);
            });

            quartoBimestre.addView(conteudos_4_bim);
            quartoBimestre.addView(btnDelete);

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
            );
            conteudos_4_bim.setLayoutParams(params);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(quartoBimestre);

            if (quartoBimestre.getChildCount() > 2) {
                View lastChild = quartoBimestre.getChildAt(quartoBimestre.getChildCount() - 3); // Último EditText
                constraintSet.connect(conteudos_4_bim.getId(), ConstraintSet.TOP, lastChild.getId(), ConstraintSet.BOTTOM, 10);
            } else {
                constraintSet.connect(conteudos_4_bim.getId(), ConstraintSet.TOP, quarBimestre.getId(), ConstraintSet.BOTTOM, 10);
            }

            constraintSet.connect(conteudos_4_bim.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16);
            constraintSet.connect(conteudos_4_bim.getId(), ConstraintSet.END, btnDelete.getId(), ConstraintSet.START, 8);
            constraintSet.connect(btnDelete.getId(), ConstraintSet.TOP, conteudos_4_bim.getId(), ConstraintSet.TOP);
            constraintSet.connect(btnDelete.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16);

            constraintSet.applyTo(quartoBimestre);
            conteudos_4_bim.requestFocus();

            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(conteudos_4_bim, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }
}
