package com.example.studyguider.view;

import android.content.pm.ActivityInfo;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.studyguider.R;
import com.example.studyguider.models.ItemTaskList;
import com.example.studyguider.viewmodels.HeaderViewModel;
import com.example.studyguider.viewmodels.TasksViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class TasksActivity extends AppCompatActivity {

    private HeaderViewModel headerViewModel;
    private ArrayList<ItemTaskList> items;
    private ItemAdapter itemsAdapter;
    private ListView lvItems;
    private EditText editText;
    private TasksViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        // Define orientação da tela para retrato
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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

        viewModel = new ViewModelProvider(this).get(TasksViewModel.class);
        viewModel.getTasks().observe(this, new Observer<List<ItemTaskList>>() {
            @Override
            public void onChanged(List<ItemTaskList> taskItems) {
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

        // Listener para salvar o texto ao pressionar "Enter" no teclado
        editText.setOnEditorActionListener((v, actionId, event) -> {
            String newItem = editText.getText().toString().trim(); // Remove espaços em branco
            if (!newItem.isEmpty()) {
                viewModel.addTask(newItem);
                editText.setText(""); // Limpa o campo após adicionar
                return true; // Indica que a ação foi tratada
            }
            return false; // Indica que a ação não foi tratada
        });

        // Listener do botão para adicionar um novo item
        Button btnAdd = findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(v -> {
            String newItem = editText.getText().toString().trim(); // Remove espaços em branco
            if (!newItem.isEmpty()) {
                viewModel.addTask(newItem);
                editText.setText(""); // Limpa o campo após adicionar
            } else {
                Toast.makeText(this, "Por favor, preencha o campo antes de adicionar.", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnSelectAll = findViewById(R.id.btn_select_all);
        Button btnDeleteSelected = findViewById(R.id.btn_delete_selected);

        btnSelectAll.setOnClickListener(v -> {
            if (items.isEmpty()) {
                Toast.makeText(this, "Não há itens na lista para selecionar.", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean allChecked = true;
            // Verifica todos os itens na lista de dados (items)
            for (ItemTaskList item : items) {
                if (!item.isCompleted()) {
                    allChecked = false;
                    break; // Saia do loop assim que encontrar um item não selecionado
                }
            }
            // Atualiza o estado de cada item na lista de dados
            for (ItemTaskList item : items) {
                boolean newCheckedState = !allChecked; // Alterna o estado
                item.setCompleted(newCheckedState);
                viewModel.updateTaskCompletion(item.getId(), newCheckedState);
            }
            // Notifica a adaptação da lista para atualizar a visualização
            itemsAdapter.notifyDataSetChanged();
        });

        btnDeleteSelected.setOnClickListener(v -> {
            ArrayList<String> itemsToRemoveIds = new ArrayList<>();
            for (ItemTaskList item : items) {
                if (item.isCompleted()) {
                    itemsToRemoveIds.add(item.getId());
                }
            }
            if (itemsToRemoveIds.isEmpty()) {
                Toast.makeText(this, "Não há itens selecionados para apagar.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Confirmação antes de deletar
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Confirmar Exclusão")
                    .setMessage("Você tem certeza que deseja apagar os itens selecionados?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        for (String itemId : itemsToRemoveIds) {
                            viewModel.deleteTask(itemId);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        });
    }

    private class ItemAdapter extends ArrayAdapter<ItemTaskList> {
        private ArrayList<ItemTaskList> items;

        public ItemAdapter(ArrayList<ItemTaskList> items) {
            super(TasksActivity.this, R.layout.dialog_to_do_list, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.dialog_to_do_list, parent, false);
            }

            ItemTaskList item = items.get(position);
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
