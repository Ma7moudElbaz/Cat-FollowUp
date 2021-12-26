package com.example.followup.requests;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.example.followup.R;
import com.example.followup.job_orders.JobOrdersActivity;
import com.example.followup.requests.add.AddPhotographyActivity;
import com.example.followup.requests.list.Photography_requests_list;
import com.example.followup.requests.add.AddPrintActivity;
import com.example.followup.requests.list.Print_requests_list;
import com.example.followup.requests.add.AddProductionActivity;
import com.example.followup.requests.list.Production_requests_list;
import com.example.followup.requests.add.AddPurchaseActivity;
import com.example.followup.requests.list.Purchase_requests_list;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;


public class RequestsActivity extends LocalizationActivity {

    ImageView back;
    FloatingActionButton addPhotography, addProduction, addPurchasing, addPrinting;
    TabLayout requests_tab;
    TextView job_orders,project_name;
    int projectId;
    String projectName;
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

        project_name.setText(projectName);
        back.setOnClickListener(v -> onBackPressed());

        job_orders.setOnClickListener(v -> {
            Intent i =new Intent(getBaseContext(), JobOrdersActivity.class);
            i.putExtra("project_id",projectId);
            startActivity(i);
        });
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

        addPrinting.setOnClickListener(v -> {
            Intent i = new Intent(getBaseContext(), AddPrintActivity.class);
            i.putExtra("project_id", projectId);
            startActivity(i);
        });

        addProduction.setOnClickListener(v -> {
            Intent i = new Intent(getBaseContext(), AddProductionActivity.class);
            i.putExtra("project_id", projectId);
            startActivity(i);
        });

        addPurchasing.setOnClickListener(v -> {
            Intent i = new Intent(getBaseContext(), AddPurchaseActivity.class);
            i.putExtra("project_id", projectId);
            startActivity(i);
        });

        addPhotography.setOnClickListener(v -> {
            Intent i = new Intent(getBaseContext(), AddPhotographyActivity.class);
            i.putExtra("project_id", projectId);
            startActivity(i);
        });
    }


    private void initFields() {
        projectId = getIntent().getIntExtra("project_id", 0);
        projectName = getIntent().getStringExtra("project_name");
        addPhotography = findViewById(R.id.photography_btn);
        addProduction = findViewById(R.id.production_btn);
        addPurchasing = findViewById(R.id.purchase_btn);
        addPrinting = findViewById(R.id.print_btn);
        requests_tab = findViewById(R.id.requests_tab);
        back = findViewById(R.id.back);
        job_orders = findViewById(R.id.job_orders);
        project_name = findViewById(R.id.project_name);

    }


    @Override
    protected void onResume() {
        super.onResume();
        setRequestsFragment(tabPosition);
    }

    private void setRequestsFragment(int selectedTab) {
        switch (selectedTab) {
            case 0:
                setContentFragment(new Purchase_requests_list());
                break;
            case 1:
                setContentFragment(new Print_requests_list());
                break;
            case 2:
                setContentFragment(new Production_requests_list());
                break;
            case 3:
                setContentFragment(new Photography_requests_list());
                break;
        }
    }
}