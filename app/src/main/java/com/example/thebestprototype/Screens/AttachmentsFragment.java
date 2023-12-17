package com.example.thebestprototype.Screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.thebestprototype.R;
import com.example.thebestprototype.databinding.FragmentAttachmentsBinding;

public class AttachmentsFragment extends Fragment {

    FragmentAttachmentsBinding fragmentAttachmentsBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentAttachmentsBinding = FragmentAttachmentsBinding.inflate(inflater, container, false);
        return fragmentAttachmentsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentAttachmentsBinding.goback.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_attachmentsFragment_to_cameraFragment);
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentAttachmentsBinding = null;
    }

}
