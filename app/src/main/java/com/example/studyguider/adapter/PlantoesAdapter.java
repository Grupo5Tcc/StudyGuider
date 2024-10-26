package com.example.studyguider.adapter;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class PlantoesAdapter extends RecyclerView.Adapter<PlantoesAdapter.ViewHolder> {
    private static final String TAG = "UserAdapter";
    private final Context context;
    private final ArrayList<Plantoes> arrayList;
    private OnItemClickListener onItemClickListener;

    public PlantoesAdapter(Context context, ArrayList<Plantoes> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.plantoes_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Plantoes user = arrayList.get(position);

        // Configura os dados nos TextViews
        holder.professor.setText(user.getProfessor());
        holder.materia.setText(user.getMateria());
        holder.dia.setText(user.getDia());
        holder.hora.setText(user.getHora());

        // Define o clique no RelativeLayout para ações do usuário
        holder.relativeLayout.setOnClickListener(view -> {
            Log.d(TAG, "onClick: User item clicked");
            if (onItemClickListener != null) {
                onItemClickListener.onClick(user);
            }
        });

        // Lógica do botão de remover com confirmação
        holder.imageRemover.setOnClickListener(v -> {
            Log.d(TAG, "onClick: Remove button clicked");
            showConfirmationDialog(user, position);
        });
    }

    private void showConfirmationDialog(Plantoes user, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Confirme a exclusão")
                .setMessage("Você tem certeza de que deseja excluir esse campo?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    Log.d(TAG, "onClick: Deletion confirmed");
                    deleteUser(user, position);
                })
                .setNegativeButton("Não", (dialog, which) -> {
                    Log.d(TAG, "onClick: Deletion canceled");
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    private void deleteUser(Plantoes user, int position) {
        Log.d(TAG, "deleteUser: Deleting shift with ID: " + user.getId());

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Atualizar o caminho para incluir a subcoleção
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("shifts").document(userId).collection("userShifts").document(user.getId())
                    .delete().addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "deleteUser: Shift deleted successfully");
                        // Remove o item da lista local após a exclusão bem-sucedida no Firestore
                        arrayList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, arrayList.size());

                        // Exibe uma mensagem de sucesso
                        Toast.makeText(context, "Shift excluído com sucesso", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "deleteUser: Error", e);
                        // Exibe uma mensagem de erro caso a exclusão falhe
                        Toast.makeText(context, "Erro ao excluir shift", Toast.LENGTH_SHORT).show();
                    });
        }
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView professor, materia, dia, hora;
        RelativeLayout relativeLayout;
        ImageView imageRemover;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Referências aos componentes do layout
            professor = itemView.findViewById(R.id.list_item_professor);
            materia = itemView.findViewById(R.id.list_item_materia);
            dia = itemView.findViewById(R.id.list_item_dia);
            hora = itemView.findViewById(R.id.list_item_hora);
            relativeLayout = itemView.findViewById(R.id.relative_layout);
            imageRemover = itemView.findViewById(R.id.image_remover);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(Plantoes user);
    }
}