package com.example.studyguider.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.studyguider.models.TaskItem;
import com.example.studyguider.models.TaskRepository;

import java.util.List;

public class ToDoListViewModel extends ViewModel {
    private TaskRepository repository;
    private LiveData<List<TaskItem>> tasks;

    public ToDoListViewModel() {
        repository = new TaskRepository();
        tasks = repository.getTasks();
    }

    public LiveData<List<TaskItem>> getTasks() {
        return tasks;
    }

    public void addTask(String taskName) {
        repository.addTask(taskName);
    }

    public void deleteTask(String taskId) {
        repository.deleteTask(taskId);
    }

    public void updateTaskCompletion(String taskId, boolean isCompleted) {
        repository.updateTaskCompletion(taskId, isCompleted);
    }
}
