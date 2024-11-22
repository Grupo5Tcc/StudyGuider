package com.example.studyguider.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studyguider.R;
import com.example.studyguider.models.Notas;

import java.util.ArrayList;

public class NotasAdapter extends RecyclerView.Adapter<NotasAdapter.ViewHolder> {
    Context context;
    ArrayList<Notas> arrayList;
    OnItemClickListener onItemClickListener;

    public NotasAdapter(Context context, ArrayList<Notas> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public NotasAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notas_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.subjectName.setText(arrayList.get(position).getSubjectName());
        holder.creditScore.setText(arrayList.get(position).getCreditScore());
        holder.workScore.setText(arrayList.get(position).getTrab());
        holder.listScore.setText(arrayList.get(position).getList());
        holder.precScore.setText(arrayList.get(position).getPre());
        holder.projectScore.setText(arrayList.get(position).getProva());
        holder.itemView.setOnClickListener(view -> onItemClickListener.onClick(arrayList.get(position)));
    }

    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView subjectName, creditScore, workScore, listScore, precScore, projectScore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // References to layout components
            subjectName = itemView.findViewById(R.id.list_item_materia); // TextView for subject name
            creditScore = itemView.findViewById(R.id.list_item_cred); // TextView for credit score
            workScore = itemView.findViewById(R.id.list_item_trab); // TextView for work score
            listScore = itemView.findViewById(R.id.list_item_lista); // TextView for list score
            precScore = itemView.findViewById(R.id.list_item_pre); // TextView for pre-evaluation score
            projectScore = itemView.findViewById(R.id.list_item_pro); // TextView for project score
        }

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(Notas notas);
    }
}