package com.example.followup.admin.statistics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.followup.R;
import com.example.followup.requests.list.Extras_requests_list;
import com.example.followup.requests.list.Photography_requests_list;
import com.example.followup.requests.list.Print_requests_list;
import com.example.followup.requests.list.Production_requests_list;
import com.example.followup.requests.list.Purchase_requests_list;
import com.google.android.material.tabs.TabLayout;

public class StatisticsActivity extends AppCompatActivity {

    TabLayout statistics_tab;
    int tabPosition;

    public void setContentFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.contentLayout, fragment);
        fragmentTransaction.commit();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        initFields();

        statistics_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabPosition = tab.getPosition();
                setStatisticsFragment(tabPosition);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
    private void initFields() {
        statistics_tab = findViewById(R.id.statistics_tab);
    }


    private void setStatisticsFragment(int selectedTab) {
        switch (selectedTab) {
            case 0:
                setContentFragment(new Statistics_users());
                break;
            case 1:
                setContentFragment(new Statistics_projects());
                break;
            case 2:
                setContentFragment(new Statistics_requests());
                break;
            case 3:
                setContentFragment(new Statistics_job_orders());
                break;
        }
    }
}