package com.example.followup.requests.request_details;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.example.followup.R;
import com.example.followup.home.profile.ProfileFragment;
import com.example.followup.home.projects.ProjectsFragment;
import com.example.followup.home.settings.SettingsFragment;
import com.example.followup.requests.list.Photography_requests_fragment;
import com.example.followup.requests.view.Purchase_view;
import com.shuhart.stepview.StepView;

public class RequestDetailsActivity extends LocalizationActivity {

    StepView step_view;
    boolean isDetailsExpanded = false;
    boolean isCostExpanded = false;
    ImageView expandDetails, expandCost;
    FrameLayout request_details_content, request_cost_content;

    public void setDetailsFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.request_details_content, fragment);
        fragmentTransaction.commit();
    }

    public void setCostFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.request_cost_content, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        step_view = findViewById(R.id.step_view);
        initFields();
        step_view.go(1, true);

        setDetailsFragment(new Purchase_view());
        setCostFragment(new ProfileFragment());

        expandDetails.setOnClickListener(v -> toggleDetails(isDetailsExpanded));

        expandCost.setOnClickListener(v -> toggleCost(isCostExpanded));

    }

    private void initFields() {
        expandDetails = findViewById(R.id.expand_details);
        expandCost = findViewById(R.id.expand_cost);
        request_details_content = findViewById(R.id.request_details_content);
        request_cost_content = findViewById(R.id.request_cost_content);
    }

    private void expandDetails(){
        expandDetails.setImageResource(R.drawable.ic_arrow_up);
        request_details_content.setVisibility(View.VISIBLE);
        isDetailsExpanded =true;

        expandCost.setImageResource(R.drawable.ic_arrow_down);
        request_cost_content.setVisibility(View.GONE);
        isCostExpanded = false;
    }

    private void expandCost(){
        expandCost.setImageResource(R.drawable.ic_arrow_up);
        request_cost_content.setVisibility(View.VISIBLE);
        isCostExpanded = true;

        expandDetails.setImageResource(R.drawable.ic_arrow_down);
        request_details_content.setVisibility(View.GONE);
        isDetailsExpanded =false;
    }

    private void toggleCost(boolean expanded){
        if (expanded) {
            expandCost.setImageResource(R.drawable.ic_arrow_down);
            request_cost_content.setVisibility(View.GONE);
            isCostExpanded = false;
        } else {
            expandCost();
        }
    }

    private void toggleDetails(boolean expanded){
        if (expanded) {
            expandDetails.setImageResource(R.drawable.ic_arrow_down);
            request_details_content.setVisibility(View.GONE);
            isDetailsExpanded = false;
        } else {
            expandDetails();
        }
    }
}