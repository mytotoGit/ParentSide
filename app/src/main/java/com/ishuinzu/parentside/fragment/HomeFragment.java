package com.ishuinzu.parentside.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ishuinzu.parentside.R;
import com.ishuinzu.parentside.animation.PushDownAnimation;
import com.ishuinzu.parentside.app.Preferences;
import com.ishuinzu.parentside.databinding.FragmentHomeBinding;
import com.ishuinzu.parentside.object.Parent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Parent parent;
    // Notifications
    private int notificationsCount;

    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        parent = Preferences.getInstance(getContext()).getParent();
        if (getActivity() != null) {
            drawerLayout = getActivity().findViewById(R.id.layoutDrawer);
            navigationView = getActivity().findViewById(R.id.navigationView);
        }
        binding.switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                new Handler().postDelayed(() -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES), 1000);
            } else {
                new Handler().postDelayed(() -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO), 1000);
            }
        });

        binding.cardNotifications.setOnClickListener(this);
        binding.iconNotifications.setOnClickListener(this);
        PushDownAnimation.setPushDownAnimationTo(binding.cardLocation).setOnClickListener(this);
        PushDownAnimation.setPushDownAnimationTo(binding.cardAppUsage).setOnClickListener(this);
        PushDownAnimation.setPushDownAnimationTo(binding.cardHealthFitness).setOnClickListener(this);
        PushDownAnimation.setPushDownAnimationTo(binding.cardLiveLock).setOnClickListener(this);
        PushDownAnimation.setPushDownAnimationTo(binding.cardScreenRecording).setOnClickListener(this);
        PushDownAnimation.setPushDownAnimationTo(binding.cardScreenCapture).setOnClickListener(this);
        PushDownAnimation.setPushDownAnimationTo(binding.cardCallLogs).setOnClickListener(this);
        PushDownAnimation.setPushDownAnimationTo(binding.cardSMSLogs).setOnClickListener(this);
        PushDownAnimation.setPushDownAnimationTo(binding.cardAccessCamera).setOnClickListener(this);

        getNotificationsCount(parent.getId());
    }

    @SuppressLint({"StaticFieldLeak", "SetTextI18n"})
    private void getNotificationsCount(String parentID) {
        ExecutorService getNotificationsCountService = Executors.newSingleThreadExecutor();
        getNotificationsCountService.execute(() -> {
            // Background Task (NO UI)
            FirebaseDatabase.getInstance().getReference().child("notifications_counter")
                    .child(parentID)
                    .child("count")
                    .addValueEventListener(new ValueEventListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            notificationsCount = 0;

                            if (snapshot.getValue() != null) {
                                try {
                                    notificationsCount = snapshot.getValue(Integer.class);

                                    if (notificationsCount != 0) {
                                        binding.layoutCounts.setVisibility(View.VISIBLE);
                                        binding.txtCounts.setText("" + notificationsCount);
                                    } else {
                                        binding.layoutCounts.setVisibility(View.GONE);
                                    }
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                    binding.layoutCounts.setVisibility(View.GONE);
                                }
                            } else {
                                notificationsCount = 0;
                                binding.layoutCounts.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            notificationsCount = 0;
                            binding.layoutCounts.setVisibility(View.GONE);
                        }
                    });
        });
    }

    private void setFragment(Fragment fragment, String tag) {
        Fragment currentFragment = getParentFragmentManager().findFragmentById(R.id.layoutContent);

        if (currentFragment != null) {
            if (currentFragment.getClass() == fragment.getClass()) {
                return;
            }
            new Handler().postDelayed(() -> getParentFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).remove(currentFragment).replace(R.id.layoutContent, fragment, tag).commit(), 600);
        } else {
            getParentFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.layoutContent, fragment, tag).commit();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cardLocation:
                if (drawerLayout != null) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                if (navigationView != null) {
                    navigationView.setCheckedItem(R.id.nav_location_monitoring);
                }
                setFragment(LocationFragment.newInstance("", ""), "LocationMonitoring");
                break;

            case R.id.iconNotifications:
            case R.id.cardNotifications:
                if (drawerLayout != null) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                if (navigationView != null) {
                    navigationView.setCheckedItem(R.id.nav_notifications);
                }
                setFragment(NotificationsFragment.newInstance("", ""), "Notifications");
                break;

            case R.id.cardHealthFitness:
                if (drawerLayout != null) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                if (navigationView != null) {
                    navigationView.setCheckedItem(R.id.nav_health_and_fitness);
                }
                setFragment(HealthAndFitnessFragment.newInstance("", ""), "HealthAndFitness");
                break;

            case R.id.cardLiveLock:
                if (drawerLayout != null) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                if (navigationView != null) {
                    navigationView.setCheckedItem(R.id.nav_live_lock);
                }
                setFragment(LiveLockFragment.newInstance("", ""), "LiveLock");
                break;

            case R.id.cardScreenRecording:
                if (drawerLayout != null) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                if (navigationView != null) {
                    navigationView.setCheckedItem(R.id.nav_screen_recordings);
                }
                setFragment(ScreenRecordingsFragment.newInstance("", ""), "ScreenRecordings");
                break;

            case R.id.cardScreenCapture:
                if (drawerLayout != null) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                if (navigationView != null) {
                    navigationView.setCheckedItem(R.id.nav_screenshots);
                }
                setFragment(ScreenshotsFragment.newInstance("", ""), "Screenshots");
                break;

            case R.id.cardCallLogs:
                if (drawerLayout != null) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                if (navigationView != null) {
                    navigationView.setCheckedItem(R.id.nav_call_logs);
                }
                setFragment(CallLogsFragment.newInstance("", ""), "CallLogs");
                break;

            case R.id.cardSMSLogs:
                if (drawerLayout != null) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                if (navigationView != null) {
                    navigationView.setCheckedItem(R.id.nav_sms_logs);
                }
                setFragment(SMSLogsFragment.newInstance("", ""), "SMSLogs");
                break;

            case R.id.cardAppUsage:
                if (drawerLayout != null) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                if (navigationView != null) {
                    navigationView.setCheckedItem(R.id.nav_apps_usage);
                }
                setFragment(AppsUsageFragment.newInstance("", ""), "AppsUsage");
                break;

            case R.id.cardAccessCamera:
                if (drawerLayout != null) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                if (navigationView != null) {
                    navigationView.setCheckedItem(R.id.nav_access_camera);
                }
                setFragment(ChildImagesFragment.newInstance("", ""), "ChildCameraImages");
                break;
        }
    }
}