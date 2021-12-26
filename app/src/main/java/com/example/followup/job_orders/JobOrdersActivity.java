package com.example.followup.job_orders;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.example.followup.R;
import com.example.followup.home.Attach_item;
import com.example.followup.requests.RequestsActivity;
import com.example.followup.requests.list.adapters.Photography_adapter;
import com.example.followup.requests.list.models.Photography_item;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.Webservice;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobOrdersActivity extends LocalizationActivity {

    ImageView back;
    ProgressBar loading;
    RecyclerView recyclerView;

    ArrayList<Job_order_item> job_order_list;
    Job_orders_adapter job_order_adapter;

    int currentPageNum = 1;
    int lastPageNum;
    boolean mHasReachedBottomOnce = false;

    int projectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_orders);
        initFields();
        back.setOnClickListener(v -> onBackPressed());
    }

    public void getJobOrders(int pageNum) {
        loading.setVisibility(View.VISIBLE);

        Webservice.getInstance().getApi().getJobOrders(UserUtils.getAccessToken(getBaseContext()),projectId, pageNum).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    JSONObject responseObject = new JSONObject(response.body().string());
                    JSONArray jobOrdersArray = responseObject.getJSONArray("data");
                    setJobOrdersList(jobOrdersArray);
//                    JSONObject metaObject = responseObject.getJSONObject("meta");
//                    lastPageNum = metaObject.getInt("last_page");

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


    public void setJobOrdersList(JSONArray list) {
        try {
            for (int i = 0; i < list.length(); i++) {


                JSONObject currentObject = list.getJSONObject(i);
                final int id = currentObject.getInt("id");
                final int project_id = currentObject.getInt("project_id");
                final int status_code = currentObject.getInt("status");
                final String status_message = currentObject.getString("status_message");
                final String name = currentObject.getString("name");
                final String po_number = currentObject.getString("po_number");


                job_order_list.add(new Job_order_item(id, project_id, status_code, status_message,
                        name, po_number));

            }

            job_order_adapter.notifyDataSetChanged();
            mHasReachedBottomOnce = false;
            currentPageNum++;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initFields() {
        projectId = getIntent().getIntExtra("project_id",0);

        back = findViewById(R.id.back);
        loading = findViewById(R.id.loading);
        recyclerView = findViewById(R.id.recycler_view);
        job_order_list = new ArrayList<>();
        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        job_order_adapter = new Job_orders_adapter(getBaseContext(), job_order_list);
        recyclerView.setAdapter(job_order_adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && !mHasReachedBottomOnce) {
                    mHasReachedBottomOnce = true;

                    if (currentPageNum <= lastPageNum)
                        getJobOrders( currentPageNum);

                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        job_order_list.clear();
        currentPageNum = 1;
        getJobOrders(currentPageNum);
    }
}