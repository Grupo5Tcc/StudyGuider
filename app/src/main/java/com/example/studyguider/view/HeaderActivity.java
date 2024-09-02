package com.example.studyguider.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.studyguider.R;
import com.example.studyguider.viewmodels.HeaderViewModel;

public class HeaderActivity {

    private View headerView;
    private HeaderViewModel viewModel;

    public HeaderActivity(View headerView, HeaderViewModel viewModel, LifecycleOwner lifecycleOwner) {
        this.headerView = headerView;
        this.viewModel = viewModel;

        setupBindings(lifecycleOwner);
        setupListeners();
    }

    private void setupBindings(LifecycleOwner lifecycleOwner) {
        TextView usernameView = headerView.findViewById(R.id.txtUsuario);

        viewModel.getUsername().observe(lifecycleOwner, new Observer<String>() {
            @Override
            public void onChanged(String username) {
                usernameView.setText(username);
            }
        });
    }

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

