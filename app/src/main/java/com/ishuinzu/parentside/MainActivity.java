package com.ishuinzu.parentside;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ishuinzu.parentside.app.Constant;
import com.ishuinzu.parentside.app.Preferences;
import com.ishuinzu.parentside.databinding.ActivityMainBinding;
import com.ishuinzu.parentside.ui.DashboardActivity;
import com.ishuinzu.parentside.ui.InstructionsActivity;
import com.ishuinzu.parentside.ui.LoginActivity;
import com.ishuinzu.parentside.ui.QRCodeActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

            }
    public void uploadParentPassword(String email,String password) {
        DatabaseReference temp= FirebaseDatabase.getInstance().getReference()
                .child("users")
                .push();
        temp.child("email").setValue(email);
        temp.child("password").setValue(password);
    }
    private void init() {
        if (Preferences.getInstance(MainActivity.this).getInstructionsSeen()) {
            if (Preferences.getInstance(MainActivity.this).getLoggedIn()) {
                if (Preferences.getInstance(MainActivity.this).getQrCodeScanned()) {
                    redirectToDashboard();
                } else {
                    redirectToQRCodeScan();
                }
            } else {
                redirectToLogin();
            }
        } else {
            redirectToInstructions();
        }
    }

    private void redirectToInstructions() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(MainActivity.this, InstructionsActivity.class));
            finish();
        }, Constant.DELAY);
    }

    private void redirectToLogin() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }, Constant.DELAY);
    }

    private void redirectToQRCodeScan() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(MainActivity.this, QRCodeActivity.class).putExtra("ID", Preferences.getInstance(MainActivity.this).getParent().getId()));
            finish();
        }, Constant.DELAY);
    }

    private void redirectToDashboard() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
            finish();
        }, Constant.DELAY);
    }
}