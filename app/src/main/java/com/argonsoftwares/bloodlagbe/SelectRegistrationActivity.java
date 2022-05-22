package com.argonsoftwares.bloodlagbe;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SelectRegistrationActivity extends AppCompatActivity {

    private Button btn_DonorTypeRegistration, btn_RecipientTypeRegistration;
    private TextView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_registration);

        // Changing status bar color.
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.app_PrimaryColorActionbar));
        }

        btn_DonorTypeRegistration = findViewById(R.id.btn_DonorTypeRegistration);
        btn_RecipientTypeRegistration = findViewById(R.id.btn_RecipientTypeRegistration);
        backButton = findViewById(R.id.text_AlreadyRegistered);

        btn_DonorTypeRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectRegistrationActivity.this, DonorRegistrationActivity.class);
                startActivity(intent);
            }
        });

        btn_RecipientTypeRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectRegistrationActivity.this, RecipientRegistrationActivity.class);
                startActivity(intent);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectRegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
