package com.example.studyguider.view;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studyguider.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ToDoListActivity extends AppCompatActivity {

    private ArrayList<String> items;
    private ItemAdapter itemsAdapter;
    private ListView lvItems;
    private EditText editText;
    private FirebaseFirestore db;
    private boolean selectAllMode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_to_do_list);

        lvItems = findViewById(R.id.item_list);
        editText = findViewById(R.id.edit_text);
        items = new ArrayList<>();
        itemsAdapter = new ItemAdapter(items);
        lvItems.setAdapter(itemsAdapter);

        db = FirebaseFirestore.getInstance();

        loadTasksFromFirebase();

        InputFilter noNewLinesFilter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                StringBuilder filteredStringBuilder = new StringBuilder();
                for (int i = start; i < end; i++) {
                    char currentChar = source.charAt(i);
                    if (currentChar != '\n' && currentChar != '\r') {
                        filteredStringBuilder.append(currentChar);
                    }
                }
                return filteredStringBuilder.toString();
            }
        };

        int maxLength = 29;
        editText.setFilters(new InputFilter[]{noNewLinesFilter,new InputFilter.LengthFilter(maxLength)});

        Button btnAdd = findViewById(R.id.btn_add);
        Button btnSelectAll = findViewById(R.id.btn_select_all);
        Button btnDeleteSelected = findViewById(R.id.btn_delete_selected);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newItem = editText.getText().toString();
                if (!newItem.isEmpty()) {
                    // Save new item to Firestore
                    Map<String, Object> item = new HashMap<>();
                    item.put("task", newItem);
                    item.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());

                    db.collection("task_to_do_list").add(item)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    items.add(newItem);
                                    itemsAdapter.notifyDataSetChanged();
                                    editText.setText("");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("ToDoListActivity", "Error adding document", e);
                                }
                            });
                }
            }
        });

        btnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean allChecked = true;
                for (int i = 0; i < lvItems.getChildCount(); i++) {
                    View item = lvItems.getChildAt(i);
                    CheckBox checkBox = item.findViewById(R.id.checkBox);
                    if (!checkBox.isChecked()) {
                        allChecked = false;
                        checkBox.setChecked(true);
                    }
                }
                selectAllMode = !allChecked;
            }
        });

        btnDeleteSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> itemsToRemove = new ArrayList<>();
                for (int i = 0; i < lvItems.getChildCount(); i++) {
                    View item = lvItems.getChildAt(i);
                    CheckBox checkBox = item.findViewById(R.id.checkBox);
                    if (checkBox.isChecked()) {
                        TextView itemText = item.findViewById(R.id.item_text);
                        itemsToRemove.add(itemText.getText().toString());
                    }
                }
                for (String itemName : itemsToRemove) {
                    db.collection("task_to_do_list")
                            .whereEqualTo("task", itemName)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            db.collection("task_to_do_list").document(document.getId()).delete();
                                        }
                                        items.removeAll(itemsToRemove);
                                        itemsAdapter.notifyDataSetChanged();
                                    } else {
                                        Log.w("ToDoListActivity", "Error getting documents.", task.getException());
                                    }
                                }
                            });
                }
            }
        });

    }

    private class ItemAdapter extends ArrayAdapter<String> {
        private ArrayList<String> items;

        public ItemAdapter(ArrayList<String> items) {
            super(ToDoListActivity.this, R.layout.dialog_to_do_list, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.dialog_to_do_list, parent, false);
            }

            String item = items.get(position);

            TextView itemText = convertView.findViewById(R.id.item_text);
            CheckBox checkBox = convertView.findViewById(R.id.checkBox);

            itemText.setText(item);
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(false);

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    itemText.setPaintFlags(itemText.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    itemText.setPaintFlags(itemText.getPaintFlags() & (~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG));
                }
            });

            return convertView;
        }
    }

    private void loadTasksFromFirebase() {
        db.collection("task_to_do_list")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String itemName = document.getString("task");
                                items.add(itemName);
                            }
                            itemsAdapter.notifyDataSetChanged();
                        } else {
                            Log.w("ToDoListActivity", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}