package com.ishuinzu.parentside.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ishuinzu.parentside.R;
import com.ishuinzu.parentside.animation.PushDownAnimation;
import com.ishuinzu.parentside.app.Preferences;
import com.ishuinzu.parentside.databinding.ActivityDashboardBinding;
import com.ishuinzu.parentside.fragment.AppsUsageFragment;
import com.ishuinzu.parentside.fragment.CallLogsFragment;
import com.ishuinzu.parentside.fragment.ChildImagesFragment;
import com.ishuinzu.parentside.fragment.ContactUsFragment;
import com.ishuinzu.parentside.fragment.FeedbackFragment;
import com.ishuinzu.parentside.fragment.HealthAndFitnessFragment;
import com.ishuinzu.parentside.fragment.HomeFragment;
import com.ishuinzu.parentside.fragment.LiveLockFragment;
import com.ishuinzu.parentside.fragment.LocationFragment;
import com.ishuinzu.parentside.fragment.NotificationsFragment;
import com.ishuinzu.parentside.fragment.ProfileFragment;
import com.ishuinzu.parentside.fragment.SMSLogsFragment;
import com.ishuinzu.parentside.fragment.ScreenRecordingsFragment;
import com.ishuinzu.parentside.fragment.ScreenshotsFragment;
import com.ishuinzu.parentside.fragment.SettingFragment;
import com.ishuinzu.parentside.object.CameraSettingObject;
import com.ishuinzu.parentside.object.FeatureObject;
import com.ishuinzu.parentside.object.Parent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "DashboardActivity";
    private ActivityDashboardBinding binding;
    private View headerView;
    private List<FeatureObject> featureObjects;
    private MenuItem menuNotifications, menuLocationMonitoring, menuRealTimeLocation, menuAppsUsage, menuHealthFitness, menuLiveLock, menuScreenRecordings, menuScreenshots, menuCallLogs, menuSmsLogs, menuAccessCamera;
    private SwitchCompat checkLocationMonitoring, checkRealTimeLocation, checkAppsUsage, checkHealthFitness, checkLiveLock, checkScreenRecordings, checkScreenshots, checkCallLogs, checkSmsLogs, checkAccessCamera;
    private TextView txtNotifications;
    private Parent parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        PushDownAnimation.setPushDownAnimationTo(binding.imgProfile).setOnClickListener(this);
        PushDownAnimation.setPushDownAnimationTo(binding.btnDrawer).setOnClickListener(this);
        binding.navigationView.setNavigationItemSelectedListener(this);
        parent = Preferences.getInstance(DashboardActivity.this).getParent();

        designDrawer();
        setHeader();
        setFragment(HomeFragment.newInstance("PARAM 1", "PARAM 2"), "Home");
        getFeaturesData();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDrawer:
                binding.layoutDrawer.openDrawer(GravityCompat.START);
                break;

            case R.id.imgProfile:
                binding.layoutDrawer.closeDrawer(GravityCompat.START);
                binding.navigationView.setCheckedItem(R.id.nav_profile);
                setFragment(ProfileFragment.newInstance("", ""), "Profile");
                break;
        }
    }

    @SuppressLint({"NonConstantResourceId", "UseCompatLoadingForDrawables"})
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                binding.layoutDrawer.closeDrawer(GravityCompat.START);
                binding.navigationView.setCheckedItem(R.id.nav_home);
                setFragment(HomeFragment.newInstance("", ""), "Home");
                break;

