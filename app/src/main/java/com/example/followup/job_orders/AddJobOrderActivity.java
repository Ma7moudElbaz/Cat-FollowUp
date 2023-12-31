package com.example.followup.job_orders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.example.followup.R;
import com.example.followup.bottomsheets.BottomSheet_suppliers;
import com.example.followup.job_orders.job_order_requests.Job_order_request_item;
import com.example.followup.job_orders.job_order_requests.Job_orders_requests_adapter;
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

public class AddJobOrderActivity extends LocalizationActivity implements BottomSheet_suppliers.SelectedSupplierListener {

    ImageView back;
    ProgressBar requests_loading, extras_loading;
    RecyclerView requestsRecyclerView, extrasRecyclerView;
    Spinner request_types_spinner;
    private ProgressDialog dialog;
    Button create_job_order;
    LinearLayout extras_layout_container;

    ArrayList<Job_order_request_item> job_order_requests_list, job_order_extras_list;
    Job_orders_requests_adapter job_order_requests_adapter, job_order_extras_adapter;

    int currentPageNum = 1;
    int lastPageNum;
    boolean mHasReachedBottomOnce = false;

    int projectId;

    WebserviceContext ws;

    public void showSuppliersSheet() {
        BottomSheet_suppliers suppliersBottomSheet =
                new BottomSheet_suppliers(AddJobOrderActivity.this, projectId);
        suppliersBottomSheet.show(getSupportFragmentManager(), "suppliers");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job_order);
        initFields();
        back.setOnClickListener(v -> onBackPressed());
        create_job_order.setOnClickListener(v -> {
            if (validateSelectedRequests(job_order_requests_adapter.getSelectedData(), job_order_extras_adapter.getSelectedData())) {
                showSuppliersSheet();
            }
        });
        request_types_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initRequestsRecyclerView();
                if (position == 4) {
                    extras_layout_container.setVisibility(View.GONE);
                } else {
                    extras_layout_container.setVisibility(View.VISIBLE);
                }
                job_order_requests_list.clear();
                currentPageNum = 1;
                getJobOrderRequests(currentPageNum);

