package com.example.followup.project_requests;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.followup.R;
import com.example.followup.requests.photography.AddPhotographyActivity;
import com.example.followup.requests.print.AddPrintActivity;
import com.example.followup.requests.production.AddProductionActivity;
import com.example.followup.requests.purchase.AddPurchaseActivity;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class RequestsActivity extends AppCompatActivity {

    FloatingActionButton addPhotography, addProduction, addPurchasing, addPrinting;
    TabLayout requests_tab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        initFields();

        requests_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
               int tabId= tab.getPosition();
               switch (tabId){
                   case 0:
                       Toast.makeText(getBaseContext(), "Purchase", Toast.LENGTH_SHORT).show();
                       break;
                   case 1:
                       Toast.makeText(getBaseContext(), "Print", Toast.LENGTH_SHORT).show();
                       break;
                   case 2:
                       Toast.makeText(getBaseContext(), "Production", Toast.LENGTH_SHORT).show();
                       break;
                   case 3:
                       Toast.makeText(getBaseContext(), "Photography", Toast.LENGTH_SHORT).show();
                       break;
               }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        addPrinting.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), AddPrintActivity.class)));
        addProduction.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), AddProductionActivity.class)));
        addPurchasing.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), AddPurchaseActivity.class)));
        addPhotography.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), AddPhotographyActivity.class)));
    }

    private void initFields() {
        addPhotography = findViewById(R.id.photography_btn);
        addProduction = findViewById(R.id.production_btn);
        addPurchasing = findViewById(R.id.purchase_btn);
        addPrinting = findViewById(R.id.print_btn);
        requests_tab = findViewById(R.id.requests_tab);
    }
}