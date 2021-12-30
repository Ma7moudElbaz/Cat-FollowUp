package com.example.followup.job_orders;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.example.followup.R;
import com.example.followup.utils.UserType;
import com.example.followup.utils.UserUtils;

import params.com.stepview.StatusView;

public class JobOrderDetailsActivity extends LocalizationActivity {

    LinearLayout sales_approval_layout, magdi_approval_layout, hesham_approval_layout, ceo_approval_layout;
    Button sales_approve, sales_reject, magdi_approve, magdi_hold, hesham_approve, hesham_reject, hesham_ceo_approval, ceo_approve, ceo_reject;
    ImageView ceoSteps;
    TextView download;
    int jobOrderId;
    int jobOrderStatus;
    StatusView steps;
    String pdfUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_order_details);
        initFields();
        download.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl)));
        });
    }

    private void initFields() {
        download = findViewById(R.id.download);
        sales_approval_layout = findViewById(R.id.sales_approval_layout);
        magdi_approval_layout = findViewById(R.id.magdi_approval_layout);
        hesham_approval_layout = findViewById(R.id.hesham_approval_layout);
        ceo_approval_layout = findViewById(R.id.ceo_approval_layout);
        ceoSteps = findViewById(R.id.ceo_steps);
        sales_approve = findViewById(R.id.sales_approve);
        sales_reject = findViewById(R.id.sales_reject);
        magdi_approve = findViewById(R.id.magdi_approve);
        magdi_hold = findViewById(R.id.magdi_hold);
        hesham_approve = findViewById(R.id.hesham_approve);
        hesham_reject = findViewById(R.id.hesham_reject);
        hesham_ceo_approval = findViewById(R.id.hesham_ceo_approval);
        ceo_approve = findViewById(R.id.ceo_approve);
        ceo_reject = findViewById(R.id.ceo_reject);

        pdfUrl = "https://saudiblood.org/pdf/Founding-Regulations.pdf";

        jobOrderId = getIntent().getIntExtra("job_order_id", 0);
        jobOrderStatus = getIntent().getIntExtra("job_order_status", 0);
        setUserJobOrderPermissions(jobOrderStatus);
    }


    private void setUserJobOrderPermissions(int jobOrderStatus) {
        Log.e("jobOrderStatus", String.valueOf(jobOrderStatus));
        String loggedInUser = UserType.getUserType(UserUtils.getParentId(getBaseContext()), UserUtils.getChildId(getBaseContext()));
        Log.e("loggedInUser", loggedInUser);
        resetData();
        switch (jobOrderStatus) {
            case 1: {
                steps.setCurrentCount(2);
                if (loggedInUser.equals("sales")) {
                    sales_approval_layout.setVisibility(View.VISIBLE);
                } else {
                    sales_approval_layout.setVisibility(View.GONE);
                }
                break;
            }
            case 3:
            case 4: {
                steps.setCurrentCount(3);
                if (loggedInUser.equals("magdi")) {
                    magdi_approval_layout.setVisibility(View.VISIBLE);
                } else {
                    magdi_approval_layout.setVisibility(View.GONE);
                }
            }
            case 5:
            case 10: {
                steps.setCurrentCount(4);
                if (loggedInUser.equals("hesham")) {
                    hesham_approval_layout.setVisibility(View.VISIBLE);
                } else {
                    hesham_approval_layout.setVisibility(View.GONE);
                }
            }
            case 6: {
                //hesham rejected
                steps.setCurrentCount(4);
            }
            case 7: {
                steps.setCurrentCount(5);
            }
            case 8: {
                steps.setVisibility(View.GONE);
                ceoSteps.setVisibility(View.VISIBLE);
                if (loggedInUser.equals("ceo")) {
                    ceo_approval_layout.setVisibility(View.VISIBLE);
                } else {
                    ceo_approval_layout.setVisibility(View.GONE);
                }
            }
            case 9: {
                //ceo rejected
                steps.setVisibility(View.GONE);
                ceoSteps.setVisibility(View.VISIBLE);
            }
        }
    }

    private void resetData() {
        sales_approval_layout.setVisibility(View.GONE);
        magdi_approval_layout.setVisibility(View.GONE);
        hesham_approval_layout.setVisibility(View.GONE);
        ceo_approval_layout.setVisibility(View.GONE);
        ceoSteps.setVisibility(View.GONE);
    }

}