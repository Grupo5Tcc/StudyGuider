package com.example.studyguider.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studyguider.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class ProfileActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        View progressBar = findViewById(R.id.pgb_loading);


        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if(firebaseUser == null){
            Toast.makeText(ProfileActivity.this,"Something went wrong! User's details are not available at the moment",Toast.LENGTH_LONG).show();
        }else{
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser,progressBar);
        }

    }

    private void showUserProfile(FirebaseUser firebaseUser,View pgb) {

        TextView textViewName = findViewById(R.id.lbl_name_loading);
        TextView textViewEmail = findViewById(R.id.lbl_email_loading);
        TextView textViewDateOfBirth = findViewById(R.id.lbl_date_of_birth_loading);

        String userID = firebaseUser.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("user").document(userID);
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String email = documentSnapshot.getString("e_mail");
                            String dob = documentSnapshot.getString("date_of_birth");

                            textViewName.setText(name);
                            textViewEmail.setText(email);
                            textViewDateOfBirth.setText(dob);
                        } else {
                            Toast.makeText(ProfileActivity.this, "Documento não encontrado", Toast.LENGTH_LONG).show();
                        }
                        pgb.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "Erro ao carregar dados", Toast.LENGTH_LONG).show();
                        Log.d("db_error", "Erro ao carregar dados: " + e.toString());
                        pgb.setVisibility(View.GONE);
                    }
                });
    }
}
