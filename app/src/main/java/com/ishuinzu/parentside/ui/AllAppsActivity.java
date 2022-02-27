package com.ishuinzu.parentside.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ishuinzu.parentside.R;
import com.ishuinzu.parentside.adapter.AppAdapter;
import com.ishuinzu.parentside.animation.PushDownAnimation;
import com.ishuinzu.parentside.app.Preferences;
import com.ishuinzu.parentside.databinding.ActivityAllAppsBinding;
import com.ishuinzu.parentside.dialog.LoadingDialog;
import com.ishuinzu.parentside.object.AppObject;

import java.util.ArrayList;
import java.util.List;

public class AllAppsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AllAppsActivity";
    private ActivityAllAppsBinding binding;
    private LinearLayoutManager linearLayoutManager;
    private AppAdapter appAdapter;
    private List<AppObject> appObjects;
    private List<AppObject> appObjectsAllowed;
    private List<AppObject> appObjectsBlocked;

    private boolean loading = true;
    int pastVisibleItems, visibleItemCount, totalItemCount;

    private int total_apps;
    private int blocked_apps;
    private int allowed_apps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllAppsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        loadApps("Allowed");

        PushDownAnimation.setPushDownAnimationTo(binding.txtAllowed).setOnClickListener(this);
        PushDownAnimation.setPushDownAnimationTo(binding.txtBlocked).setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                onBackPressed();
                break;

            case R.id.txtAllowed:
                loadApps("Allowed");

                // Change Colors
                binding.txtAllowed.setTextColor(getColor(R.color.white));
                binding.txtBlocked.setTextColor(getColor(R.color.normalColor));
                break;

            case R.id.txtBlocked:
                loadApps("Blocked");

                // Change Colors
                binding.txtAllowed.setTextColor(getColor(R.color.normalColor));
                binding.txtBlocked.setTextColor(getColor(R.color.white));
                break;
        }
    }

    private void loadApps(String type) {
        appObjects = new ArrayList<>();
        appObjectsAllowed = new ArrayList<>();
        appObjectsBlocked = new ArrayList<>();

        switch (type) {
            case "Allowed":
                appAdapter = new AppAdapter(AllAppsActivity.this, appObjectsAllowed);
                break;

            case "Blocked":
                appAdapter = new AppAdapter(AllAppsActivity.this, appObjectsBlocked);
                break;
        }

        linearLayoutManager = new LinearLayoutManager(AllAppsActivity.this);
        binding.recyclerApps.setLayoutManager(linearLayoutManager);
        binding.recyclerApps.setAdapter(appAdapter);
        binding.recyclerApps.addOnScrollListener(onScrollListener);

        getApps();
    }

    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            if (dy > 0) {
                visibleItemCount = linearLayoutManager.getChildCount();
                totalItemCount = linearLayoutManager.getItemCount();
                pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    loading = false;

                    // Load More
                    linearLayoutManager.setStackFromEnd(true);

                    loading = true;
                }
            }
        }
    };

    private void getApps() {
        LoadingDialog.showLoadingDialog(AllAppsActivity.this);
        FirebaseDatabase.getInstance().getReference().child("apps")
                .child(Preferences.getInstance(AllAppsActivity.this).getParent().getId())
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount() != 0) {
                            appObjects.clear();
                            appObjectsAllowed.clear();
                            appObjectsBlocked.clear();
                            total_apps = 0;
                            blocked_apps = 0;
                            allowed_apps = 0;

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (dataSnapshot.getValue() != null) {
                                    AppObject appObject = dataSnapshot.getValue(AppObject.class);

                                    if (appObject != null) {
                                        appObjects.add(appObject);
                                        total_apps++;

                                        if (appObject.getIs_selected_lock().equals("true")) {
                                            appObjectsBlocked.add(appObject);
                                            blocked_apps++;
                                        } else if (appObject.getIs_selected_lock().equals("false")) {
                                            appObjectsAllowed.add(appObject);
                                            allowed_apps++;
                                        }
                                    }
                                }
                            }
                            updateProgresses(total_apps, allowed_apps, blocked_apps);
                            appAdapter.notifyDataSetChanged();
                            LoadingDialog.closeDialog();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        LoadingDialog.closeDialog();
                    }
                });
    }

    private void updateProgresses(int all, int allowed, int blocked) {
        binding.progressTotalApps.setProgress(all, (all * 1.5));
        binding.progressAllowed.setProgress(allowed, all);
        binding.progressBlocked.setProgress(blocked, all);
    }
}