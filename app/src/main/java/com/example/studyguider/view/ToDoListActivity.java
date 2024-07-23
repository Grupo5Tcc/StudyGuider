package com.example.studyguider.view;

import android.os.Bundle;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studyguider.R;

import java.util.ArrayList;

public class ToDoListActivity extends AppCompatActivity {

    private ArrayList<String> items;
    private ItemAdapter itemsAdapter;
    private ListView lvItems;
    private EditText editText;
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
                    items.add(newItem);
                    itemsAdapter.notifyDataSetChanged();
                    editText.setText("");
                }
            }
        });

        btnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAllMode = true;
                for (int i = 0; i < lvItems.getChildCount(); i++) {
                    View item = lvItems.getChildAt(i);
                    CheckBox checkBox = item.findViewById(R.id.checkBox);
                    checkBox.setChecked(true);
                }
            }
        });

        btnDeleteSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectAllMode) {
                    items.clear();
                    selectAllMode = false;
                } else {
                    ArrayList<String> itemsToRemove = new ArrayList<>();
                    for (int i = 0; i < lvItems.getChildCount(); i++) {
                        View item = lvItems.getChildAt(i);
                        CheckBox checkBox = item.findViewById(R.id.checkBox);
                        if (checkBox.isChecked()) {
                            TextView itemText = item.findViewById(R.id.item_text);
                            itemsToRemove.add(itemText.getText().toString());
                        }
                    }
                    items.removeAll(itemsToRemove);
                }
                itemsAdapter.notifyDataSetChanged();
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
}