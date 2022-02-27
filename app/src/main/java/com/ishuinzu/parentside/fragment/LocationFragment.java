package com.ishuinzu.parentside.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ishuinzu.parentside.R;
import com.ishuinzu.parentside.adapter.ChildLocationAdapter;
import com.ishuinzu.parentside.app.Preferences;
import com.ishuinzu.parentside.databinding.FragmentLocationBinding;
import com.ishuinzu.parentside.object.ChildDevice;
import com.ishuinzu.parentside.object.ChildLocation;
import com.ishuinzu.parentside.ui.NewChildLocationActivity;

import java.util.ArrayList;
import java.util.List;

public class LocationFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "LocationFragment";
    private FragmentLocationBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ChildDevice childDevice;
    private List<ChildLocation> childLocations;
    private ChildLocationAdapter childLocationAdapter;
    private String mParam1;
    private String mParam2;

    public LocationFragment() {
    }

    public static LocationFragment newInstance(String param1, String param2) {
        LocationFragment fragment = new LocationFragment();
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
        binding = FragmentLocationBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
    }

    private void init() {
        childDevice = Preferences.getInstance(getContext()).getChildDevice();
        binding.btnAddChildLocation.setOnClickListener(this);

        loadTimeBasedLocations();
    }

    private void getChildLocations() {
        FirebaseDatabase.getInstance().getReference()
                .child("locations")
                .child(Preferences.getInstance(getContext()).getParent().getId())
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount() != 0) {
                            if (childLocations != null) {
                                childLocations.clear();
                            }
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                ChildLocation childLocation = dataSnapshot.getValue(ChildLocation.class);
                                childLocations.add(childLocation);
                            }
                            childLocationAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddChildLocation:
                startActivity(new Intent(getContext(), NewChildLocationActivity.class));
                break;
        }
    }

    private void loadTimeBasedLocations() {
        childLocations = new ArrayList<>();
        childLocationAdapter = new ChildLocationAdapter(getContext(), childLocations);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        binding.recyclerTimeBasedLocations.setAdapter(childLocationAdapter);
        binding.recyclerTimeBasedLocations.setLayoutManager(linearLayoutManager);

        getChildLocations();
    }
}