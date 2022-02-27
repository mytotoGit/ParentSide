package com.ishuinzu.parentside.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;
import com.ishuinzu.parentside.R;
import com.ishuinzu.parentside.animation.PushDownAnimation;
import com.ishuinzu.parentside.app.Preferences;
import com.ishuinzu.parentside.app.Utils;
import com.ishuinzu.parentside.databinding.ActivityChildInformationBinding;
import com.ishuinzu.parentside.dialog.LoadingDialog;
import com.ishuinzu.parentside.object.ChildDevice;
import com.ishuinzu.parentside.object.DateStamp;
import com.ishuinzu.parentside.object.FeatureObject;
import com.ishuinzu.parentside.object.Parent;

import java.util.ArrayList;
import java.util.List;

public class ChildInformationActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ConnectedActivity";
    private ActivityChildInformationBinding binding;
    private ChildDevice childDevice;
    private Parent parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChildInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    @SuppressLint({"HardwareIds", "SetTextI18n"})
    private void init() {
        childDevice = Preferences.getInstance(ChildInformationActivity.this).getChildDevice();
        parent = Preferences.getInstance(ChildInformationActivity.this).getParent();

        if (childDevice != null) {
            binding.txtDeviceModel.setText(childDevice.getBrand() + " " + childDevice.getModel());
            binding.txtAndroidVersion.setText(Utils.getAndroidVersion(Integer.parseInt(childDevice.getSdk_number())));
            binding.txtBrand.setText(childDevice.getBrand());
            binding.txtDisplay.setText(childDevice.getDisplay());
            binding.txtHardware.setText(childDevice.getHardware());
            binding.txtManufacturer.setText(childDevice.getManufacturer());
            binding.txtModel.setText(childDevice.getModel());
        }

        PushDownAnimation.setPushDownAnimationTo(binding.btnCloseScreen).setOnClickListener(this);
        PushDownAnimation.setPushDownAnimationTo(binding.cardGoToHome).setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCloseScreen:
                onBackPressed();
                break;

            case R.id.cardGoToHome:
                LoadingDialog.showLoadingDialog(ChildInformationActivity.this);
                setFeatures();
                break;
        }
    }

    private void setFeatures() {
        // Set Features To Firebase
        List<FeatureObject> featureObjects = new ArrayList<>();
        List<String> names = new ArrayList<>();

        FeatureObject apps_and_usage = new FeatureObject("", "false", new DateStamp());
        FeatureObject call_logs = new FeatureObject("", "false", new DateStamp());
        FeatureObject health_and_fitness = new FeatureObject("", "true", new DateStamp());
        FeatureObject live_lock = new FeatureObject("", "false", new DateStamp());
        FeatureObject location_monitoring = new FeatureObject("", "false", new DateStamp());
        FeatureObject real_time_location = new FeatureObject("", "true", new DateStamp());
        FeatureObject screen_capturing = new FeatureObject("", "false", new DateStamp());
        FeatureObject screen_recording = new FeatureObject("", "false", new DateStamp());
        FeatureObject sms_logs = new FeatureObject("", "false", new DateStamp());
        FeatureObject sync_child = new FeatureObject("", "true", new DateStamp());
        FeatureObject sync_hidden_camera = new FeatureObject("", "false", new DateStamp());

        featureObjects.add(apps_and_usage);
        names.add("apps_and_usage");
        featureObjects.add(call_logs);
        names.add("call_logs");
        featureObjects.add(health_and_fitness);
        names.add("health_and_fitness");
        featureObjects.add(live_lock);
        names.add("live_lock");
        featureObjects.add(location_monitoring);
        names.add("location_monitoring");
        featureObjects.add(real_time_location);
        names.add("real_time_location");
        featureObjects.add(screen_capturing);
        names.add("screen_capturing");
        featureObjects.add(screen_recording);
        names.add("screen_recording");
        featureObjects.add(sms_logs);
        names.add("sms_logs");
        featureObjects.add(sync_child);
        names.add("sync_child");
        featureObjects.add(sync_hidden_camera);
        names.add("sync_hidden_camera");

        String currentName = "";
        FeatureObject currentFeature = new FeatureObject();

        for (int i = 0; i < featureObjects.size(); i++) {
            currentName = names.get(i);
            currentFeature = featureObjects.get(i);

            // Upload
            FirebaseDatabase.getInstance().getReference().child("features")
                    .child(parent.getId())
                    .child(currentName)
                    .setValue(currentFeature)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Feature Uploaded");
                        }
                    });
        }
        LoadingDialog.closeDialog();
        startActivity(new Intent(ChildInformationActivity.this, DashboardActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }
}