package com.example.followup.project_requests;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.followup.R;
import com.example.followup.requests.photography.AddPhotographyActivity;
import com.example.followup.requests.printing.AddPrintingActivity;
import com.example.followup.requests.production.AddProductionActivity;
import com.example.followup.requests.purchasing.AddPurchasingActivity;
import com.github.clans.fab.FloatingActionButton;

public class RequestsActivity extends AppCompatActivity {

    FloatingActionButton addPhotography,addProduction,addPurchasing,addPrinting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        initFields();

        addPrinting.setOnClickListener(v -> {
            startActivity(new Intent(getBaseContext(), AddPrintingActivity.class));
        });

        addProduction.setOnClickListener(v -> {
            startActivity(new Intent(getBaseContext(), AddProductionActivity.class));
        });

        addPurchasing.setOnClickListener(v -> {
            startActivity(new Intent(getBaseContext(), AddPurchasingActivity.class));
        });

        addPhotography.setOnClickListener(v -> {
            startActivity(new Intent(getBaseContext(), AddPhotographyActivity.class));
        });
    }

    private void initFields() {
        addPhotography = findViewById(R.id.photography_btn);
        addProduction = findViewById(R.id.production_btn);
        addPurchasing = findViewById(R.id.purchase_btn);
        addPrinting = findViewById(R.id.printing_btn);
    }
}