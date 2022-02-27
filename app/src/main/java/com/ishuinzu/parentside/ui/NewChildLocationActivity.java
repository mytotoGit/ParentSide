package com.ishuinzu.parentside.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.ishuinzu.parentside.R;
import com.ishuinzu.parentside.animation.PushDownAnimation;
import com.ishuinzu.parentside.app.Preferences;
import com.ishuinzu.parentside.databinding.ActivityNewChildLocationBinding;
import com.ishuinzu.parentside.object.ChildLocation;
import com.ishuinzu.parentside.object.DateStamp;
import com.ishuinzu.parentside.object.LatitudeLongitude;
import com.ishuinzu.parentside.object.TimeStamp;
import com.ishuinzu.parentside.widget.datetimepicker.view.datePicker.DatePicker;
import com.ishuinzu.parentside.widget.datetimepicker.view.timePicker.TimePicker;

public class NewChildLocationActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "NewChildLocationActivity";
    private ActivityNewChildLocationBinding binding;
    private SupportMapFragment supportMapFragment;
    private GoogleMap googleMaps;

    // Variables
    private final LatitudeLongitude current_location = new LatitudeLongitude();
    private final DateStamp date = new DateStamp();
    private final TimeStamp end_time = new TimeStamp();
    private final LatitudeLongitude last_location = new LatitudeLongitude();
    private String slot_id = "";
    private final LatitudeLongitude start_location = new LatitudeLongitude();
    private final TimeStamp start_time = new TimeStamp();
    private LatitudeLongitude tracking_location = new LatitudeLongitude();

    // Child Location
    private ChildLocation childLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewChildLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);
        PushDownAnimation.setPushDownAnimationTo(binding.btnFullScreenMapExit).setOnClickListener(view -> finish());

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(onMapReadyCallback);
        }

        // Click Listener
        binding.btnFullScreenMapExit.setOnClickListener(this);
        binding.btnShowChildLocationDialog.setOnClickListener(this);
    }

    OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            googleMaps = googleMap;
            LatLng collegeLocation = new LatLng(33.609189, 73.062295);
            googleMaps.addMarker(new MarkerOptions().position(collegeLocation).title("Govt Gordon College Rawalpindi"));
            googleMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(collegeLocation, 15));
            googleMaps.getUiSettings().setZoomControlsEnabled(true);
            googleMaps.getUiSettings().setScrollGesturesEnabled(true);
            googleMaps.getUiSettings().setAllGesturesEnabled(true);
            googleMaps.setOnMapClickListener(latLng -> {
                Log.d(TAG, "Latitude : " + latLng.latitude);
                Log.d(TAG, "Longitude : " + latLng.longitude);

                tracking_location.setLatitude(latLng.latitude);
                tracking_location.setLongitude(latLng.longitude);
            });
        }
    };

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnFullScreenMapExit:
                // Close Screen
                finish();

            case R.id.btnShowChildLocationDialog:
                // Show Child Location Dialog
                showChildLocationDialog();
                break;
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void showChildLocationDialog() {
        final Dialog dialog = new Dialog(NewChildLocationActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_child_location_details);
        dialog.setCancelable(true);

        TimePicker startTimePicker = dialog.findViewById(R.id.startTimePicker);
        TimePicker endTimePicker = dialog.findViewById(R.id.endTimePicker);
        DatePicker datePicker = dialog.findViewById(R.id.datePicker);

        startTimePicker.setTimeSelectListener(new TimePicker.TimeSelectListener() {
            @Override
            public void onTimeSelected(int hour, int minute) {
                Log.d(TAG, "Start Time - " + hour + ":" + minute);

                start_time.setHour(hour);
                start_time.setMinute(minute);
            }
        });
        endTimePicker.setTimeSelectListener(new TimePicker.TimeSelectListener() {
            @Override
            public void onTimeSelected(int hour, int minute) {
                Log.d(TAG, "End Time - " + hour + ":" + minute);

                end_time.setHour(hour);
                end_time.setMinute(minute);
            }
        });
        datePicker.setDataSelectListener(new DatePicker.DataSelectListener() {
            @Override
            public void onDateSelected(long dateCurrent, int day, int month, int year) {
                Log.d(TAG, "Date - " + day + "/" + (month + 1) + "/" + year);

                date.setDay(day);
                date.setMonth(month + 1);
                date.setYear(year);
            }
        });

        (dialog.findViewById(R.id.btnDone)).setOnClickListener(v -> {
            // Upload Child Location
            uploadChildLocation(dialog);
        });

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setAttributes(layoutParams);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_transparent));
        dialog.show();
    }

    private void uploadChildLocation(Dialog dialog) {
        childLocation = new ChildLocation();
        slot_id = "" + System.currentTimeMillis();
        current_location.setLatitude(0);
        current_location.setLongitude(0);
        current_location.setIs_allowed(true);
        start_location.setLatitude(0);
        start_location.setLongitude(0);
        start_location.setIs_allowed(true);
        last_location.setLatitude(0);
        last_location.setLongitude(0);
        last_location.setIs_allowed(true);
        tracking_location.setIs_allowed(true);
        Boolean tracking = true;

        childLocation.setCurrent_location(current_location);
        childLocation.setDate(date);
        childLocation.setEnd_time(end_time);
        childLocation.setLast_location(last_location);
        childLocation.setSlot_id(slot_id);
        childLocation.setStart_location(start_location);
        childLocation.setStart_time(start_time);
        childLocation.setTracking(tracking);
        childLocation.setTracking_location(tracking_location);

        FirebaseDatabase.getInstance().getReference().child("locations")
                .child(Preferences.getInstance(NewChildLocationActivity.this).getParent().getId())
                .child(childLocation.getSlot_id())
                .setValue(childLocation);
        dialog.dismiss();
    }
}