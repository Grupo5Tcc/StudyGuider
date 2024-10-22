package com.example.studyguider.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.studyguider.models.ItemAfazeresList;
import com.example.studyguider.repository.TaskRepositoryToDoList;

import java.util.List;

public class AfazeresViewModel extends ViewModel {
    private TaskRepositoryToDoList repository;
    private LiveData<List<ItemAfazeresList>> tasks;

    public AfazeresViewModel() {
        repository = new TaskRepositoryToDoList();
        tasks = repository.getTasks();
    }

    public LiveData<List<ItemAfazeresList>> getTasks() {
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
