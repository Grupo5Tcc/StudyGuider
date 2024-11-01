package com.example.studyguider.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.studyguider.R;
import com.example.studyguider.viewmodels.HeaderViewModel;

public class  HeaderActivity {

    private View headerView;
    private HeaderViewModel viewModel;

    // Construtor que inicializa a view do cabeçalho e o ViewModel, e configura o binding e os listeners
    public HeaderActivity(View headerView, HeaderViewModel viewModel, LifecycleOwner lifecycleOwner) {
        this.headerView = headerView;
        this.viewModel = viewModel;

        setupBindings(lifecycleOwner);
        setupListeners();
    }

    // Configuração do binding para observar o nome de usuário e atualizar a interface
    private void setupBindings(LifecycleOwner lifecycleOwner) {
        TextView usernameView = headerView.findViewById(R.id.txtUsuario);

        // Observa mudanças no nome de usuário no ViewModel e atualiza o campo de texto
        viewModel.getUsername().observe(lifecycleOwner, new Observer<String>() {
            @Override
            public void onChanged(String username) {
                usernameView.setText(username);
            }
        });
    }

    // Configura o listener para a imagem de perfil
    private void setupListeners() {
        ImageView profileImage = headerView.findViewById(R.id.imgPerfil);
        if (profileImage != null) {
            profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewModel.onProfileImageClicked(headerView.getContext());
                }
            });
        }
    }
}


