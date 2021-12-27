package com.example.followup.job_orders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.followup.R;
import com.example.followup.job_orders.requests.Job_order_request_item;
import com.example.followup.job_orders.requests.Job_orders_requests_adapter;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.Webservice;

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

public class AddJobOrderActivity extends AppCompatActivity {

    ImageView back;
    ProgressBar loading;
    RecyclerView recyclerView;
    Spinner request_types_spinner;
    private ProgressDialog dialog;
    Button create_job_order;

    ArrayList<Job_order_request_item> job_order_requests_list;
    Job_orders_requests_adapter job_order_requests_adapter;

    int currentPageNum = 1;
    int lastPageNum;
    boolean mHasReachedBottomOnce = false;

    int projectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job_order);
        initFields();
        back.setOnClickListener(v -> onBackPressed());
        create_job_order.setOnClickListener(v -> {
            if (validateSelectedRequests(job_order_requests_adapter.getSelectedData())) {
                createJobOrder(job_order_requests_adapter.getSelectedData());
//                setJobOrderMap(job_order_requests_adapter.getSelectedData());
            }
        });
        request_types_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                job_order_requests_list.clear();
                currentPageNum = 1;
                getJobOrderRequests(currentPageNum);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private boolean validateSelectedRequests(List<Job_order_request_item> items) {
        if (items.isEmpty()) {
            Toast.makeText(getBaseContext(), "No Requests Selected", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getFinal_cost().length() == 0) {
                    Toast.makeText(getBaseContext(), "You must add final cost to all selected requests", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
    }

    public void getJobOrderRequests(int pageNum) {
        loading.setVisibility(View.VISIBLE);

        Webservice.getInstance().getApi().getJobOrderRequests(UserUtils.getAccessToken(getBaseContext()), 6, projectId, (request_types_spinner.getSelectedItemPosition() + 1), pageNum).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    JSONObject responseObject = new JSONObject(response.body().string());
                    JSONArray jobOrdersArray = responseObject.getJSONArray("data");
                    setJobOrderRequestsList(jobOrdersArray);
                    JSONObject metaObject = responseObject.getJSONObject("meta");
                    lastPageNum = metaObject.getInt("last_page");

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
                final String request_id = currentObject.getString("item_name");


                job_order_requests_list.add(new Job_order_request_item(id, request_id, "", false));

            }

            job_order_requests_adapter.notifyDataSetChanged();
            mHasReachedBottomOnce = false;
            currentPageNum++;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void createJobOrder(List<Job_order_request_item> items) {
        Map<String, String> map = setJobOrderMap(items);

        dialog.show();
        Webservice.getInstance().getApi().addJobOrder(UserUtils.getAccessToken(getBaseContext()), map).enqueue(new Callback<ResponseBody>() {
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

    private Map<String, String> setJobOrderMap(List<Job_order_request_item> items) {

        StringBuilder requestIds = new StringBuilder();
        StringBuilder actual_costs = new StringBuilder();

        for (int i = 0; i < items.size(); i++) {
            if (i == 0) {
                requestIds.append(items.get(i).getId());
                actual_costs.append(items.get(i).getFinal_cost());
            } else {
                requestIds.append(",").append(items.get(i).getId());
                actual_costs.append(",").append(items.get(i).getId());
            }
        }

        Map<String, String> map = new HashMap<>();
        map.put("job_order_name", "name");
        map.put("project_id", String.valueOf(projectId));
        map.put("request_ids", requestIds.toString().toString());
        map.put("actual_costs", actual_costs.toString().toString());

        Log.e("TAG", map.toString());
        return map;
    }

    private void initFields() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please, Wait...");
        dialog.setCancelable(false);

        projectId = getIntent().getIntExtra("project_id", 0);

        back = findViewById(R.id.back);
        loading = findViewById(R.id.loading);
        create_job_order = findViewById(R.id.create_job_order);
        request_types_spinner = findViewById(R.id.request_types_spinner);
        recyclerView = findViewById(R.id.recycler_view);
        job_order_requests_list = new ArrayList<>();
        initRecyclerView();

    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        job_order_requests_adapter = new Job_orders_requests_adapter(getBaseContext(), job_order_requests_list);
        recyclerView.setAdapter(job_order_requests_adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && !mHasReachedBottomOnce) {
                    mHasReachedBottomOnce = true;

                    if (currentPageNum <= lastPageNum)
                        getJobOrderRequests(currentPageNum);

                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}