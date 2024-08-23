package com.example.studyguider.view;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studyguider.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlannerActivity extends AppCompatActivity {

    private LinearLayout containerTarefas;
    private CalendarView calendarView;

    // HashMap para armazenar tarefas associadas a cada data
    private HashMap<String, List<String>> tarefasPorData = new HashMap<>();

    // Data atualmente selecionada
    private String dataSelecionada;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_planner);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        containerTarefas = findViewById(R.id.container_tarefas);
        calendarView = findViewById(R.id.calendarView);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Formatar a data como uma string
            dataSelecionada = dayOfMonth + "/" + (month + 1) + "/" + year;

            // Exibir as tarefas para a data selecionada
            exibirTarefasParaData(dataSelecionada);
            containerTarefas.setVisibility(View.VISIBLE);
        });
    }

    // Método para exibir tarefas para uma data específica
    private void exibirTarefasParaData(String data) {
        containerTarefas.removeAllViews(); // Limpa as tarefas atuais

        List<String> tarefas = tarefasPorData.getOrDefault(data, new ArrayList<>());

        // Se não houver tarefas, exibir uma mensagem
        if (tarefas.isEmpty()) {
            TextView mensagemSemTarefas = new TextView(this);
            mensagemSemTarefas.setText("Nenhuma tarefa para esta data.");
            mensagemSemTarefas.setTextColor(Color.WHITE);
            containerTarefas.addView(mensagemSemTarefas);
        } else {
            // Exibir todas as tarefas para a data selecionada
            for (String tarefa : tarefas) {
                // Usar CardView para melhor layout e estilo
                CardView tarefaCardView = new CardView(this);
                tarefaCardView.setCardElevation(8);
                tarefaCardView.setRadius(16);
                tarefaCardView.setUseCompatPadding(true);
                tarefaCardView.setCardBackgroundColor(Color.parseColor("#FFBB86FC"));
                LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                cardParams.setMargins(16, 16, 16, 16);
                tarefaCardView.setLayoutParams(cardParams);

                // Layout interno para a tarefa
                LinearLayout tarefaLayout = new LinearLayout(this);
                tarefaLayout.setOrientation(LinearLayout.HORIZONTAL);
                tarefaLayout.setPadding(16, 16, 16, 16);
                tarefaLayout.setGravity(Gravity.CENTER_VERTICAL);
                tarefaLayout.setBackgroundColor(Color.parseColor("#FFBB86FC"));

                // Texto da tarefa
                TextView tarefaTextView = new TextView(this);
                tarefaTextView.setText(tarefa);
                tarefaTextView.setTextColor(Color.WHITE);
                tarefaTextView.setTextSize(18);
                tarefaTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                // Botão para excluir a tarefa
                TextView excluirTarefaTextView = new TextView(this);
                excluirTarefaTextView.setText("Excluir");
                excluirTarefaTextView.setTextColor(Color.RED);
                excluirTarefaTextView.setPadding(16, 0, 0, 0);
                excluirTarefaTextView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));

                excluirTarefaTextView.setOnClickListener(v -> excluirTarefa(data, tarefa));

                tarefaLayout.addView(tarefaTextView);
                tarefaLayout.addView(excluirTarefaTextView);
                tarefaCardView.addView(tarefaLayout);
                containerTarefas.addView(tarefaCardView);
            }
        }

        // Adicionar opção para adicionar nova tarefa
        TextView adicionarTarefaTextView = new TextView(this);
        adicionarTarefaTextView.setText("+ Adicionar nova tarefa");
        adicionarTarefaTextView.setTextColor(Color.YELLOW);
        adicionarTarefaTextView.setGravity(Gravity.CENTER);
        adicionarTarefaTextView.setTextSize(18);
        adicionarTarefaTextView.setPadding(0, 16, 0, 16);
        adicionarTarefaTextView.setOnClickListener(v -> adicionarTarefaDialog(data));
        containerTarefas.addView(adicionarTarefaTextView);
    }

    // Método para adicionar uma nova tarefa a uma data específica com input do usuário
    private void adicionarTarefaDialog(String data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Adicionar Nova Tarefa");

        // Adicionar um campo de entrada
        final EditText input = new EditText(this);
        input.setHint("Digite a nova tarefa");
        builder.setView(input);

        // Configurar botões de diálogo
        builder.setPositiveButton("Adicionar", (dialog, which) -> {
            String novaTarefa = input.getText().toString();
            if (!novaTarefa.isEmpty()) {
                List<String> tarefas = tarefasPorData.getOrDefault(data, new ArrayList<>());
                tarefas.add(novaTarefa);
                tarefasPorData.put(data, tarefas);
                exibirTarefasParaData(data); // Atualiza a exibição das tarefas
                Toast.makeText(this, "Tarefa adicionada para " + data, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Por favor, digite uma tarefa.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Método para excluir uma tarefa de uma data específica
    private void excluirTarefa(String data, String tarefa) {
        List<String> tarefas = tarefasPorData.get(data);
        if (tarefas != null) {
            tarefas.remove(tarefa);
            if (tarefas.isEmpty()) {
                tarefasPorData.remove(data); // Remove a entrada do HashMap se não houver mais tarefas
            } else {
                tarefasPorData.put(data, tarefas); // Atualiza o HashMap com a lista de tarefas atualizada
            }
            exibirTarefasParaData(data); // Atualiza a exibição das tarefas
            Toast.makeText(this, "Tarefa excluída para " + data, Toast.LENGTH_SHORT).show();
        }
    }

    // Método para editar uma tarefa de uma data específica
    private void editarTarefa(String data, String tarefaAntiga) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Tarefa");

        // Adicionar um campo de entrada
        final EditText input = new EditText(this);
        input.setText(tarefaAntiga);
        builder.setView(input);

        // Configurar botões de diálogo
        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String tarefaNova = input.getText().toString();
            if (!tarefaNova.isEmpty()) {
                List<String> tarefas = tarefasPorData.get(data);
                if (tarefas != null) {
                    tarefas.remove(tarefaAntiga);
                    tarefas.add(tarefaNova);
                    tarefasPorData.put(data, tarefas); // Atualiza o HashMap com a lista de tarefas atualizada
                    exibirTarefasParaData(data); // Atualiza a exibição das tarefas
                    Toast.makeText(this, "Tarefa editada para " + data, Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}