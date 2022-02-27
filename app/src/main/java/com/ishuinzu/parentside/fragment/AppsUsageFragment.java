package com.ishuinzu.parentside.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ishuinzu.parentside.R;
import com.ishuinzu.parentside.adapter.AppUsageAdapter;
import com.ishuinzu.parentside.animation.PushDownAnimation;
import com.ishuinzu.parentside.app.Preferences;
import com.ishuinzu.parentside.app.Utils;
import com.ishuinzu.parentside.databinding.FragmentAppsUsageBinding;
import com.ishuinzu.parentside.dialog.LoadingDialog;
import com.ishuinzu.parentside.object.AppObject;
import com.ishuinzu.parentside.object.AppUsageObject;
import com.ishuinzu.parentside.ui.AllAppsActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class AppsUsageFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "AppUsageFragment";
    private FragmentAppsUsageBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String dateID;
    private LinearLayoutManager linearLayoutManager;
    private AppUsageAdapter appUsageAdapter;
    private List<AppUsageObject> appUsageObjects;
    private boolean loading = true;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    // Total Usage Time
    private long total_usage = 0;
    // Links List
    private List<String> linksObjects;
    private String mParam1;
    private String mParam2;

    public AppsUsageFragment() {
    }

    public static AppsUsageFragment newInstance(String param1, String param2) {
        AppsUsageFragment fragment = new AppsUsageFragment();
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
        binding = FragmentAppsUsageBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
    }

    private void init() {
        PushDownAnimation.setPushDownAnimationTo(binding.btnAllApps).setOnClickListener(this);
        PushDownAnimation.setPushDownAnimationTo(binding.btnGetDate).setOnClickListener(this);

        dateID = Utils.getDateID();
        updateViews(dateID);
        loadAppUsages();
    }

    @SuppressLint("SetTextI18n")
    private void updateViews(String dateID) {
        binding.txtSelectedDate.setText(dateID.substring(0, 2) + "/" + dateID.substring(2, 4) + "/" + dateID.substring(4));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAllApps:
                startActivity(new Intent(getContext(), AllAppsActivity.class));
                break;

            case R.id.btnGetDate:
                showCalendarDialog();
                break;
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void showCalendarDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_calendar);
        dialog.setCancelable(true);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        CalendarView calendarView = dialog.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener((calendarView1, i, i1, i2) -> {
            dateID = Utils.getValueInDoubleFigure(i1 + 1) + Utils.getValueInDoubleFigure(i2) + Utils.getValueInDoubleFigure(i);
            binding.txtSelectedDate.setText(dateID);
            updateViews(dateID);
            getAppUsages(dateID);
            dialog.dismiss();
        });

        dialog.getWindow().setAttributes(layoutParams);
        dialog.getWindow().setBackgroundDrawable(requireContext().getDrawable(R.drawable.background_transparent));
        dialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadAppUsages() {
        appUsageObjects = new ArrayList<>();
        linksObjects = new ArrayList<>();

        appUsageAdapter = new AppUsageAdapter(getContext(), appUsageObjects);
        appUsageAdapter.notifyDataSetChanged();
        linearLayoutManager = new LinearLayoutManager(getContext());
        binding.recyclerAppUsages.setLayoutManager(linearLayoutManager);
        binding.recyclerAppUsages.setAdapter(appUsageAdapter);
        binding.recyclerAppUsages.addOnScrollListener(onScrollListener);

        getAppUsages(dateID);
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

    private void getAppUsages(String dateID) {
        LoadingDialog.showLoadingDialog(getContext());
        FirebaseDatabase.getInstance().getReference()
                .child("app_usages")
                .child(Preferences.getInstance(getContext()).getParent().getId())
                .child(dateID)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        appUsageObjects.clear();
                        linksObjects.clear();
                        total_usage = 0;

                        if (snapshot.getChildrenCount() != 0) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (dataSnapshot.getValue() != null) {
                                    AppUsageObject appUsageObject = dataSnapshot.getValue(AppUsageObject.class);
                                    if (appUsageObject != null) {
                                        appUsageObjects.add(appUsageObject);

                                        // Get Icon Link
                                        FirebaseDatabase.getInstance().getReference()
                                                .child("apps")
                                                .child(Preferences.getInstance(getContext()).getParent().getId())
                                                .child(appUsageObject.getName())
                                                .get()
                                                .addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        if (task.getResult() != null) {
                                                            AppObject appObject = task.getResult().getValue(AppObject.class);
                                                            if (appObject != null) {
                                                                linksObjects.add(appObject.getIcon_link());
                                                            }

                                                            // Add In Total Usage
                                                            total_usage = total_usage + appUsageObject.getUsage_time();
                                                            // Updates
                                                            appUsageAdapter.setLinksObjects(linksObjects);
                                                        } else {
                                                            // Add In Total Usage
                                                            total_usage = total_usage + appUsageObject.getUsage_time();
                                                        }
                                                    } else {
                                                        // Add In Total Usage
                                                        total_usage = total_usage + appUsageObject.getUsage_time();
                                                    }
                                                    appUsageAdapter.setTotal_usage(total_usage);
                                                    updateUsage(total_usage);
                                                });
                                    }
                                }
                            }
                        } else {
                            updateUsage(0);
                            Toasty.error(requireContext(), "No Record Found", Toasty.LENGTH_SHORT).show();
                        }
                        appUsageAdapter.notifyDataSetChanged();
                        updateTopUsageApp(appUsageObjects);
                        LoadingDialog.closeDialog();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        LoadingDialog.closeDialog();
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void updateUsage(long total_usage) {
        binding.progressTotalUsage.setProgress(total_usage, 86400000);
        binding.progressTotalUsage.setTextColor(requireContext().getColor(R.color.background));
        binding.txtUsageTime.setText(String.format(getString(R.string.total), Utils.formatMilliSeconds(total_usage)));
        binding.txtUsageTime.setVisibility(View.VISIBLE);
    }

    private void updateTopUsageApp(List<AppUsageObject> appUsageObjects) {
        AppUsageObject topAppUsageObject;
        List<Long> appUsages = new ArrayList<>();

        if (appUsageObjects.size() != 0) {
            for (int i = 0; i < appUsageObjects.size(); i++) {
                appUsages.add(appUsageObjects.get(i).getUsage_time());
            }
            final long maxAppUsage = Collections.max(appUsages);
            for (int i = 0; i < appUsageObjects.size(); i++) {
                if (appUsageObjects.get(i).getUsage_time() == maxAppUsage) {
                    topAppUsageObject = appUsageObjects.get(i);
                    updateTopUsageAppUI(topAppUsageObject);
                    break;
                }
            }
        }
    }

    private void updateTopUsageAppUI(AppUsageObject topAppUsageObject) {
        binding.txtTopAppName.setText(topAppUsageObject.getName());
        FirebaseDatabase.getInstance().getReference().child("apps")
                .child(Preferences.getInstance(getContext()).getParent().getId())
                .child(topAppUsageObject.getName())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        AppObject appObject = task.getResult().getValue(AppObject.class);

                        if (appObject != null) {
                            Glide.with(requireContext()).load(appObject.getIcon_link()).into(binding.topAppImage);
                        }
                    }
                });
    }
}