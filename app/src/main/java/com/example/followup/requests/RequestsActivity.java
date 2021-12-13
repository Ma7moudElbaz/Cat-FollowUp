package com.example.followup.requests;


import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.example.followup.R;
import com.example.followup.job_orders.JobOrdersActivity;
import com.example.followup.requests.photography.AddPhotographyActivity;
import com.example.followup.requests.photography.Photography_requests_fragment;
import com.example.followup.requests.print.AddPrintActivity;
import com.example.followup.requests.print.Print_requests_fragment;
import com.example.followup.requests.production.AddProductionActivity;
import com.example.followup.requests.production.Production_requests_fragment;
import com.example.followup.requests.purchase.AddPurchaseActivity;
import com.example.followup.requests.purchase.Purchase_requests_fragment;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;


public class RequestsActivity extends LocalizationActivity {

    ImageView back;
    FloatingActionButton addPhotography, addProduction, addPurchasing, addPrinting;
    TabLayout requests_tab;
    TextView job_orders;
    int projectId;
    int tabPosition;

    public int getProjectId() {
        return projectId;
    }

    public void setContentFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.contentLayout, fragment);
        fragmentTransaction.commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        initFields();

        back.setOnClickListener(v -> onBackPressed());
        job_orders.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), JobOrdersActivity.class)));

        requests_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                 tabPosition = tab.getPosition();
                 setRequestsFragment(tabPosition);

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
        projectId = getIntent().getIntExtra("project_id", 0);
        addPhotography = findViewById(R.id.photography_btn);
        addProduction = findViewById(R.id.production_btn);
        addPurchasing = findViewById(R.id.purchase_btn);
        addPrinting = findViewById(R.id.print_btn);
        requests_tab = findViewById(R.id.requests_tab);
        back = findViewById(R.id.back);
        job_orders = findViewById(R.id.job_orders);

    }


    @Override
    protected void onResume() {
        super.onResume();
        setContentFragment(new Purchase_requests_fragment());
    }

    private void setRequestsFragment(int selectedTab){
        switch (selectedTab) {
            case 0:
                setContentFragment(new Purchase_requests_fragment());
                break;
            case 1:
                setContentFragment(new Print_requests_fragment());
                break;
            case 2:
                setContentFragment(new Production_requests_fragment());
                break;
            case 3:
                setContentFragment(new Photography_requests_fragment());
                break;
        }
    }
}