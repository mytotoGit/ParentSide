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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ishuinzu.parentside.R;
import com.ishuinzu.parentside.adapter.LiveLockAdapter;
import com.ishuinzu.parentside.animation.PushDownAnimation;
import com.ishuinzu.parentside.app.Preferences;
import com.ishuinzu.parentside.app.Utils;
import com.ishuinzu.parentside.databinding.FragmentLiveLockBinding;
import com.ishuinzu.parentside.dialog.LoadingDialog;
import com.ishuinzu.parentside.object.LiveLockObject;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class LiveLockFragment extends Fragment {
    private static final String TAG = "LiveLockFragment";
    private FragmentLiveLockBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private List<LiveLockObject> liveLockObjects;
    private LiveLockAdapter liveLockAdapter;
    private String dateID;
    private String mParam1;
    private String mParam2;

    public LiveLockFragment() {
    }

    public static LiveLockFragment newInstance(String param1, String param2) {
        LiveLockFragment fragment = new LiveLockFragment();
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
        binding = FragmentLiveLockBinding.inflate(inflater);
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
        updateDate(dateID);
    }

    @SuppressLint("SetTextI18n")
    private void updateDate(String id) {
        binding.txtSelectedDate.setText(id.substring(0, 2) + "/" + id.substring(2, 4) + "/" + id.substring(4));

        LoadingDialog.showLoadingDialog(getContext());
        loadLiveLocks(id);
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
            updateDate(dateID);
            dialog.dismiss();
        });

        dialog.getWindow().setAttributes(layoutParams);
        dialog.getWindow().setBackgroundDrawable(requireContext().getDrawable(R.drawable.background_transparent));
        dialog.show();
    }

    private void loadLiveLocks(String id) {
        liveLockObjects = new ArrayList<>();
        liveLockAdapter = new LiveLockAdapter(getContext(), liveLockObjects);
        binding.recyclerLiveLocks.setAdapter(liveLockAdapter);

        getLiveLocks(id);
    }

    private void getLiveLocks(String date) {
        FirebaseDatabase.getInstance().getReference().child("live_lock_history")
                .child(Preferences.getInstance(getContext()).getParent().getId())
                .child(date)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount() != 0) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (dataSnapshot != null) {
                                    LiveLockObject liveLockObject = dataSnapshot.getValue(LiveLockObject.class);

                                    if (liveLockObject != null) {
                                        liveLockObjects.add(liveLockObject);
                                    }
                                }
                            }
                            liveLockAdapter.notifyDataSetChanged();
                        } else {
                            Toasty.error(getContext(), "No Record Found", Toasty.LENGTH_SHORT).show();
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