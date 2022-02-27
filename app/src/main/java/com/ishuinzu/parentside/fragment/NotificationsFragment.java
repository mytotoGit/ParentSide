package com.ishuinzu.parentside.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ishuinzu.parentside.adapter.NotificationAdapter;
import com.ishuinzu.parentside.app.Preferences;
import com.ishuinzu.parentside.app.Utils;
import com.ishuinzu.parentside.databinding.FragmentNotificationsBinding;
import com.ishuinzu.parentside.dialog.LoadingDialog;
import com.ishuinzu.parentside.object.NotificationObject;
import com.ishuinzu.parentside.object.Parent;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {
    private static final String TAG = "NotificationsFragment";
    private FragmentNotificationsBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String dateID;
    private Parent parent;
    private List<NotificationObject> notificationObjects;
    private NotificationAdapter notificationAdapter;
    private String mParam1;
    private String mParam2;

    public NotificationsFragment() {
    }

    public static NotificationsFragment newInstance(String param1, String param2) {
        NotificationsFragment fragment = new NotificationsFragment();
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
        binding = FragmentNotificationsBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init();
    }

    private void init() {
        dateID = Utils.getDateID();
        parent = Preferences.getInstance(getContext()).getParent();
        binding.calendarView.setOnDateChangeListener((calendarView, i, i1, i2) -> {
            dateID = Utils.getValueInDoubleFigure(i1 + 1) + Utils.getValueInDoubleFigure(i2) + Utils.getValueInDoubleFigure(i);
            getNotifications(dateID);
        });

        // Set Firebase Count 0
        FirebaseDatabase.getInstance().getReference().child("notifications_counter").child(parent.getId()).child("count").setValue(0);
        loadNotifications();
    }

    private void loadNotifications() {
        notificationObjects = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(getContext(), notificationObjects);
        binding.recyclerNotifications.setAdapter(notificationAdapter);

        getNotifications(dateID);
    }

    private void getNotifications(String dateID) {
        LoadingDialog.showLoadingDialog(getContext());
        FirebaseDatabase.getInstance().getReference().child("notifications")
                .child(Preferences.getInstance(getContext()).getParent().getId())
                .child(dateID)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        notificationObjects.clear();

                        if (snapshot.getChildrenCount() != 0) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (dataSnapshot != null) {
                                    NotificationObject notificationObject = dataSnapshot.getValue(NotificationObject.class);

                                    if (notificationObject != null) {
                                        notificationObjects.add(notificationObject);
                                    }
                                }
                            }
                        }
                        notificationAdapter.notifyDataSetChanged();
                        LoadingDialog.closeDialog();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        LoadingDialog.closeDialog();
                    }
                });
    }
}