package com.example.studyguider.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studyguider.R;
import com.example.studyguider.adapter.SubjectsAdapter;
import com.example.studyguider.models.Subjects;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SubjectsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subjects);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = findViewById(R.id.recycler);

        FloatingActionButton add = findViewById(R.id.addMateria);
        add.setOnClickListener(view -> startActivity(new Intent(SubjectsActivity.this, SubjectsAddActivity.class)));

        // Escutar alterações em tempo real com addSnapshotListener
        db.collection("subjects").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(SubjectsActivity.this, "Falha ao carregar dados: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                ArrayList<Subjects> arrayList = new ArrayList<>();
                assert value != null;
                for (QueryDocumentSnapshot document : value) {
                    Subjects materia = document.toObject(Subjects.class);
                    materia.setId(document.getId());
                    arrayList.add(materia);
                }

                SubjectsAdapter adapter = new SubjectsAdapter(SubjectsActivity.this, arrayList);
                recyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener(new SubjectsAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Subjects materia) {
                        App.materia = materia;
                        startActivity(new Intent(SubjectsActivity.this, ConteudosActivity.class));
                    }
                });
            }
        });
    }
}