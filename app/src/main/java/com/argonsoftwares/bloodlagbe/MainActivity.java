package com.argonsoftwares.bloodlagbe;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.argonsoftwares.bloodlagbe.Adapter.UserAdapter;
import com.argonsoftwares.bloodlagbe.Model.User;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private CircleImageView nav_profileImage;
    private TextView nav_fullName, nav_email, nav_bloodGroup, nav_type;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private List<User> userList;
    private UserAdapter userAdapter;

    // Firebase variables
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Changing status bar color.
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.app_PrimaryColorActionbar));
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Blood Lagbe");

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        nav_profileImage = navigationView.getHeaderView(0).findViewById(R.id.nav_userProfileImage);
        nav_fullName = navigationView.getHeaderView(0).findViewById(R.id.nav_userFullName);
        nav_email = navigationView.getHeaderView(0).findViewById(R.id.nav_userEmail);
        nav_bloodGroup = navigationView.getHeaderView(0).findViewById(R.id.nav_userBloodGroup);
        nav_type = navigationView.getHeaderView(0).findViewById(R.id.nav_userUserType);

        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList);
        recyclerView.setAdapter(userAdapter);

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(FirebaseAuth
                        .getInstance()
                        .getCurrentUser()
                        .getUid());
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type = snapshot.child("type").getValue().toString();
                if (type.equals("Donor")) {
                    readRecipientsData();
                } else {
                    readDonorsData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference = FirebaseDatabase.getInstance().
                getReference().
                child("users").
                child(FirebaseAuth.getInstance().
                        getCurrentUser().getUid()
                );
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fullName = snapshot.child("fullName").getValue().toString();
                    nav_fullName.setText(fullName);

                    String email = snapshot.child("email").getValue().toString();
                    nav_email.setText(email);

                    String bloodGroup = snapshot.child("bloodGroup").getValue().toString();
                    nav_bloodGroup.setText("Blood Group: " + bloodGroup);

                    String userType = snapshot.child("type").getValue().toString();
                    nav_type.setText("Type: " + userType);

                    if (snapshot.child("profileImageUrl").exists()) {
                        String imageUrl = snapshot.child("profileImageUrl").getValue().toString();
                        Glide.with(getApplicationContext()).load(imageUrl).into(nav_profileImage);
                    } else {
                        nav_profileImage.setImageResource(R.drawable.profile_avatar);
                    }

                    Menu menu = navigationView.getMenu();
                    if (nav_type.equals("Donor")) {
                        menu.findItem(R.id.sentEmail).setTitle("Received Emails");
                        menu.findItem(R.id.sentEmail).setVisible(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readDonorsData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = databaseReference.orderByChild("type").equalTo("Donor");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                if (userList.isEmpty()) {
                    Toast.makeText(
                                    getApplicationContext(),
                                    "No users found",
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readRecipientsData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = databaseReference.orderByChild("type").equalTo("Recipient");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                if (userList.isEmpty()) {
                    Toast.makeText(
                                    getApplicationContext(),
                                    "No users found",
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sentEmail:
                Intent sentEmailIntent = new Intent(MainActivity.this, SentEmailActivity.class);
                sentEmailIntent.putExtra("group", "Compatible with me");
                startActivity(sentEmailIntent);
                break;

            case R.id.compatible:
                Intent compatibleIntent = new Intent(MainActivity.this, CategorySelectedActivity.class);
                compatibleIntent.putExtra("group", "Compatible with me");
                startActivity(compatibleIntent);
                break;

            case R.id.blood_aPositive:
                Intent aPositiveIntent = new Intent(MainActivity.this, CategorySelectedActivity.class);
                aPositiveIntent.putExtra("group", "A+");
                startActivity(aPositiveIntent);
                break;

            case R.id.blood_aNegative:
                Intent aNegativeIntent = new Intent(MainActivity.this, CategorySelectedActivity.class);
                aNegativeIntent.putExtra("group", "A-");
                startActivity(aNegativeIntent);
                break;

            case R.id.blood_bPositive:
                Intent bPositiveIntent = new Intent(MainActivity.this, CategorySelectedActivity.class);
                bPositiveIntent.putExtra("group", "B+");
                startActivity(bPositiveIntent);
                break;

            case R.id.blood_bNegative:
                Intent bNegativeIntent = new Intent(MainActivity.this, CategorySelectedActivity.class);
                bNegativeIntent.putExtra("group", "B-");
                startActivity(bNegativeIntent);
                break;

            case R.id.blood_abPositive:
                Intent abPositiveIntent = new Intent(MainActivity.this, CategorySelectedActivity.class);
                abPositiveIntent.putExtra("group", "AB+");
                startActivity(abPositiveIntent);
                break;

            case R.id.blood_abNegative:
                Intent abNegativeIntent = new Intent(MainActivity.this, CategorySelectedActivity.class);
                abNegativeIntent.putExtra("group", "AB-");
                startActivity(abNegativeIntent);
                break;

            case R.id.blood_oNegative:
                Intent oNegativeIntent = new Intent(MainActivity.this, CategorySelectedActivity.class);
                oNegativeIntent.putExtra("group", "AB-");
                startActivity(oNegativeIntent);
                break;

            case R.id.blood_oPositive:
                Intent oPositibeIntent = new Intent(MainActivity.this, CategorySelectedActivity.class);
                oPositibeIntent.putExtra("group", "O+");
                startActivity(oPositibeIntent);
                break;

            case R.id.notifications:
                Intent notificationsIntent = new Intent(MainActivity.this, CategorySelectedActivity.class);
                startActivity(notificationsIntent);
                break;

            case R.id.profile:
                Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
                break;
            case R.id.logOut:
                FirebaseAuth.getInstance().signOut();
                Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(logoutIntent);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}