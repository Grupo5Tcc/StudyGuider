package com.example.studyguider.adapter;

// Importações necessárias
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studyguider.R;
import com.example.studyguider.models.Plantoes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser ;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

// Classe adaptadora para o RecyclerView que exibe uma lista de plantões
public class PlantoesAdapter extends RecyclerView.Adapter<PlantoesAdapter.ViewHolder> {
    private static final String TAG = "User Adapter"; // Tag para log
    private final Context context; // Contexto da aplicação
    private final ArrayList<Plantoes> arrayList; // Lista de plantões
    private OnItemClickListener onItemClickListener; // Listener para cliques nos itens

    // Construtor da classe
    public PlantoesAdapter(Context context, ArrayList<Plantoes> arrayList) {
        this.context = context; // Inicializa o contexto
        this.arrayList = arrayList; // Inicializa a lista de plantões
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout para cada item da lista de plantões
        View view = LayoutInflater.from(context).inflate(R.layout.plantoes_list_item, parent, false);
        return new ViewHolder(view); // Retorna um novo ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Plantoes user = arrayList.get(position); // Obtém o plantão na posição atual

        // Configura os dados nos TextViews
        holder.professor.setText(user.getProfessor()); // Nome do professor
        holder.materia.setText(user.getMateria()); // Nome da matéria
        holder.dia.setText(user.getDia()); // Dia do plantão
        holder.hora.setText(user.getHora()); // Hora do plantão

        // Define o clique no RelativeLayout para ações do usuário
        holder.relativeLayout.setOnClickListener(view -> {
            Log.d(TAG, "onClick: User item clicked"); // Log do clique
            if (onItemClickListener != null) {
                onItemClickListener.onClick(user); // Chama o listener de clique
            }
        });

        // Lógica do botão de remover com confirmação
        holder.imageRemover.setOnClickListener(v -> {
            Log.d(TAG, "onClick: Remove button clicked"); // Log do clique no botão de remover
            showConfirmationDialog(user, position); // Exibe o diálogo de confirmação
        });
    }

    // Método para exibir o diálogo de confirmação antes de excluir
    private void showConfirmationDialog(Plantoes user, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Confirme a exclusão") // Título do diálogo
                .setMessage("Você tem certeza de que deseja excluir esse campo?") // Mensagem do diálogo
                .setPositiveButton("Sim", (dialog, which) -> {
                    Log.d(TAG, "onClick: Deletion confirmed"); // Log da confirmação de exclusão
                    deleteUser (user, position); // Chama o método para excluir o plantão
                })
                .setNegativeButton("Não", (dialog, which) -> {
                    Log.d(TAG, "onClick: Deletion canceled"); // Log do cancelamento da exclusão
                    dialog.dismiss(); // Fecha o diálogo
                })
                .create()
                .show(); // Exibe o diálogo
    }

    // Método para excluir o plantão
    private void deleteUser (Plantoes user, int position) {
        Log.d(TAG, "delete:User  Deleting shift with ID: " + user.getId()); // Log da exclusão

        FirebaseUser  currentUser  = FirebaseAuth.getInstance().getCurrentUser (); // Obtém o usuário atual
        if (currentUser  != null) { // Verifica se o usuário está autenticado
            String userId = currentUser .getUid(); // Obtém o ID do usuário

            // Atualiza o caminho para incluir a subcoleção
            FirebaseFirestore db = FirebaseFirestore.getInstance(); // Obtém a instância do Firestore
            db.collection("shifts").document(userId).collection("userShifts").document(user.getId())
                    .delete().addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "delete: :User  Shift deleted successfully"); // Log da exclusão bem-sucedida
                        // Remove o item da lista local após a exclusão bem-sucedida no Firestore
                        arrayList.remove(position); // Remove o plantão da lista
                        notifyItemRemoved(position); // Notifica que o item foi removido
                        notifyItemRangeChanged(position, arrayList.size()); // Atualiza a lista

                        // Exibe uma mensagem de sucesso
                        Toast.makeText(context, "Plantão excluído com sucesso", Toast.LENGTH_SHORT).show(); // Mensagem de sucesso
                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "delete:User  Error", e); // Log de erro
                        // Exibe uma mensagem de erro caso a exclusão falhe
                        Toast.makeText(context, "Erro ao excluir plantão", Toast.LENGTH_SHORT).show(); // Mensagem de erro
                    });
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size(); // Retorna o tamanho da lista de plantões
    }

    // Classe interna ViewHolder para gerenciar as views de cada item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView professor, materia, dia, hora; // TextViews para exibir os dados do plantão
        RelativeLayout relativeLayout; // Layout que contém os dados do plantão
        ImageView imageRemover; // Imagem do botão de remover

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Referências aos componentes do layout
            professor = itemView.findViewById(R.id.list_item_professor); // Referência ao TextView do professor
            materia = itemView.findViewById(R.id.list_item_materia); // Referência ao TextView da matéria
            dia = itemView.findViewById(R.id.list_item_dia); // Referência ao TextView do dia
            hora = itemView.findViewById(R.id.list_item_hora); // Referência ao TextView da hora
            relativeLayout = itemView.findViewById(R.id.relative_layout); // Referência ao layout relativo
            imageRemover = itemView.findViewById(R.id.image_remover); // Referência à imagem do botão de remover
        }
    }

    // Método para definir o listener de clique
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener; // Inicializa o listener
    }

    // Interface para o listener de clique
    public interface OnItemClickListener {
        void onClick(Plantoes user); // Método a ser implementado para o clique
    }
}