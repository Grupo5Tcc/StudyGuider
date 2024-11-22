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
        holder.nomeMateria.setText(arrayList.get(position).getNomeMateria());
        holder.notaCred.setText(arrayList.get(position).getCred());
        holder.notaTrab.setText(arrayList.get(position).getTrab());
        holder.notaList.setText(arrayList.get(position).getList());
        holder.notaPrec.setText(arrayList.get(position).getPre());
        holder.notaPro.setText(arrayList.get(position).getProva());
        holder.itemView.setOnClickListener(view -> onItemClickListener.onClick(arrayList.get(position)));
    }

    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomeMateria, notaCred, notaTrab, notaList, notaPrec, notaPro;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeMateria = itemView.findViewById(R.id.list_item_materia);
            notaCred = itemView.findViewById(R.id.list_item_cred);
            notaTrab = itemView.findViewById(R.id.list_item_trab);
            notaList = itemView.findViewById(R.id.list_item_lista);
            notaPrec = itemView.findViewById(R.id.list_item_pre);
            notaPro = itemView.findViewById(R.id.list_item_pro);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(Notas notas);
    }
}