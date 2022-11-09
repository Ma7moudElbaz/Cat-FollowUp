package com.example.followup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.example.followup.home.HomeActivity;

public class ResultActivity extends LocalizationActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent i = new Intent(getBaseContext(), HomeActivity.class);
        i.putExtra("fromActivity","notification");
        startActivity(i);
        finish();
    }
}