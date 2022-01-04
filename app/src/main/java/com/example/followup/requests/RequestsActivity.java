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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.example.followup.R;
import com.example.followup.job_orders.list.JobOrdersActivity;
import com.example.followup.requests.add.AddPhotographyActivity;
import com.example.followup.requests.list.Photography_requests_list;
import com.example.followup.requests.add.AddPrintActivity;
import com.example.followup.requests.list.Print_requests_list;
import com.example.followup.requests.add.AddProductionActivity;
import com.example.followup.requests.list.Production_requests_list;
import com.example.followup.requests.add.AddPurchaseActivity;
import com.example.followup.requests.list.Purchase_requests_list;
import com.example.followup.utils.UserType;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.Webservice;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RequestsActivity extends LocalizationActivity {

    ImageView back;
    FloatingActionButton addPhotography, addProduction, addPurchasing, addPrinting;
    TabLayout requests_tab;
    TextView job_orders, project_name;
    int projectId;
    int tabPosition;
    ProgressBar loading;

    //    String projectName;
//    boolean canEditProject;
    FloatingActionMenu add_menu_btn;

    public int getProjectId() {
        return projectId;
    }

    public void setContentFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.contentLayout, fragment);
        fragmentTransaction.commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        initFields();

        back.setOnClickListener(v -> onBackPressed());

        job_orders.setOnClickListener(v -> {
            Intent i = new Intent(getBaseContext(), JobOrdersActivity.class);
            i.putExtra("project_id", projectId);
            startActivity(i);
        });
        requests_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabPosition = tab.getPosition();
                setRequestsFragment(tabPosition);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        addPrinting.setOnClickListener(v -> {
            Intent i = new Intent(getBaseContext(), AddPrintActivity.class);
            i.putExtra("project_id", projectId);
            startActivity(i);
        });

        addProduction.setOnClickListener(v -> {
            Intent i = new Intent(getBaseContext(), AddProductionActivity.class);
            i.putExtra("project_id", projectId);
            startActivity(i);
        });

        addPurchasing.setOnClickListener(v -> {
            Intent i = new Intent(getBaseContext(), AddPurchaseActivity.class);
            i.putExtra("project_id", projectId);
            startActivity(i);
        });

        addPhotography.setOnClickListener(v -> {
            Intent i = new Intent(getBaseContext(), AddPhotographyActivity.class);
            i.putExtra("project_id", projectId);
            startActivity(i);
        });
        getProjectDetails();
    }


    private void initFields() {
        projectId = getIntent().getIntExtra("project_id", 0);
//        projectName = getIntent().getStringExtra("project_name");
//        canEditProject = getIntent().getBooleanExtra("can_edit_project", false);
        addPhotography = findViewById(R.id.photography_btn);
        addProduction = findViewById(R.id.production_btn);
        addPurchasing = findViewById(R.id.purchase_btn);
        addPrinting = findViewById(R.id.print_btn);
        requests_tab = findViewById(R.id.requests_tab);
        back = findViewById(R.id.back);
        job_orders = findViewById(R.id.job_orders);
        project_name = findViewById(R.id.project_name);
        loading = findViewById(R.id.loading);

        add_menu_btn = findViewById(R.id.add_menu_btn);
//        if (canEditProject) {
//            add_menu_btn.setVisibility(View.VISIBLE);
//        } else {
//            add_menu_btn.setVisibility(View.GONE);
//        }

    }

    private void setFields(String projectName, boolean canEditProject) {
        project_name.setText(projectName);

        if (canEditProject) {
            add_menu_btn.setVisibility(View.VISIBLE);
        } else {
            add_menu_btn.setVisibility(View.GONE);
        }
    }

    private void getProjectDetails() {
        loading.setVisibility(View.VISIBLE);

        Webservice.getInstance().getApi().getProjectDetails(UserUtils.getAccessToken(getBaseContext()), projectId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    JSONObject responseObject = new JSONObject(response.body().string());
                    JSONObject dataObj = responseObject.getJSONObject("data");
                    String projectName = dataObj.getString("project_name");
                    int created_by_id = dataObj.getJSONObject("user").getInt("id");
                    final String assigned_to = dataObj.getString("assign_to");
                    int assigned_to_id = 0;
                    if (!assigned_to.equals("null")) {
                        assigned_to_id = Integer.parseInt(assigned_to);
                    }
                    boolean canEditProject = UserType.canEditProject(getBaseContext(), created_by_id, assigned_to_id);
                    setFields(projectName, canEditProject);
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


    @Override
    protected void onResume() {
        super.onResume();
        setRequestsFragment(tabPosition);
    }

    private void setRequestsFragment(int selectedTab) {
        switch (selectedTab) {
            case 0:
                setContentFragment(new Purchase_requests_list());
                break;
            case 1:
                setContentFragment(new Print_requests_list());
                break;
            case 2:
                setContentFragment(new Production_requests_list());
                break;
            case 3:
                setContentFragment(new Photography_requests_list());
                break;
        }
    }

    public Map<String, String> getFilterMap() {
        Map<String, String> map = new HashMap<>();
        map.put("created_by", "");
        map.put("status", "");
        map.put("search", "");

        return map;
    }
}