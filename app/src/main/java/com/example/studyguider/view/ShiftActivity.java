package com.example.studyguider.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studyguider.R;
import com.example.studyguider.adapter.ShiftAdapter;
import com.example.studyguider.models.Shift;
import com.example.studyguider.viewmodels.HeaderViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class ShiftActivity extends AppCompatActivity {

    private HeaderViewModel headerViewModel;
    private RecyclerView recyclerView;
    private FloatingActionButton add, refresh;
    private ShiftAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shift);
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
        recyclerView = findViewById(R.id.recycler);

        add = findViewById(R.id.addShift);
        add.setOnClickListener(view -> startActivity(new Intent(ShiftActivity.this, ShiftAddActivity.class)));

        db.collection("shifts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Shift> arrayList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Shift shift = document.toObject(Shift.class);
                        shift.setId(document.getId());
                        arrayList.add(shift);
                    }
                    ShiftAdapter adapter = new ShiftAdapter(ShiftActivity.this, arrayList);
                    recyclerView.setAdapter(adapter);

                    adapter.setOnItemClickListener(new ShiftAdapter.OnItemClickListener() {
                        @Override
                        public void onClick(Shift shift) {
                            App.plantoes = shift;
                            startActivity(new Intent(ShiftActivity.this, ShiftEditActivity.class));
                        }
                    });
                } else {
                    Toast.makeText(ShiftActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("shifts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Shift> arrayList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Shift shift = document.toObject(Shift.class);
                                shift.setId(document.getId());
                                arrayList.add(shift);
                            }
                            ShiftAdapter adapter = new ShiftAdapter(ShiftActivity.this, arrayList);
                            recyclerView.setAdapter(adapter);

                            adapter.setOnItemClickListener(new ShiftAdapter.OnItemClickListener() {
                                @Override
                                public void onClick(Shift shift) {
                                    App.plantoes = shift;
                                    startActivity(new Intent(ShiftActivity.this, ShiftEditActivity.class));
                                }
                            });
                        } else {
                            Toast.makeText(ShiftActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

}