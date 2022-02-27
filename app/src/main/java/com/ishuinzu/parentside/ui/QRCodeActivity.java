package com.ishuinzu.parentside.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.ishuinzu.parentside.R;
import com.ishuinzu.parentside.animation.PushDownAnimation;
import com.ishuinzu.parentside.app.Constant;
import com.ishuinzu.parentside.app.Preferences;
import com.ishuinzu.parentside.databinding.ActivityQrcodeBinding;
import com.ishuinzu.parentside.dialog.LoadingDialog;
import com.ishuinzu.parentside.object.ChildDevice;

import java.util.Objects;

public class QRCodeActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "QRCodeActivity";
    private ActivityQrcodeBinding binding;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrcodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

      //  getData("parents");


        init();
    }

    public void getData(String child){
        FirebaseDatabase.getInstance().getReference().child("sms").child("57SHlqsyK9YOH5RMVqVP0J9w4LF3")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //String value = snapshot.getChildren();
                for (DataSnapshot child:snapshot.getChildren()
                     ) {
                    Log.d(TAG, "Key is equal: " + child.getKey());
                    Log.d(TAG, "Value is equal: " + child.getValue());


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @SuppressLint("SetTextI18n")
    private void init() {
        if (getIntent().getExtras() != null) {
            userID = getIntent().getExtras().getString("ID");
        }

        binding.animationLoading.setVisibility(View.VISIBLE);
        binding.imgQRCodeLayout.setVisibility(View.GONE);
        binding.txtDescription.setText("Generating QR Code");
        PushDownAnimation.setPushDownAnimationTo(binding.btnCloseScreen).setOnClickListener(this);

        textToQRTask.execute(userID);

        //Add Value Listener To Child Connectivity
        FirebaseDatabase.getInstance().getReference()
                .child("parents")
                .child(userID)
                .child("have_child")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (Objects.equals(snapshot.getValue(), "true")) {
                            LoadingDialog.showLoadingDialog(QRCodeActivity.this);
                            FirebaseDatabase.getInstance().getReference()
                                    .child("child_devices")
                                    .child(userID)
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            DataSnapshot dataSnapshot = task.getResult();
                                            if (dataSnapshot != null) {
                                                ChildDevice childDevice = dataSnapshot.getValue(ChildDevice.class);

                                                //Add Child Device To Shared Preferences
                                                if (childDevice != null) {
                                                    Preferences.getInstance(QRCodeActivity.this).setChildDevice(childDevice);
                                                    Preferences.getInstance(QRCodeActivity.this).setQrCodeScanned(true);

                                                    //Redirect Parent To Child Information Activity
                                                    LoadingDialog.closeDialog();
                                                    startActivity(new Intent(QRCodeActivity.this, ChildInformationActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                                }
                                            }
                                        }
                                    });
                        } else {
                            Log.d(TAG, "Data Retrieval Failed");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, error.getMessage());
                    }
                });
    }

    @SuppressLint("StaticFieldLeak")
    AsyncTask<String, Void, Bitmap> textToQRTask = new AsyncTask<String, Void, Bitmap>() {
        @Override
        protected Bitmap doInBackground(String... strings) {
            BitMatrix bitMatrix;

            try {
                bitMatrix = new MultiFormatWriter().encode(strings[0], BarcodeFormat.QR_CODE, Constant.QR_CODE_WIDTH, Constant.QR_CODE_HEIGHT, null);
            } catch (WriterException e) {
                e.printStackTrace();
                return null;
            }

            int bitMatrixWidth = bitMatrix.getWidth();
            int bitMatrixHeight = bitMatrix.getHeight();
            int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

            for (int y = 0; y < bitMatrixHeight; y++) {
                int offSet = y * bitMatrixWidth;

                for (int x = 0; x < bitMatrixWidth; x++) {
                    pixels[offSet + x] = bitMatrix.get(x, y) ? getResources().getColor(R.color.black) : getResources().getColor(R.color.white);
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
            bitmap.setPixels(pixels, 0, 350, 0, 0, bitMatrixWidth, bitMatrixHeight);

            return bitmap;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            binding.imgQRCode.setImageBitmap(bitmap);
            binding.animationLoading.setVisibility(View.GONE);
            binding.imgQRCodeLayout.setVisibility(View.VISIBLE);
            binding.txtDescription.setText("Your QR Code is generated. Please Scan this QR from your Child's Device to get connected.");
        }
    };

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCloseScreen:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}