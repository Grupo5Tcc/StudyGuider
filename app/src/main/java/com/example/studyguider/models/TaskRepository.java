package com.example.studyguider.models;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

public class TaskRepository {
    private static final String TAG = "TaskRepository";
    private FirebaseFirestore db;
    private MutableLiveData<List<TaskItem>> tasksLiveData;

    public TaskRepository() {
        db = FirebaseFirestore.getInstance();
        tasksLiveData = new MutableLiveData<>();
    }

    public LiveData<List<TaskItem>> getTasks() {
        loadTasksFromFirebase();
        return tasksLiveData;
    }

    private void loadTasksFromFirebase() {
        db.collection("task_to_do_list")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<TaskItem> items = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String taskName = document.getString("task");
                                boolean completed = document.getBoolean("completed");
                                items.add(new TaskItem(document.getId(), taskName, completed));
                            }
                            tasksLiveData.postValue(items);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void addTask(String taskName) {
        Map<String, Object> item = new HashMap<>();
        item.put("task", taskName);
        item.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        item.put("completed", false);

        db.collection("task_to_do_list").add(item)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        loadTasksFromFirebase();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void deleteTask(String taskId) {
        db.collection("task_to_do_list").document(taskId).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadTasksFromFirebase();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    public void updateTaskCompletion(String taskId, boolean isCompleted) {
        db.collection("task_to_do_list").document(taskId)
                .update("completed", isCompleted)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadTasksFromFirebase();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }
}
