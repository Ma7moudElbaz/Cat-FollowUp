package com.example.followup.job_orders.edit_job_order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.example.followup.R;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.WebserviceContext;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditJobOrderActivity extends LocalizationActivity implements Edit_job_order_requests_adapter.AdapterCallback {
    ImageView back;
    TextInputEditText supplier_name;
    ProgressBar loading;
    RecyclerView recyclerView;
    Button submit;
    private ProgressDialog dialog;

    ArrayList<Edit_job_order_request_item> edit_job_order_requests_list;
    Edit_job_order_requests_adapter edit_job_order_requests_adapter;

    int jobOrderId;

    WebserviceContext ws;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_job_order);
        initFields();
        back.setOnClickListener(v -> onBackPressed());
        submit.setOnClickListener(view -> editJobOrder(edit_job_order_requests_list));
        getJobOrderRequests();
    }

    private void initFields() {

        ws = new WebserviceContext(this);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please, Wait...");
        dialog.setCancelable(false);

        jobOrderId = getIntent().getIntExtra("job_order_id", 0);

        back = findViewById(R.id.back);
        supplier_name = findViewById(R.id.supplier_name);
        loading = findViewById(R.id.loading);
        submit = findViewById(R.id.submit);
        recyclerView = findViewById(R.id.recycler_view);
        edit_job_order_requests_list = new ArrayList<>();
        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        edit_job_order_requests_adapter = new Edit_job_order_requests_adapter(this, edit_job_order_requests_list);
        recyclerView.setAdapter(edit_job_order_requests_adapter);
    }

    public void getJobOrderRequests() {
        loading.setVisibility(View.VISIBLE);

        ws.getApi().getEditJobOrderRequests(UserUtils.getAccessToken(getBaseContext()), jobOrderId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    assert response.body() != null;
                    JSONObject responseObject = new JSONObject(response.body().string());
                    JSONArray jobOrdersArray = responseObject.getJSONObject("data").getJSONArray("requests");
                    String supplierNameText = responseObject.getJSONObject("data").getJSONObject("jo").getString("supplier_name");
                    supplier_name.setText(supplierNameText);
                    setJobOrderRequestsList(jobOrdersArray);
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

    @SuppressLint("NotifyDataSetChanged")
    public void setJobOrderRequestsList(JSONArray list) {
        edit_job_order_requests_list.clear();
        try {
            for (int i = 0; i < list.length(); i++) {

                JSONObject currentObject = list.getJSONObject(i);
                final int id = currentObject.getInt("id");
                final String item_name = currentObject.getString("item_name");
                final String actual_cost = currentObject.getString("actual_cost");
                final String quantity = currentObject.getString("quantity");
                final String cost_per_id = currentObject.getString("cost_per_id");
                final String cost_type = currentObject.getString("cost_per_type");
                edit_job_order_requests_list.add(new Edit_job_order_request_item(id, item_name, cost_type, cost_per_id, quantity, actual_cost));

            }
            edit_job_order_requests_adapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void editJobOrder(List<Edit_job_order_request_item> items) {
        Map<String, String> map = setJobOrderMap(items);

        dialog.show();
        ws.getApi().editJobOrder(UserUtils.getAccessToken(getBaseContext()), map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        Toast.makeText(getBaseContext(), "Request Added successfully", Toast.LENGTH_LONG).show();
                        onBackPressed();

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

    private Map<String, String> setJobOrderMap(List<Edit_job_order_request_item> items) {

        StringBuilder requestIds = new StringBuilder();
        StringBuilder actual_costs = new StringBuilder();
        StringBuilder quantity = new StringBuilder();
        StringBuilder cost_per_id = new StringBuilder();

        for (int i = 0; i < items.size(); i++) {
            if (i == 0) {
                requestIds.append(items.get(i).getRequest_id());
                actual_costs.append(items.get(i).getActual_cost());
                quantity.append(items.get(i).getQuantity());
                cost_per_id.append(items.get(i).getCost_per_id());
            } else {
                requestIds.append(",").append(items.get(i).getRequest_id());
                actual_costs.append(",").append(items.get(i).getActual_cost());
                quantity.append(",").append(items.get(i).getQuantity());
                cost_per_id.append(",").append(items.get(i).getCost_per_id());
            }
        }

        Map<String, String> map = new HashMap<>();
        map.put("job_order_id", String.valueOf(jobOrderId));
        map.put("request_ids", requestIds.toString());
        map.put("actual_costs", actual_costs.toString());
        map.put("quantity", quantity.toString());
        map.put("cost_per_type", cost_per_id.toString());
        map.put("supplier_name", Objects.requireNonNull(supplier_name.getText()).toString());

        Log.e("requests map", map.toString());
        return map;
    }


    private void deleteJobOrderRequest(String requestId, String reason) {
        Map<String, String> map = new HashMap<>();

        map.put("request_ids", requestId);
        map.put("job_order_id", String.valueOf(jobOrderId));
        map.put("reason", reason);

        dialog.show();
        ws.getApi().deleteJobOrderRequest(UserUtils.getAccessToken(getBaseContext()), map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        Toast.makeText(getBaseContext(), "Request deleted successfully", Toast.LENGTH_LONG).show();
                        getJobOrderRequests();

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
    public void adapterCallback(int job_order_request_id, String reason) {
        new AlertDialog.Builder(this)
                .setTitle("Are you sure? ")
                .setPositiveButton("Yes", (dialog, which) -> deleteJobOrderRequest(String.valueOf(job_order_request_id),reason))
                .setNegativeButton("Dismiss", null)
                .show();
    }
}