package com.ishuinzu.parentside.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ishuinzu.parentside.adapter.SMSAdapter;
import com.ishuinzu.parentside.app.Preferences;
import com.ishuinzu.parentside.app.Utils;
import com.ishuinzu.parentside.databinding.ActivityListMessagesBinding;
import com.ishuinzu.parentside.dialog.LoadingDialog;
import com.ishuinzu.parentside.object.SMSObject;

import java.util.ArrayList;
import java.util.List;

public class ListMessagesActivity extends AppCompatActivity {
    private static final String TAG = "ListMessagesActivity";
    private ActivityListMessagesBinding binding;
    private String address;
    private String id;
    private List<SMSObject> smsObjects;
    private SMSAdapter smsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListMessagesBinding.inflate(getLayoutInflater());
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
        loadSMSs(address, id);
    }

    private void loadSMSs(String address, String id) {
        smsObjects = new ArrayList<>();
        smsAdapter = new SMSAdapter(ListMessagesActivity.this, smsObjects);
        binding.recyclerSMSs.setAdapter(smsAdapter);

        getSMSs(address, id);
    }

    private void getSMSs(String address, String id) {
        LoadingDialog.showLoadingDialog(ListMessagesActivity.this);
        FirebaseDatabase.getInstance().getReference().child("sms")
                .child(Preferences.getInstance(ListMessagesActivity.this).getParent().getId())
                .child(id)
                .child(address)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        smsObjects.clear();

                        if (snapshot.getChildrenCount() != 0) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                SMSObject smsObject = dataSnapshot.getValue(SMSObject.class);

                                if (smsObject != null) {
                                    smsObjects.add(smsObject);
                                }
                            }
                        }
                        smsAdapter.notifyDataSetChanged();
                        LoadingDialog.closeDialog();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        LoadingDialog.closeDialog();
                    }
                });
    }
}