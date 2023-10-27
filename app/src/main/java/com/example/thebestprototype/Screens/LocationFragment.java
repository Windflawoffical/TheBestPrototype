package com.example.thebestprototype.Screens;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.thebestprototype.MainActivity;
import com.example.thebestprototype.Model.User;
import com.example.thebestprototype.R;
import com.example.thebestprototype.Services.LocationService;
import com.example.thebestprototype.databinding.FragmentLocationBinding;

public class LocationFragment extends Fragment {

    FragmentLocationBinding fragmentLocationBinding;


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            double latitude = intent.getDoubleExtra("Latitude", 1);
            double longtitude = intent.getDoubleExtra("Longtitude", 5);
            String latitudestring = String.valueOf(latitude);
            String longtitudestring = String.valueOf(longtitude);
            fragmentLocationBinding.latitude.setText(String.valueOf("Широта: " + latitudestring));
            fragmentLocationBinding.longtitude.setText(String.valueOf("Долгота: " + longtitudestring));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentLocationBinding = FragmentLocationBinding.inflate(inflater, container, false);
        return fragmentLocationBinding.getRoot();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentLocationBinding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentLocationBinding.bottomNavigation.setSelectedItemId(R.id.Location);
        fragmentLocationBinding.bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.Location:
                    return true;
                case R.id.Camera:
                    Navigation.findNavController(view).navigate(R.id.action_locationFragment_to_cameraFragment);
                    return true;
                case R.id.Signal:
                    Navigation.findNavController(view).navigate(R.id.action_locationFragment_to_signalFragment);
                    return true;
                case R.id.Logout:
                    SharedPreferences logout = getActivity().getSharedPreferences("Checkbox", Context.MODE_PRIVATE);
                    logout.edit().clear().apply();
                    SharedPreferences email = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    email.edit().clear().apply();
                    Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    StopLocationService();
                    return true;
            }
            return false;
        });

        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        fragmentLocationBinding.StartLocationService.setOnClickListener(v -> {
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            } else {
                showGPSDisabledAlertToUser();
            }

        });
        fragmentLocationBinding.StopLocationService.setOnClickListener(v -> {
            StopLocationService();
            fragmentLocationBinding.longtitude.setText("");
            fragmentLocationBinding.latitude.setText("Сервис выключен!");
        });
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (result) {
                    // PERMISSION GRANTED
                    StartLocationService();


                } else {
                    // PERMISSION NOT GRANTED
                    Toast.makeText(getActivity(), "Permission denied!", Toast.LENGTH_SHORT).show();
                }
            }
    );



    private boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager != null){
            for(ActivityManager.RunningServiceInfo serviceInfo:
            activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (LocationService.class.getName().equals(serviceInfo.service.getClassName())) {
                    if(serviceInfo.foreground){
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }


    private void StartLocationService() {
        if(!isLocationServiceRunning()){
            Intent intent = new Intent(getActivity().getApplicationContext(), LocationService.class);
            intent.setAction("StartLocationService");
            getActivity().startService(intent);
            Toast.makeText(getActivity().getApplicationContext(), "Location service started", Toast.LENGTH_SHORT).show();
        }
    }
    private void StopLocationService() {
        if(isLocationServiceRunning()){
            Intent intent = new Intent(getActivity().getApplicationContext(), LocationService.class);
            getActivity().stopService(intent);
            Toast.makeText(getActivity().getApplicationContext(), "Location service stopped", Toast.LENGTH_SHORT).show();
        }
    }
    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(fragmentLocationBinding.getRoot().getContext());
        alertDialogBuilder.setMessage("Геолокация на устройстве отключена. Для работы приложения геолокация должна быть включена!")
                .setCancelable(false)
                .setPositiveButton("Включить геолокацию",
                        (dialog, id) -> {
                            Intent callGPSSettingIntent = new Intent(
                                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(callGPSSettingIntent);
                        });
        alertDialogBuilder.setNegativeButton("Отмена",
                (dialog, id) -> dialog.cancel());
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, new IntentFilter("GET_LOCATION"));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

}
