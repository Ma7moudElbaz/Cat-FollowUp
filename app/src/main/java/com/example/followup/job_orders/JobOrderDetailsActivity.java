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
import com.example.followup.utils.UserType;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.Webservice;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import params.com.stepview.StatusViewScroller;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobOrderDetailsActivity extends LocalizationActivity implements BottomSheet_choose_reason.ReasonSubmitListener {

    private ProgressDialog dialog;
    LinearLayout sales_approval_layout, magdi_approval_layout, hesham_approval_layout, ceo_approval_layout;
    Button sales_approve, sales_reject, magdi_approve, magdi_hold, hesham_approve, hesham_reject, hesham_ceo_approval, ceo_approve, ceo_reject;
    ProgressBar loading;
    ImageView back;
    ImageView ceoSteps;
    TextView download;
    int jobOrderId, projectId;
    int jobOrderStatus;
    String poNumber;
    StatusViewScroller steps;
    String pdfUrl;

    SwipeRefreshLayout swipe_refresh;

    public void showPoNumberBottomSheet() {
        BottomSheet_choose_reason langBottomSheet =
                new BottomSheet_choose_reason("Add your project po number to proceed", "PO Number", "", "po");
        langBottomSheet.show(getSupportFragmentManager(), "po");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_order_details);
        initFields();
        back.setOnClickListener(v -> onBackPressed());
        download.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))));
        sales_reject.setOnClickListener(v -> updateStatusDialog(2, ""));
        sales_approve.setOnClickListener(v -> {
            if (poNumber.equals("null")) {
                showPoNumberBottomSheet();
            } else {
                updateStatusDialog(3, "");
            }
        });
        magdi_hold.setOnClickListener(v -> updateStatusDialog(4, ""));
        magdi_approve.setOnClickListener(v -> updateStatusDialog(5, ""));
        hesham_reject.setOnClickListener(v -> updateStatusDialog(6, ""));
        hesham_approve.setOnClickListener(v -> updateStatusDialog(7, ""));
        hesham_ceo_approval.setOnClickListener(v -> updateStatusDialog(8, ""));
        ceo_reject.setOnClickListener(v -> updateStatusDialog(9, ""));
        ceo_approve.setOnClickListener(v -> updateStatusDialog(10, ""));

        swipe_refresh.setOnRefreshListener(() -> {
            swipe_refresh.setRefreshing(false);
            onResume();
        });

    }

    private void initFields() {

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
        ceoSteps = findViewById(R.id.ceo_steps);
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

        swipe_refresh = findViewById(R.id.swipe_refresh);


        jobOrderId = getIntent().getIntExtra("job_order_id", 0);
        projectId = getIntent().getIntExtra("project_id", 0);
    }


    private void setUserJobOrderPermissions(int jobOrderStatus) {
        Log.e("jobOrderStatus", String.valueOf(jobOrderStatus));
        String loggedInUser = UserType.getUserType(UserUtils.getParentId(getBaseContext()), UserUtils.getChildId(getBaseContext()));
        Log.e("loggedInUser", loggedInUser);
        resetData();
        switch (jobOrderStatus) {
            case 1: {
                steps.getStatusView().setCurrentCount(2);
                if (loggedInUser.equals("sales")) {
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
                steps.setVisibility(View.GONE);
                ceoSteps.setVisibility(View.VISIBLE);
                if (loggedInUser.equals("ceo")) {
                    ceo_approval_layout.setVisibility(View.VISIBLE);
                } else {
                    ceo_approval_layout.setVisibility(View.GONE);
                }
                break;
            }
            case 9: {
                //ceo rejected
                steps.setVisibility(View.GONE);
                ceoSteps.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    private void resetData() {
        sales_approval_layout.setVisibility(View.GONE);
        magdi_approval_layout.setVisibility(View.GONE);
        hesham_approval_layout.setVisibility(View.GONE);
        ceo_approval_layout.setVisibility(View.GONE);
        ceoSteps.setVisibility(View.GONE);
        steps.setVisibility(View.VISIBLE);
    }

    public void updateStatusDialog(int status, String reason) {
        new AlertDialog.Builder(JobOrderDetailsActivity.this)
                .setTitle("Are you sure? ")
                .setPositiveButton("Yes", (dialog, which) -> {
                    updateStatus(status, reason);
                })
                .setNegativeButton("Dismiss", null)
                .show();
    }


    public void updateStatus(int status, String reason) {
        Map<String, String> map = new HashMap<>();
        map.put("job_order_id", String.valueOf(jobOrderId));
        map.put("status", String.valueOf(status));
//        map.put("reason", reason);

        dialog.show();
        Webservice.getInstance().getApi().changeJobOrderStatus(UserUtils.getAccessToken(getBaseContext()), map).enqueue(new Callback<ResponseBody>() {
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

        Webservice.getInstance().getApi().getJobOrderDetails(UserUtils.getAccessToken(getBaseContext()), jobOrderId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    loading.setVisibility(View.GONE);
                    JSONObject responseObject = new JSONObject(response.body().string());
                    JSONObject dataObj = responseObject.getJSONObject("data");
                    pdfUrl = dataObj.getString("url");
                    jobOrderStatus = dataObj.getInt("status");
                    poNumber = dataObj.getString("po_number");
                    setUserJobOrderPermissions(jobOrderStatus);

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
        Webservice.getInstance().getApi().addPoNumber(UserUtils.getAccessToken(getBaseContext()), map).enqueue(new Callback<ResponseBody>() {
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
    public void reasonSubmitListener(String poNumber, String type) {
        addPoNumber(projectId, poNumber);
    }
}