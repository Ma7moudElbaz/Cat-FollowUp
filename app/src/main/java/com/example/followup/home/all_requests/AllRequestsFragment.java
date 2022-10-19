package com.example.followup.home.all_requests;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.followup.R;
import com.example.followup.bottomsheets.BottomSheet_choose_filter_all_requests;
import com.example.followup.bottomsheets.BottomSheet_choose_filter_requests;
import com.example.followup.bottomsheets.BottomSheet_companies;
import com.example.followup.bottomsheets.BottomSheet_projects;
import com.example.followup.home.projects.ProjectsFragment;
import com.example.followup.requests.RequestsActivity;
import com.example.followup.requests.list.adapters.Purchase_adapter;
import com.example.followup.requests.list.models.Purchase_item;
import com.example.followup.requests.view.attaches.Attach_item;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.WebserviceContext;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllRequestsFragment extends Fragment implements BottomSheet_choose_filter_all_requests.FilterListener,BottomSheet_projects.SelectedProjectsListener {

    RecyclerView recyclerView;
    ProgressBar loading;
    TextView search, project_name;
    ImageView filterBtn;
    TabLayout requests_tab;

    ArrayList<Request_item> requests_list;
    Request_adapter request_adapter;

    int currentPageNum = 1;
    int lastPageNum;
    boolean mHasReachedBottomOnce = false;


    String selectedStatus = "";
    int selectedStatusIndex = 0;
    String project_id = "";


    WebserviceContext ws;

    private static void hideKeyboardFragment(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showFilterSheet() {
        BottomSheet_choose_filter_all_requests filterSheet =
                new BottomSheet_choose_filter_all_requests(selectedStatusIndex, this);
        filterSheet.show(getChildFragmentManager(), "requests_filter");
    }

    private void showProjectsBottomSheet() {
        BottomSheet_projects projectsBottomSheet =
                new BottomSheet_projects(this);
        projectsBottomSheet.show(getParentFragmentManager(), "projects");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFields(view);

        filterBtn.setOnClickListener(v -> showFilterSheet());

        project_name.setOnClickListener(v -> showProjectsBottomSheet());

        search.setOnEditorActionListener((v, actionId, event) -> {

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                reloadRequestsData();
                hideKeyboardFragment(getContext(), view);
                return true;
            }
            return false;
        });
        requests_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                reloadRequestsData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        getRequests(currentPageNum, getFilterMap());
    }

    private void initFields(View view) {
        ws = new WebserviceContext(getActivity());

        loading = view.findViewById(R.id.loading);
        search = view.findViewById(R.id.search);
        project_name = view.findViewById(R.id.project_name);
        filterBtn = view.findViewById(R.id.filter_btn);
        recyclerView = view.findViewById(R.id.recycler_view);
        requests_tab = view.findViewById(R.id.requests_tab);
        requests_list = new ArrayList<>();
        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        request_adapter = new Request_adapter(getContext(), requests_list);
        recyclerView.setAdapter(request_adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && !mHasReachedBottomOnce) {
                    mHasReachedBottomOnce = true;

                    if (currentPageNum <= lastPageNum)
                        getRequests(currentPageNum, getFilterMap());

                }
            }
        });
    }


    public void getRequests(int pageNum, Map<String, String> filterMap) {
        loading.setVisibility(View.VISIBLE);

        ws.getApi().getAllRequests(UserUtils.getAccessToken(getContext()), pageNum, filterMap).enqueue(new Callback<ResponseBody>() {
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
                Toast.makeText(getContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
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
                final String cost_status = currentObject.getString("status_cost");
                int cost_status_code = 1;
                if (!cost_status.equals("null")) {
                    cost_status_code = Integer.parseInt(cost_status);
                }
                final String project_name = currentObject.getString("project_name");
                final String request_name = currentObject.getString("item_name");
                final String request_type = currentObject.getString("type_name");
                final String status = currentObject.getString("status_message");
                final String created_by = currentObject.getJSONObject("created_by_name").getString("name");

                final boolean have_action = currentObject.getBoolean("have_action");

                int project_creator_id = currentObject.getInt("project_creator_id");
                final String assigned_to = currentObject.getString("project_assign_id");
                int project_assign_id = 0;
                if (!assigned_to.equals("null")) {
                    project_assign_id = Integer.parseInt(assigned_to);
                }


                requests_list.add(new Request_item(id, type_id, created_by_id, project_creator_id, project_assign_id,
                        status_code, cost_status_code, project_name, request_name, request_type, status, created_by, have_action));
            }

            request_adapter.notifyDataSetChanged();
            mHasReachedBottomOnce = false;
            currentPageNum++;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Map<String, String> getFilterMap() {
        Map<String, String> map = new HashMap<>();
        map.put("project_id", project_id);
        map.put("type_id", String.valueOf(requests_tab.getSelectedTabPosition() + 1));
        map.put("search", search.getText().toString());
        switch (selectedStatus) {
            case "0":
                map.put("status", selectedStatus);
                map.put("cost_status", selectedStatus);
                break;
            case "1":
            case "6":
            case "7":
                map.put("status", selectedStatus);
                map.put("cost_status", "");
                break;

            default:
                map.put("status", "");
                map.put("cost_status", selectedStatus);
                break;
        }

        return map;
    }

    private void reloadRequestsData() {
        requests_list.clear();
        currentPageNum = 1;
        getRequests(currentPageNum, getFilterMap());
    }

    @Override
    public void applyFilterListener(int returnedSelectedStatusIndex, String returnedSelectedStatus) {
        if (returnedSelectedStatusIndex == -1) {
            selectedStatus = "";
        } else {
            selectedStatus = returnedSelectedStatus;
        }
        selectedStatusIndex = returnedSelectedStatusIndex;
        reloadRequestsData();
    }

    @Override
    public void selectedProject(String selectedProjectName, String selectedProjectId) {
        project_name.setText(selectedProjectName);
        project_id = selectedProjectId;
        reloadRequestsData();
    }
}