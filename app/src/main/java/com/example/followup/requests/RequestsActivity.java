package com.example.followup.requests;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.example.followup.R;
import com.example.followup.bottomsheets.BottomSheet_choose_filter_requests;
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
import com.example.followup.webservice.WebserviceContext;
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


public class RequestsActivity extends LocalizationActivity implements BottomSheet_choose_filter_requests.FilterListener {

    public void showFilterSheet() {
        BottomSheet_choose_filter_requests langBottomSheet =
                new BottomSheet_choose_filter_requests(selectedStatusIndex);
        langBottomSheet.show(getSupportFragmentManager(), "requests_filter");
    }

    public static void hideKeyboardActivity(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    ImageView back;
    FloatingActionButton addPhotography, addProduction, addPurchasing, addPrinting;
    TabLayout requests_tab;
    TextView job_orders, project_name;
    int projectId;
    int tabPosition;
    boolean firstCall = true;
    ProgressBar loading;
    TextView search;
    ImageView filterBtn;

    int selectedStatusIndex = -1;
    String selectedStatus = "";
    String[] chipsStatus = new String[]{"", "1", "2", "4", "5", "3", "6"};

    //    String projectName;
//    boolean canEditProject;
    FloatingActionMenu add_menu_btn;

    SwipeRefreshLayout swipe_refresh;
    int children_id;
    int purchase_no, printing_no, production_no, photo_no;

    WebserviceContext ws;

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
        search.setOnEditorActionListener((v, actionId, event) -> {

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                setRequestsFragment(tabPosition);
                hideKeyboardActivity(RequestsActivity.this);
                return true;
            }
            return false;
        });

        filterBtn.setOnClickListener(v -> showFilterSheet());

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

        setSelectedUserTab(children_id);

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

        swipe_refresh.setOnRefreshListener(() -> {
            swipe_refresh.setRefreshing(false);
            setRequestsFragment(tabPosition);
        });

    }


    private void initFields() {
        ws = new WebserviceContext(this);
        projectId = getIntent().getIntExtra("project_id", 0);
        addPhotography = findViewById(R.id.photography_btn);
        addProduction = findViewById(R.id.production_btn);
        addPurchasing = findViewById(R.id.purchase_btn);
        addPrinting = findViewById(R.id.print_btn);
        requests_tab = findViewById(R.id.requests_tab);
        back = findViewById(R.id.back);
        job_orders = findViewById(R.id.job_orders);
        project_name = findViewById(R.id.project_name);
        loading = findViewById(R.id.loading);
        search = findViewById(R.id.search);
        filterBtn = findViewById(R.id.filter_btn);

        swipe_refresh = findViewById(R.id.swipe_refresh);

        add_menu_btn = findViewById(R.id.add_menu_btn);

        children_id = UserUtils.getChildId(getBaseContext());

    }

    private void setFields(String projectName, boolean canEditProject, int projectStatus) {
        project_name.setText(projectName);
        Log.e("project status", "" + projectStatus);

        if (canEditProject && projectStatus == 1) {
            add_menu_btn.setVisibility(View.VISIBLE);
        } else {
            add_menu_btn.setVisibility(View.GONE);
        }
    }

    private void getProjectDetails() {
        ws.getApi().getProjectDetails(UserUtils.getAccessToken(getBaseContext()), projectId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    JSONObject responseObject = new JSONObject(response.body().string());
                    JSONObject dataObj = responseObject.getJSONObject("data");
                    String projectName = dataObj.getString("project_name");
                    int projectStatus = dataObj.getInt("status");
                    int created_by_id = dataObj.getJSONObject("user").getInt("id");
                    final String assigned_to = dataObj.getString("assign_to");
                    int assigned_to_id = 0;
                    if (!assigned_to.equals("null")) {
                        assigned_to_id = Integer.parseInt(assigned_to);
                    }
                    boolean canEditProject = UserType.canEditProject(getBaseContext(), created_by_id, assigned_to_id);
                    setFields(projectName, canEditProject, projectStatus);

                    purchase_no = dataObj.getInt("requests_purchasing");
                    printing_no = dataObj.getInt("requests_printing");
                    production_no = dataObj.getInt("requests_production");
                    photo_no = dataObj.getInt("requests_photography");
                    setUserBadges();

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
            }
        });
    }

    private void setUserBadges() {
        switch (children_id) {
            case 5:
                setBadges(printing_no);
                break;
            case 6:
                setBadges(photo_no);
                break;
            case 7:
                setBadges(purchase_no);
                break;
            case 8:
                setBadges(production_no);
                break;
            default:
                setBadges(purchase_no, printing_no, production_no, photo_no);
                break;
        }
    }

    private void setBadges(int purchase_no, int printing_no, int production_no, int photo_no) {
        requests_tab.getTabAt(0).getOrCreateBadge().setNumber(purchase_no);
        requests_tab.getTabAt(1).getOrCreateBadge().setNumber(printing_no);
        requests_tab.getTabAt(2).getOrCreateBadge().setNumber(production_no);
        requests_tab.getTabAt(3).getOrCreateBadge().setNumber(photo_no);
    }

    private void setBadges(int currentNo) {
        requests_tab.getTabAt(0).getOrCreateBadge().setNumber(currentNo);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getProjectDetails();
        if (!firstCall) {
            setRequestsFragment(tabPosition);
        }
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

    private void setSelectedUserTab(int children_id) {
        switch (children_id) {
            case 5:
                requests_tab.getTabAt(1).select();
                removeTabs(1);
                break;
            case 6:
                requests_tab.getTabAt(3).select();
                removeTabs(3);
                break;
            case 7:
                requests_tab.getTabAt(0).select();
                removeTabs(0);
                break;
            case 8:
                requests_tab.getTabAt(2).select();
                removeTabs(2);
                break;
            default:
                requests_tab.getTabAt(0).select();
                break;
        }
        firstCall = false;
    }

    private void removeTabs(int keepTab) {
        int tab_to_keep = keepTab;
        int tab_to_delete = 0;
        for (int i = 0; i < 3; i++) {
            if (tab_to_keep != 0) {
                requests_tab.removeTabAt(tab_to_delete);
                tab_to_keep--;
            } else {
                tab_to_delete = 1;
                requests_tab.removeTabAt(tab_to_delete);
            }
        }
    }

    public Map<String, String> getFilterMap() {
        Map<String, String> map = new HashMap<>();
        map.put("created_by", "");
        map.put("status", selectedStatus);
        map.put("search", search.getText().toString());

        return map;
    }

    @Override
    public void applyFilterListener(int returenedSelectedStatusIndex) {
        if (returenedSelectedStatusIndex == -1) {
            selectedStatus = "";
        } else {
            selectedStatus = chipsStatus[returenedSelectedStatusIndex];
        }

        selectedStatusIndex = returenedSelectedStatusIndex;
        setRequestsFragment(tabPosition);
    }
}