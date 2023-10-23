package com.example.thebestprototype.Screens;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.thebestprototype.MainActivity;
import com.example.thebestprototype.R;
import com.example.thebestprototype.databinding.FragmentSignalBinding;

import java.util.List;


public class SignalFragment extends Fragment {

    FragmentSignalBinding fragmentSignalBinding;
    TelephonyManager telephonyManager;

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

        telephonyManager = (TelephonyManager) requireActivity().getSystemService(Context.TELEPHONY_SERVICE);

        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);

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
                    Log.d("CellSignalStrength", "" + getSignalStrength(telephonyManager));
//                    fragmentSignalBinding.infoforsignal.setText(getSignalStrength(telephonyManager));


                } else {
                    // PERMISSION NOT GRANTED
                    Toast.makeText(getActivity().getApplicationContext(), "Permission denied!", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private static String getSignalStrength(TelephonyManager telephonyManager) throws SecurityException {
        String strength = null;
        List<CellInfo> cellInfos = telephonyManager.getAllCellInfo();   //This will give info of all sims present inside your mobile
        if(cellInfos != null) {
            for (int i = 0 ; i < cellInfos.size() ; i++) {
                if (cellInfos.get(i).isRegistered()) {
                    if (cellInfos.get(i) instanceof CellInfoWcdma) {
                        CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfos.get(i);
                        CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();
                        strength = String.valueOf(cellSignalStrengthWcdma.getDbm());
                    } else if (cellInfos.get(i) instanceof CellInfoGsm) {
                        CellInfoGsm cellInfogsm = (CellInfoGsm) cellInfos.get(i);
                        CellSignalStrengthGsm cellSignalStrengthGsm = cellInfogsm.getCellSignalStrength();
                        strength = String.valueOf(cellSignalStrengthGsm.getDbm());
                    } else if (cellInfos.get(i) instanceof CellInfoLte) {
                        CellInfoLte cellInfoLte = (CellInfoLte) cellInfos.get(i);
                        CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                        strength = String.valueOf(cellSignalStrengthLte.getDbm());
                    } else if (cellInfos.get(i) instanceof CellInfoCdma) {
                        CellInfoCdma cellInfoCdma = (CellInfoCdma) cellInfos.get(i);
                        CellSignalStrengthCdma cellSignalStrengthCdma = cellInfoCdma.getCellSignalStrength();
                        strength = String.valueOf(cellSignalStrengthCdma.getDbm());
                    }
                }
            }
        }
        return "Your cell signal strength = " + strength;
    }
}


