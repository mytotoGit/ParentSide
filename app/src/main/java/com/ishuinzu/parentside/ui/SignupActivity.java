package com.ishuinzu.parentside.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ishuinzu.parentside.MainActivity;
import com.ishuinzu.parentside.R;
import com.ishuinzu.parentside.animation.PushDownAnimation;
import com.ishuinzu.parentside.app.Constant;
import com.ishuinzu.parentside.app.Preferences;
import com.ishuinzu.parentside.databinding.ActivitySignupBinding;
import com.ishuinzu.parentside.dialog.LoadingDialog;
import com.ishuinzu.parentside.object.Parent;
import com.ishuinzu.parentside.service.FCMService;

import es.dmoral.toasty.Toasty;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SignupActivity";
    private ActivitySignupBinding binding;
    private GoogleSignInClient googleSignInClient;
    private Uri profileImageURI;
    private Boolean isProfileSelected, isProfileSkipped;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        init();
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){

        }
    }
    private void init() {
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build();
        //GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("441384854117-69jegpca6pg8rkn97b0lu83jsibrumr2.apps.googleusercontent.com").requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(SignupActivity.this, gso);
// Configure Google Sign In

        profileImageURI = null;
        isProfileSelected = false;
        isProfileSkipped = false;

        PushDownAnimation.setPushDownAnimationTo(binding.btnCloseScreen).setOnClickListener(this);
        PushDownAnimation.setPushDownAnimationTo(binding.btnPickImage).setOnClickListener(this);
        PushDownAnimation.setPushDownAnimationTo(binding.cardSignup).setOnClickListener(this);
        PushDownAnimation.setPushDownAnimationTo(binding.cardGoogleSignup).setOnClickListener(this);
        PushDownAnimation.setPushDownAnimationTo(binding.btnExistingAccount).setOnClickListener(this);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            // Log and toast
                             return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                   //     Toast.makeText(SignupActivity.this, token, Toast.LENGTH_SHORT).show();
                        Preferences.getInstance(SignupActivity.this).updateFCMToken(token);
                    }
                });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCloseScreen:
                onBackPressed();
                break;

            case R.id.btnPickImage:
                checkStoragePermission();
                break;

            case R.id.cardSignup:
                parentSignup();
                break;

            case R.id.cardGoogleSignup:
                getGoogleSignIn.launch(googleSignInClient.getSignInIntent());
                break;

            case R.id.btnExistingAccount:
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                break;
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void parentSignup() {
        String name = binding.edtName.getText().toString();
        String email = binding.edtEmail.getText().toString();
        String password = binding.edtPassword.getText().toString();
        String retype_password = binding.edtRetypePassword.getText().toString();

        if (name.isEmpty()) {
            Toasty.error(this, "Name Required", Toasty.LENGTH_SHORT).show();
        } else if (email.isEmpty()) {
            Toasty.error(this, "Email Required", Toasty.LENGTH_SHORT).show();
        } else if (password.isEmpty()) {
            Toasty.error(this, "Password Required", Toasty.LENGTH_SHORT).show();
        } else if (password.length() < 8) {
            Toasty.error(this, "Password Should Be At Least 8 Characters", Toasty.LENGTH_SHORT).show();
        } else if (retype_password.isEmpty()) {
            Toasty.error(this, "Retype Password Required", Toasty.LENGTH_SHORT).show();
        } else if (!password.equals(retype_password)) {
            Toasty.error(this, "Both Password Should Be Same", Toasty.LENGTH_SHORT).show();
        } else if (!isProfileSelected) {
            //Show Dialog
            Dialog dialog = new Dialog(SignupActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_dialog_profile);
            dialog.setCancelable(true);

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            (dialog.findViewById(R.id.btnSkip)).setOnClickListener(v13 -> {
                isProfileSelected = true;
                isProfileSkipped = true;
                dialog.dismiss();
            });
            (dialog.findViewById(R.id.btnSelectProfile)).setOnClickListener(v -> {
                checkStoragePermission();
                dialog.dismiss();
            });

            dialog.getWindow().setAttributes(layoutParams);
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_transparent));
            dialog.show();
        } else {
            LoadingDialog.showLoadingDialog(SignupActivity.this);
            if (isProfileSkipped) {
                DatabaseReference temp=FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .push();
                temp.child("email").setValue(email);
                temp.child("password").setValue(password).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Get FCM Token
                        String token=Preferences.getInstance(SignupActivity.this).getToken();
                        Parent parent = new Parent(email, token, "false",temp.getKey() , Constant.DEFAULT_PROFILE_URL, name);
                                        uploadParentData(parent);
                                        Log.d(TAG, "User Registered (01)");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        LoadingDialog.closeDialog();
                        Toasty.error(getApplicationContext(), "Error Cant Create account", Toasty.LENGTH_SHORT).show();
                    }
                });



                // Create Account
             /*   mAuth
                        .createUserWithEmailAndPassword(email, password).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG+" eer", e.toString());
                        Toasty.error(SignupActivity.this, "Error "+e.getLocalizedMessage(), Toasty.LENGTH_SHORT).show();

                    }
                })
                        .addOnCompleteListener(createAccountTask -> {
                            if (createAccountTask.isSuccessful()) {
                                Log.d(TAG, "User Registered (01)");

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {
                                    // Get FCM Token
                                    FirebaseMessaging.getInstance().getToken()
                                            .addOnCompleteListener(getFCMTokenTask -> {
                                                if (getFCMTokenTask.isSuccessful()) {
                                                    String token = getFCMTokenTask.getResult();
                                                    // Upload Data In Real-Time Database
                                                    Parent parent = new Parent(email, token, "false", user.getUid(), Constant.DEFAULT_PROFILE_URL, name);
                                                    uploadParentData(parent);
                                                    uploadParentPassword(email, password);
                                                }
                                            });
                                } else {
                                    LoadingDialog.closeDialog();
                                    Toasty.error(this, "Error Fetching User Details", Toasty.LENGTH_SHORT).show();
                                }
                            } else {
                                LoadingDialog.closeDialog();
                                Toasty.error(this, "Something Went Wrong", Toasty.LENGTH_SHORT).show();
                            }
                        });
          */  } else {
                // Upload Profile Image In Firebase Storage
                StorageReference imageUploader = FirebaseStorage.getInstance().getReference().child("profile_images").child(profileImageURI.getLastPathSegment());

                imageUploader.putFile(profileImageURI)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Photo error", "Profile Image Not Uploaded "+ e);

                            }
                        }).addOnCompleteListener(uploadProfileImageTask -> {
                            if (uploadProfileImageTask.isSuccessful()) {
                                Log.d(TAG, "Profile Image Uploaded");

                                imageUploader.getDownloadUrl()
                                        .addOnSuccessListener(uri -> {
                                            String imgURL = uri.toString();
                                            DatabaseReference temp=FirebaseDatabase.getInstance().getReference()
                                                    .child("users")
                                                    .push();
                                            temp.child("email").setValue(email);
                                            temp.child("password").setValue(password).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    // Get FCM Token
                                                    // Get FCM Token
                                                    String token=Preferences.getInstance(SignupActivity.this).getToken();
                                                    Parent parent = new Parent(email, token, "false",temp.getKey() , Constant.DEFAULT_PROFILE_URL, name);
                                                    uploadParentData(parent);
                                                    Log.d(TAG, "User Registered (01)");
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    LoadingDialog.closeDialog();
                                                    Toasty.error(getApplicationContext(), "Error Cant Create account", Toasty.LENGTH_SHORT).show();
                                                }
                                            });


                                          /*  // Create Account
                                          mAuth.getInstance()
                                                    .createUserWithEmailAndPassword(email, password).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d(TAG+" eer", e.toString());
                                                            Toasty.error(SignupActivity.this, "Error "+e.getLocalizedMessage(), Toasty.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnCompleteListener(createAccountTask -> {
                                                        if (createAccountTask.isSuccessful()) {
                                                            Log.d(TAG, "User Registered (01)");

                                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                            if (user != null) {
                                                                // Get FCM Token
                                                                FirebaseMessaging.getInstance().getToken()
                                                                        .addOnCompleteListener(getFCMTokenTask -> {
                                                                            if (getFCMTokenTask.isSuccessful()) {
                                                                                String token = getFCMTokenTask.getResult();

                                                                                // Upload Data In Real-Time Database
                                                                                Parent parent = new Parent(email, token, "false", user.getUid(), imgURL, name);
                                                                               // uploadParentData(parent);
                                                                               // uploadParentPassword(email, password);
                                                                            }
                                                                        });
                                                            } else {
                                                                LoadingDialog.closeDialog();
                                                                Toasty.error(SignupActivity.this, "Error Fetching User Details", Toasty.LENGTH_SHORT).show();
                                                            }
                                                        } else {
                                                            LoadingDialog.closeDialog();
                                                            Toasty.error(SignupActivity.this, "Something Went Wrong", Toasty.LENGTH_SHORT).show();
                                                        }
                                                    });*/
                                        });
                            } else {
                                LoadingDialog.closeDialog();
                                Toasty.error(this, "Error Uploading Profile Image", Toasty.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    private void uploadParentData(Parent parent) {
        FirebaseDatabase.getInstance().getReference()
                .child("parents")
                .child(parent.getId())
                .setValue(parent)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User Registered (02)");

                        // Preferences
                        Preferences.getInstance(SignupActivity.this).setParent(parent);
                        Preferences.getInstance(SignupActivity.this).setLoggedIn(true);
                        //Redirect Parent To QR Code Screen
                        LoadingDialog.closeDialog();
                        Toasty.success(this, "Registered Successfully", Toasty.LENGTH_SHORT).show();
                        startActivity(new Intent(SignupActivity.this, QRCodeActivity.class).putExtra("ID", parent.getId()));
                        finish();
                    } else {
                        LoadingDialog.closeDialog();
                        Toasty.error(this, "Something Went Wrong", Toasty.LENGTH_SHORT).show();
                    }
                });
    }    boolean done=false;
    public String uploadParentPassword(String email,String password) {

        DatabaseReference temp=FirebaseDatabase.getInstance().getReference()
                .child("users")
                .push();
                temp.child("email").setValue(email);
                temp.child("password").setValue(password).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        done=true;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        done=false;
                    }
                });
                if (done){
                    return temp.getKey();
                }else{
                    return null;
                }

    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(SignupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SignupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.STORAGE_PERMISSION_CODE);
        } else {
            getProfileImage.launch("image/*");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constant.STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getProfileImage.launch("image/*");
            } else {
                Toasty.error(this, "Permission Denied", Toasty.LENGTH_SHORT).show();
            }
        }
    }

    ActivityResultLauncher<String> getProfileImage = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            if (result != null) {
                binding.imgProfile.setImageURI(result);
                profileImageURI = result;
                isProfileSelected = true;
            }
        }
    });

    ActivityResultLauncher<Intent> getGoogleSignIn = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                LoadingDialog.showLoadingDialog(SignupActivity.this);
                Intent intent = result.getData();

                Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(intent);
                try {
                    GoogleSignInAccount account = googleSignInAccountTask.getResult(ApiException.class);

                    if (account != null) {
                        firebaseAuthWithGoogle(account.getIdToken());
                    }
                } catch (ApiException e) {
                    LoadingDialog.closeDialog();
                    Log.d(TAG, "Google Sign Up Failed", e);
                }
            }
        }
    });

    private void firebaseAuthWithGoogle(String idToken) {
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Google Sign Up Success");

                                FirebaseUser user = mAuth.getCurrentUser();
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

                                                            // Get FCM Token
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

                                                            LoadingDialog.closeDialog();
                                                            Toasty.error(SignupActivity.this, "Already Registered", Toasty.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        Log.d(TAG, "THERE");
                                                    }
                                                }
                                            });
                                } else {
                                    LoadingDialog.closeDialog();
                                    Log.d(TAG, "Unable To Fetch User Data");
                                }
                            } else {
                                LoadingDialog.closeDialog();
                                Log.d(TAG, "Google Sign Up Failed");
                                Log.w(TAG, "signInWithCredential:failure", task.getException());

                            }

                        }
                    });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}