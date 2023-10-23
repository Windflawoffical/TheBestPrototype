package com.example.thebestprototype.Screens;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.thebestprototype.API.UserAPI;
import com.example.thebestprototype.Model.User;
import com.example.thebestprototype.R;
import com.example.thebestprototype.API.RetrofitService;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminFragment extends Fragment implements OnMapReadyCallback {

    MapView mapView;
    private GoogleMap map;
    List<User> usersfromdb = null;
    LatLng MarkerPos;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin, container, false);


        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) v.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);


        mapView.getMapAsync(this);


        return v;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RetrofitService retrofitService = new RetrofitService();
        UserAPI userAPI = retrofitService.getRetrofit().create(UserAPI.class);
        userAPI.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                Log.d("Response", "Response  =" + response);
                usersfromdb = response.body();

//                Log.d("Latitude and Longtitude", "Latitude for " + 1 + " user = " + latitude.get(0) + "; Longtitude for " + 1 + " user = " + longtitude.get(0));
//                User user = users.get(0);
//                Log.d("User", "The " + 1 + " user: " + user);
//                Log.d("Latitude and Longtitude", "Latitude for " + 1 + " user = " + user.getLatitude() + "; Longtitude for " + 1 + " user = " + user.getLongtitude());
//                for(int i = 0; i <= users.size(); i++){
//                    Log.d("User", "The " + i + " user: " + users.get(i));
//                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.d("Response", "Response  =" + t);
                Toast.makeText(getActivity(), "Не удалось получить всех юзеров!", Toast.LENGTH_LONG).show();
            }
        });
        ImageButton imageButton = (ImageButton)getView().findViewById(R.id.logoutbutton);
        imageButton.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_adminFragment_to_loginFragment);
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        map = googleMap;
        map.getUiSettings().setAllGesturesEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setOnCameraIdleListener(() -> {
            final LatLngBounds screenBounds = map.getProjection().getVisibleRegion().latLngBounds;
            if(usersfromdb != null){
                for (int i = 0; i < usersfromdb.size(); i++) {
                    if (screenBounds.contains(MarkerPos = new LatLng(usersfromdb.get(i).getLatitude(), usersfromdb.get(i).getLongtitude()))) {
                        map.addMarker((new MarkerOptions()
                                .anchor(0.5f, 0.5f)
                                .title(usersfromdb.get(i).getFullname())
                                .snippet(usersfromdb.get(i).getEmail())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                                .position(MarkerPos)
                        ));
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
