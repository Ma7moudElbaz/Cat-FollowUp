package com.example.followup.job_orders.edit_job_order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.followup.R;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.WebserviceContext;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditJobOrderActivity extends AppCompatActivity {
    ImageView back;
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
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editJobOrder(edit_job_order_requests_list);
            }
        });
        getJobOrderRequests();
    }

    private void initFields() {

        ws = new WebserviceContext(this);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please, Wait...");
        dialog.setCancelable(false);

        jobOrderId = getIntent().getIntExtra("job_order_id", 0);

        back = findViewById(R.id.back);
        loading = findViewById(R.id.loading);
        submit = findViewById(R.id.submit);
        recyclerView = findViewById(R.id.recycler_view);
        edit_job_order_requests_list = new ArrayList<>();
        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        edit_job_order_requests_adapter = new Edit_job_order_requests_adapter(getBaseContext(), edit_job_order_requests_list);
        recyclerView.setAdapter(edit_job_order_requests_adapter);
    }

    public void getJobOrderRequests() {
        loading.setVisibility(View.VISIBLE);

        ws.getApi().getEditJobOrderRequests(UserUtils.getAccessToken(getBaseContext()), jobOrderId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    JSONObject responseObject = new JSONObject(response.body().string());
                    JSONArray jobOrdersArray = responseObject.getJSONObject("data").getJSONArray("requests");
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

    public void setJobOrderRequestsList(JSONArray list) {
        try {
            for (int i = 0; i < list.length(); i++) {

                JSONObject currentObject = list.getJSONObject(i);
                final int id = currentObject.getInt("id");
                final String item_name = currentObject.getString("item_name");
                final String actual_cost = currentObject.getString("actual_cost");
                edit_job_order_requests_list.add(new Edit_job_order_request_item(id, item_name, actual_cost));

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
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.code() == 200 || response.code() == 201) {
                        Toast.makeText(getBaseContext(), "Request Added successfully", Toast.LENGTH_LONG).show();
                        onBackPressed();

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

    private Map<String, String> setJobOrderMap(List<Edit_job_order_request_item> items) {

        StringBuilder requestIds = new StringBuilder();
        StringBuilder actual_costs = new StringBuilder();

        for (int i = 0; i < items.size(); i++) {
            if (i == 0) {
                requestIds.append(items.get(i).getRequest_id());
                actual_costs.append(items.get(i).getActual_cost());
            } else {
                requestIds.append(",").append(items.get(i).getRequest_id());
                actual_costs.append(",").append(items.get(i).getActual_cost());
            }
        }

        Map<String, String> map = new HashMap<>();
        map.put("request_ids", requestIds.toString());
        map.put("actual_costs", actual_costs.toString());

        Log.e("requests map", map.toString());
        return map;
    }

}