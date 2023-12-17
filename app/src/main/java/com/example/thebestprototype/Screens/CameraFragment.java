package com.example.thebestprototype.Screens;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.thebestprototype.MainActivity;
import com.example.thebestprototype.R;
import com.example.thebestprototype.Services.DataService;
import com.example.thebestprototype.databinding.FragmentCameraBinding;

public class CameraFragment extends Fragment {

    FragmentCameraBinding fragmentCameraBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentCameraBinding = FragmentCameraBinding.inflate(inflater, container, false);
        return fragmentCameraBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentCameraBinding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        fragmentCameraBinding.bottomNavigation.setSelectedItemId(R.id.Camera);
        fragmentCameraBinding.bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.Camera:
                    return true;
                case R.id.Location:
                    Navigation.findNavController(view).navigate(R.id.action_cameraFragment_to_dataFragment);
                    return true;
                case R.id.Logout:
                    if (isLocationServiceRunning()) {
                        Log.d("RABOTAET", "RABOTAET" + "VNATYRE");
                    }
                    if (isLocationServiceRunning()) {
                        StopLocationService();
                    }
                    SharedPreferences logout = requireActivity().getSharedPreferences("Checkbox", Context.MODE_PRIVATE);
                    logout.edit().clear().apply();
                    SharedPreferences email = androidx.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());
                    email.edit().clear().apply();
                    Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        });
        fragmentCameraBinding.gotoattachments.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_cameraFragment_to_attachmentsFragment);
        });


        fragmentCameraBinding.addphoto.setOnClickListener(v -> galleryActivityLauncher.launch(new String[]{"image/*"}));
    }

    ActivityResultLauncher<String[]> galleryActivityLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            if (result != null) {
                fragmentCameraBinding.addphoto.setImageURI(result);
            } else {
                Log.d("Result", "onActivityResult: the result is null for some reason");
            }
        }
    });

    private boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager != null){
            for(ActivityManager.RunningServiceInfo serviceInfo:
                    activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (DataService.class.getName().equals(serviceInfo.service.getClassName())) {
                    if(serviceInfo.foreground){
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    private void StopLocationService() {
        if(isLocationServiceRunning()){
            Intent intent = new Intent(getActivity().getApplicationContext(), DataService.class);
            getActivity().stopService(intent);
            Toast.makeText(getActivity().getApplicationContext(), "Location service stopped", Toast.LENGTH_SHORT).show();
        }
    }
}
