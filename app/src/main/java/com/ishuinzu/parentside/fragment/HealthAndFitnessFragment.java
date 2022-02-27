package com.ishuinzu.parentside.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ishuinzu.parentside.app.Preferences;
import com.ishuinzu.parentside.app.Utils;
import com.ishuinzu.parentside.databinding.FragmentHealthAndFitnessBinding;
import com.ishuinzu.parentside.dialog.LoadingDialog;
import com.ishuinzu.parentside.object.Parent;
import com.ishuinzu.parentside.object.StepCounterObject;

public class HealthAndFitnessFragment extends Fragment {
    private static final String TAG = "HealthAndFitnessFragment";
    private FragmentHealthAndFitnessBinding binding;
    private String dateID;
    private Parent parent;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public HealthAndFitnessFragment() {
    }

    public static HealthAndFitnessFragment newInstance(String param1, String param2) {
        HealthAndFitnessFragment fragment = new HealthAndFitnessFragment();
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
        binding = FragmentHealthAndFitnessBinding.inflate(inflater);
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
            getStepCounterData(dateID, parent.getId());
        });

        getStepCounterData(dateID, parent.getId());
    }

    private void getStepCounterData(String dateID, String parentID) {
        LoadingDialog.showLoadingDialog(getContext());
        // Default
        binding.progressStepCounts.setProgress(0, 0);
        binding.txtTimeCalculated.setText("---");
        binding.txtCaloriesCalculated.setText("---");
        binding.txtDistanceCalculated.setText("---");
        binding.txtSpeedCalculated.setText("---");

        // Update Firebase Values
        DatabaseReference stepCountReference = FirebaseDatabase.getInstance().getReference().child("step_counter").child(parentID).child(dateID);
        stepCountReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    StepCounterObject stepCounter = snapshot.getValue(StepCounterObject.class);

                    if (stepCounter != null) {
                        binding.progressStepCounts.setProgress(Integer.parseInt(stepCounter.getCount().substring(0, stepCounter.getCount().length() - 6)), 1000);
                        binding.txtTimeCalculated.setText(stepCounter.getTime());
                        binding.txtCaloriesCalculated.setText(stepCounter.getCalories());
                        binding.txtDistanceCalculated.setText(stepCounter.getDistance());
                        binding.txtSpeedCalculated.setText(stepCounter.getSpeed());
                    } else {
                        binding.progressStepCounts.setProgress(0, 1000);
                        binding.txtTimeCalculated.setText("");
                        binding.txtCaloriesCalculated.setText("");
                        binding.txtDistanceCalculated.setText("");
                        binding.txtSpeedCalculated.setText("");
                    }
                }
                LoadingDialog.closeDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.progressStepCounts.setProgress(0, 1000);
                binding.txtTimeCalculated.setText("");
                binding.txtCaloriesCalculated.setText("");
                binding.txtDistanceCalculated.setText("");
                binding.txtSpeedCalculated.setText("");
                LoadingDialog.closeDialog();
            }
        });
    }
}