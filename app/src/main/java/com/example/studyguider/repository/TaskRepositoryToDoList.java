package com.example.studyguider.repository;

// Importações necessárias
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.studyguider.models.ItemTaskList;
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
import java.util.List;
import java.util.Map;

// Classe que gerencia as operações relacionadas às tarefas da lista de afazeres
public class TaskRepositoryToDoList {
    private static final String TAG = "TaskRepository"; // Tag para logs
    private FirebaseFirestore db; // Instância do Firestore
    private MutableLiveData<List<ItemTaskList>> tasksLiveData; // LiveData para observar a lista de tarefas

    // Construtor da classe
    public TaskRepositoryToDoList() {
        db = FirebaseFirestore.getInstance(); // Inicializa a instância do Firestore
        tasksLiveData = new MutableLiveData<>(); // Inicializa o LiveData
    }

    // Método para obter as tarefas
    public LiveData<List<ItemTaskList>> getTasks() {
        loadTasksFromFirebase(); // Carrega as tarefas do Firestore
        return tasksLiveData; // Retorna o LiveData com as tarefas
    }

    // Método privado para carregar as tarefas do Firestore
    private void loadTasksFromFirebase() {
        db.collection("task_to_do_list") // Acessa a coleção de tarefas
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser ().getUid()) // Filtra as tarefas pelo ID do usuário atual
                .get() // Obtém os documentos da coleção
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) { // Verifica se a tarefa foi bem-sucedida
                            List<ItemTaskList> items = new ArrayList<>(); // Cria uma lista para armazenar as tarefas
                            for (QueryDocumentSnapshot document : task.getResult()) { // Itera pelos documentos retornados
                                String taskName = document.getString("task"); // Obtém o nome da tarefa
                                boolean completed = document.getBoolean("completed"); // Obtém o status de conclusão
                                items.add(new ItemTaskList(document.getId(), taskName, completed)); // Adiciona a tarefa à lista
                            }
                            tasksLiveData.postValue(items); // Atualiza o LiveData com a nova lista de tarefas
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException()); // Log de erro ao obter documentos
                        }
                    }
                });
    }

    // Método para adicionar uma nova tarefa
    public void addTask(String taskName) {
        Map<String, Object> item = new HashMap<>(); // Cria um mapa para armazenar os dados da nova tarefa
        item.put("task", taskName); // Adiciona o nome da tarefa ao mapa
        item.put("userId", FirebaseAuth.getInstance().getCurrentUser ().getUid()); // Adiciona o ID do usuário ao mapa
        item.put("completed", false); // Define o status de conclusão como falso

        // Adiciona a nova tarefa na coleção de tarefas
        db.collection("task_to_do_list").add(item)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        loadTasksFromFirebase(); // Carrega as tarefas novamente após a adição
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e); // Log de erro ao adicionar documento
                    }
                });
    }

    // Método para excluir uma tarefa
    public void deleteTask(String taskId) {
        db.collection("task_to_do_list").document(taskId).delete() // Exclui o documento da tarefa com o ID fornecido
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadTasksFromFirebase(); // Carrega as tarefas novamente após a exclusão
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e); // Log de erro ao excluir documento
                    }
                });
    }

    // Método para atualizar o status de conclusão de uma tarefa
    public void updateTaskCompletion(String taskId, boolean isCompleted) {
        db.collection("task_to_do_list").document(taskId)
                .update("completed", isCompleted) // Atualiza o status de conclusão da tarefa
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadTasksFromFirebase(); // Carrega as tarefas novamente após a atualização
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e); // Log de erro ao atualizar documento
                    }
                });
    }
}