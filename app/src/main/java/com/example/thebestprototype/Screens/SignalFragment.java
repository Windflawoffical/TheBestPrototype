package com.example.thebestprototype.Screens;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.thebestprototype.MainActivity;
import com.example.thebestprototype.R;
import com.example.thebestprototype.Services.SignalService;
import com.example.thebestprototype.databinding.FragmentSignalBinding;



public class SignalFragment extends Fragment {

    FragmentSignalBinding fragmentSignalBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentSignalBinding = FragmentSignalBinding.inflate(inflater, container, false);
        return fragmentSignalBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentSignalBinding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);

//        fragmentSignalBinding.getsignalinfobutton.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity().getApplicationContext(), SignalService.class);
//            //intent.setAction("StartSignalService");
//            getActivity().startService(intent);
//        });

        fragmentSignalBinding.StopService.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity().getApplicationContext(), SignalService.class);
            getActivity().stopService(intent);
        });

        fragmentSignalBinding.bottomNavigation.setSelectedItemId(R.id.Signal);
        fragmentSignalBinding.bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.Signal:
                    return true;
                case R.id.Camera:
                    Navigation.findNavController(view).navigate(R.id.action_signalFragment_to_cameraFragment);
                    return true;
                case R.id.Location:
                    Navigation.findNavController(view).navigate(R.id.action_signalFragment_to_locationFragment);
                    return true;
                case R.id.Logout:
                    SharedPreferences logout = requireActivity().getSharedPreferences("Checkbox", Context.MODE_PRIVATE);
                    logout.edit().clear().apply();
                    SharedPreferences email = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    email.edit().clear().apply();
                    Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        });
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (result) {
                    // PERMISSION GRANTED
                    fragmentSignalBinding.getsignalinfobutton.setOnClickListener(v -> {
                        Intent intent = new Intent(getActivity().getApplicationContext(), SignalService.class);
                        //intent.setAction("StartSignalService");
                        getActivity().startService(intent);
                    });


                } else {
                    // PERMISSION NOT GRANTED
                    Toast.makeText(getActivity(), "Permission denied!", Toast.LENGTH_SHORT).show();
                }
            }
    );
}


