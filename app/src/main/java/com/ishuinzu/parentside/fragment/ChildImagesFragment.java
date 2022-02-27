package com.ishuinzu.parentside.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ishuinzu.parentside.R;
import com.ishuinzu.parentside.adapter.ChildImageAdapter;
import com.ishuinzu.parentside.animation.PushDownAnimation;
import com.ishuinzu.parentside.app.Preferences;
import com.ishuinzu.parentside.app.Utils;
import com.ishuinzu.parentside.databinding.FragmentChildImagesBinding;
import com.ishuinzu.parentside.dialog.LoadingDialog;
import com.ishuinzu.parentside.object.CameraImageObject;
import com.ishuinzu.parentside.object.Parent;

import java.util.ArrayList;
import java.util.List;

public class ChildImagesFragment extends Fragment {
    private static final String TAG = "ChildImagesFragment";
    private FragmentChildImagesBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private List<CameraImageObject> cameraImageObjects;
    private ChildImageAdapter childImageAdapter;
    private String dateID;
    private Parent parent;

    public ChildImagesFragment() {
    }

    public static ChildImagesFragment newInstance(String param1, String param2) {
        ChildImagesFragment fragment = new ChildImagesFragment();
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
        binding = FragmentChildImagesBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init();
    }

    private void init() {
        // Click Listener
        PushDownAnimation.setPushDownAnimationTo(binding.btnGetDate).setOnClickListener(view -> showCalendarDialog());

        dateID = Utils.getDateID();
        parent = Preferences.getInstance(getContext()).getParent();
        updateDate(dateID, parent.getId());
    }

    @SuppressLint("SetTextI18n")
    private void updateDate(String id, String parentID) {
        binding.txtSelectedDate.setText(id.substring(0, 2) + "/" + id.substring(2, 4) + "/" + id.substring(4));

        LoadingDialog.showLoadingDialog(getContext());
        loadLiveLocks(id, parentID);
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
            updateDate(dateID, parent.getId());
            dialog.dismiss();
        });

        dialog.getWindow().setAttributes(layoutParams);
        dialog.getWindow().setBackgroundDrawable(requireContext().getDrawable(R.drawable.background_transparent));
        dialog.show();
    }

    private void loadLiveLocks(String id, String parentID) {
        cameraImageObjects = new ArrayList<>();
        childImageAdapter = new ChildImageAdapter(getContext(), cameraImageObjects);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        binding.recyclerChildImages.setLayoutManager(linearLayoutManager);
        binding.recyclerChildImages.setAdapter(childImageAdapter);

        getLiveLocks(id, parentID);
    }

    private void getLiveLocks(String date, String parentID) {
        FirebaseDatabase.getInstance().getReference().child("child_camera_images")
                .child(Preferences.getInstance(getContext()).getParent().getId())
                .child(date)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        cameraImageObjects.clear();

                        if (snapshot.getChildrenCount() != 0) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (dataSnapshot != null) {
                                    CameraImageObject cameraImageObject = dataSnapshot.getValue(CameraImageObject.class);

                                    if (cameraImageObject != null) {
                                        cameraImageObjects.add(cameraImageObject);
                                    }
                                }
                            }
                            childImageAdapter.notifyDataSetChanged();
                        }
                        LoadingDialog.closeDialog();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        LoadingDialog.closeDialog();
                    }
                });
    }
}