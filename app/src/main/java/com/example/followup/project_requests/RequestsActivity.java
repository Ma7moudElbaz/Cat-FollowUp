package com.example.followup.project_requests;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.followup.R;
import com.example.followup.requests.photography.AddPhotographyActivity;
import com.example.followup.requests.print.AddPrintActivity;
import com.example.followup.requests.production.AddProductionActivity;
import com.example.followup.requests.purchase.AddPurchaseActivity;
import com.github.clans.fab.FloatingActionButton;

public class RequestsActivity extends AppCompatActivity {

    FloatingActionButton addPhotography, addProduction, addPurchasing, addPrinting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        initFields();

        addPrinting.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), AddPrintActivity.class)));
        addProduction.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), AddProductionActivity.class)));
        addPurchasing.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), AddPurchaseActivity.class)));
        addPhotography.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), AddPhotographyActivity.class)));
    }

    private void initFields() {
        addPhotography = findViewById(R.id.photography_btn);
        addProduction = findViewById(R.id.production_btn);
        addPurchasing = findViewById(R.id.purchase_btn);
        addPrinting = findViewById(R.id.printing_btn);
    }
}