package com.example.studyguider.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.studyguider.models.ItemTaskList;
import com.example.studyguider.repository.TaskRepositoryToDoList;

import java.util.List;

public class TasksViewModel extends ViewModel {
    // Repositório que lida com as operações de tarefas
    private TaskRepositoryToDoList repository;

    // LiveData que mantém a lista de tarefas para observação
    private LiveData<List<ItemTaskList>> tasks;

    public TasksViewModel() {
        // Inicializa o repositório e carrega as tarefas
        repository = new TaskRepositoryToDoList();
        tasks = repository.getTasks();
    }

    // Retorna a lista observável de tarefas
    public LiveData<List<ItemTaskList>> getTasks() {
        return tasks;
    }

    // Adiciona uma nova tarefa usando o repositório
    public void addTask(String taskName) {
        repository.addTask(taskName);
    }

    // Remove uma tarefa pelo ID usando o repositório
    public void deleteTask(String taskId) {
        repository.deleteTask(taskId);
    }

    // Atualiza o estado de conclusão de uma tarefa usando o repositório
    public void updateTaskCompletion(String taskId, boolean isCompleted) {
        repository.updateTaskCompletion(taskId, isCompleted);
    }
}
