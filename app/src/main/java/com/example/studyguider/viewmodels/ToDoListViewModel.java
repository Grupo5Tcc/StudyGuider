package com.example.studyguider.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.studyguider.models.TaskItemToDoList;
import com.example.studyguider.models.TaskRepositoryToDoList;

import java.util.List;

public class ToDoListViewModel extends ViewModel {
    private TaskRepositoryToDoList repository;
    private LiveData<List<TaskItemToDoList>> tasks;

    public ToDoListViewModel() {
        repository = new TaskRepositoryToDoList();
        tasks = repository.getTasks();
    }

    public LiveData<List<TaskItemToDoList>> getTasks() {
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
