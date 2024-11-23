package com.example.studyguider.view;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;


import com.example.studyguider.R;
import com.example.studyguider.adapter.SubjectsAdapter;
import com.example.studyguider.models.Subjects;
import com.example.studyguider.viewmodels.HeaderViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;


public class SubjectsActivity extends AppCompatActivity {

    private HeaderViewModel headerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_materias);

        // Define orientação da tela para retrato
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        headerViewModel = new ViewModelProvider(this).get(HeaderViewModel.class);

        View headerView = findViewById(R.id.header);
        HeaderActivity headerActivity = new HeaderActivity(headerView, headerViewModel, this);


        FirebaseUser currentUser1 = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser1 != null) {
            headerViewModel.fetchUsername(currentUser1);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = findViewById(R.id.recycler);

        // Configura o botão para adicionar uma nova matéria, redirecionando para a tela de adição
        FloatingActionButton add = findViewById(R.id.addMateria);
        add.setOnClickListener(view -> startActivity(new Intent(SubjectsActivity.this, SubjectsAddActivity.class)));

        // Verifica o usuário autenticado para carregar seus dados específicos
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Modifique a consulta para buscar materias no documento correspondente ao userId
            db.collection("subjects").document(userId).collection("userSubjects")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        // Tratamento de erro ao buscar dados
                        if (error != null) {
                            Toast.makeText(SubjectsActivity.this, "Falha ao carregar dados: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ArrayList<Subjects> arrayList = new ArrayList<>();
                        assert value != null;
                        for (QueryDocumentSnapshot document : value) {
                            Subjects subjects = document.toObject(Subjects.class);
                            subjects.setId(document.getId());
                            arrayList.add(subjects);
                        }

                        SubjectsAdapter adapter = new SubjectsAdapter(SubjectsActivity.this, arrayList);
                        recyclerView.setAdapter(adapter);

                    }
                });
        }
    }
}
