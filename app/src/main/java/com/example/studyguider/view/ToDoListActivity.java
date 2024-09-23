package com.example.studyguider.view;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.studyguider.R;
import com.example.studyguider.models.TaskItemToDoList;
import com.example.studyguider.viewmodels.HeaderViewModel;
import com.example.studyguider.viewmodels.ToDoListViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class ToDoListActivity extends AppCompatActivity {

    private HeaderViewModel headerViewModel;
    private ArrayList<TaskItemToDoList> items;
    private ItemAdapter itemsAdapter;
    private ListView lvItems;
    private EditText editText;
    private ToDoListViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        headerViewModel = new ViewModelProvider(this).get(HeaderViewModel.class);

        View headerView = findViewById(R.id.header);
        HeaderActivity headerActivity = new HeaderActivity(headerView, headerViewModel, this);

        FirebaseUser currentUser1 = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser1 != null) {
            headerViewModel.fetchUsername(currentUser1);
        }

        lvItems = findViewById(R.id.item_list);
        editText = findViewById(R.id.edit_text);
        items = new ArrayList<>();
        itemsAdapter = new ItemAdapter(items);
        lvItems.setAdapter(itemsAdapter);

        viewModel = new ViewModelProvider(this).get(ToDoListViewModel.class);
        viewModel.getTasks().observe(this, new Observer<List<TaskItemToDoList>>() {
            @Override
            public void onChanged(List<TaskItemToDoList> taskItems) {
                items.clear();
                items.addAll(taskItems);
                itemsAdapter.notifyDataSetChanged();
            }
        });

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

        int maxLength = 28;
        editText.setFilters(new InputFilter[]{noNewLinesFilter, new InputFilter.LengthFilter(maxLength)});

        Button btnAdd = findViewById(R.id.btn_add);
        Button btnSelectAll = findViewById(R.id.btn_select_all);
        Button btnDeleteSelected = findViewById(R.id.btn_delete_selected);

        btnAdd.setOnClickListener(v -> {
            String newItem = editText.getText().toString();
            if (!newItem.isEmpty()) {
                viewModel.addTask(newItem);
                editText.setText("");
            }
        });

        btnSelectAll.setOnClickListener(v -> {
            boolean allChecked = true;
            // Verifica todos os itens na lista de dados (items)
            for (TaskItemToDoList item : items) {
                if (!item.isCompleted()) {
                    allChecked = false;
                    break; // Saia do loop assim que encontrar um item não selecionado
                }
            }
            // Atualiza o estado de cada item na lista de dados
            for (TaskItemToDoList item : items) {
                boolean newCheckedState = !allChecked; // Alterna o estado
                item.setCompleted(newCheckedState);
                viewModel.updateTaskCompletion(item.getId(), newCheckedState);
            }
            // Notifica a adaptação da lista para atualizar a visualização
            itemsAdapter.notifyDataSetChanged();
        });

        btnDeleteSelected.setOnClickListener(v -> {
            ArrayList<String> itemsToRemoveIds = new ArrayList<>();
            for (TaskItemToDoList item : items) {
                if (item.isCompleted()) {
                    itemsToRemoveIds.add(item.getId());
                }
            }
            for (String itemId : itemsToRemoveIds) {
                viewModel.deleteTask(itemId);
            }
        });
    }

    private class ItemAdapter extends ArrayAdapter<TaskItemToDoList> {
        private ArrayList<TaskItemToDoList> items;

        public ItemAdapter(ArrayList<TaskItemToDoList> items) {
            super(ToDoListActivity.this, R.layout.dialog_to_do_list, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.dialog_to_do_list, parent, false);
            }

            TaskItemToDoList item = items.get(position);
            TextView itemText = convertView.findViewById(R.id.item_text);
            CheckBox checkBox = convertView.findViewById(R.id.checkBox);

            itemText.setText(item.getTask());

            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(item.isCompleted());
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                viewModel.updateTaskCompletion(item.getId(), isChecked);
            });

            convertView.setOnClickListener(v -> checkBox.toggle()); // Permite clicar em todo o item
            return convertView;
        }
    }
}
