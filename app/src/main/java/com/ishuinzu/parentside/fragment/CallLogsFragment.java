package com.ishuinzu.parentside.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ishuinzu.parentside.R;
import com.ishuinzu.parentside.adapter.ConversationAdapter;
import com.ishuinzu.parentside.animation.PushDownAnimation;
import com.ishuinzu.parentside.app.Preferences;
import com.ishuinzu.parentside.app.Utils;
import com.ishuinzu.parentside.databinding.FragmentCallLogsBinding;
import com.ishuinzu.parentside.dialog.LoadingDialog;
import com.ishuinzu.parentside.object.CallObject;
import com.ishuinzu.parentside.object.ConversationObject;

import java.util.ArrayList;
import java.util.List;

public class CallLogsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "CallLogsFragment";
    private FragmentCallLogsBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private String dateID;
    private List<ConversationObject> conversationObjects;
    private ConversationAdapter conversationAdapter;
    private List<List<CallObject>> listConversations;
    private List<String> listAddresses;

    public CallLogsFragment() {
    }

    public static CallLogsFragment newInstance(String param1, String param2) {
        CallLogsFragment fragment = new CallLogsFragment();
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
        binding = FragmentCallLogsBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init();
    }

    private void init() {
        PushDownAnimation.setPushDownAnimationTo(binding.btnGetDate).setOnClickListener(this);

        dateID = Utils.getDateID();
        updateDate(dateID);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
            updateDate(dateID);
            dialog.dismiss();
        });

        dialog.getWindow().setAttributes(layoutParams);
        dialog.getWindow().setBackgroundDrawable(requireContext().getDrawable(R.drawable.background_transparent));
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void updateDate(String id) {
        binding.txtSelectedDate.setText(id.substring(0, 2) + "/" + id.substring(2, 4) + "/" + id.substring(4));
        loadConversations(id);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadConversations(String id) {
        listAddresses = new ArrayList<>();
        listConversations = new ArrayList<>();
        conversationObjects = new ArrayList<>();
        conversationAdapter = new ConversationAdapter(getContext(), conversationObjects, "CALLS");
        binding.recyclerCallLogs.setAdapter(conversationAdapter);

        getConversationLogs(id);
    }

    private void getConversationLogs(String id) {
        LoadingDialog.showLoadingDialog(getContext());

        FirebaseDatabase.getInstance().getReference()
                .child("calls")
                .child(Preferences.getInstance(getContext()).getParent().getId())
                .child(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listAddresses.clear();

                        if (snapshot.getChildrenCount() != 0) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String address = dataSnapshot.getKey();
                                if (address != null) {
                                    if (!listAddresses.contains(address)) {
                                        listAddresses.add(address);
                                    }
                                }
                            }
                            getConversationsFromAddresses(id, listAddresses);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        LoadingDialog.closeDialog();
                    }
                });
    }

    private void getConversationsFromAddresses(String id, List<String> listAddresses) {
        for (int i = 0; i < listAddresses.size(); i++) {
            int finalI = i;
            listConversations.add(new ArrayList<>());

            FirebaseDatabase.getInstance().getReference()
                    .child("calls")
                    .child(Preferences.getInstance(getContext()).getParent().getId())
                    .child(id)
                    .child(listAddresses.get(i))
                    .addValueEventListener(new ValueEventListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            listConversations.get(finalI).clear();

                            if (snapshot.getChildrenCount() != 0) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    CallObject callObject = dataSnapshot.getValue(CallObject.class);

                                    if (callObject != null) {
                                        listConversations.get(finalI).add(callObject);
                                    }
                                }
                                conversationObjects.add(new ConversationObject(listAddresses.get(finalI), listConversations.get(finalI).size()));

                                if (finalI == listAddresses.size() - 1) {
                                    conversationAdapter.notifyDataSetChanged();
                                    LoadingDialog.closeDialog();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            LoadingDialog.closeDialog();
                        }
                    });
        }
    }

    private void updateProgresses(int total, int incoming, int outgoing, int missed) {
        binding.progressTotalCalls.setProgress(total, (total * 2));
        binding.progressIncoming.setProgress(incoming, total);
        binding.progressOutgoing.setProgress(outgoing, total);
        binding.progressMissed.setProgress(missed, total);

        LoadingDialog.closeDialog();
    }
}