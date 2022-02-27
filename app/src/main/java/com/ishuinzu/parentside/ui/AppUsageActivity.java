package com.ishuinzu.parentside.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hadiidbouk.charts.BarData;
import com.ishuinzu.parentside.app.Preferences;
import com.ishuinzu.parentside.databinding.ActivityAppUsageBinding;
import com.ishuinzu.parentside.object.AppUsageObject;

import java.util.ArrayList;
import java.util.List;

public class AppUsageActivity extends AppCompatActivity {
    private static final String TAG = "AppUsageActivity";
    private ActivityAppUsageBinding binding;
    private String app_name;
    private String app_icon_link;
    private String package_name;
    private List<String> keys;
    private List<Long> appUsages;
    private ArrayList<BarData> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppUsageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        if (getIntent().getExtras() != null) {
            app_name = getIntent().getExtras().getString("NAME");
            app_icon_link = getIntent().getExtras().getString("LINK");
            package_name = getIntent().getExtras().getString("PACKAGE");
        }

        binding.btnBack.setOnClickListener(view -> finish());
        binding.txtTitle.setText(app_name);

        updateUI();
    }

    private void updateUI() {
        keys = new ArrayList<>();
        appUsages = new ArrayList<>();
        dataList = new ArrayList<>();

        getAppUsages();
    }

    private void getAppUsages() {
        FirebaseDatabase.getInstance().getReference().child("app_usages")
                .child(Preferences.getInstance(AppUsageActivity.this).getParent().getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        keys.clear();

                        if (snapshot.getChildrenCount() != 0) {
                            for (DataSnapshot keySnapshot : snapshot.getChildren()) {
                                keys.add(keySnapshot.getKey());
                            }
                            updateUsages(keys);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void updateUsages(List<String> keys) {
        for (int i = 0; i < keys.size(); i++) {
            Log.d(TAG, keys.get(i));
            int finalI = i;
            FirebaseDatabase.getInstance().getReference().child("app_usages")
                    .child(Preferences.getInstance(AppUsageActivity.this).getParent().getId())
                    .child(keys.get(i))
                    .child(app_name)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            AppUsageObject appUsageObject = snapshot.getValue(AppUsageObject.class);

                            if (appUsageObject != null) {
                                appUsages.add(appUsageObject.getUsage_time());
                                dataList.add(new BarData(keys.get(finalI).substring(2, 4) + "/" + keys.get(finalI).substring(0, 2), (appUsageObject.getUsage_time() / 1000f / 60f), "" + (appUsageObject.getUsage_time() / 1000f / 60f) + " min"));

                                if (finalI == keys.size() - 1) {
                                    Log.d(TAG, "Size : " + dataList.size());
                                    binding.chartProgressBar.setDataList(dataList);
                                    binding.chartProgressBar.build();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
        }
    }
}