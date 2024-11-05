package com.example.studyguider.view;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.studyguider.adapter.MateriasAdapter;
import com.example.studyguider.models.Materias;
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


public class MateriasActivity extends AppCompatActivity {


    private HeaderViewModel headerViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_materias);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        headerViewModel = new ViewModelProvider(this).get(HeaderViewModel.class);

        View materiaView = findViewById(R.id.materia_item);


        View headerView = findViewById(R.id.header);
        HeaderActivity headerActivity = new HeaderActivity(headerView, headerViewModel, this);


        FirebaseUser currentUser1 = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser1 != null) {
            headerViewModel.fetchUsername(currentUser1);
        }


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = findViewById(R.id.recycler);


        FloatingActionButton add = findViewById(R.id.addMateria);
        add.setOnClickListener(view -> startActivity(new Intent(MateriasActivity.this, MateriasAddActivity.class)));


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();


            // Modifique a consulta para buscar materias no documento correspondente ao userId
            db.collection("subjects").document(userId).collection("userSubjects")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Toast.makeText(MateriasActivity.this, "Falha ao carregar dados: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                return;
                            }


                            ArrayList<Materias> arrayList = new ArrayList<>();
                            assert value != null;
                            for (QueryDocumentSnapshot document : value) {
                                Materias subjects = document.toObject(Materias.class);
                                subjects.setId(document.getId());
                                arrayList.add(subjects);
                            }


                            MateriasAdapter adapter = new MateriasAdapter(MateriasActivity.this, arrayList);
                            recyclerView.setAdapter(adapter);


                            adapter.setOnClickListener(new MateriasAdapter.OnItemClickListener() {
                                @Override
                                public void onClick(Materias materias) {
                                    App.materia = materias;
                                    Intent intent = new Intent(MateriasActivity.this, ConteudosActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    });
        }
    }
}
