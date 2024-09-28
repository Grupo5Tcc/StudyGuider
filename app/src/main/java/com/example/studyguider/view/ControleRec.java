package com.example.studyguider.view;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.studyguider.R;
import com.example.studyguider.models.DadosRec;
import com.example.studyguider.viewmodels.HeaderViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

public class ControleRec extends AppCompatActivity {

    private HeaderViewModel headerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_controle_rec);

        headerViewModel = new ViewModelProvider(this).get(HeaderViewModel.class);

        View headerView = findViewById(R.id.header);
        HeaderActivity headerActivity = new HeaderActivity(headerView, headerViewModel, this);

        FirebaseUser currentUser1 = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser1 != null) {
            headerViewModel.fetchUsername(currentUser1);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            EditText editText = findViewById(R.id.conteuos_txt);

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    editText.post(() -> {

                        editText.scrollTo(0, editText.getLayout().getLineTop(editText.getLineCount()) - editText.getHeight());


                        if (s.length() > 0 && s.charAt(s.length() - 1) == '.') {
                            EditText editText1 = editText;
                            editText1.clearFocus();
                            hideKeyboard(editText);
                        }
                    });
                }
            });

            return insets;
        });

        // Adicione os containers da página add_rec ao linearLayout
        LinearLayout linearLayout = findViewById(R.id.linerec);
        for (int i = 0; i < 3; i++) {
            View container = getLayoutInflater().inflate(R.layout.activity_add_rec, null);
            linearLayout.addView(container);

            // Encontre o botão dentro do container e adicione um listener a ele
            Button button = container.findViewById(R.id.salvar);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    salvarDados(container);
                }
            });
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void salvarDados(View container) {
        // Encontre os elementos dentro do container
        CheckBox c1 = container.findViewById(R.id.checkSujes1);
        CheckBox c2 = container.findViewById(R.id.checkSujes2);
        CheckBox c3 = container.findViewById(R.id.checkSujes3);
        CheckBox c4 = container.findViewById(R.id.checkSujes4);
        EditText conteudos = container.findViewById(R.id.conteuos_txt);

        // Crie um ID único para cada container
        String containerId = UUID.randomUUID().toString();

        // Salve os dados no banco de dados Firestore
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("rec").document(containerId).set(new DadosRec(
                containerId,
                c1.isChecked(),
                c2.isChecked(),
                c3.isChecked(),
                c4.isChecked(),
                conteudos.getText().toString()
        ));

        Toast.makeText(this, "Dados salvos com sucesso!", Toast.LENGTH_SHORT).show();
    }
}