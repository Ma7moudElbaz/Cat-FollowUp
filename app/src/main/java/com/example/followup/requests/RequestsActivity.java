package com.example.followup.requests;


import android.content.Intent;
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
import com.example.followup.home.projects.Project_item;
import com.example.followup.job_orders.JobOrdersActivity;
import com.example.followup.requests.photography.AddPhotographyActivity;
import com.example.followup.requests.print.AddPrintActivity;
import com.example.followup.requests.production.AddProductionActivity;
import com.example.followup.requests.purchase.AddPurchaseActivity;
import com.example.followup.requests.purchase.Purchase_adapter;
import com.example.followup.requests.purchase.Purchase_item;
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

public class RequestsActivity extends LocalizationActivity {

    ImageView back;
    FloatingActionButton addPhotography, addProduction, addPurchasing, addPrinting;
    TabLayout requests_tab;
    TextView job_orders;
    int selectedTab=0;

    RecyclerView recyclerView;
    ProgressBar loading;

    ArrayList<Purchase_item> purchase_list;
    Purchase_adapter purchase_adapter;

    int currentPageNum = 1;
    int lastPageNum;
    boolean mHasReachedBottomOnce = false;

    int projectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        initFields();

        back.setOnClickListener(v -> onBackPressed());
        job_orders.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), JobOrdersActivity.class)));

        requests_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
               int tabId= tab.getPosition();
               switch (tabId){
                   case 0:
                       Toast.makeText(getBaseContext(), "Purchase", Toast.LENGTH_SHORT).show();
                       break;
                   case 1:
                       Toast.makeText(getBaseContext(), "Print", Toast.LENGTH_SHORT).show();
                       break;
                   case 2:
                       Toast.makeText(getBaseContext(), "Production", Toast.LENGTH_SHORT).show();
                       break;
                   case 3:
                       Toast.makeText(getBaseContext(), "Photography", Toast.LENGTH_SHORT).show();
                       break;
               }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        addPrinting.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), AddPrintActivity.class)));
        addProduction.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), AddProductionActivity.class)));
        addPurchasing.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), AddPurchaseActivity.class)));
        addPhotography.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), AddPhotographyActivity.class)));
    }

    public void getRequests(int selectedTab,int pageNum) {
        loading.setVisibility(View.VISIBLE);

        Webservice.getInstance().getApi().getRequests(UserUtils.getAccessToken(getBaseContext()),projectId,(selectedTab+1), pageNum).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    JSONObject responseObject = new JSONObject(response.body().string());
                    JSONArray requestsArray = responseObject.getJSONArray("data");
                    setPurchaseList(requestsArray);
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


    public void setPurchaseList(JSONArray list) {
        try {
            for (int i = 0; i < list.length(); i++) {
                JSONObject currentObject = list.getJSONObject(i);
                final int id = currentObject.getInt("id");
                final int type_id = currentObject.getInt("type_id");
                final int created_by_id = currentObject.getInt("created_by_id");
                final int status_code = currentObject.getInt("status");
                final int quantity = currentObject.getInt("quantity");
                final String status_message = currentObject.getString("status");
                final String item_name = currentObject.getString("item_name");
                final String description = currentObject.getString("description");
                final String delivery_address = currentObject.getString("delivery_address");
                final String note = currentObject.getString("note");
                final String color = currentObject.getString("color");
                final String material = currentObject.getString("material");
                final String brand = currentObject.getString("brand");
                final String created_by_name = currentObject.getString("created_by_name");

                ArrayList<Attach_item> attach_files = new ArrayList<>();

                purchase_list.add(new Purchase_item(id,type_id,created_by_id,status_code,quantity, status_message,
                        item_name,description,delivery_address,note,color,material,brand,created_by_name,attach_files));

            }

            purchase_adapter.notifyDataSetChanged();
            mHasReachedBottomOnce = false;
            currentPageNum++;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initFields() {
        projectId = getIntent().getIntExtra("project_id",0);
        addPhotography = findViewById(R.id.photography_btn);
        addProduction = findViewById(R.id.production_btn);
        addPurchasing = findViewById(R.id.purchase_btn);
        addPrinting = findViewById(R.id.print_btn);
        requests_tab = findViewById(R.id.requests_tab);
        back = findViewById(R.id.back);
        job_orders = findViewById(R.id.job_orders);

        loading = findViewById(R.id.loading);
        recyclerView = findViewById(R.id.recycler_view);
        purchase_list = new ArrayList<>();
        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        purchase_adapter = new Purchase_adapter(getBaseContext(), purchase_list);
        recyclerView.setAdapter(purchase_adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && !mHasReachedBottomOnce) {
                    mHasReachedBottomOnce = true;

                    if (currentPageNum <= lastPageNum)
                        getRequests(selectedTab,currentPageNum);

                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        purchase_list.clear();
        currentPageNum = 1;
        getRequests(selectedTab,currentPageNum);
    }
}