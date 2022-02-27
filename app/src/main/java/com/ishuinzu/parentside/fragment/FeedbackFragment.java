package com.ishuinzu.parentside.fragment;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.FirebaseDatabase;
import com.ishuinzu.parentside.animation.PushDownAnimation;
import com.ishuinzu.parentside.app.Preferences;
import com.ishuinzu.parentside.databinding.FragmentFeedbackBinding;
import com.ishuinzu.parentside.object.FeedbackObject;

import es.dmoral.toasty.Toasty;

public class FeedbackFragment extends Fragment {
    private static final String TAG = "FeedbackFragment";
    private FragmentFeedbackBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Feedback
    private double rate;
    private String feedback;

    private String mParam1;
    private String mParam2;

    public FeedbackFragment() {
    }

    public static FeedbackFragment newInstance(String param1, String param2) {
        FeedbackFragment fragment = new FeedbackFragment();
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
        binding = FragmentFeedbackBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        // Change Feedback Text Appearance
        String feedbackText = "How likely would you recommend <b>Parental Control &amp; Monitor Apps</b> to your friend or colleague?";
        binding.txtFeedback.setText(Html.fromHtml(feedbackText));
        binding.simpleRatingBar.setOnRatingChangeListener((ratingBar, rating, fromUser) -> rate = rating);
        PushDownAnimation.setPushDownAnimationTo(binding.cardSendFeedback).setOnClickListener(view -> sendFeedback());
    }

    private void sendFeedback() {
        feedback = binding.edtFeedback.getText().toString();

        if (feedback.equals("") || feedback.isEmpty()) {
            Toasty.error(requireContext(), "Feedback Required", Toasty.LENGTH_SHORT).show();
            return;
        }

        FeedbackObject feedbackObject = new FeedbackObject();
        feedbackObject.setFeedback(feedback);
        feedbackObject.setId(Preferences.getInstance(getContext()).getParent().getId());
        feedbackObject.setRate(rate);
        feedbackObject.setTime(System.currentTimeMillis());

        FirebaseDatabase.getInstance().getReference().child("feedbacks")
                .child("" + feedbackObject.getTime())
                .setValue(feedbackObject)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toasty.success(requireContext(), "Thanks! Feedback Send", Toasty.LENGTH_SHORT).show();
                    }
                });
    }
}