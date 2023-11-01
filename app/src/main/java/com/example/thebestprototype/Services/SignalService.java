package com.example.thebestprototype.Services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.IBinder;
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

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.thebestprototype.API.RetrofitService;
import com.example.thebestprototype.API.UserAPI;
import com.example.thebestprototype.Model.User;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignalService extends Service {

    RetrofitService retrofitService = new RetrofitService();
    UserAPI userAPI = retrofitService.getRetrofit().create(UserAPI.class);
    SharedPreferences preferences;
    String email;
    final String LOG_TAG = "SignalService";

    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        if(intent != null){
            sendDataToFragment(getCellSignalPower(this));
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            email = preferences.getString("Useremail", "defaul@mail.ru");
            Log.d("Email", "Email = " + email);
            Log.d("Signal", "Signal = " + getCellSignalPower(this));
            User user = new User();
            user.setEmail(email);
            user.setSignalpower(getCellSignalPower(this));
            userAPI.updateSignal(user).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    Log.d("Prishlo", "Response = " + response);
                    if(response.isSuccessful())
                    {
                        Log.d("YSPEX", "YSPEX = yspex");
                    } else {
                        Log.d("NEYDA4A", "NEYDA4A = NEYDA4A");
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.d("NEYDA4A", "NEYDA4A = error message " + t);
                }
            });
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private int getCellSignalPower(Context context) throws SecurityException {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String strength = null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
        }
        return Integer.parseInt(strength);
    }

    public void onDestroy() {
        stopSelf();
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return null;

    }

    public void sendDataToFragment(int signal){
        Intent intent = new Intent("GET_SIGNAL"); //FILTER is a string to identify this intent
        intent.putExtra("Signal", signal);
        sendBroadcast(intent);
    }
}