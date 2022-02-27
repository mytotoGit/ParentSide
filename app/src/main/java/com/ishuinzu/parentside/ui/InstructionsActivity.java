package com.ishuinzu.parentside.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ishuinzu.parentside.R;
import com.ishuinzu.parentside.adapter.InstructionAdapter;
import com.ishuinzu.parentside.animation.PushDownAnimation;
import com.ishuinzu.parentside.app.Preferences;
import com.ishuinzu.parentside.databinding.ActivityInstructionsBinding;
import com.ishuinzu.parentside.object.Instruction;
import com.ishuinzu.parentside.transformer.PagerTransformer;

import java.util.ArrayList;
import java.util.List;

public class InstructionsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "InstructionsActivity";
    private ActivityInstructionsBinding binding;
    private List<Instruction> instructions;
    private InstructionAdapter instructionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInstructionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        PushDownAnimation.setPushDownAnimationTo(binding.btnCloseScreen).setOnClickListener(this);
        PushDownAnimation.setPushDownAnimationTo(binding.cardSignup).setOnClickListener(this);
        PushDownAnimation.setPushDownAnimationTo(binding.cardLogin).setOnClickListener(this);

        loadInstructions();
    }

    private void loadInstructions() {
        instructions = new ArrayList<>();

        instructions.add(new Instruction(R.raw.logo, "A Warm Welcome", "Welcome To Parental Control & Monitor Application System"));
        instructions.add(new Instruction(R.raw.logo, "Parent Signup", "Parents Can Create Their Account"));
        instructions.add(new Instruction(R.raw.logo, "Parent Login", "Parent Can Login & Can Ask Their Child To Scan QR Code"));
        instructions.add(new Instruction(R.raw.logo, "Location Monitoring", "Location Monitoring Based On Time & Date"));
        instructions.add(new Instruction(R.raw.logo, "SMS Logs", "Parent Can See SMS Logs Of Their Child"));

        instructionAdapter = new InstructionAdapter(InstructionsActivity.this, instructions);
        binding.pagerInstructions.setAdapter(instructionAdapter);
        binding.pagerInstructions.setPageTransformer(true, new PagerTransformer(InstructionsActivity.this));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCloseScreen:
                onBackPressed();
                break;

            case R.id.cardSignup:
                Preferences.getInstance(InstructionsActivity.this).setInstructionsSeen(true);
                startActivity(new Intent(InstructionsActivity.this, SignupActivity.class));
                break;

            case R.id.cardLogin:
                Preferences.getInstance(InstructionsActivity.this).setInstructionsSeen(true);
                startActivity(new Intent(InstructionsActivity.this, LoginActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}