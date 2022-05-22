package com.argonsoftwares.bloodlagbe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView name, bloodGroup, type, mobile, nid, email, dob, weight;
    private CircleImageView profileImage;
    private Button editProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Changing status bar color.
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.app_PrimaryColorActionbar));
        }

        toolbar = findViewById(R.id.profileView_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        profileImage = findViewById(R.id.profileView_Image);
        bloodGroup = findViewById(R.id.profileView_bloodGroup);
        name = findViewById(R.id.profileView_name);
        type = findViewById(R.id.profileView_type);
        mobile = findViewById(R.id.profileView_phoneNumber);
        nid = findViewById(R.id.profileView_nid);
        email = findViewById(R.id.profileView_email);
        dob = findViewById(R.id.profileView_dob);
        //weight = findViewById(R.id.profileView_weight);
        //editProfile = findViewById(R.id.btn_EditProfile);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        databaseReference.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    type.setText(snapshot.child("type").getValue().toString());
                    name.setText(snapshot.child("fullName").getValue().toString());
                    bloodGroup.setText("Blood Group: " + snapshot.child("bloodGroup").getValue().toString());
                    mobile.setText("Mobile: " + snapshot.child("phoneNumber").getValue().toString());
                    nid.setText("NID: " + snapshot.child("nid").getValue().toString());
                    email.setText("E-mail: " + snapshot.child("email").getValue().toString());
                    //dob.setText(snapshot.child("dob").getValue().toString());
                    //weight.setText(snapshot.child("weight").getValue().toString());

                    if (snapshot.child("profileImageUrl").exists()) {
                        Glide.with(getApplicationContext()).load(snapshot.child("profileImageUrl").getValue().toString()).into(profileImage);
                    } else {
                        profileImage.setImageResource(R.drawable.profile_avatar);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}