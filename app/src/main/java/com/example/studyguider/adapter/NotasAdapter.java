package com.example.studyguider.adapter;

// Importações necessárias
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

// Classe adaptadora para o RecyclerView que exibe uma lista de notas
public class NotasAdapter extends RecyclerView.Adapter<NotasAdapter.ViewHolder> {
    Context context; // Contexto da aplicação
    ArrayList<Notas> arrayList; // Lista de notas
    OnItemClickListener onItemClickListener; // Listener para cliques nos itens

    // Construtor da classe
    public NotasAdapter(Context context, ArrayList<Notas> arrayList) {
        this.context = context; // Inicializa o contexto
        this.arrayList = arrayList; // Inicializa a lista de notas
    }

    @NonNull
    @Override
    public NotasAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout para cada item da lista de notas
        View view = LayoutInflater.from(context).inflate(R.layout.notas_list_item, parent, false);
        return new ViewHolder(view); // Retorna um novo ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Configura os dados nos TextViews com base na posição do item na lista
        holder.nomeMateria.setText(arrayList.get(position).getSubjectName()); // Nome da matéria
        holder.notaCred.setText(arrayList.get(position).getCreditScore()); // Nota de crédito
        holder.notaTrab.setText(arrayList.get(position).getTrab()); // Nota do trabalho
        holder.notaList.setText(arrayList.get(position).getList()); // Nota da lista
        holder.notaPrec.setText(arrayList.get(position).getPre()); // Nota da prova
        holder.notaPro.setText(arrayList.get(position).getProva()); // Nota da prova final

        // Define o clique no item da lista
        holder.itemView.setOnClickListener(view -> onItemClickListener.onClick(arrayList.get(position)));
    }

    @Override
    public int getItemCount() {
        return arrayList.size(); // Retorna o tamanho da lista de notas
    }

    // Classe interna ViewHolder para gerenciar as views de cada item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomeMateria, notaCred, notaTrab, notaList, notaPrec, notaPro; // TextViews para exibir as notas

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Referências aos componentes do layout
            nomeMateria = itemView.findViewById(R.id.list_item_materia);
            notaCred = itemView.findViewById(R.id.list_item_cred);
            notaTrab = itemView.findViewById(R.id.list_item_trab);
            notaList = itemView.findViewById(R.id.list_item_lista);
            notaPrec = itemView.findViewById(R.id.list_item_pre);
            notaPro = itemView.findViewById(R.id.list_item_pro);
        }
    }

    // Método para definir o listener de clique
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener; // Inicializa o listener
    }

    // Interface para o listener de clique
    public interface OnItemClickListener {
        void onClick(Notas notas); // Método a ser implementado para o clique
    }
}