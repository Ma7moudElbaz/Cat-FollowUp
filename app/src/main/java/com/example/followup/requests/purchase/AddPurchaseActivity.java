package com.example.followup.requests.purchase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.example.followup.R;

public class AddPurchaseActivity extends LocalizationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_purchase);
    }
}