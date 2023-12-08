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
import com.example.thebestprototype.R;
import com.example.thebestprototype.Services.DataService;
import com.example.thebestprototype.databinding.FragmentDataBinding;

public class DataFragment extends Fragment {

    FragmentDataBinding fragmentDataBinding;


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            double latitude = intent.getDoubleExtra("Latitude", 0);
            double longtitude = intent.getDoubleExtra("Longtitude", 0);
            int cellsignalpower = intent.getIntExtra("CellSignalPower", 0);
            fragmentDataBinding.latitude.setText(String.valueOf("Широта: " + String.valueOf(latitude)));
            fragmentDataBinding.longtitude.setText(String.valueOf("Долгота: " + String.valueOf(longtitude)));
            fragmentDataBinding.cellsignalpower.setText(String.valueOf("Уровень сигнала: " + String.valueOf(cellsignalpower)));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentDataBinding = FragmentDataBinding.inflate(inflater, container, false);
        return fragmentDataBinding.getRoot();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentDataBinding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentDataBinding.bottomNavigation.setSelectedItemId(R.id.Location);
        fragmentDataBinding.bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.Location:
                    return true;
                case R.id.Camera:
                    Navigation.findNavController(view).navigate(R.id.action_dataFragment_to_cameraFragment);
                    return true;
                case R.id.Logout:
                    SharedPreferences logout = getActivity().getSharedPreferences("Checkbox", Context.MODE_PRIVATE);
                    logout.edit().clear().apply();
                    SharedPreferences email = androidx.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());
                    email.edit().clear().apply();
                    Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    StopLocationService();
                    return true;
            }
            return false;
        });

        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        fragmentDataBinding.StartLocationService.setOnClickListener(v -> {
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            } else {
                showGPSDisabledAlertToUser();
            }

        });
        fragmentDataBinding.StopLocationService.setOnClickListener(v -> {
            StopLocationService();
            fragmentDataBinding.latitude.setText("Сервис выключен!");
            fragmentDataBinding.longtitude.setText("");
            fragmentDataBinding.cellsignalpower.setText("");

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


    private void StartLocationService() {
        if(!isLocationServiceRunning()){
            Intent intent = new Intent(getActivity().getApplicationContext(), DataService.class);
            intent.setAction("StartLocationService");
            getActivity().startService(intent);
            Toast.makeText(getActivity().getApplicationContext(), "Location service started", Toast.LENGTH_SHORT).show();
        }
    }
    private void StopLocationService() {
        if(isLocationServiceRunning()){
            Intent intent = new Intent(getActivity().getApplicationContext(), DataService.class);
            getActivity().stopService(intent);
            Toast.makeText(getActivity().getApplicationContext(), "Location service stopped", Toast.LENGTH_SHORT).show();
        }
    }
    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(fragmentDataBinding.getRoot().getContext());
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
