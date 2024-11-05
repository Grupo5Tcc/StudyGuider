package com.example.studyguider.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studyguider.R;
import com.example.studyguider.models.Materias;

import java.util.ArrayList;

public class MateriasAdapter extends RecyclerView.Adapter<MateriasAdapter.ViewHolder> {
    Context context;
    private ArrayList<Materias> arrayList;
    private OnItemClickListener onItemClickListener;

    public MateriasAdapter(Context context, ArrayList<Materias> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.materia_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nomeMateria.setText(arrayList.get(position).getNomeMateria());
        holder.professor.setText(arrayList.get(position).getProfessor());
        holder.itemView.setOnClickListener(view -> onItemClickListener.onClick(arrayList.get(position)));

    }

    public int getItemCount() {
        return arrayList.size();
    }

    public void setOnClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomeMateria, professor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeMateria = itemView.findViewById(R.id.list_item_materia);
            professor = itemView.findViewById(R.id.list_item_professor);
        }
    }

    public interface OnItemClickListener {
        void onClick(Materias materia);
    }
}