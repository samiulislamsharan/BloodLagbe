package com.argonsoftwares.bloodlagbe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DonorRegistrationActivity extends AppCompatActivity {

    private TextView backButton, dob, weight;
    private CircleImageView profileImage;
    private TextInputEditText fullName, nid, phoneNumber, email, password;
    private Spinner bloodGroup;
    private Button registerButton;
    private Uri resultUri;
    private ProgressDialog progress;

    // Firebase variables
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_registration);

        // Changing status bar color.
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.app_PrimaryColorActionbar));
        }

        profileImage = findViewById(R.id.profile_image);
        fullName = findViewById(R.id.input_RegisterFullName);
        nid = findViewById(R.id.input_RegisterNID);
        phoneNumber = findViewById(R.id.input_RegisterPhoneNumber);
        email = findViewById(R.id.input_RegisterEmail);
        password = findViewById(R.id.input_RegisterPassword);
        bloodGroup = findViewById(R.id.input_bloodGroupsSpinner);
        registerButton = findViewById(R.id.btn_DonorRegistration);
        backButton = findViewById(R.id.text_BackButton);
        progress = new ProgressDialog(this);

        // Firebase variables
        firebaseAuth = FirebaseAuth.getInstance();

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(getApplicationContext())
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent fileManagerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                fileManagerIntent.setType("image/*");
                                startActivityForResult(fileManagerIntent, 1);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String GetFullName = fullName.getText().toString().trim();
                final String GetNID = nid.getText().toString().trim();
                final String GetPhoneNumber = phoneNumber.getText().toString().trim();
                final String GetBloodGroup = bloodGroup.getSelectedItem().toString().trim();
                final String GetEmail = email.getText().toString().trim();
                final String GetPassword = password.getText().toString().trim();

                if (TextUtils.isEmpty(GetEmail)) {
                    email.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(GetPassword)) {
                    password.setError("Password is required");
                    return;
                }
                if (TextUtils.isEmpty(GetFullName)) {
                    fullName.setError("Full Name is required");
                    return;
                }
                if (TextUtils.isEmpty(GetNID)) {
                    nid.setError("NID is required");
                    return;
                }
                if (TextUtils.isEmpty(GetPhoneNumber)) {
                    phoneNumber.setError("Phone Number is required");
                    return;
                }
                if (bloodGroup.equals("Select your Blood Group")) {
                    Toast.makeText(DonorRegistrationActivity.this,
                            "Please select your blood group",
                            Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    progress.setMessage("Registering Donor...");
                    progress.setCanceledOnTouchOutside(false);
                    progress.show();

                    firebaseAuth.createUserWithEmailAndPassword(GetEmail, GetPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                String errorMessage = task.getException().toString();
                                Toast.makeText(DonorRegistrationActivity.this,
                                        "Error" + errorMessage,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                String currentUserId = firebaseAuth.getCurrentUser().getUid();
                                databaseReference =
                                        FirebaseDatabase.getInstance()
                                                .getReference()
                                                .child("users")
                                                .child(currentUserId);
                                HashMap userInfo = new HashMap();
                                userInfo.put("id", currentUserId);
                                userInfo.put("fullName", GetFullName);
                                userInfo.put("email", GetEmail);
                                userInfo.put("nid", GetNID);
                                userInfo.put("phoneNumber", GetPhoneNumber);
                                userInfo.put("bloodGroup", GetBloodGroup);
                                userInfo.put("type", "Donor");
                                userInfo.put("search", "donor" + GetBloodGroup);

                                databaseReference.updateChildren(userInfo).addOnCompleteListener(
                                        new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(DonorRegistrationActivity.this,
                                                            "Data saved successfully",
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(DonorRegistrationActivity.this,
                                                            task.getException().toString(),
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                                finish();
                                                //progress.dismiss();
                                            }
                                        });
                                // Get the profile image URL then compress it and try to save it to
                                // the Firebase Storage
                                if (resultUri != null) {
                                    final StorageReference filePath =
                                            FirebaseStorage.getInstance().getReference().child("profile_image").child(currentUserId);
                                    Bitmap bitmap = null;
                                    try {
                                        bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                                    byte[] data = baos.toByteArray();
                                    UploadTask uploadTask = filePath.putBytes(data);

                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(DonorRegistrationActivity.this,
                                                    "Image upload failed",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            if (taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() != null) {
                                                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String downloadUrl = uri.toString();
                                                        Map newImage = new HashMap();
                                                        newImage.put("profileImageUrl", downloadUrl);
                                                        databaseReference.updateChildren(newImage).addOnCompleteListener(
                                                                new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Toast.makeText(DonorRegistrationActivity.this,
                                                                                    "Image url added to database",
                                                                                    Toast.LENGTH_SHORT).show();
                                                                        } else {
                                                                            Toast.makeText(DonorRegistrationActivity.this,
                                                                                    task.getException().toString(),
                                                                                    Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                        finish();
                                                    }
                                                });
                                            }
                                        }
                                    });
                                    Intent intent = new Intent(DonorRegistrationActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    progress.dismiss();
                                }
                            }
                        }
                    });
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DonorRegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            resultUri = data.getData();
            profileImage.setImageURI(resultUri);
        }
    }
}