//            case R.id.nav_child:
//                binding.layoutDrawer.closeDrawer(GravityCompat.START);
//                binding.navigationView.setCheckedItem(R.id.nav_child);
//                setFragment(ChildDeviceFragment.newInstance("", ""), "ChildDevice");
//                break;

            case R.id.nav_notifications:
                binding.layoutDrawer.closeDrawer(GravityCompat.START);
                binding.navigationView.setCheckedItem(R.id.nav_notifications);
                setFragment(NotificationsFragment.newInstance("", ""), "Notifications");
                break;

            case R.id.nav_location_monitoring:
                binding.layoutDrawer.closeDrawer(GravityCompat.START);
                binding.navigationView.setCheckedItem(R.id.nav_location_monitoring);
                setFragment(LocationFragment.newInstance("", ""), "LocationMonitoring");
                break;

            case R.id.nav_real_time_location:
                binding.layoutDrawer.closeDrawer(GravityCompat.START);
                binding.navigationView.setCheckedItem(R.id.nav_real_time_location);
                startActivity(new Intent(DashboardActivity.this, FullScreenMapActivity.class).putExtra("TYPE", "REAL_TIME_LOCATION"));
                break;

            case R.id.nav_apps_usage:
                binding.layoutDrawer.closeDrawer(GravityCompat.START);
                binding.navigationView.setCheckedItem(R.id.nav_apps_usage);
                setFragment(AppsUsageFragment.newInstance("", ""), "AppsUsage");
                break;

            case R.id.nav_health_and_fitness:
                binding.layoutDrawer.closeDrawer(GravityCompat.START);
                binding.navigationView.setCheckedItem(R.id.nav_health_and_fitness);
                setFragment(HealthAndFitnessFragment.newInstance("", ""), "HealthAndFitness");
                break;

            case R.id.nav_live_lock:
                binding.layoutDrawer.closeDrawer(GravityCompat.START);
                binding.navigationView.setCheckedItem(R.id.nav_live_lock);
                setFragment(LiveLockFragment.newInstance("", ""), "LiveLock");
                break;

            case R.id.nav_screen_recordings:
                binding.layoutDrawer.closeDrawer(GravityCompat.START);
                binding.navigationView.setCheckedItem(R.id.nav_screen_recordings);
                setFragment(ScreenRecordingsFragment.newInstance("", ""), "ScreenRecordings");
                break;

            case R.id.nav_screenshots:
                binding.layoutDrawer.closeDrawer(GravityCompat.START);
                binding.navigationView.setCheckedItem(R.id.nav_screenshots);
                setFragment(ScreenshotsFragment.newInstance("", ""), "Screenshots");
                break;

            case R.id.nav_call_logs:
                binding.layoutDrawer.closeDrawer(GravityCompat.START);
                binding.navigationView.setCheckedItem(R.id.nav_call_logs);
                setFragment(CallLogsFragment.newInstance("", ""), "CallLogs");
                break;

            case R.id.nav_sms_logs:
                binding.layoutDrawer.closeDrawer(GravityCompat.START);
                binding.navigationView.setCheckedItem(R.id.nav_sms_logs);
                setFragment(SMSLogsFragment.newInstance("", ""), "SMSLogs");
                break;

            case R.id.nav_feedback:
                binding.layoutDrawer.closeDrawer(GravityCompat.START);
                binding.navigationView.setCheckedItem(R.id.nav_feedback);
                setFragment(FeedbackFragment.newInstance("", ""), "Feedback");
                break;

            case R.id.nav_setting:
                binding.layoutDrawer.closeDrawer(GravityCompat.START);
                binding.navigationView.setCheckedItem(R.id.nav_setting);
                setFragment(SettingFragment.newInstance("", ""), "Setting");
                break;

            case R.id.nav_contact_us:
                binding.layoutDrawer.closeDrawer(GravityCompat.START);
                binding.navigationView.setCheckedItem(R.id.nav_contact_us);
                setFragment(ContactUsFragment.newInstance("", ""), "ContactUs");
                break;

            case R.id.nav_profile:
                binding.layoutDrawer.closeDrawer(GravityCompat.START);
                binding.navigationView.setCheckedItem(R.id.nav_profile);
                setFragment(ProfileFragment.newInstance("", ""), "Profile");
                break;

            case R.id.nav_access_camera:
                binding.layoutDrawer.closeDrawer(GravityCompat.START);
                binding.navigationView.setCheckedItem(R.id.nav_access_camera);
                setFragment(ChildImagesFragment.newInstance("", ""), "ChildCameraImages");
                break;

            case R.id.nav_logout:
                binding.layoutDrawer.closeDrawer(GravityCompat.START);
                // Show Logout Dialog after 600ms
                new Handler().postDelayed(() -> {
                    Dialog dialog = new Dialog(DashboardActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.layout_dialog_logout);
                    dialog.setCancelable(true);

                    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                    layoutParams.copyFrom(dialog.getWindow().getAttributes());
                    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

                    (dialog.findViewById(R.id.btnCancel)).setOnClickListener(v -> {
                        dialog.dismiss();
                    });
                    (dialog.findViewById(R.id.btnLogout)).setOnClickListener(v -> {
                        // Logout Code Here
                        dialog.dismiss();
                    });

                    dialog.getWindow().setAttributes(layoutParams);
                    dialog.getWindow().setBackgroundDrawable(DashboardActivity.this.getDrawable(R.drawable.background_transparent));
                    dialog.show();
                }, 600);
                break;
        }
        return false;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void designDrawer() {
        binding.navigationView.setElevation(0f);
        float radius = 42.0f;
        MaterialShapeDrawable materialShapeDrawable = (MaterialShapeDrawable) binding.navigationView.getBackground();
        materialShapeDrawable.setShapeAppearanceModel(materialShapeDrawable.getShapeAppearanceModel()
                .toBuilder()
                .setBottomRightCorner(CornerFamily.ROUNDED, radius)
                .build()
        );
        NavigationMenuView navigationMenuView = (NavigationMenuView) binding.navigationView.getChildAt(0);
        if (navigationMenuView != null) {
            navigationMenuView.setVerticalScrollBarEnabled(false);
        }

        menuNotifications = binding.navigationView.getMenu().findItem(R.id.nav_notifications);
        menuLocationMonitoring = binding.navigationView.getMenu().findItem(R.id.nav_location_monitoring);
        menuRealTimeLocation = binding.navigationView.getMenu().findItem(R.id.nav_real_time_location);
        menuAppsUsage = binding.navigationView.getMenu().findItem(R.id.nav_apps_usage);
        menuHealthFitness = binding.navigationView.getMenu().findItem(R.id.nav_health_and_fitness);
        menuLiveLock = binding.navigationView.getMenu().findItem(R.id.nav_live_lock);
        menuScreenRecordings = binding.navigationView.getMenu().findItem(R.id.nav_screen_recordings);
        menuScreenshots = binding.navigationView.getMenu().findItem(R.id.nav_screenshots);
        menuCallLogs = binding.navigationView.getMenu().findItem(R.id.nav_call_logs);
        menuSmsLogs = binding.navigationView.getMenu().findItem(R.id.nav_sms_logs);
        menuAccessCamera = binding.navigationView.getMenu().findItem(R.id.nav_access_camera);

        txtNotifications = (TextView) menuNotifications.getActionView();
        checkLocationMonitoring = (SwitchCompat) menuLocationMonitoring.getActionView();
        checkRealTimeLocation = (SwitchCompat) menuRealTimeLocation.getActionView();
        checkAppsUsage = (SwitchCompat) menuAppsUsage.getActionView();
        checkHealthFitness = (SwitchCompat) menuHealthFitness.getActionView();
        checkLiveLock = (SwitchCompat) menuLiveLock.getActionView();
        checkScreenRecordings = (SwitchCompat) menuScreenRecordings.getActionView();
        checkScreenshots = (SwitchCompat) menuScreenshots.getActionView();
        checkCallLogs = (SwitchCompat) menuCallLogs.getActionView();
        checkSmsLogs = (SwitchCompat) menuSmsLogs.getActionView();
        checkAccessCamera = (SwitchCompat) menuAccessCamera.getActionView();

        checkAppsUsage.setChecked(false);
        checkCallLogs.setChecked(false);
        checkHealthFitness.setChecked(true);
        checkLiveLock.setChecked(false);
        checkLocationMonitoring.setChecked(false);
        checkRealTimeLocation.setChecked(false);
        checkScreenshots.setChecked(false);
        checkScreenRecordings.setChecked(false);
        checkSmsLogs.setChecked(false);
        checkAccessCamera.setChecked(false);

        txtNotifications.setGravity(Gravity.CENTER_VERTICAL);
        txtNotifications.setTextSize(24f);
        txtNotifications.setTypeface(null, Typeface.BOLD);
        txtNotifications.setTextColor(getColor(R.color.blue_200));
        setDrawablesToSwitches(checkAppsUsage);
        setDrawablesToSwitches(checkCallLogs);
        setDrawablesToSwitches(checkHealthFitness);
        setDrawablesToSwitches(checkLiveLock);
        setDrawablesToSwitches(checkLocationMonitoring);
        setDrawablesToSwitches(checkRealTimeLocation);
        setDrawablesToSwitches(checkScreenshots);
        setDrawablesToSwitches(checkScreenRecordings);
        setDrawablesToSwitches(checkSmsLogs);
        setDrawablesToSwitches(checkAccessCamera);

        checkAppsUsage.setOnCheckedChangeListener((compoundButton, b) ->
                FirebaseDatabase.getInstance().getReference().child("features")
                        .child(Preferences.getInstance(DashboardActivity.this).getParent().getId())
                        .child("apps_and_usage")
                        .child("is_set")
                        .setValue("" + b)
        );
        checkCallLogs.setOnCheckedChangeListener((compoundButton, b) ->
                FirebaseDatabase.getInstance().getReference().child("features")
                        .child(Preferences.getInstance(DashboardActivity.this).getParent().getId())
                        .child("call_logs")
                        .child("is_set")
                        .setValue("" + b)
        );
        checkHealthFitness.setOnCheckedChangeListener((compoundButton, b) -> compoundButton.setChecked(true));
        checkLiveLock.setOnCheckedChangeListener((compoundButton, b) ->
                FirebaseDatabase.getInstance().getReference().child("features")
                        .child(Preferences.getInstance(DashboardActivity.this).getParent().getId())
                        .child("live_lock")
                        .child("is_set")
                        .setValue("" + b)
        );
        checkLocationMonitoring.setOnCheckedChangeListener((compoundButton, b) ->
                FirebaseDatabase.getInstance().getReference().child("features")
                        .child(Preferences.getInstance(DashboardActivity.this).getParent().getId())
                        .child("location_monitoring")
                        .child("is_set")
                        .setValue("" + b));
        checkRealTimeLocation.setOnCheckedChangeListener((compoundButton, b) ->
                FirebaseDatabase.getInstance().getReference().child("features")
                        .child(Preferences.getInstance(DashboardActivity.this).getParent().getId())
                        .child("real_time_location")
                        .child("is_set")
                        .setValue("" + b));
        checkScreenshots.setOnCheckedChangeListener((compoundButton, b) ->
                FirebaseDatabase.getInstance().getReference().child("features")
                        .child(Preferences.getInstance(DashboardActivity.this).getParent().getId())
                        .child("screen_capturing")
                        .child("is_set")
                        .setValue("" + b));
        checkScreenRecordings.setOnCheckedChangeListener((compoundButton, b) ->
                FirebaseDatabase.getInstance().getReference().child("features")
                        .child(Preferences.getInstance(DashboardActivity.this).getParent().getId())
                        .child("screen_recording")
                        .child("is_set")
                        .setValue("" + b));
        checkSmsLogs.setOnCheckedChangeListener((compoundButton, b) ->
                FirebaseDatabase.getInstance().getReference().child("features")
                        .child(Preferences.getInstance(DashboardActivity.this).getParent().getId())
                        .child("sms_logs")
                        .child("is_set")
                        .setValue("" + b));
        checkAccessCamera.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                // Show Dialog
                final Dialog dialog = new Dialog(DashboardActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.layout_dialog_camera_settings);
                dialog.setCancelable(true);

                final ChipGroup chipGroupCameraSide = dialog.findViewById(R.id.chipGroupCameraSide);
                final ChipGroup chipGroupCameraResolution = dialog.findViewById(R.id.chipGroupCameraResolution);
                final ChipGroup chipGroupImageFormat = dialog.findViewById(R.id.chipGroupImageFormat);
                final ChipGroup chipGroupCameraRotation = dialog.findViewById(R.id.chipGroupCameraRotation);

                final String[] side = new String[1];
                final String[] resolution = new String[1];
                final String[] format = new String[1];
                final String[] rotation = new String[1];

                // Defaults
                side[0] = "Front";
                resolution[0] = "High";
                format[0] = "JPEG";
                rotation[0] = "0";

                chipGroupCameraSide.setOnCheckedChangeListener((group, checkedId) -> {
                    if (R.id.sideFront == checkedId) {
                        side[0] = "Front";
                    } else if (R.id.sideBack == checkedId) {
                        side[0] = "Back";
                    } else {
                        side[0] = "Front";
                    }
                });
                chipGroupCameraResolution.setOnCheckedChangeListener((group, checkedId) -> {
                    if (checkedId == R.id.resolutionHigh) {
                        resolution[0] = "High";
                    } else if (checkedId == R.id.resolutionMedium) {
                        resolution[0] = "Medium";
                    } else if (checkedId == R.id.resolutionLow) {
                        resolution[0] = "Low";
                    } else {
                        resolution[0] = "High";
                    }
                });
                chipGroupImageFormat.setOnCheckedChangeListener((group, checkedId) -> {
                    if (checkedId == R.id.formatJPEG) {
                        format[0] = "JPEG";
                    } else if (checkedId == R.id.formatPNG) {
                        format[0] = "PNG";
                    } else if (checkedId == R.id.formatWEBP) {
                        format[0] = "WEBP";
                    } else {
                        format[0] = "JPEG";
                    }
                });
                chipGroupCameraRotation.setOnCheckedChangeListener((group, checkedId) -> {
                    if (checkedId == R.id.rotation0) {
                        rotation[0] = "0";
                    } else if (checkedId == R.id.rotation90) {
                        rotation[0] = "90";
                    } else if (checkedId == R.id.rotation180) {
                        rotation[0] = "180";
                    } else if (checkedId == R.id.rotation270) {
                        rotation[0] = "270";
                    } else {
                        rotation[0] = "0";
                    }
                });

                (dialog.findViewById(R.id.btnCancel)).setOnClickListener(v -> {
                    checkAccessCamera.setChecked(false);
                    dialog.dismiss();
                });
                (dialog.findViewById(R.id.btnAccessNow)).setOnClickListener(v -> {
                    Log.d(TAG, "SIDE : " + side[0]);
                    Log.d(TAG, "RESOLUTION : " + resolution[0]);
                    Log.d(TAG, "FORMAT : " + format[0]);
                    Log.d(TAG, "ROTATION : " + rotation[0]);

                    // Camera Settings
                    CameraSettingObject cameraSettingObject = new CameraSettingObject();
                    cameraSettingObject.setFormat(format[0]);
                    cameraSettingObject.setResolution(resolution[0]);
                    cameraSettingObject.setRotation(rotation[0]);
                    cameraSettingObject.setSide(side[0]);

                    FirebaseDatabase.getInstance().getReference().child("camera_settings")
                            .child(Preferences.getInstance(DashboardActivity.this).getParent().getId())
                            .setValue(cameraSettingObject)
                            .addOnCompleteListener(taskUpdateCameraSettings -> {
                                if (taskUpdateCameraSettings.isSuccessful()) {
                                    FirebaseDatabase.getInstance().getReference().child("features")
                                            .child(Preferences.getInstance(DashboardActivity.this).getParent().getId())
                                            .child("sync_hidden_camera")
                                            .child("is_set")
                                            .setValue("true");
                                    dialog.dismiss();
                                }
                            });
                });

                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

                dialog.getWindow().setAttributes(layoutParams);
                dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_transparent));
                dialog.show();
            } else {
                FirebaseDatabase.getInstance().getReference().child("features")
                        .child(Preferences.getInstance(DashboardActivity.this).getParent().getId())
                        .child("sync_hidden_camera")
                        .child("is_set")
                        .setValue("false");
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setDrawablesToSwitches(SwitchCompat switchCompat) {
        switchCompat.setThumbDrawable(getDrawable(R.drawable.switch_thumb));
        switchCompat.setTrackDrawable(getDrawable(R.drawable.switch_track));
    }

    private void setHeader() {
        binding.txtSubtitle.setText(parent.getName());
        headerView = getLayoutInflater().inflate(R.layout.layout_drawer_header, binding.navigationView, false);
        binding.navigationView.addHeaderView(headerView);

        TextView txtUserName = headerView.findViewById(R.id.txtUserName);
        TextView txtUserEmail = headerView.findViewById(R.id.txtUserEmail);
        CircleImageView imgLogo = headerView.findViewById(R.id.imgLogo);

        FirebaseDatabase.getInstance().getReference().child("parents")
                .child(Preferences.getInstance(DashboardActivity.this).getParent().getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            Parent obtainedParent = snapshot.getValue(Parent.class);
                            if (obtainedParent != null) {
                                txtUserName.setText(obtainedParent.getName());
                                txtUserEmail.setText(obtainedParent.getEmail());
                                Glide.with(getApplicationContext()).load(obtainedParent.getImg_url()).into(imgLogo);
                                Glide.with(getApplicationContext()).load(obtainedParent.getImg_url()).into(binding.imgProfile);
                                headerView.requestLayout();
                                headerView.invalidate();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        txtUserName.setText(Preferences.getInstance(DashboardActivity.this).getParent().getName());
                        txtUserEmail.setText(Preferences.getInstance(DashboardActivity.this).getParent().getEmail());
                        Glide.with(getApplicationContext()).load(Preferences.getInstance(DashboardActivity.this).getParent().getImg_url()).into(imgLogo);
                        Glide.with(getApplicationContext()).load(Preferences.getInstance(DashboardActivity.this).getParent().getImg_url()).into(binding.imgProfile);
                        headerView.requestLayout();
                        headerView.invalidate();
                    }
                });
    }

    private void setFragment(Fragment fragment, String tag) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.layoutContent);

        if (currentFragment != null) {
            if (currentFragment.getClass() == fragment.getClass()) {
                return;
            }
            new Handler().postDelayed(() -> getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).remove(currentFragment).replace(R.id.layoutContent, fragment, tag).commit(), 600);
        } else {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.layoutContent, fragment, tag).commit();
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.layoutContent);

        if (currentFragment != null) {
            if (currentFragment.getTag() != null) {
                if (!currentFragment.getTag().equals("Home")) {
                    binding.navigationView.setCheckedItem(R.id.nav_home);
                    onNavigationItemSelected(Objects.requireNonNull(binding.navigationView.getCheckedItem()));
                } else {
                    Dialog dialog = new Dialog(DashboardActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.layout_dialog_exit);
                    dialog.setCancelable(true);

                    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                    layoutParams.copyFrom(dialog.getWindow().getAttributes());
                    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

                    (dialog.findViewById(R.id.btnCancel)).setOnClickListener(v -> {
                        dialog.dismiss();
                    });
                    (dialog.findViewById(R.id.btnExitNow)).setOnClickListener(v -> {
                        super.onBackPressed();
                        dialog.dismiss();
                    });

                    dialog.getWindow().setAttributes(layoutParams);
                    dialog.getWindow().setBackgroundDrawable(DashboardActivity.this.getDrawable(R.drawable.background_transparent));
                    dialog.show();
                }
            }
        }
    }

    private void updateChecks(List<FeatureObject> featureList, int count) {
        if (featureList.size() >= 11) {
            checkAppsUsage.setChecked(featureList.get(0).getIs_set().equals("true"));
            //checkCallLogs.setChecked(featureList.get(1).getIs_set().equals("true"));
            checkHealthFitness.setChecked(true);
            checkLiveLock.setChecked(featureList.get(3).getIs_set().equals("true"));
            checkLocationMonitoring.setChecked(featureList.get(4).getIs_set().equals("true"));
            checkRealTimeLocation.setChecked(featureList.get(5).getIs_set().equals("true"));
            checkScreenshots.setChecked(featureList.get(6).getIs_set().equals("true"));
            checkScreenRecordings.setChecked(featureList.get(7).getIs_set().equals("true"));
            checkSmsLogs.setChecked(featureList.get(8).getIs_set().equals("true"));
            checkAccessCamera.setChecked(featureList.get(10).getIs_set().equals("true"));
        }
        if (count > 0) {
            txtNotifications.setText("\u25CF");
        } else {
            txtNotifications.setText("");
        }
    }

    private void getFeaturesData() {
        featureObjects = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("features")
                .child(Preferences.getInstance(DashboardActivity.this).getParent().getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        featureObjects.clear();

                        if (snapshot.getValue() != null) {
                            if (snapshot.getChildrenCount() != 0) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    FeatureObject featureObject = dataSnapshot.getValue(FeatureObject.class);

                                    if (featureObject != null) {
                                        featureObjects.add(featureObject);
                                    }
                                }
                                // Get Notifications Count
                                FirebaseDatabase.getInstance().getReference().child("notifications_counter")
                                        .child(Preferences.getInstance(DashboardActivity.this).getParent().getId())
                                        .child("count")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.getValue() != null) {
                                                    int notificationsCount = snapshot.getValue(Integer.class);
                                                    updateChecks(featureObjects, notificationsCount);
                                                } else {
                                                    updateChecks(featureObjects, 0);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                updateChecks(featureObjects, 0);
                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
}