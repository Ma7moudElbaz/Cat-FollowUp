package com.example.followup.requests;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.example.followup.R;
import com.shuhart.stepview.StepView;

public class RequestDetailsActivity extends LocalizationActivity {

    StepView step_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);

//        step_view.go(1, true);


    }
}