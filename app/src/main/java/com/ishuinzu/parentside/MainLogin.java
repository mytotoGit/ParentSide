package com.ishuinzu.parentside;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ishuinzu.parentside.app.Preferences;
import com.ishuinzu.parentside.databinding.ActivityMainLoginBinding;

public class MainLogin extends AppCompatActivity {
    private ActivityMainLoginBinding binding;
    int counter=0;
    EditText editTextCounter;
    LinearLayout counterpage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        start();
    }
    ListView listView;
    private void start() {
        counterpage=findViewById(R.id.counterPage);
        editTextCounter=findViewById(R.id.editTextCounter);
        editTextCounter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
              if (editable.toString().equalsIgnoreCase("zma")){
                 counterpage.setVisibility(View.GONE);
              }
            }
        });
      editTextCounter.setFocusable(false);

    }

    @Override
    protected void onPause() {
        super.onPause();
        counterpage.setVisibility(View.VISIBLE);
    }

    public void onClick(View view) {
        editTextCounter.setText(String.valueOf(counter++));
        editTextCounter.setFocusableInTouchMode(true);
        editTextCounter.setFocusable(true);
    }
    public void login() {
        //Preferences.getInstance(this).setParent(parent);
        Preferences.getInstance(this).setLoggedIn(true);
    }

}