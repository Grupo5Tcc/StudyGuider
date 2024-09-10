package com.example.studyguider.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton; // Certifique-se de importar ImageButton
import android.widget.ListView;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
    private boolean selectAllMode = false;

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

        ImageButton backButton = findViewById(R.id.myButton); //
        backButton.setOnClickListener(v -> {
            // Ao clicar, vai para a página de menu
            Intent intent = new Intent(ToDoListActivity.this, MenuActivity.class);
            startActivity(intent);
            finish();  // Fecha a ToDoListActivity se não quiser que o usuário volte a ela
        });

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
                    viewModel.addTask(newItem);
                    editText.setText("");
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
                ArrayList<String> itemsToRemoveIds = new ArrayList<>();
                for (int i = 0; i < lvItems.getChildCount(); i++) {
                    View item = lvItems.getChildAt(i);
                    CheckBox checkBox = item.findViewById(R.id.checkBox);
                    if (checkBox.isChecked()) {
                        TaskItemToDoList taskItem = items.get(i);
                        itemsToRemoveIds.add(taskItem.getId());
                    }
                }
                for (String itemId : itemsToRemoveIds) {
                    viewModel.deleteTask(itemId);
                }
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

            // Configure o texto do item
            itemText.setText(item.getTask());

            // Desativa o listener antes de alterar o estado do checkbox para evitar loops infinitos
            checkBox.setOnCheckedChangeListener(null);

            // Define o estado do checkbox baseado no estado do item
            checkBox.setChecked(item.isCompleted());

            // Reativa o listener após o estado do checkbox ser configurado
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Atualiza o estado de conclusão da tarefa
                viewModel.updateTaskCompletion(item.getId(), isChecked);
            });

            return convertView;
        }
    }
}