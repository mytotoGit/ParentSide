package com.ishuinzu.parentside.ui;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ishuinzu.parentside.R;
import com.ishuinzu.parentside.animation.PushDownAnimation;
import com.ishuinzu.parentside.app.Preferences;
import com.ishuinzu.parentside.databinding.ActivityFullScreenMapBinding;
import com.ishuinzu.parentside.object.ChildDevice;

public class FullScreenMapActivity extends AppCompatActivity {
    private static final String TAG = "FullScreenMapActivity";
    private ActivityFullScreenMapBinding binding;
    private SupportMapFragment supportMapFragment;
    private GoogleMap googleMaps;

    // Intent
    private String type;
    private double lat01, lat02, lng01, lng02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFullScreenMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);
        PushDownAnimation.setPushDownAnimationTo(binding.btnFullScreenMapExit).setOnClickListener(view -> finish());

        if (getIntent().getExtras() != null) {
            type = getIntent().getExtras().getString("TYPE");

            switch (type) {
                case "SINGLE":
                    lat01 = getIntent().getExtras().getDouble("LAT");
                    lng01 = getIntent().getExtras().getDouble("LNG");
                    break;
                case "DOUBLE":
                    lat01 = getIntent().getExtras().getDouble("LAT1");
                    lng01 = getIntent().getExtras().getDouble("LNG1");
                    lat02 = getIntent().getExtras().getDouble("LAT2");
                    lng02 = getIntent().getExtras().getDouble("LNG2");
                    break;
                case "REAL_TIME_LOCATION":
                    lat01 = Preferences.getInstance(FullScreenMapActivity.this).getChildDevice().getLocation().getLatitude();
                    lng01 = Preferences.getInstance(FullScreenMapActivity.this).getChildDevice().getLocation().getLongitude();
                    break;
            }
        }

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (type.equals("SINGLE") || type.equals("DOUBLE")) {
            if (supportMapFragment != null) {
                if (type.equals("SINGLE")) {
                    supportMapFragment.getMapAsync(onMapReadySingle);
                } else {
                    supportMapFragment.getMapAsync(onMapReadyDouble);
                }
            }
        } else {
            getLocationUpdates();
        }
    }

    OnMapReadyCallback onMapReadySingle = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            googleMaps = googleMap;
            LatLng childCurrentLocation = new LatLng(lat01, lng01);
            googleMaps.addMarker(new MarkerOptions().position(childCurrentLocation).title("Tracking Location"));
            googleMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(childCurrentLocation, 15));
            googleMaps.getUiSettings().setZoomControlsEnabled(true);
            googleMaps.getUiSettings().setScrollGesturesEnabled(true);
            googleMaps.getUiSettings().setAllGesturesEnabled(true);
            googleMaps.addCircle(new CircleOptions().center(childCurrentLocation).radius(100).strokeColor(getColor(R.color.blue_700)).fillColor(0x30FF0000).strokeWidth(1));
        }
    };

    OnMapReadyCallback onMapReadyDouble = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            googleMaps = googleMap;
            LatLng childCurrentLocation = new LatLng(lat01, lng01);
            LatLng childCurrentLocation02 = new LatLng(lat02, lng02);
            googleMaps.addMarker(new MarkerOptions().position(childCurrentLocation).title("Start Location"));
            googleMaps.addMarker(new MarkerOptions().position(childCurrentLocation02).title("End Location"));
            googleMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(childCurrentLocation, 15));
            googleMaps.getUiSettings().setZoomControlsEnabled(true);
            googleMaps.getUiSettings().setScrollGesturesEnabled(true);
            googleMaps.getUiSettings().setAllGesturesEnabled(true);
            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
            options.add(childCurrentLocation);
            options.add(childCurrentLocation02);
            googleMap.addPolyline(options);
        }
    };

    OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            googleMaps = googleMap;
            LatLng childCurrentLocation = new LatLng(Preferences.getInstance(getApplicationContext()).getChildDevice().getLocation().getLatitude(), Preferences.getInstance(getApplicationContext()).getChildDevice().getLocation().getLongitude());
            googleMaps.addMarker(new MarkerOptions().position(childCurrentLocation).title("Child Location"));
            googleMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(childCurrentLocation, 15));
            googleMaps.getUiSettings().setZoomControlsEnabled(true);
            googleMaps.getUiSettings().setScrollGesturesEnabled(true);
            googleMaps.getUiSettings().setAllGesturesEnabled(true);
            googleMaps.addCircle(new CircleOptions().center(childCurrentLocation).radius(100).strokeColor(getColor(R.color.blue_700)).fillColor(0x30FF0000).strokeWidth(1));
        }
    };

    private void getLocationUpdates() {
        FirebaseDatabase.getInstance().getReference()
                .child("child_devices")
                .child(Preferences.getInstance(getApplicationContext()).getParent().getId())
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            ChildDevice childDevice = snapshot.getValue(ChildDevice.class);

                            // Update Location in Preferences
                            assert childDevice != null;
                            Preferences.getInstance(getApplicationContext()).updateLocation(childDevice.getLocation());
                            if (googleMaps != null) {
                                googleMaps.clear();
                            }
                            if (supportMapFragment != null) {
                                supportMapFragment.getMapAsync(onMapReadyCallback);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, error.getMessage());
                    }
                });
    }
}