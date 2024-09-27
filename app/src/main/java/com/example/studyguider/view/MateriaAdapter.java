package com.example.studyguider.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studyguider.R;
import com.example.studyguider.models.Materia;

import java.util.ArrayList;

public class MateriaAdapter extends RecyclerView.Adapter<MateriaAdapter.ViewHolder> {
    Context context;
    ArrayList<Materia> arrayList;
    OnItemClickListener onItemClickListener;

    public MateriaAdapter(Context context, ArrayList<Materia> arrayList) {
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
        holder.conteudos.setText(arrayList.get(position).getConteudos());
        holder.media.setText(arrayList.get(position).getMedia());
        holder.itemView.setOnClickListener(view -> onItemClickListener.onClick(arrayList.get(position)));
    }

    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomeMateria, professor, conteudos, media;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeMateria = itemView.findViewById(R.id.list_item_materia);
            professor = itemView.findViewById(R.id.list_item_professor);
            conteudos = itemView.findViewById(R.id.list_item_conteudos);
            media = itemView.findViewById(R.id.list_item_media);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(Materia materia);
    }
}