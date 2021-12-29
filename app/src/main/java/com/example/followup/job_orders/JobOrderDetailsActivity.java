package com.example.followup.job_orders;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.example.followup.R;

public class JobOrderDetailsActivity extends LocalizationActivity {

    TextView download;
    int jobOrderId;
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
        pdfUrl = "https://saudiblood.org/pdf/Founding-Regulations.pdf";
        jobOrderId = getIntent().getIntExtra("job_order_id",0);

    }

}