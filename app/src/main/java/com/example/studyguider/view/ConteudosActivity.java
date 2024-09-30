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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conteudos);

        primeiroBimestre = findViewById(R.id.primeiro_bimestre);
        btnAdd1 = findViewById(R.id.btn_add1);

        btnAdd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cria um novo EditText
                EditText editText = new EditText(ConteudosActivity.this);
                editText.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

                // Adiciona o EditText ao container
                primeiroBimestre.addView(editText);
            }
        });
    }
}
