package com.example.followup.requests;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.example.followup.R;
import com.example.followup.requests.view.Photography_view;
import com.example.followup.requests.view.Print_view;
import com.example.followup.requests.view.Production_view;
import com.example.followup.requests.view.Purchase_view;
import com.example.followup.supplier_costs.add.AddPhotographySupplierCostActivity;
import com.example.followup.supplier_costs.add.AddPrintSupplierCostActivity;
import com.example.followup.supplier_costs.add.AddProductionSupplierCostActivity;
import com.example.followup.supplier_costs.add.AddPurchaseSupplierCostActivity;
import com.example.followup.supplier_costs.view.Photography_supplierCost_view;
import com.example.followup.supplier_costs.view.Print_supplierCost_view;
import com.example.followup.supplier_costs.view.Production_supplierCost_view;
import com.example.followup.supplier_costs.view.Purchase_supplierCost_view;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.Webservice;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestDetailsActivity extends LocalizationActivity {

    boolean isDetailsExpanded = false;
    boolean isCostExpanded = false;
    ImageView back,expandDetails, expandCost;
    FrameLayout request_details_content, cost_details_content;
    RelativeLayout request_cost_container;
    LinearLayout no_cost_container;
    ProgressBar loading;
    Button add_cost;


    int request_id, type_id;
    JSONObject dataObj;

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
        add_cost.setOnClickListener(v -> gotoAddCost(request_id,type_id));

    }

    private void gotoAddCost(int request_id,int type_id) {
        Intent i ;
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
            default:
                throw new IllegalStateException("Unexpected value: " + type_id);
        }
        i.putExtra("request_id",request_id);
        startActivity(i);

    }

    private void initFields() {
        request_id = getIntent().getIntExtra("request_id", 0);
        type_id = getIntent().getIntExtra("type_id", 0);
        loading = findViewById(R.id.loading);
        expandDetails = findViewById(R.id.expand_details);
        expandCost = findViewById(R.id.expand_cost);
        back = findViewById(R.id.back);
        request_details_content = findViewById(R.id.request_details_content);
        request_cost_container = findViewById(R.id.request_cost_container);
        cost_details_content = findViewById(R.id.cost_details_content);
        no_cost_container = findViewById(R.id.no_cost_container);
        add_cost = findViewById(R.id.add_cost);
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

    private void getRequests() {
        loading.setVisibility(View.VISIBLE);

        Webservice.getInstance().getApi().getRequestDetails(UserUtils.getAccessToken(getBaseContext()), request_id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    JSONObject responseObject = new JSONObject(response.body().string());
                    dataObj = responseObject.getJSONObject("data");
                    setFragments(type_id);
                    setCostContainer(!dataObj.getString("cost").equals("null"));
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

    private void setCostContainer(boolean hasCost){
        if (hasCost){
            cost_details_content.setVisibility(View.VISIBLE);
            no_cost_container.setVisibility(View.GONE);
        }else {
            cost_details_content.setVisibility(View.GONE);
            no_cost_container.setVisibility(View.VISIBLE);
        }
    }

    private void setFragments(int type_id) {
        switch (type_id) {
            case 1:
                setDetailsFragment(new Purchase_view());
                setCostFragment(new Purchase_supplierCost_view());
                break;
            case 2:
                setDetailsFragment(new Print_view());
                setCostFragment(new Print_supplierCost_view());
                break;
            case 3:
                setDetailsFragment(new Production_view());
                setCostFragment(new Production_supplierCost_view());
                break;
            case 4:
                setDetailsFragment(new Photography_view());
                setCostFragment(new Photography_supplierCost_view());
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRequests();
    }
}