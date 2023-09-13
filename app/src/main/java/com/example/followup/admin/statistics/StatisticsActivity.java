package com.example.followup.admin.statistics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.followup.R;
import com.example.followup.admin.statistics.job_orders.Statistics_job_orders;
import com.example.followup.admin.statistics.projects.Statistics_projects;
import com.example.followup.admin.statistics.requests.Statistics_requests;
import com.example.followup.admin.statistics.users.Statistics_users;
import com.google.android.material.tabs.TabLayout;

public class StatisticsActivity extends AppCompatActivity {

    TabLayout statistics_tab;
    ImageView back;
    int tabPosition;
    SwipeRefreshLayout swipe_refresh;

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

        setStatisticsFragment(tabPosition);
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

        swipe_refresh.setOnRefreshListener(() -> {
            swipe_refresh.setRefreshing(false);
            setStatisticsFragment(tabPosition);
        });
        back.setOnClickListener(v -> onBackPressed());

    }
    private void initFields() {
        statistics_tab = findViewById(R.id.statistics_tab);
        swipe_refresh = findViewById(R.id.swipe_refresh);
        back = findViewById(R.id.back);
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