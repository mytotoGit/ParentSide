package com.ishuinzu.parentside.ui;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ishuinzu.parentside.databinding.ActivityPlayVideoBinding;

public class PlayVideoActivity extends AppCompatActivity {
    private static final String TAG = "PlayVideoActivity";
    private ActivityPlayVideoBinding binding;
    private String url;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get Data From Intent
        if (getIntent().getExtras() != null) {
            url = getIntent().getExtras().getString("URL");
            date = getIntent().getExtras().getString("DATE");
        }

        // Video View
        binding.videoView.setMediaController(binding.mediaController);
        binding.videoView.setVideoURI(Uri.parse(url));
        binding.mediaController.showComplete();
        binding.mediaController.setTitle("Recording Date : " + date);
        binding.videoView.pause();
    }
}