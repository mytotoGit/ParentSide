package com.ishuinzu.parentside.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ishuinzu.parentside.R;
import com.ishuinzu.parentside.animation.PushDownAnimation;
import com.ishuinzu.parentside.app.Preferences;
import com.ishuinzu.parentside.databinding.ActivityLoginBinding;
import com.ishuinzu.parentside.dialog.LoadingDialog;
import com.ishuinzu.parentside.object.Parent;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;
    private ActivityLoginBinding binding;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        init();



    }

    private void init() {
       // GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("441384854117-69jegpca6pg8rkn97b0lu83jsibrumr2.apps.googleusercontent.com").requestEmail().build();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);

        PushDownAnimation.setPushDownAnimationTo(binding.btnCloseScreen).setOnClickListener(this);
        PushDownAnimation.setPushDownAnimationTo(binding.cardLogin).setOnClickListener(this);
        PushDownAnimation.setPushDownAnimationTo(binding.cardGoogleSignin).setOnClickListener(this);
        PushDownAnimation.setPushDownAnimationTo(binding.btnCreateAccount).setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCloseScreen:
                onBackPressed();
                break;

            case R.id.cardLogin:
                parentLogin();
                break;

            case R.id.cardGoogleSignin:
                getGoogleSignIn.launch(googleSignInClient.getSignInIntent());
                break;

            case R.id.btnCreateAccount:
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                break;
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){

        }
    }

    private void parentLogin() {
        String email = binding.edtEmail.getText().toString();
        String password = binding.edtPassword.getText().toString();

        if (email.isEmpty()) {
            Toasty.error(this, "Email Required", Toasty.LENGTH_SHORT).show();
        } else if (password.isEmpty()) {
            Toasty.error(this, "Password Required", Toasty.LENGTH_SHORT).show();
        } else if (password.length() < 8) {
            Toasty.error(this, "Password Should Be At Least 8 Characters", Toasty.LENGTH_SHORT).show();
        } else {
            //Login User
            LoadingDialog.showLoadingDialog(LoginActivity.this);
            mAuth.signInWithEmailAndPassword(email, password).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG+" eer", e.toString());
                    Toasty.error(LoginActivity.this, "Error "+e.getLocalizedMessage(), Toasty.LENGTH_SHORT).show();

                }
            })
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success "+mAuth.getUid());
                                FirebaseUser user = mAuth.getCurrentUser();

                                if (user != null) {
                                    //Get User Details
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("parents")
                                            .child(user.getUid())
                                            .get()
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    DataSnapshot parentData = task1.getResult();

                                                    //Getting Parent Data
                                                    if (parentData != null) {
                                                        Parent parent = parentData.getValue(Parent.class);

                                                        if (parent != null) {
                                                            //Set User Logged In
                                                            Preferences.getInstance(LoginActivity.this).setLoggedIn(true);
                                                            Preferences.getInstance(LoginActivity.this).setParent(parent);

                                                            //Redirect Parent To QR Code Screen
                                                            LoadingDialog.closeDialog();
                                                            Toasty.success(LoginActivity.this, "Logged In", Toasty.LENGTH_SHORT).show();
                                                            startActivity(new Intent(LoginActivity.this, QRCodeActivity.class).putExtra("ID", parent.getId()));
                                                            finish();
                                                        } else {
                                                          LoadingDialog.closeDialog();
                                                            Toasty.error(LoginActivity.this, "Error Fetching User Data", Toasty.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                } else {
                                                    LoadingDialog.closeDialog();
                                                    Toasty.error(LoginActivity.this, "Error Fetching User Data", Toasty.LENGTH_SHORT).show();
                                                }
                                            });
                                }


                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

