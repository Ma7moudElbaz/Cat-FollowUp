package com.example.followup.requests;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.example.followup.R;
import com.example.followup.job_orders.list.JobOrdersActivity;
import com.example.followup.supplier_costs.add.AddExtrasSupplierCost;
import com.example.followup.supplier_costs.edit.EditExtrasSupplierCostActivity;
import com.example.followup.requests.view.Extras_view;
import com.example.followup.requests.view.Photography_view;
import com.example.followup.requests.view.Print_view;
import com.example.followup.requests.view.Production_view;
import com.example.followup.requests.view.Purchase_view;
import com.example.followup.supplier_costs.add.AddPhotographySupplierCostActivity;
import com.example.followup.supplier_costs.add.AddPrintSupplierCostActivity;
import com.example.followup.supplier_costs.add.AddProductionSupplierCostActivity;
import com.example.followup.supplier_costs.add.AddPurchaseSupplierCostActivity;
import com.example.followup.supplier_costs.edit.EditPhotographySupplierCostActivity;
import com.example.followup.supplier_costs.edit.EditPrintSupplierCostActivity;
import com.example.followup.supplier_costs.edit.EditProductionSupplierCostActivity;
import com.example.followup.supplier_costs.edit.EditPurchaseSupplierCostActivity;
import com.example.followup.supplier_costs.view.Extras_supplierCost_view;
import com.example.followup.supplier_costs.view.Photography_supplierCost_view;
import com.example.followup.supplier_costs.view.Print_supplierCost_view;
import com.example.followup.supplier_costs.view.Production_supplierCost_view;
import com.example.followup.supplier_costs.view.Purchase_supplierCost_view;
import com.example.followup.utils.UserType;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.WebserviceContext;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import params.com.stepview.StatusViewScroller;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestDetailsActivity extends LocalizationActivity {

    public int getCostStatus() {
        return costStatus;
    }

    boolean isDetailsExpanded = false;
    boolean isCostExpanded = false;
    private ProgressDialog dialog;
    ImageView back, expandDetails, expandCost, editCost, cancel_request;
    FrameLayout request_details_content, cost_details_content;
    RelativeLayout request_cost_container;
    LinearLayout no_cost_container, sales_approval_layout;
    Button sales_approve, sales_reject;
    ProgressBar loading;
    Button add_cost;
    int costStatus;
    int costId;

    TextView job_orders, txt_canceled;

    int request_id, type_id;
    JSONObject dataObj;
    StatusViewScroller steps;

    SwipeRefreshLayout swipe_refresh;
    WebserviceContext ws;

    int projectId, countryId;
    boolean canEditProject, hasPoNumber;

    ImageView requestStepperImg;

    public JSONObject getDataObj() {
        return dataObj;
    }

    public void setDetailsFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.request_details_content, fragment);
        fragmentTransaction.commit();
    }

    public void setCostFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.cost_details_content, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        initFields();

        back.setOnClickListener(v -> onBackPressed());
        expandDetails.setOnClickListener(v -> toggleDetails(isDetailsExpanded));
        expandCost.setOnClickListener(v -> toggleCost(isCostExpanded));
        add_cost.setOnClickListener(v -> gotoAddCost(request_id, type_id));
        editCost.setOnClickListener(v -> {
            gotoEditCost(costId, type_id);
        });

        sales_reject.setOnClickListener(v -> updateStatusDialog(5, ""));
        sales_approve.setOnClickListener(v -> updateStatusDialog(6, ""));
        cancel_request.setOnClickListener(v -> deleteRequestDialog());

        swipe_refresh.setOnRefreshListener(() -> {
            swipe_refresh.setRefreshing(false);
            onResume();
        });

        job_orders.setOnClickListener(v -> {
            Intent i = new Intent(getBaseContext(), JobOrdersActivity.class);
            i.putExtra("project_id", projectId);
            i.putExtra("country_id", countryId);
            i.putExtra("is_project_owner", canEditProject);
            i.putExtra("has_po_number", hasPoNumber);
            startActivity(i);
        });
    }

    private void gotoEditCost(int cost_id, int type_id) {
        Intent i;
        switch (type_id) {
            case 1:
                i = new Intent(getBaseContext(), EditPurchaseSupplierCostActivity.class);
                break;
            case 2:
                i = new Intent(getBaseContext(), EditPrintSupplierCostActivity.class);
                break;
            case 3:
                i = new Intent(getBaseContext(), EditProductionSupplierCostActivity.class);
                break;
            case 4:
                i = new Intent(getBaseContext(), EditPhotographySupplierCostActivity.class);
                break;
            case 5:
                i = new Intent(getBaseContext(), EditExtrasSupplierCostActivity.class);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type_id);
        }
        i.putExtra("cost_id", cost_id);
        i.putExtra("dataObj", dataObj.toString());
        startActivity(i);

    }

    private void gotoAddCost(int request_id, int type_id) {
        Intent i;
        switch (type_id) {
            case 1:
                i = new Intent(getBaseContext(), AddPurchaseSupplierCostActivity.class);
                break;
            case 2:
                i = new Intent(getBaseContext(), AddPrintSupplierCostActivity.class);
                break;
            case 3:
                i = new Intent(getBaseContext(), AddProductionSupplierCostActivity.class);
                break;
            case 4:
                i = new Intent(getBaseContext(), AddPhotographySupplierCostActivity.class);
                break;
            case 5:
                i = new Intent(getBaseContext(), AddExtrasSupplierCost.class);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type_id);
        }
        i.putExtra("request_id", request_id);
        startActivity(i);

    }

    private void initFields() {
        ws = new WebserviceContext(this);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Please, Wait...");
        dialog.setCancelable(false);

        request_id = getIntent().getIntExtra("request_id", 0);
//        type_id = getIntent().getIntExtra("type_id", 0);
        loading = findViewById(R.id.loading);
        expandDetails = findViewById(R.id.expand_details);
        expandCost = findViewById(R.id.expand_cost);
        back = findViewById(R.id.back);
        request_details_content = findViewById(R.id.request_details_content);
        request_cost_container = findViewById(R.id.request_cost_container);
        cost_details_content = findViewById(R.id.cost_details_content);
        no_cost_container = findViewById(R.id.no_cost_container);
        add_cost = findViewById(R.id.add_cost);
        steps = findViewById(R.id.steps);
        sales_approval_layout = findViewById(R.id.sales_approval_layout);
        sales_approve = findViewById(R.id.sales_approve);
        sales_reject = findViewById(R.id.sales_reject);
        editCost = findViewById(R.id.edit_cost);
        cancel_request = findViewById(R.id.cancel_request);
        job_orders = findViewById(R.id.job_orders);
        txt_canceled = findViewById(R.id.txt_canceled);

        swipe_refresh = findViewById(R.id.swipe_refresh);
        requestStepperImg = findViewById(R.id.requestStepperImg);

    }

    private void expandDetails() {
        expandDetails.setImageResource(R.drawable.ic_arrow_up);
        request_details_content.setVisibility(View.VISIBLE);
        isDetailsExpanded = true;

        expandCost.setImageResource(R.drawable.ic_arrow_down);
        request_cost_container.setVisibility(View.GONE);
        isCostExpanded = false;
    }

    private void expandCost() {
        expandCost.setImageResource(R.drawable.ic_arrow_up);
        request_cost_container.setVisibility(View.VISIBLE);
        isCostExpanded = true;

        expandDetails.setImageResource(R.drawable.ic_arrow_down);
        request_details_content.setVisibility(View.GONE);
        isDetailsExpanded = false;
    }

    private void toggleCost(boolean expanded) {
        if (expanded) {
            expandCost.setImageResource(R.drawable.ic_arrow_down);
            request_cost_container.setVisibility(View.GONE);
            isCostExpanded = false;
        } else {
            expandCost();
        }
    }

    private void toggleDetails(boolean expanded) {
        if (expanded) {
            expandDetails.setImageResource(R.drawable.ic_arrow_down);
            request_details_content.setVisibility(View.GONE);
            isDetailsExpanded = false;
        } else {
            expandDetails();
        }
    }

    private void getRequestDetails() {
        loading.setVisibility(View.VISIBLE);

        ws.getApi().getRequestDetails(UserUtils.getAccessToken(getBaseContext()), request_id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    JSONObject responseObject = new JSONObject(response.body().string());
                    dataObj = responseObject.getJSONObject("data");
                    type_id = dataObj.getInt("type_id");
                    projectId = dataObj.getInt("project_id");
                    int requestStatus = dataObj.getInt("status");
                    if (dataObj.getString("cost").equals("null")) {
                        costStatus = 1;
                    } else {
                        costStatus = dataObj.getJSONObject("cost").getInt("status");
                        costId = dataObj.getJSONObject("cost").getInt("id");
                    }
                    int project_creator_id = dataObj.getInt("project_creator_id");
                    final String assigned_to = dataObj.getString("project_assign_id");
                    int assigned_to_id = 0;
                    if (!assigned_to.equals("null")) {
                        assigned_to_id = Integer.parseInt(assigned_to);
                    }

                    hasPoNumber = !dataObj.getString("po_number").equals("null");
                    canEditProject = UserType.canEditProject(getBaseContext(), project_creator_id, assigned_to_id);
                    countryId = dataObj.getInt("country_id");
                    if (countryId == 1) {
                        setEgUserCostPermissions(requestStatus, costStatus, canEditProject);
                    } else {
                        setKsaUserCostPermissions(requestStatus, costStatus, canEditProject);
                    }
                    setFragments(type_id, costStatus);
                    loading.setVisibility(View.GONE);

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

    private void setCostContainer(boolean hasCost) {
        if (hasCost) {
            cost_details_content.setVisibility(View.VISIBLE);
            no_cost_container.setVisibility(View.GONE);
        } else {
            cost_details_content.setVisibility(View.GONE);
            no_cost_container.setVisibility(View.VISIBLE);
        }
    }

    private void setFragments(int type_id, int cost_status) {
        switch (type_id) {
            case 1:
                setDetailsFragment(new Purchase_view());
                if (cost_status > 1)
                    setCostFragment(new Purchase_supplierCost_view());
                break;
            case 2:
                setDetailsFragment(new Print_view());
                if (cost_status > 1)
                    setCostFragment(new Print_supplierCost_view());
                break;
            case 3:
                setDetailsFragment(new Production_view());
                if (cost_status > 1)
                    setCostFragment(new Production_supplierCost_view());
                break;
            case 4:
                setDetailsFragment(new Photography_view());
                if (cost_status > 1)
                    setCostFragment(new Photography_supplierCost_view());
                break;
            case 5:
                setDetailsFragment(new Extras_view());
                if (cost_status > 1)
                    setCostFragment(new Extras_supplierCost_view());
                break;
            default:
                Toast.makeText(getBaseContext(), "typeId" + type_id + " cost" + cost_status, Toast.LENGTH_SHORT).show();
        }
    }

    private void setEgUserCostPermissions(int requestStatus, int costStatus, Boolean canEditProject) {
        setEgRequestStepper(costStatus);
        String loggedInUser = UserType.getUserType(UserUtils.getParentId(getBaseContext()), UserUtils.getChildId(getBaseContext()), UserUtils.getCountryId(getBaseContext()));
        resetData();
        if (requestStatus == 0) {
            txt_canceled.setVisibility(View.VISIBLE);
        }
        switch (costStatus) {
            case 1: {
                setCostContainer(false);
                steps.getStatusView().setCurrentCount(1);
                if (loggedInUser.equals("nagatTeam") || loggedInUser.equals("nagat")) {
                    add_cost.setVisibility(View.VISIBLE);
                } else {
                    add_cost.setVisibility(View.GONE);
                }
                break;
            }
            case 2: {
                steps.getStatusView().setCurrentCount(2);
                //handle buttons in SupplierCost fragments
                break;
            }
            case 3:
            case 5: {
                steps.getStatusView().setCurrentCount(2);
                if (loggedInUser.equals("nagatTeam") || loggedInUser.equals("nagat")) {
                    editCost.setVisibility(View.VISIBLE);
                } else {
                    editCost.setVisibility(View.GONE);
                }
                break;
            }
            case 4: {
                steps.getStatusView().setCurrentCount(3);
                if (canEditProject) {
                    sales_approval_layout.setVisibility(View.VISIBLE);
                } else {
                    sales_approval_layout.setVisibility(View.GONE);
                }
                break;
            }
            case 6: {
                steps.getStatusView().setCurrentCount(4);
                break;
            }
        }
    }

    private void setEgRequestStepper(int costStatus) {
        switch (costStatus) {
            case 1: {
                requestStepperImg.setImageResource(R.drawable.request_1);
                break;
            }
            case 2: {
                requestStepperImg.setImageResource(R.drawable.request_2);
                break;
            }
            case 3: {
                requestStepperImg.setImageResource(R.drawable.request_3);
                break;
            }
            case 4: {
                requestStepperImg.setImageResource(R.drawable.request_4);
                break;
            }
            case 5: {
                requestStepperImg.setImageResource(R.drawable.request_5);
                break;
            }
            case 6: {
                requestStepperImg.setImageResource(R.drawable.request_6);
                break;
            }

        }
    }


    private void setKsaUserCostPermissions(int requestStatus, int costStatus, Boolean canEditProject) {
        setKsaRequestStepper(costStatus);
        String loggedInUser = UserType.getUserType(UserUtils.getParentId(getBaseContext()), UserUtils.getChildId(getBaseContext()), UserUtils.getCountryId(getBaseContext()));
        resetData();
        if (requestStatus == 0) {
            txt_canceled.setVisibility(View.VISIBLE);
        }
        switch (costStatus) {
            case 1: {
                setCostContainer(false);
                steps.getStatusView().setCurrentCount(1);
                if (loggedInUser.equals("speranza")) {
                    add_cost.setVisibility(View.VISIBLE);
                } else {
                    add_cost.setVisibility(View.GONE);
                }
                break;
            }
            case 5: {
                steps.getStatusView().setCurrentCount(2);
                if (loggedInUser.equals("speranza")) {
                    editCost.setVisibility(View.VISIBLE);
                } else {
                    editCost.setVisibility(View.GONE);
                }
                break;
            }
            case 4: {
                steps.getStatusView().setCurrentCount(3);
                if (canEditProject) {
                    sales_approval_layout.setVisibility(View.VISIBLE);
                } else {
                    sales_approval_layout.setVisibility(View.GONE);
                }
                break;
            }
            case 6: {
                steps.getStatusView().setCurrentCount(4);
                break;
            }
        }
    }

    private void setKsaRequestStepper(int costStatus) {
        switch (costStatus) {
            case 1: {
                requestStepperImg.setImageResource(R.drawable.request_ksa_1);
                break;
            }
            case 4: {
                requestStepperImg.setImageResource(R.drawable.request_ksa_4);
                break;
            }
            case 5: {
                requestStepperImg.setImageResource(R.drawable.request_ksa_5);
                break;
            }
            case 6: {
                requestStepperImg.setImageResource(R.drawable.request_ksa_6);
                break;
            }

        }
    }


    private void resetData() {
        setCostContainer(true);
        editCost.setVisibility(View.GONE);
        add_cost.setVisibility(View.GONE);
        txt_canceled.setVisibility(View.GONE);
        sales_approval_layout.setVisibility(View.GONE);
    }

    public void deleteRequestDialog() {
        new AlertDialog.Builder(RequestDetailsActivity.this)
                .setTitle("Are you sure? ")
                .setPositiveButton("Yes", (dialog, which) -> {
                    deleteRequest(request_id);
                })
                .setNegativeButton("Dismiss", null)
                .show();
    }

    public void deleteRequest(int request_id) {
        dialog.show();
        ws.getApi().deleteRequest(UserUtils.getAccessToken(getBaseContext()), request_id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        Toast.makeText(getBaseContext(), "Updated successfully", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    } else {
                        JSONObject res = new JSONObject(response.errorBody().string());
                        Toast.makeText(getBaseContext(), res.getString("error"), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException | JSONException e) {
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

    public void updateStatusDialog(int status, String reason) {
        new AlertDialog.Builder(RequestDetailsActivity.this)
                .setTitle("Are you sure? ")
                .setPositiveButton("Yes", (dialog, which) -> {
                    updateStatus(status, reason);
                })
                .setNegativeButton("Dismiss", null)
                .show();
    }

    public void updateStatus(int status, String reason) {

        Map<String, String> map = new HashMap<>();
        map.put("cost_id", String.valueOf(costId));
        map.put("status", String.valueOf(status));
        map.put("reason", reason);

        dialog.show();
        ws.getApi().changeCostStatus(UserUtils.getAccessToken(getBaseContext()), map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.code() == 200 || response.code() == 201) {
                        Toast.makeText(getBaseContext(), "Updated successfully", Toast.LENGTH_LONG).show();
                        getRequestDetails();
                    } else {
                        JSONObject res = new JSONObject(response.errorBody().string());
                        Toast.makeText(getBaseContext(), res.getString("error"), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException | JSONException e) {
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
        getRequestDetails();
    }
}