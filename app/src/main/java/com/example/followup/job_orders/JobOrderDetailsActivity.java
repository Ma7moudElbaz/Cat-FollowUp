package com.example.followup.job_orders;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.example.followup.R;
import com.example.followup.bottomsheets.BottomSheet_choose_reason;
import com.example.followup.bottomsheets.BottomSheet_po_number;
import com.example.followup.utils.UserType;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.WebserviceContext;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import params.com.stepview.StatusViewScroller;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobOrderDetailsActivity extends LocalizationActivity implements BottomSheet_choose_reason.ReasonSubmitListener, BottomSheet_po_number.PoNumberSubmitListener {

    private ProgressDialog dialog;
    LinearLayout sales_approval_layout, magdi_approval_layout, hesham_approval_layout, ceo_approval_layout;
    Button sales_approve, sales_reject, magdi_approve, magdi_hold, hesham_approve, hesham_reject, hesham_ceo_approval, ceo_approve, ceo_reject;
    ProgressBar loading;
    ImageView back;
    TextView download, financial_reasons, ceo_reasons;
    int jobOrderId, projectId;
    int jobOrderStatus;
    String poNumber;
    StatusViewScroller steps;
    String pdfUrl;

    SwipeRefreshLayout swipe_refresh;

    WebserviceContext ws;

    ImageView joStepperImg;

    public void showReasonSheet(String title, String subtitle, String reasonHint, String type) {
        BottomSheet_choose_reason langBottomSheet =
                new BottomSheet_choose_reason(title, subtitle, reasonHint, type);
        langBottomSheet.show(getSupportFragmentManager(), type);
    }

    public void showPoNumberSheet() {
        BottomSheet_po_number langBottomSheet =
                new BottomSheet_po_number("po_number");
        langBottomSheet.show(getSupportFragmentManager(), "po_number");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_order_details);
        initFields();
        back.setOnClickListener(v -> onBackPressed());
        download.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl)), null));
        sales_reject.setOnClickListener(v -> updateStatusDialog(2, "", ""));
        sales_approve.setOnClickListener(v -> {
            if (poNumber.equals("null")) {
                showPoNumberSheet();
            } else {
                updateStatusDialog(3, "", "");
            }
        });
        magdi_hold.setOnClickListener(v -> updateStatusDialog(4, "", ""));
        magdi_approve.setOnClickListener(v -> updateStatusDialog(5, "", ""));
        hesham_reject.setOnClickListener(v -> showReasonSheet("Rejection reason", "", "", "hesham"));
        hesham_approve.setOnClickListener(v -> updateStatusDialog(7, "", ""));
        hesham_ceo_approval.setOnClickListener(v -> updateStatusDialog(8, "", ""));
        ceo_reject.setOnClickListener(v -> showReasonSheet("Rejection reason", "", "", "ceo"));
        ceo_approve.setOnClickListener(v -> updateStatusDialog(10, "", ""));

        swipe_refresh.setOnRefreshListener(() -> {
            swipe_refresh.setRefreshing(false);
            onResume();
        });

    }

    private void initFields() {

        ws = new WebserviceContext(this);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Please, Wait...");
        dialog.setCancelable(false);

        back = findViewById(R.id.back);
        loading = findViewById(R.id.loading);
        download = findViewById(R.id.download);
        sales_approval_layout = findViewById(R.id.sales_approval_layout);
        magdi_approval_layout = findViewById(R.id.magdi_approval_layout);
        hesham_approval_layout = findViewById(R.id.hesham_approval_layout);
        ceo_approval_layout = findViewById(R.id.ceo_approval_layout);
        steps = findViewById(R.id.steps);
        sales_approve = findViewById(R.id.sales_approve);
        sales_reject = findViewById(R.id.sales_reject);
        magdi_approve = findViewById(R.id.magdi_approve);
        magdi_hold = findViewById(R.id.magdi_hold);
        hesham_approve = findViewById(R.id.hesham_approve);
        hesham_reject = findViewById(R.id.hesham_reject);
        hesham_ceo_approval = findViewById(R.id.hesham_ceo_approval);
        ceo_approve = findViewById(R.id.ceo_approve);
        ceo_reject = findViewById(R.id.ceo_reject);

        financial_reasons = findViewById(R.id.financial_reasons);
        ceo_reasons = findViewById(R.id.ceo_reasons);

        String loggedInUser = UserType.getUserType(UserUtils.getParentId(getBaseContext()), UserUtils.getChildId(getBaseContext()));
        if (loggedInUser.equals("hesham")) {
            ceo_reasons.setVisibility(View.VISIBLE);
        }

        swipe_refresh = findViewById(R.id.swipe_refresh);
        joStepperImg = findViewById(R.id.joStepperImg);


        jobOrderId = getIntent().getIntExtra("job_order_id", 0);
    }


    private void setUserJobOrderPermissions(int jobOrderStatus, boolean canEditProject, int ceo) {
        setJoStepper(jobOrderStatus,ceo);
        String loggedInUser = UserType.getUserType(UserUtils.getParentId(getBaseContext()), UserUtils.getChildId(getBaseContext()));
        resetData();
        switch (jobOrderStatus) {
            case 1: {
                steps.getStatusView().setCurrentCount(2);
                if (canEditProject) {
                    sales_approval_layout.setVisibility(View.VISIBLE);
                } else {
                    sales_approval_layout.setVisibility(View.GONE);
                }
                break;
            }
            case 3:
            case 4: {
                steps.getStatusView().setCurrentCount(3);
                if (loggedInUser.equals("magdi")) {
                    magdi_approval_layout.setVisibility(View.VISIBLE);
                } else {
                    magdi_approval_layout.setVisibility(View.GONE);
                }
                break;
            }
            case 5:
            case 10: {
                steps.getStatusView().setCurrentCount(4);
                if (loggedInUser.equals("hesham")) {
                    hesham_approval_layout.setVisibility(View.VISIBLE);
                    if (ceo == 0){
                        hesham_approve.setVisibility(View.GONE);
                    }else {
                        hesham_approve.setVisibility(View.VISIBLE);
                    }
                } else {
                    hesham_approval_layout.setVisibility(View.GONE);
                }
                break;
            }
            case 6: {
                //hesham rejected
                steps.getStatusView().setCurrentCount(4);
                break;
            }
            case 7: {
                steps.getStatusView().setCurrentCount(5);
                break;
            }
            case 8: {
                if (loggedInUser.equals("ceo")) {
                    ceo_approval_layout.setVisibility(View.VISIBLE);
                } else {
                    ceo_approval_layout.setVisibility(View.GONE);
                }
                break;
            }
            case 9: {
                //ceo rejected
                break;
            }
        }
    }

    private void setJoStepper(int joStatus, int ceo) {
        switch (joStatus) {
            case 1: {
                joStepperImg.setImageResource(R.drawable.jo_1);
                break;
            }
            case 2: {
                joStepperImg.setImageResource(R.drawable.jo_2);
                break;
            }
            case 3: {
                joStepperImg.setImageResource(R.drawable.jo_3);
                break;
            }
            case 4: {
                joStepperImg.setImageResource(R.drawable.jo_4);
                break;
            }
            case 5: {
                joStepperImg.setImageResource(R.drawable.jo_5);
                break;
            }
            case 6: {
                if (ceo == 1) {
                    joStepperImg.setImageResource(R.drawable.jo_6_ceo);
                } else {
                    joStepperImg.setImageResource(R.drawable.jo_6);
                }
                break;
            }
            case 7: {
                if (ceo == 1) {
                    joStepperImg.setImageResource(R.drawable.jo_7_ceo);
                } else {
                    joStepperImg.setImageResource(R.drawable.jo_7);
                }
                break;
            }
            case 8: {
                joStepperImg.setImageResource(R.drawable.jo_8);
                break;
            }
            case 9: {
                joStepperImg.setImageResource(R.drawable.jo_9);
                break;
            }
            case 10: {
                joStepperImg.setImageResource(R.drawable.jo_10);
                break;
            }
        }
    }

    private void resetData() {
        sales_approval_layout.setVisibility(View.GONE);
        magdi_approval_layout.setVisibility(View.GONE);
        hesham_approval_layout.setVisibility(View.GONE);
        ceo_approval_layout.setVisibility(View.GONE);
        steps.setVisibility(View.VISIBLE);
    }

    public void updateStatusDialog(int status, String ceo_reasons, String financial_reasons) {
        new AlertDialog.Builder(JobOrderDetailsActivity.this)
                .setTitle("Are you sure? ")
                .setPositiveButton("Yes", (dialog, which) -> {
                    updateStatus(status, ceo_reasons, financial_reasons);
                })
                .setNegativeButton("Dismiss", null)
                .show();
    }


    public void updateStatus(int status, String ceo_reasons, String financial_reasons) {
        Map<String, String> map = new HashMap<>();
        map.put("job_order_id", String.valueOf(jobOrderId));
        map.put("status", String.valueOf(status));
        map.put("ceo_reasons", ceo_reasons);
        map.put("financial_reasons", financial_reasons);

        dialog.show();
        ws.getApi().changeJobOrderStatus(UserUtils.getAccessToken(getBaseContext()), map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.code() == 200 || response.code() == 201) {
                        Toast.makeText(getBaseContext(), "Updated successfully", Toast.LENGTH_LONG).show();
                        getJobOrderDetails();
                    } else {
                        Toast.makeText(getBaseContext(), response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getBaseContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void getJobOrderDetails() {
        loading.setVisibility(View.VISIBLE);

        ws.getApi().getJobOrderDetails(UserUtils.getAccessToken(getBaseContext()), jobOrderId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    loading.setVisibility(View.GONE);
                    JSONObject responseObject = new JSONObject(response.body().string());
                    JSONObject dataObj = responseObject.getJSONObject("data");
                    String pdfLinkUrl = dataObj.getString("url");
                    pdfUrl = "https://docs.google.com/viewerng/viewer?url=" + pdfLinkUrl;

                    jobOrderStatus = dataObj.getInt("status");
                    poNumber = dataObj.getString("po_number");
                    projectId = dataObj.getInt("project_id");

                    String ceo_reasons_text = dataObj.getString("ceo_reasons");
                    String financial_reasons_text = dataObj.getString("financial_reasons");

                    if (!ceo_reasons_text.equals("null")) {
                        ceo_reasons.setText("CEO Rejection Reasons : " + ceo_reasons_text);
                    }
                    if (!financial_reasons_text.equals("null")) {
                        financial_reasons.setText("Hisham Rejection Reasons : " + financial_reasons_text);
                    }

                    int project_creator_id = dataObj.getInt("project_creator_id");
                    final String assigned_to = dataObj.getString("project_assign_id");
                    int assigned_to_id = 0;
                    if (!assigned_to.equals("null")) {
                        assigned_to_id = Integer.parseInt(assigned_to);
                    }
                    boolean canEditProject = UserType.canEditProject(getBaseContext(), project_creator_id, assigned_to_id);
                    int ceo = dataObj.getInt("ceo");
                    setUserJobOrderPermissions(jobOrderStatus, canEditProject, ceo);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d("Error Throw", t.toString());
                Log.d("commit Test Throw", t.toString());
                Log.d("Call", t.toString());
                Toast.makeText(getBaseContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void addPoNumber(int projectId, String poNumber) {
        Map<String, String> map = new HashMap<>();
        map.put("project_id", String.valueOf(projectId));
        map.put("number", poNumber);

        dialog.show();
        ws.getApi().addPoNumber(UserUtils.getAccessToken(getBaseContext()), map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.code() == 200 || response.code() == 201) {
                        Toast.makeText(getBaseContext(), "PO Added successfully", Toast.LENGTH_LONG).show();
                        getJobOrderDetails();
                    } else {
                        Toast.makeText(getBaseContext(), response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getBaseContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getJobOrderDetails();
    }


    @Override
    public void reasonSubmitListener(String reason, String type) {
        switch (type) {
            case "ceo":
                updateStatusDialog(9, reason, "");
                break;
            case "hesham":
                updateStatusDialog(6, "", reason);
                break;
        }
    }

    @Override
    public void poNumberSubmitListener(String po_number, String type) {
        addPoNumber(projectId, po_number);
    }
}