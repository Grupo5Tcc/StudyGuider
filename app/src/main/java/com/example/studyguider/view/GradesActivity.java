package com.example.studyguider.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studyguider.R;
import com.example.studyguider.adapter.GradesAdapter;
import com.example.studyguider.models.Grades;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class GradesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_controle_notas);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = findViewById(R.id.recycler);

        FloatingActionButton add = findViewById(R.id.addNota);
        add.setOnClickListener(view -> startActivity(new Intent(GradesActivity.this, GradesAddActivity.class)));

        db.collection("grades").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(GradesActivity.this, "Falha ao carregar dados: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                ArrayList<Grades> arrayList = new ArrayList<>();
                assert value != null;
                for (QueryDocumentSnapshot document : value) {
                    Grades grades = document.toObject(Grades.class);
                    grades.setId(document.getId());
                    arrayList.add(grades);
                }

                GradesAdapter adapter = new GradesAdapter(GradesActivity.this, arrayList);
                recyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener(new GradesAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Grades grades) {
                        App.grades = grades;
                        startActivity(new Intent(GradesActivity.this, GradesEditActivity.class));
                    }
                });
            }
        });
    }
}