                job_order_extras_list.clear();
                getJobOrderExtras();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private boolean validateSelectedRequests(List<Job_order_request_item> requests_items, List<Job_order_request_item> extras_items) {
        if (requests_items.isEmpty() && extras_items.isEmpty()) {
            Toast.makeText(getBaseContext(), "No Requests Selected", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            for (int i = 0; i < requests_items.size(); i++) {
                if (requests_items.get(i).getFinal_cost().length() == 0) {
                    Toast.makeText(getBaseContext(), "You must add final cost to all selected requests", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            for (int i = 0; i < extras_items.size(); i++) {
                if (extras_items.get(i).getFinal_cost().length() == 0) {
                    Toast.makeText(getBaseContext(), "You must add final cost to all selected requests", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
    }

    public void getJobOrderRequests(int pageNum) {
        requests_loading.setVisibility(View.VISIBLE);

        ws.getApi().getJobOrderRequests(UserUtils.getAccessToken(getBaseContext()), 6, projectId, (request_types_spinner.getSelectedItemPosition() + 1), pageNum).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    assert response.body() != null;
                    JSONObject responseObject = new JSONObject(response.body().string());
                    JSONArray jobOrdersArray = responseObject.getJSONArray("data");
                    setJobOrderRequestsList(jobOrdersArray);
                    JSONObject metaObject = responseObject.getJSONObject("meta");
                    lastPageNum = metaObject.getInt("last_page");

                    requests_loading.setVisibility(View.GONE);

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
                requests_loading.setVisibility(View.GONE);
            }
        });
    }

    public void getJobOrderExtras() {
        extras_loading.setVisibility(View.VISIBLE);

        ws.getApi().getJobOrderExtras(UserUtils.getAccessToken(getBaseContext()), projectId, (request_types_spinner.getSelectedItemPosition() + 1)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    assert response.body() != null;
                    JSONObject responseObject = new JSONObject(response.body().string());
                    JSONArray jobOrdersArray = responseObject.getJSONArray("data");
                    setJobOrderExtrasList(jobOrdersArray);
                    extras_loading.setVisibility(View.GONE);

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
                extras_loading.setVisibility(View.GONE);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setJobOrderRequestsList(JSONArray list) {
        try {
            for (int i = 0; i < list.length(); i++) {

                JSONObject currentObject = list.getJSONObject(i);
                final int id = currentObject.getInt("id");
                final String request_id = currentObject.getString("item_name");
                final int type_id = currentObject.getInt("type_id");
                final String final_cost = currentObject.getJSONObject("cost").getString("cost_name");
                final String quantity = currentObject.getJSONObject("cost").getString("quantity_request");
                final String cost_type = currentObject.getJSONObject("cost").getString("cost_per_type");


                job_order_requests_list.add(new Job_order_request_item(id, type_id, request_id, final_cost, quantity, cost_type, false));

            }

            job_order_requests_adapter.notifyDataSetChanged();
            mHasReachedBottomOnce = false;
            currentPageNum++;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    public void setJobOrderExtrasList(JSONArray list) {
        try {
            for (int i = 0; i < list.length(); i++) {

                JSONObject currentObject = list.getJSONObject(i);
                final int id = currentObject.getInt("request_id");
                final String request_id = currentObject.getString("item_name");
                final int type_id = currentObject.getInt("extra_request_type");
                final String final_cost = currentObject.getJSONObject("cost").getString("cost_name");
                final String quantity = currentObject.getJSONObject("cost").getString("quantity_request");
                final String cost_type = currentObject.getJSONObject("cost").getString("cost_per_type");


                job_order_extras_list.add(new Job_order_request_item(id, type_id, request_id, final_cost, quantity, cost_type, false));

            }

            job_order_extras_adapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void createJobOrder(List<Job_order_request_item> requests_items, List<Job_order_request_item> extras_items, String supplierName) {
        Map<String, String> map = setJobOrderMap(requests_items, extras_items, supplierName);

        dialog.show();
        ws.getApi().addJobOrder(UserUtils.getAccessToken(getBaseContext()), map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        Toast.makeText(getBaseContext(), "Job Order Added successfully", Toast.LENGTH_LONG).show();
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

    private Map<String, String> setJobOrderMap(List<Job_order_request_item> requests_items, List<Job_order_request_item> extras_items, String supplierName) {

        ArrayList<String> requestIds_arr = new ArrayList<>();
        ArrayList<String> actual_costs_arr = new ArrayList<>();

        for (int i = 0; i < requests_items.size(); i++) {
            requestIds_arr.add(String.valueOf(requests_items.get(i).getId()));
            actual_costs_arr.add(requests_items.get(i).getFinal_cost());
        }
        for (int i = 0; i < extras_items.size(); i++) {
            requestIds_arr.add(String.valueOf(extras_items.get(i).getId()));
            actual_costs_arr.add(extras_items.get(i).getFinal_cost());
        }

        String requestIds = TextUtils.join(",", requestIds_arr);
        String actual_costs = TextUtils.join(",", actual_costs_arr);

        Map<String, String> map = new HashMap<>();
        map.put("job_order_name", "");
        map.put("supplier_name", supplierName);
        map.put("project_id", String.valueOf(projectId));
        int type_id_data = request_types_spinner.getSelectedItemPosition() + 1;
        map.put("type_id", String.valueOf(type_id_data));
        map.put("request_ids", requestIds);
        map.put("actual_costs", actual_costs);

        Log.e("TAG", map.toString());
        return map;
    }

    private void initFields() {

        ws = new WebserviceContext(this);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please, Wait...");
        dialog.setCancelable(false);

        projectId = getIntent().getIntExtra("project_id", 0);

        back = findViewById(R.id.back);
        requests_loading = findViewById(R.id.requests_loading);
        requests_loading = findViewById(R.id.requests_loading);
        extras_loading = findViewById(R.id.extras_loading);
        create_job_order = findViewById(R.id.create_job_order);
        request_types_spinner = findViewById(R.id.request_types_spinner);
        requestsRecyclerView = findViewById(R.id.recycler_view_requests);
        job_order_requests_list = new ArrayList<>();
        extrasRecyclerView = findViewById(R.id.recycler_view_extras);
        extras_layout_container = findViewById(R.id.extras_layout_container);
        job_order_extras_list = new ArrayList<>();
        initRequestsRecyclerView();
        initExtrasRecyclerView();

    }

    private void initRequestsRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        requestsRecyclerView.setLayoutManager(layoutManager);
        job_order_requests_adapter = new Job_orders_requests_adapter(getBaseContext(), job_order_requests_list);
        requestsRecyclerView.setAdapter(job_order_requests_adapter);

        requestsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

    private void initExtrasRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        extrasRecyclerView.setLayoutManager(layoutManager);
        job_order_extras_adapter = new Job_orders_requests_adapter(getBaseContext(), job_order_extras_list);
        extrasRecyclerView.setAdapter(job_order_extras_adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void selectedSupplier(String selectedSupplierName, String selectedSupplierId) {
        createJobOrder(job_order_requests_adapter.getSelectedData(), job_order_extras_adapter.getSelectedData(), selectedSupplierName);
    }
}