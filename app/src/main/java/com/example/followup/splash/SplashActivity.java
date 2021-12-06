package com.example.followup.splash;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.example.followup.login.LoginActivity;
import com.example.followup.R;

public class SplashActivity extends LocalizationActivity {

    Button signIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        signIn = findViewById(R.id.btn_sign_in);
        signIn.setOnClickListener(v -> {
            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        });
    }
}