/*
            LoadingDialog.showLoadingDialog(LoginActivity.this);
            FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email, password)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG+ "error", "Sign In Failed" +e );

                        }
                    }).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Sign In Success");

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                //Get User Details
                                FirebaseDatabase.getInstance().getReference()
                                        .child("parents")
                                        .child(user.getUid())
                                        .get()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                DataSnapshot parentData = task1.getResult();

                                                //Getting Parent Data
                                                if (parentData != null) {
                                                    Parent parent = parentData.getValue(Parent.class);

                                                    if (parent != null) {
                                                        //Set User Logged In
                                                        Preferences.getInstance(LoginActivity.this).setLoggedIn(true);
                                                        Preferences.getInstance(LoginActivity.this).setParent(parent);

                                                        //Redirect Parent To QR Code Screen
                                                        LoadingDialog.closeDialog();
                                                        Toasty.success(this, "Logged In", Toasty.LENGTH_SHORT).show();
                                                        startActivity(new Intent(LoginActivity.this, QRCodeActivity.class).putExtra("ID", parent.getId()));
                                                        finish();
                                                    } else {
                                                        LoadingDialog.closeDialog();
                                                        Toasty.error(this, "Error Fetching User Data", Toasty.LENGTH_SHORT).show();
                                                    }
                                                }
                                            } else {
                                                LoadingDialog.closeDialog();
                                                Toasty.error(this, "Error Fetching User Data", Toasty.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Log.d(TAG, "Sign In Failed" + Objects.requireNonNull(task.getException()).getMessage());
                            LoadingDialog.closeDialog();
                            Toasty.error(LoginActivity.this, "Sign In Failed", Toasty.LENGTH_SHORT).show();
                        }
                    });*/
        }
    }

    ActivityResultLauncher<Intent> getGoogleSignIn = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            LoadingDialog.showLoadingDialog(LoginActivity.this);
            Intent intent = result.getData();

            Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(intent);
            try {
                GoogleSignInAccount account = googleSignInAccountTask.getResult(ApiException.class);

                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            } catch (ApiException e) {
                LoadingDialog.closeDialog();
                Log.d(TAG, "Google Sign In Failed", e);
            }
        }
    });

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        //Check Account
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Google Sign In Success");

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            //Check User Data
                            FirebaseDatabase.getInstance().getReference()
                                    .child("parents")
                                    .child(user.getUid())
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            DataSnapshot snapshot = task1.getResult();
                                            if (snapshot != null) {
                                                Log.d(TAG, "HERE");

                                                Parent parentData = snapshot.getValue(Parent.class);
                                                if (parentData == null) {
                                                    Log.d(TAG, "PARENT NULL");

                                                    FirebaseMessaging.getInstance().getToken()
                                                            .addOnCompleteListener(task2 -> {
                                                                if (task2.isSuccessful()) {
                                                                    String token = task2.getResult();

                                                                    // Upload Data In Real-Time Database
                                                                    Parent parent = new Parent(user.getEmail(), token, "false", user.getUid(), "" + user.getPhotoUrl(), user.getDisplayName());
                                                                    uploadParentData(parent);
                                                                }
                                                            });
                                                } else {
                                                    Log.d(TAG, "PARENT ALREADY EXIST");

                                                    // Get Data
                                                    Preferences.getInstance(LoginActivity.this).setLoggedIn(true);
                                                    Preferences.getInstance(LoginActivity.this).setParent(parentData);
                                                    LoadingDialog.closeDialog();

                                                    //Redirect User To Scan Qr Code Activity
                                                    startActivity(new Intent(LoginActivity.this, QRCodeActivity.class).putExtra("ID", user.getUid()));
                                                }
                                            } else {
                                                Log.d(TAG, "THERE");
                                            }
                                        }
                                    });

                        } else {
                            LoadingDialog.closeDialog();
                            Log.d(TAG, "Google Sign In Failed");
                        }
                    }
                });
    }

    private void uploadParentData(Parent parent) {
        FirebaseDatabase.getInstance().getReference()
                .child("parents")
                .child(parent.getId())
                .setValue(parent)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User Done (02)");

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        if (user != null) {
                            // Get Data
                            Preferences.getInstance(LoginActivity.this).setLoggedIn(true);
                            Preferences.getInstance(LoginActivity.this).setParent(parent);
                            LoadingDialog.closeDialog();

                            //Redirect User To Scan Qr Code Activity
                            startActivity(new Intent(LoginActivity.this, QRCodeActivity.class).putExtra("ID", user.getUid()));
                        }
                    } else {
                        LoadingDialog.closeDialog();
                        Toasty.error(this, "Something Went Wrong", Toasty.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}