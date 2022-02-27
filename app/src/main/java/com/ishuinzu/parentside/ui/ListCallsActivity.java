package com.ishuinzu.parentside.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ishuinzu.parentside.adapter.CallAdapter;
import com.ishuinzu.parentside.app.Preferences;
import com.ishuinzu.parentside.app.Utils;
import com.ishuinzu.parentside.databinding.ActivityListCallsBinding;
import com.ishuinzu.parentside.dialog.LoadingDialog;
import com.ishuinzu.parentside.object.CallObject;

import java.util.ArrayList;
import java.util.List;

public class ListCallsActivity extends AppCompatActivity {
    private static final String TAG = "ListCallsActivity";
    private ActivityListCallsBinding binding;
    private String address;
    private String id;
    private List<CallObject> callObjects;
    private CallAdapter callAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListCallsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        binding.btnBack.setOnClickListener(view -> finish());

        if (getIntent().getExtras() != null) {
            address = getIntent().getExtras().getString("ADDRESS");
            id = Utils.getDateID();
        }

        binding.txtTitle.setText(address);
        loadCalls(address, id);
    }

    private void loadCalls(String address, String id) {
        callObjects = new ArrayList<>();
        callAdapter = new CallAdapter(ListCallsActivity.this, callObjects);
        binding.recyclerCalls.setAdapter(callAdapter);

        getCalls(address, id);
    }

    private void getCalls(String address, String id) {
        LoadingDialog.showLoadingDialog(ListCallsActivity.this);
        FirebaseDatabase.getInstance().getReference().child("calls")
                .child(Preferences.getInstance(ListCallsActivity.this).getParent().getId())
                .child(id)
                .child(address)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        callObjects.clear();

                        if (snapshot.getChildrenCount() != 0) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                CallObject callObject = dataSnapshot.getValue(CallObject.class);

                                if (callObject != null) {
                                    callObjects.add(callObject);
                                }
                            }
                        }
                        callAdapter.notifyDataSetChanged();
                        LoadingDialog.closeDialog();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        LoadingDialog.closeDialog();
                    }
                });
    }
}