package com.ishuinzu.parentside.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.ishuinzu.parentside.R;
import com.ishuinzu.parentside.animation.PushDownAnimation;
import com.ishuinzu.parentside.app.Constant;
import com.ishuinzu.parentside.app.Preferences;
import com.ishuinzu.parentside.databinding.FragmentProfileBinding;
import com.ishuinzu.parentside.object.Parent;

import es.dmoral.toasty.Toasty;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ProfileFragment";
    private FragmentProfileBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private Parent parent;
    // Update Email
    private String dialogEmail, dialogPassword;
    // Profile Image
    private Uri profileImageURI;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        binding = FragmentProfileBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init();
    }

    private void init() {
        PushDownAnimation.setPushDownAnimationTo(binding.cardUpdateProfile).setOnClickListener(this);
        PushDownAnimation.setPushDownAnimationTo(binding.layoutPersonal).setOnClickListener(this);
        PushDownAnimation.setPushDownAnimationTo(binding.layoutChildDevice).setOnClickListener(this);
        PushDownAnimation.setPushDownAnimationTo(binding.btnPickImage).setOnClickListener(this);

        profileImageURI = null;
        getPersonalDetails();
    }

    private void getPersonalDetails() {
        // Get Database Data
        parent = Preferences.getInstance(getContext()).getParent();
        textToQRTask.execute(parent.getId());
        FirebaseDatabase.getInstance().getReference().child("parents")
                .child(parent.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            Parent obtainedParent = snapshot.getValue(Parent.class);
                            if (obtainedParent != null) {
                                updatePersonalUI(obtainedParent);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        updatePersonalUI(parent);
                    }
                });
    }

    private void updatePersonalUI(Parent obtainedParent) {
        binding.txtUserName.setText(obtainedParent.getName());
        binding.txtUserEmail.setText(obtainedParent.getEmail());
        binding.edtName.setText(obtainedParent.getName());
        binding.edtEmail.setText(obtainedParent.getEmail());
        Glide.with(requireContext()).load(obtainedParent.getImg_url()).into(binding.imgProfile);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cardUpdateProfile:
                updateProfile();
                break;

            case R.id.layoutPersonal:
                binding.layoutPersonal.setBackgroundColor(getResources().getColor(R.color.blue_200));
                binding.layoutChildDevice.setBackgroundColor(getResources().getColor(R.color.background));
                binding.txtPersonal.setTextColor(getResources().getColor(R.color.white));
                binding.txtChildDevice.setTextColor(getResources().getColor(R.color.text));
                break;

            case R.id.layoutChildDevice:
                binding.layoutChildDevice.setBackgroundColor(getResources().getColor(R.color.blue_200));
                binding.layoutPersonal.setBackgroundColor(getResources().getColor(R.color.background));
                binding.txtPersonal.setTextColor(getResources().getColor(R.color.text));
                binding.txtChildDevice.setTextColor(getResources().getColor(R.color.white));
                break;

            case R.id.btnPickImage:
                checkStoragePermission();
                break;
        }
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.STORAGE_PERMISSION_CODE);
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
                Toasty.error(requireContext(), "Permission Denied", Toasty.LENGTH_SHORT).show();
            }
        }
    }

    ActivityResultLauncher<String> getProfileImage = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            if (result != null) {
                binding.imgProfile.setImageURI(result);
                profileImageURI = result;

                // Update Profile
                updateProfileImage(profileImageURI);
            }
        }
    });

    private void updateProfileImage(Uri uri) {
        StorageReference profileUpdateStorage = FirebaseStorage.getInstance().getReference().child("images").child("updated").child("profile_" + System.currentTimeMillis());
        profileUpdateStorage.putFile(uri).addOnCompleteListener(uploadNewProfileTask -> {
            if (uploadNewProfileTask.isSuccessful()) {
                profileUpdateStorage.getDownloadUrl().addOnSuccessListener(newURI -> {
                    String newURL = newURI.toString();

                    FirebaseDatabase.getInstance().getReference().child("parents")
                            .child(parent.getId())
                            .child("img_url")
                            .setValue(newURL)
                            .addOnCompleteListener(updateURLTask -> {
                                if (updateURLTask.isSuccessful()) {
                                    // Update Preferences
                                    parent.setImg_url(newURL);
                                    Preferences.getInstance(getContext()).setParent(parent);
                                    Toasty.success(requireContext(), "Profile Image Updated", Toasty.LENGTH_SHORT).show();
                                } else {
                                    Toasty.error(requireContext(), "Error Uploading Profile", Toasty.LENGTH_SHORT).show();
                                }
                            });
                });
            } else {
                Toasty.error(requireContext(), "Error Uploading Profile", Toasty.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile() {
        String name = binding.edtName.getText().toString();
        String email = binding.edtEmail.getText().toString();

        if (name.equals("") || name.isEmpty()) {
            Toasty.error(requireContext(), "Name Required", Toasty.LENGTH_SHORT).show();
            return;
        } else if (email.equals("") || email.isEmpty()) {
            Toasty.error(requireContext(), "Email Required", Toasty.LENGTH_SHORT).show();
            return;
        } else if (name.equals(parent.getName())) {
            // No Changes in Name
            if (email.equals(parent.getEmail())) {
                // No Changes in Email
                Toasty.info(requireContext(), "No Changes", Toasty.LENGTH_SHORT).show();
                return;
            } else {
                updateEmailOnly(email);
            }
        } else {
            // Changes in Name
            if (email.equals(parent.getEmail())) {
                // No Changes in Email
                updateNameOnly(name);
            } else {
                updateDetails(name, email);
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void updateEmailOnly(String email) {
        // Show Dialog
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_signin);
        dialog.setCancelable(true);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        TextInputEditText edtDialogEmail = dialog.findViewById(R.id.edtDialogEmail);
        TextInputEditText edtDialogPassword = dialog.findViewById(R.id.edtDialogPassword);

        (dialog.findViewById(R.id.btnCancel)).setOnClickListener(v -> {
            dialog.dismiss();
        });
        (dialog.findViewById(R.id.btnSignNow)).setOnClickListener(v -> {
            dialogEmail = edtDialogEmail.getText().toString();
            dialogPassword = edtDialogPassword.getText().toString();

            if (dialogEmail.isEmpty()) {
                Toasty.error(requireContext(), "Email Required", Toasty.LENGTH_SHORT).show();
                return;
            } else if (dialogPassword.isEmpty()) {
                Toasty.error(requireContext(), "Password Required", Toasty.LENGTH_SHORT).show();
                return;
            }
            addNewEmail(email, dialogEmail, dialogPassword);
            dialog.dismiss();
        });

        dialog.getWindow().setAttributes(layoutParams);
        dialog.getWindow().setBackgroundDrawable(requireContext().getDrawable(R.drawable.background_transparent));
        dialog.show();
    }

    private void addNewEmail(String newEmail, String loginEmail, String loginPassword) {
        // Try Login
        FirebaseAuth.getInstance().signInWithEmailAndPassword(loginEmail, loginPassword)
                .addOnCompleteListener(taskLogin -> {
                    if (taskLogin.isSuccessful()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        if (user != null) {
                            // Change Email
                            user.updateEmail(newEmail).addOnCompleteListener(taskChangeEmail -> {
                                if (taskChangeEmail.isSuccessful()) {
                                    // Update Email in Database
                                    FirebaseDatabase.getInstance().getReference().child("parents")
                                            .child(user.getUid())
                                            .child("email")
                                            .setValue(newEmail)
                                            .addOnCompleteListener(taskUpdateEmail -> {
                                                if (taskUpdateEmail.isSuccessful()) {
                                                    // Update Preferences
                                                    parent.setEmail(newEmail);
                                                    Preferences.getInstance(getContext()).setParent(parent);
                                                }
                                            });
                                }
                            });
                        }
                    }
                });
    }

    private void updateNameOnly(String name) {
        FirebaseDatabase.getInstance().getReference().child("parents")
                .child(parent.getId())
                .child("name")
                .setValue(name)
                .addOnCompleteListener(nameChangeTask -> {
                    if (nameChangeTask.isSuccessful()) {
                        parent.setName(name);
                        Preferences.getInstance(getContext()).setParent(parent);
                    }
                });
    }

    private void updateDetails(String name, String email) {
        FirebaseDatabase.getInstance().getReference().child("parents")
                .child(parent.getId())
                .child("name")
                .setValue(name)
                .addOnCompleteListener(nameChangeTask -> {
                    if (nameChangeTask.isSuccessful()) {
                        parent.setName(name);
                        Preferences.getInstance(getContext()).setParent(parent);
                        updateEmailOnly(email);
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
            binding.imgQRCodeLayout.setVisibility(View.VISIBLE);
        }
    };
}