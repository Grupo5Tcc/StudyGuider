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
import com.example.studyguider.models.Subjects;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser ;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

// Classe adaptadora para o RecyclerView que exibe uma lista de matérias
public class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.ViewHolder> {
    private static final String TAG = "User Adapter"; // Tag para logs
    private final Context context; // Contexto da aplicação
    private final ArrayList<Subjects> arrayList; // Lista de matérias
    private SubjectsAdapter.OnItemClickListener onItemClickListener; // Listener para cliques nos itens

    // Construtor da classe
    public SubjectsAdapter(Context context, ArrayList<Subjects> arrayList) {
        this.context = context; // Inicializa o contexto
        this.arrayList = arrayList; // Inicializa a lista de matérias
    }

    @NonNull
    @Override
    public SubjectsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout para cada item da lista
        View view = LayoutInflater.from(context).inflate(R.layout.materia_list_item, parent, false);
        return new SubjectsAdapter.ViewHolder(view); // Retorna um novo ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectsAdapter.ViewHolder holder, int position) {
        Subjects user = arrayList.get(position); // Obtém a matéria correspondente à posição

        // Configura os dados nos TextViews
        holder.professor.setText(user.getProfessor());
        holder.materia.setText(user.getNomeMateria());

        // Define o clique no RelativeLayout para ações do usuário
        holder.relativeLayout.setOnClickListener(view -> {
            Log.d(TAG, "onClick: User item clicked"); // Log do clique
            if (onItemClickListener != null) {
                onItemClickListener.onClick(user); // Chama o listener se não for nulo
            }
        });

        // Lógica do botão de remover com confirmação
        holder.imageRemover.setOnClickListener(v -> {
            Log.d(TAG, "onClick: Remove button clicked"); // Log do clique no botão de remover
            showConfirmationDialog(user, position); // Mostra o diálogo de confirmação
        });
    }

    // Método para mostrar um diálogo de confirmação antes de excluir
    private void showConfirmationDialog(Subjects user, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Confirme a exclusão") // Título do diálogo
                .setMessage("Você tem certeza de que deseja excluir esse campo?") // Mensagem do diálogo
                .setPositiveButton("Sim", (dialog, which) -> {
                    Log.d(TAG, "onClick: Deletion confirmed"); // Log da confirmação
                    deleteUser (user, position); // Chama o método para excluir a matéria
                })
                .setNegativeButton("Não", (dialog, which) -> {
                    Log.d(TAG, "onClick: Deletion canceled"); // Log do cancelamento
                    dialog.dismiss(); // Fecha o diálogo
                })
                .create()
                .show(); // Mostra o diálogo
    }

    // Método para excluir a matéria
    private void deleteUser (Subjects user, int position) {
        Log.d(TAG, "delete:User  Deleting subjects with ID: " + user.getId()); // Log da exclusão

        FirebaseUser  currentUser  = FirebaseAuth.getInstance().getCurrentUser (); // Obtém o usuário atual
        if (currentUser  != null) { // Verifica se o usuário está logado
            String userId = currentUser .getUid(); // Obtém o ID do usuário

            // Atualiza o caminho para incluir a subcoleção
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("subjects").document(userId).collection("userSubjects").document(user.getId())
                    .delete().addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "delete:User  Subject deleted successfully"); // Log da exclusão bem-sucedida
                        // Remove o item da lista local após a exclusão bem-sucedida no Firestore
                        arrayList.remove(position);
                        notifyItemRemoved(position); // Notifica que o item foi removido
                        notifyItemRangeChanged(position, arrayList.size()); // Notifica que a faixa de itens foi alterada

                        // Exibe uma mensagem de sucesso
                        Toast.makeText(context, "Matéria excluída com sucesso", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "delete:User   Error", e); // Log de erro na exclusão
                        // Exibe uma mensagem de erro caso a exclusão falhe
                        Toast.makeText(context, "Erro ao excluir matéria", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size(); // Retorna o tamanho da lista de matérias
    }

    // Classe interna ViewHolder para gerenciar as views de cada item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView professor, materia; // TextViews para professor e matéria
        RelativeLayout relativeLayout; // Layout relativo para o item
        ImageView imageRemover; // Imagem do botão de remover

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Referências aos componentes do layout
            professor = itemView.findViewById(R.id.list_item_professor);
            materia = itemView.findViewById(R.id.list_item_materia);
            relativeLayout = itemView.findViewById(R.id.materia_item);
            imageRemover = itemView.findViewById(R.id.image_remover);
        }
    }

    // Método para definir o listener de clique
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener; // Inicializa o listener
    }

    // Interface para o listener de clique
    public interface OnItemClickListener {
        void onClick(Subjects user); // Método a ser implementado para o clique
    }
}