package com.example.thebestprototype.Services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.thebestprototype.API.RetrofitService;
import com.example.thebestprototype.API.UserAPI;
import com.example.thebestprototype.MainActivity;
import com.example.thebestprototype.Model.User;
import com.example.thebestprototype.R;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataService extends Service {

    RetrofitService retrofitService = new RetrofitService();
    UserAPI userAPI = retrofitService.getRetrofit().create(UserAPI.class);
    SharedPreferences preferences;
    Double latitude = 0.0;
    Double longtitude = 0.0;
    String email;
    final String LOG_TAG = "DataService";

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Log.d("CellSignalPowerUpdate: ", "new - " + getCellSignalPower(DataService.this));
            if(locationResult.getLastLocation() != null){
                latitude = locationResult.getLastLocation().getLatitude();
                longtitude = locationResult.getLastLocation().getLongitude();
                User user = new User();
                user.setEmail(email);
                user.setLatitude(latitude);
                user.setLongtitude(longtitude);
                userAPI.updateLocation(user).enqueue(new Callback<User>() {
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
                Log.d("LOCATION_UPDATE", latitude + ", " + longtitude);
            }
            sendDataToFragment(latitude, longtitude, getCellSignalPower(DataService.this));
        }
    };

    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
    }

    public void onDestroy() {
        stopLocationService();
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    private void startLocationService() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Location_nofication_channel");
        builder.setSmallIcon(R.drawable.ic_location);
        builder.setContentTitle("Location Service");
        builder.setContentText("Running");
        builder.setContentIntent(contentIntent);
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(notificationManager != null && notificationManager.getNotificationChannel("Location_nofication_channel") == null) {
                NotificationChannel notificationChannel = new NotificationChannel("Location_nofication_channel", "Location Service", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("This channel is used by location service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 30000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(30000)
                .setMaxUpdateDelayMillis(100)
                .build();

        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        startForeground(175, builder.build());
    }
    private void stopLocationService() {
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        if(intent != null)
        {
            startLocationService();
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            email = preferences.getString("Useremail", "defaul@mail.ru");
            Log.d("Email", "Email = " + email);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void sendDataToFragment(double latitude, double longtitude, int cellsignalpower){
        Intent intent = new Intent("GET_LOCATION"); //FILTER is a string to identify this intent
        intent.putExtra("Latitude", latitude);
        intent.putExtra("Longtitude", longtitude);
        intent.putExtra("CellSignalPower", cellsignalpower);
        sendBroadcast(intent);
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

}
