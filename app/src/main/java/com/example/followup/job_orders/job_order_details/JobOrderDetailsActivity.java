package com.example.followup.job_orders.job_order_details;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.example.followup.R;
import com.example.followup.bottomsheets.BottomSheet_choose_reason;
import com.example.followup.bottomsheets.BottomSheet_po_number;
import com.example.followup.job_orders.edit_job_order.EditJobOrderActivity;
import com.example.followup.job_orders.job_order_details.comments.CommentsActivity;
import com.example.followup.job_orders.job_order_details.payment.Payment_item;
import com.example.followup.job_orders.job_order_details.payment.Payments_adapter;
import com.example.followup.utils.RealPathUtil;
import com.example.followup.utils.StringCheck;
import com.example.followup.utils.UserType;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.WebserviceContext;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobOrderDetailsActivity extends LocalizationActivity implements BottomSheet_choose_reason.ReasonSubmitListener, BottomSheet_po_number.PoNumberSubmitListener {

    private ProgressDialog dialog;
    LinearLayout sales_approval_layout, magdi_approval_layout, hesham_approval_layout, ceo_approval_layout, payment_layout;
    RelativeLayout adel_approval_layout;
    Button sales_approve, sales_reject, magdi_approve, magdi_hold, hesham_approve, hesham_reject, hesham_ceo_approval, ceo_approve, ceo_reject, adel_pay, choose_file;
    TextView filesChosen;
    TextView payment_percent_txt, adel_seen_txt, adel_unseen_txt, txt_hany_notes;
    LinearProgressIndicator progress_indicator;
    EditText payment_percent;
    List<String> filesSelected;
    ImageView back;
    TextView download, reasons, edit;
    int jobOrderId, projectId;
    int jobOrderStatus;
    String poNumber;
    String pdfUrl;
    ExtendedFloatingActionButton btn_comments, btn_ceo_reminder,btn_sales_reminder;


    RecyclerView recyclerView;
    ArrayList<Payment_item> payments_list;
    Payments_adapter payments_adapter;

    SwipeRefreshLayout swipe_refresh;

    WebserviceContext ws;

    ImageView joStepperImg;

    int countryId;

    private static final int FILES_REQUEST_CODE = 764546;

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

        download.setOnClickListener(v -> {
            Intent i = new Intent(getBaseContext(),PdfViewer.class);
            i.putExtra("url",pdfUrl);
            startActivity(i);
        });

        sales_reject.setOnClickListener(v -> showReasonSheet("Rejection reason", "", "", "sales"));
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
        hesham_approve.setOnClickListener(view -> {
            if (countryId == 2) {
                showReasonSheet("Notes", "", "", "hany_approve");
            } else {
                updateStatusDialog(7, "", "");
            }
        });
        hesham_ceo_approval.setOnClickListener(v -> updateStatusDialog(8, "", ""));

        ceo_reject.setOnClickListener(v -> showReasonSheet("Rejection reason", "", "", "ceo"));
        ceo_approve.setOnClickListener(v -> updateStatusDialog(10, "", ""));

        btn_comments.setOnClickListener(view -> {
            Intent i = new Intent(JobOrderDetailsActivity.this, CommentsActivity.class);
            i.putExtra("job_order_id", jobOrderId);
            i.putExtra("project_id", projectId);
            startActivity(i);
        });

        btn_ceo_reminder.setOnClickListener(v -> sendCeoReminder());
        btn_sales_reminder.setOnClickListener(v -> sendSalesReminder());

        edit.setOnClickListener(view -> {
            Intent i = new Intent(JobOrderDetailsActivity.this, EditJobOrderActivity.class);
            i.putExtra("job_order_id", jobOrderId);
            startActivity(i);
        });
        swipe_refresh.setOnRefreshListener(() -> {
            getJobOrderDetails();
        });

        adel_pay.setOnClickListener(v -> {
            if (validateFields()) {
                addPayment();
            }
        });
        choose_file.setOnClickListener(v -> Dexter.withContext(getBaseContext())
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            pickFromGallery();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                    }
                }).check());

    }

    private void initFields() {

        ws = new WebserviceContext(this);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Please, Wait...");
        dialog.setCancelable(false);

        filesSelected = new ArrayList<>();

        back = findViewById(R.id.back);
        btn_comments = findViewById(R.id.btn_comments);
        btn_ceo_reminder = findViewById(R.id.btn_ceo_reminder);
        btn_sales_reminder = findViewById(R.id.btn_sales_reminder);
        download = findViewById(R.id.download);
        sales_approval_layout = findViewById(R.id.sales_approval_layout);
        magdi_approval_layout = findViewById(R.id.magdi_approval_layout);
        hesham_approval_layout = findViewById(R.id.hesham_approval_layout);
        ceo_approval_layout = findViewById(R.id.ceo_approval_layout);
        adel_approval_layout = findViewById(R.id.adel_approval_layout);
        payment_layout = findViewById(R.id.payment_layout);
        sales_approve = findViewById(R.id.sales_approve);
        sales_reject = findViewById(R.id.sales_reject);
        magdi_approve = findViewById(R.id.magdi_approve);
        magdi_hold = findViewById(R.id.magdi_hold);
        hesham_approve = findViewById(R.id.hesham_approve);
        hesham_reject = findViewById(R.id.hesham_reject);
        hesham_ceo_approval = findViewById(R.id.hesham_ceo_approval);
        ceo_approve = findViewById(R.id.ceo_approve);
        ceo_reject = findViewById(R.id.ceo_reject);
        adel_pay = findViewById(R.id.adel_pay);
        choose_file = findViewById(R.id.choose_file);
        filesChosen = findViewById(R.id.files_chosen);
        payment_percent = findViewById(R.id.payment_percent);
        payment_percent_txt = findViewById(R.id.payment_percent_txt);
        adel_seen_txt = findViewById(R.id.adel_seen_txt);
        adel_unseen_txt = findViewById(R.id.adel_unseen_txt);
        txt_hany_notes = findViewById(R.id.hany_notes);
        progress_indicator = findViewById(R.id.progress_indicator);

        reasons = findViewById(R.id.reasons);
        edit = findViewById(R.id.edit);

        swipe_refresh = findViewById(R.id.swipe_refresh);
        joStepperImg = findViewById(R.id.joStepperImg);


        jobOrderId = getIntent().getIntExtra("job_order_id", 0);

        recyclerView = findViewById(R.id.recycler_view);
        payments_list = new ArrayList<>();
        initRecyclerView();
    }


    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        payments_adapter = new Payments_adapter(getBaseContext(), payments_list);
        recyclerView.setAdapter(payments_adapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setPaymentsList(JSONArray list) {
        payments_list.clear();
        try {
            for (int i = 0; i < list.length(); i++) {

                JSONObject currentObject = list.getJSONObject(i);
                final int id = currentObject.getInt("id");
                final int job_order_id = currentObject.getInt("job_order_id");
                final String percentage = currentObject.getString("percentage");
                final String attachment = currentObject.getString("attachment");
                final String created_at = currentObject.getString("created_at");

                payments_list.add(new Payment_item(id, job_order_id, percentage, attachment, created_at));

            }
            payments_adapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setEgUserJobOrderPermissions(int jobOrderStatus, boolean canEditProject, int ceo) {
        setEgJoStepper(jobOrderStatus, ceo);
        String loggedInUser = UserType.getUserType(UserUtils.getParentId(getBaseContext()), UserUtils.getChildId(getBaseContext()), UserUtils.getCountryId(getBaseContext()));
        resetData();

        if (loggedInUser.equals("hesham") && jobOrderStatus > 2 && jobOrderStatus != 9 && jobOrderStatus != 6 && jobOrderStatus != 7) {
            edit.setVisibility(View.VISIBLE);
        } else {
            edit.setVisibility(View.GONE);
        }
        switch (jobOrderStatus) {
            case 1: {
                if (canEditProject) {
                    sales_approval_layout.setVisibility(View.VISIBLE);
                } else {
                    sales_approval_layout.setVisibility(View.GONE);
                }

                if (loggedInUser.equals("hesham")||loggedInUser.equals("nagat")){
                    btn_sales_reminder.setVisibility(View.VISIBLE);
                }
                break;
            }
            case 3:
            case 4: {
                if (loggedInUser.equals("magdi")) {
                    magdi_approval_layout.setVisibility(View.VISIBLE);
                } else {
                    magdi_approval_layout.setVisibility(View.GONE);
                }
                break;
            }
            case 5:
            case 10: {
                if (loggedInUser.equals("hesham")) {
                    hesham_approval_layout.setVisibility(View.VISIBLE);
                    if (ceo == 0) {
                        hesham_approve.setVisibility(View.GONE);
                    } else {
                        hesham_approve.setVisibility(View.VISIBLE);
                    }
                } else {
                    hesham_approval_layout.setVisibility(View.GONE);
                }
                break;
            }
            case 8: {
                if (loggedInUser.equals("ceo")) {
                    ceo_approval_layout.setVisibility(View.VISIBLE);
                } else {
                    ceo_approval_layout.setVisibility(View.GONE);
                }

                if (loggedInUser.equals("hesham")||loggedInUser.equals("nagat")){
                    btn_ceo_reminder.setVisibility(View.VISIBLE);
                }
                break;
            }
        }
    }

    private void setEgJoStepper(int joStatus, int ceo) {
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

    private void setKsaUserJobOrderPermissions(int jobOrderStatus, boolean canEditProject, int ceo) {
        setKsaJoStepper(jobOrderStatus, ceo);
        String loggedInUser = UserType.getUserType(UserUtils.getParentId(getBaseContext()), UserUtils.getChildId(getBaseContext()), UserUtils.getCountryId(getBaseContext()));
        resetData();

        if (loggedInUser.equals("hany") && jobOrderStatus > 2 && jobOrderStatus != 9 && jobOrderStatus != 6 && jobOrderStatus != 7) {
            edit.setVisibility(View.VISIBLE);
        } else {
            edit.setVisibility(View.GONE);
        }
        switch (jobOrderStatus) {
            case 1: {
                if (canEditProject) {
                    sales_approval_layout.setVisibility(View.VISIBLE);
                } else {
                    sales_approval_layout.setVisibility(View.GONE);
                }

                if (loggedInUser.equals("hany")||loggedInUser.equals("speranza")){
                    btn_ceo_reminder.setVisibility(View.VISIBLE);
                    btn_sales_reminder.setVisibility(View.VISIBLE);
                }
                break;
            }
            case 3:
            case 4: {
                if (loggedInUser.equals("hazem")) {
                    magdi_approval_layout.setVisibility(View.VISIBLE);
                } else {
                    magdi_approval_layout.setVisibility(View.GONE);
                }
                break;
            }
            case 5:
            case 10: {
                if (loggedInUser.equals("hany")) {
                    hesham_approval_layout.setVisibility(View.VISIBLE);
                    if (ceo == 0) {
                        hesham_approve.setVisibility(View.GONE);
                    } else {
                        hesham_approve.setVisibility(View.VISIBLE);
                    }
                } else {
                    hesham_approval_layout.setVisibility(View.GONE);
                }
                break;
            }
            case 7: {
                if (loggedInUser.equals("adel")) {
                    adel_approval_layout.setVisibility(View.VISIBLE);
                    txt_hany_notes.setVisibility(View.VISIBLE);
                }
                payment_layout.setVisibility(View.VISIBLE);
                break;
            }
            case 8: {
                if (loggedInUser.equals("ceo")) {
                    ceo_approval_layout.setVisibility(View.VISIBLE);
                } else {
                    ceo_approval_layout.setVisibility(View.GONE);
                }

                if (loggedInUser.equals("hany")||loggedInUser.equals("speranza")){
                    btn_ceo_reminder.setVisibility(View.VISIBLE);
                }
                break;
            }
            case 11: {
                payment_layout.setVisibility(View.VISIBLE);
                break;
            }

        }
    }

    private void setKsaJoStepper(int joStatus, int ceo) {
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
                    joStepperImg.setImageResource(R.drawable.jo_7_ceo_ksa);
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
            case 11: {
                joStepperImg.setImageResource(R.drawable.jo_11);
                break;
            }
        }
    }

    private void resetData() {
        sales_approval_layout.setVisibility(View.GONE);
        magdi_approval_layout.setVisibility(View.GONE);
        hesham_approval_layout.setVisibility(View.GONE);
        ceo_approval_layout.setVisibility(View.GONE);
        adel_approval_layout.setVisibility(View.GONE);
        txt_hany_notes.setVisibility(View.GONE);
        btn_ceo_reminder.setVisibility(View.GONE);
        btn_sales_reminder.setVisibility(View.GONE);
    }

    public void updateStatusDialog(int status, String reason, String hany_notes) {
        new AlertDialog.Builder(JobOrderDetailsActivity.this)
                .setTitle("Are you sure? ")
                .setPositiveButton("Yes", (dialog, which) -> updateStatus(status, reason, hany_notes))
                .setNegativeButton("Dismiss", null)
                .show();
    }


    public void sendCeoReminder() {
        Map<String, String> map = new HashMap<>();
        map.put("job_order_id", String.valueOf(jobOrderId));

        dialog.show();
        ws.getApi().sendJobOrderReminder(UserUtils.getAccessToken(getBaseContext()), map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        Toast.makeText(getBaseContext(), "Sent successfully", Toast.LENGTH_LONG).show();
                        getJobOrderDetails();
                    } else {
                        assert response.errorBody() != null;
                        Toast.makeText(getBaseContext(), response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getBaseContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    public void sendSalesReminder() {
        Map<String, String> map = new HashMap<>();
        map.put("job_order_id", String.valueOf(jobOrderId));
        map.put("sales", "1");

        dialog.show();
        ws.getApi().sendJobOrderReminder(UserUtils.getAccessToken(getBaseContext()), map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        Toast.makeText(getBaseContext(), "Sent successfully", Toast.LENGTH_LONG).show();
                        getJobOrderDetails();
                    } else {
                        assert response.errorBody() != null;
                        Toast.makeText(getBaseContext(), response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getBaseContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    public void updateStatus(int status, String reason, String hany_notes) {
        Map<String, String> map = new HashMap<>();
        map.put("job_order_id", String.valueOf(jobOrderId));
        map.put("status", String.valueOf(status));
        map.put("reason", reason);
        map.put("percentage", hany_notes);

        dialog.show();
        ws.getApi().changeJobOrderStatus(UserUtils.getAccessToken(getBaseContext()), map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        Toast.makeText(getBaseContext(), "Updated successfully", Toast.LENGTH_LONG).show();
                        getJobOrderDetails();
                    } else {
                        assert response.errorBody() != null;
                        Toast.makeText(getBaseContext(), response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getBaseContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void getJobOrderDetails() {
        swipe_refresh.setRefreshing(true);

        ws.getApi().getJobOrderDetails(UserUtils.getAccessToken(getBaseContext()), jobOrderId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    swipe_refresh.setRefreshing(false);
                    assert response.body() != null;
                    JSONObject responseObject = new JSONObject(response.body().string());
                    JSONObject dataObj = responseObject.getJSONObject("data");
                    String pdfLinkUrl = dataObj.getString("url");
//                    pdfUrl = "https://docs.google.com/viewerng/viewer?url=" + pdfLinkUrl;
                    pdfUrl = pdfLinkUrl;

                    jobOrderStatus = dataObj.getInt("status");
                    poNumber = dataObj.getString("po_number");
                    projectId = dataObj.getInt("project_id");
                    countryId = dataObj.getInt("country_id");

                    if (countryId == 2) {
                        setPaymentsList(dataObj.getJSONArray("payments"));
                        String paidAmount = dataObj.getString("paied");
                        int paidAmountInt = Integer.parseInt(paidAmount.substring(0, paidAmount.length() - 1));
                        payment_percent_txt.setText(paidAmount + " paid");
                        progress_indicator.setProgress(paidAmountInt);
                        txt_hany_notes.setText(StringCheck.returnEmpty(dataObj.getString("percentage")));
                    }

                    String reasonsTxt = dataObj.getString("reason_description");

                    boolean isAdelSeen = dataObj.getBoolean("adel_seen");
                    if (isAdelSeen) {
                        adel_seen_txt.setVisibility(View.VISIBLE);
                        adel_unseen_txt.setVisibility(View.GONE);
                    } else {
                        adel_seen_txt.setVisibility(View.GONE);
                        adel_unseen_txt.setVisibility(View.VISIBLE);
                    }

                    if (!reasonsTxt.equals("null")) {
                        reasons.setText(reasonsTxt);
                    }

                    int project_creator_id = dataObj.getInt("project_creator_id");
                    final String assigned_to = dataObj.getString("project_assign_id");
                    int assigned_to_id = 0;
                    if (!assigned_to.equals("null")) {
                        assigned_to_id = Integer.parseInt(assigned_to);
                    }
                    boolean canEditProject = UserType.canEditProject(getBaseContext(), project_creator_id, assigned_to_id);
                    int ceo = dataObj.getInt("ceo");

                    if (countryId == 1) {
                        setEgUserJobOrderPermissions(jobOrderStatus, canEditProject, ceo);
                    } else {
                        setKsaUserJobOrderPermissions(jobOrderStatus, canEditProject, ceo);
                    }

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
                swipe_refresh.setRefreshing(false);
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
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        Toast.makeText(getBaseContext(), "PO Added successfully", Toast.LENGTH_LONG).show();
                        getJobOrderDetails();
                    } else {
                        assert response.errorBody() != null;
                        Toast.makeText(getBaseContext(), response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
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
                updateStatusDialog(6, reason, "");
                break;
            case "sales":
                updateStatusDialog(2, reason, "");
                break;
            case "hany_approve":
                updateStatusDialog(7, "", reason);
                break;
        }
    }

    @Override
    public void poNumberSubmitListener(String po_number, String type) {
        addPoNumber(projectId, po_number);
    }

    private void pickFromGallery() {
        //Create an Intent with action as ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        // Launching the Intent
        startActivityForResult(intent, FILES_REQUEST_CODE);
    }

    private boolean validateFields() {
        if (payment_percent.length() == 0) {
            payment_percent.setError("This is required field");
            return false;
        }
        if (filesSelected.size() == 0) {
            Toast.makeText(this, "You must add attachment", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void addPayment() {
        Map<String, RequestBody> map = setPaymentMapRequestBody();
        List<MultipartBody.Part> fileToUpload = addAttaches(filesSelected);
        dialog.show();
        ws.getApi().addPayment(UserUtils.getAccessToken(getBaseContext()), fileToUpload, map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        Toast.makeText(getBaseContext(), "Cost Added successfully", Toast.LENGTH_LONG).show();
                        getJobOrderDetails();

                    } else {
                        assert response.errorBody() != null;
                        JSONObject res = new JSONObject(response.errorBody().string());
                        Toast.makeText(getBaseContext(), res.getString("error"), Toast.LENGTH_LONG).show();
                        Toast.makeText(getBaseContext(), response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getBaseContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private Map<String, RequestBody> setPaymentMapRequestBody() {
        Map<String, RequestBody> map = new HashMap<>();
        map.put("percentage", RequestBody.create(MediaType.parse("text/plain"), payment_percent.getText().toString()));
        map.put("job_order_id", RequestBody.create(MediaType.parse("text/plain"), String.valueOf(jobOrderId)));

        return map;
    }

    private List<MultipartBody.Part> addAttaches(List<String> files) {
        List<MultipartBody.Part> list = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            File file = new File(files.get(i));
            RequestBody fileReqBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("attachment", file.getName(), fileReqBody);
            list.add(part);
        }
        return list;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            if (requestCode == FILES_REQUEST_CODE) {
                filesSelected.clear();
                //data.getData returns the content URI for the selected files
                if (data == null) {
                    return;
                } else if (data.getClipData() != null) {
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        Uri uri = data.getClipData().getItemAt(i).getUri();
                        filesSelected.add(RealPathUtil.getRealPath(getBaseContext(), uri));
                    }
                } else {
                    Uri uri = data.getData();
                    filesSelected.add(RealPathUtil.getRealPath(getBaseContext(), uri));
                }
                filesChosen.setText(filesSelected.size() + " Files Selected");

                Log.e("Data selected", filesSelected.toString());
            }
    }
}