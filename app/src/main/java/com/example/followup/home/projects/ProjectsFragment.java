package com.example.followup.home.projects;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.example.followup.bottomsheets.BottomSheet_choose_filter_projects;
import com.example.followup.bottomsheets.BottomSheet_companies;
import com.example.followup.bottomsheets.BottomSheet_po_number_from_fragment;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.WebserviceContext;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mindorks.editdrawabletext.EditDrawableText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectsFragment extends Fragment implements Projects_adapter_with_callback.AdapterCallback, BottomSheet_choose_filter_projects.FilterListener, BottomSheet_companies.SelectedCompanyListener, BottomSheet_po_number_from_fragment.PoNumberSubmitListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_projects, container, false);
    }

    public void showFilterSheet() {
        BottomSheet_choose_filter_projects filterBottomSheet =
                new BottomSheet_choose_filter_projects(ProjectsFragment.this, selectedStatusIndex);
        filterBottomSheet.show(getParentFragmentManager(), "requests_filter");
    }


    public void showCompaniesBottomSheet() {
        BottomSheet_companies companiesBottomSheet =
                new BottomSheet_companies(ProjectsFragment.this);
        companiesBottomSheet.show(getParentFragmentManager(), "companies");
    }

    public void showPoNumberSheet(int projectId) {
        BottomSheet_po_number_from_fragment langBottomSheet =
                new BottomSheet_po_number_from_fragment(ProjectsFragment.this, projectId, "po_number");
        langBottomSheet.show(getParentFragmentManager(), "po_number");
    }

    public static void hideKeyboardFragment(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    FloatingActionButton fab_addProject;
    RecyclerView recyclerView;
    ProgressBar loading;
    TextView client_company;
    EditDrawableText search;
    ImageView filterBtn;

    ArrayList<Project_item> projects_list;
    Projects_adapter_with_callback projects_adapter;

    int currentPageNum = 1;
    int lastPageNum;
    boolean mHasReachedBottomOnce = false;

    int selectedStatusIndex = -1;
    String selectedStatus = "";
    String selectedCompanyId = "";
    final String[] chipsStatus = new String[]{"", "1", "0", "2"};

    SwipeRefreshLayout swipe_refresh;


    WebserviceContext ws;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFields(view);
        fab_addProject.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddProjectActivity.class)));
        filterBtn.setOnClickListener(v -> showFilterSheet());

        client_company.setOnClickListener(v -> showCompaniesBottomSheet());

        search.setDrawableClickListener(drawablePosition -> {
            reloadData();
            hideKeyboardFragment(requireContext(), view);
        });

        search.setOnEditorActionListener((v, actionId, event) -> {

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                reloadData();
                hideKeyboardFragment(requireContext(), view);
                return true;
            }
            return false;
        });

        swipe_refresh.setOnRefreshListener(this::reloadData);

    }


    public void getProjects(int pageNum, Map<String, String> filterMap) {
        swipe_refresh.setRefreshing(true);

        ws.getApi().getProjects(UserUtils.getAccessToken(getContext()), pageNum, filterMap).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    assert response.body() != null;
                    JSONObject responseObject = new JSONObject(response.body().string());
                    JSONArray projectsArray = responseObject.getJSONArray("data");
                    setProjectsList(projectsArray);
                    JSONObject metaObject = responseObject.getJSONObject("meta");
                    lastPageNum = metaObject.getInt("last_page");

                    swipe_refresh.setRefreshing(false);

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
                swipe_refresh.setRefreshing(false);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setProjectsList(JSONArray list) {
        try {
            for (int i = 0; i < list.length(); i++) {
                JSONObject currentObject = list.getJSONObject(i);
                final int id = currentObject.getInt("id");
                final int user_id = currentObject.getInt("user_id");
                final int status_code = currentObject.getInt("status");
                final String status_message = currentObject.getString("status_message");
                final String client_company = currentObject.getString("client_company");
                final String project_name = currentObject.getString("project_name");
                final String client_name = currentObject.getString("client_name");
                final String project_country = currentObject.getString("project_country");
                final String project_timeline = currentObject.getString("project_timeline");
                final String created_at = currentObject.getString("created_at");
                final String created_by = currentObject.getJSONObject("user").getString("name");
                final int created_by_id = currentObject.getJSONObject("user").getInt("id");
                final String assigned_to = currentObject.getString("assign_to");
                final boolean have_action = currentObject.getBoolean("have_action");
                int assigned_to_id = 0;
                if (!assigned_to.equals("null")) {
                    assigned_to_id = Integer.parseInt(assigned_to);
                }

                projects_list.add(new Project_item(id, user_id, status_code, created_by_id, assigned_to_id, status_message, client_company, project_name, client_name, project_country, project_timeline, created_at, created_by, have_action));

            }

            projects_adapter.notifyDataSetChanged();
            mHasReachedBottomOnce = false;
            currentPageNum++;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Map<String, String> getFilterMap() {

        Map<String, String> map = new HashMap<>();
        map.put("status", selectedStatus);
        map.put("client_name", "");
        map.put("client_company", "");
        map.put("country_id", "");
        map.put("search", search.getText().toString());
        map.put("company_id", selectedCompanyId);

        return map;
    }

    private void initFields(View view) {
        ws = new WebserviceContext(getActivity());
        fab_addProject = view.findViewById(R.id.fab_add_project);
        loading = view.findViewById(R.id.loading);
        search = view.findViewById(R.id.search);
        filterBtn = view.findViewById(R.id.filter_btn);
        recyclerView = view.findViewById(R.id.recycler_view);
        client_company = view.findViewById(R.id.client_company);

        swipe_refresh = view.findViewById(R.id.swipe_refresh);
        projects_list = new ArrayList<>();
        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        projects_adapter = new Projects_adapter_with_callback(getContext(), this, projects_list);
        recyclerView.setAdapter(projects_adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && !mHasReachedBottomOnce) {
                    mHasReachedBottomOnce = true;

                    if (currentPageNum <= lastPageNum)
                        getProjects(currentPageNum, getFilterMap());

                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadData();
    }

    private void reloadData() {
        projects_list.clear();
        currentPageNum = 1;
        getProjects(currentPageNum, getFilterMap());
    }


    @Override
    public void adapterCallback(String action, int project_id) {
        switch (action) {
            case "cancel":
                new AlertDialog.Builder(getContext())
                        .setTitle("Are you sure? ")
                        .setPositiveButton("Yes", (dialog, which) -> cancelProject(project_id))
                        .setNegativeButton("Dismiss", null)
                        .show();

                break;
            case "done":
                new AlertDialog.Builder(getContext())
                        .setTitle("Are you sure? ")
                        .setPositiveButton("Yes", (dialog, which) -> doneProject(project_id))
                        .setNegativeButton("Dismiss", null)
                        .show();
                break;
            case "po_number":
                showPoNumberSheet(project_id);
                break;
        }
    }

    public void cancelProject(int project_id) {

        loading.setVisibility(View.VISIBLE);
        WebserviceContext.getInstance().getApi().projectCancel(UserUtils.getAccessToken(getContext()), project_id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Updated successfully", Toast.LENGTH_LONG).show();
                        projects_list.clear();
                        currentPageNum = 1;
                        getProjects(currentPageNum, getFilterMap());
                    } else {
                        assert response.errorBody() != null;
                        JSONObject res = new JSONObject(response.errorBody().string());
                        Toast.makeText(getContext(), res.getString("error"), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.GONE);
            }
        });
    }

    public void doneProject(int project_id) {

        loading.setVisibility(View.VISIBLE);
        ws.getApi().projectDone(UserUtils.getAccessToken(getContext()), project_id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Updated successfully", Toast.LENGTH_LONG).show();
                        projects_list.clear();
                        currentPageNum = 1;
                        getProjects(currentPageNum, getFilterMap());
                    } else {
                        assert response.errorBody() != null;
                        Toast.makeText(getContext(), response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void addPoNumber(int projectId, String poNumber) {
        Map<String, String> map = new HashMap<>();
        map.put("project_id", String.valueOf(projectId));
        map.put("number", poNumber);

        loading.setVisibility(View.VISIBLE);
        ws.getApi().addPoNumber(UserUtils.getAccessToken(getContext()), map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "PO Added successfully", Toast.LENGTH_LONG).show();
                    } else {
                        assert response.errorBody() != null;
                        Toast.makeText(getContext(), response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void applyFilterListener(int returnedSelectedStatusIndex) {
        if (returnedSelectedStatusIndex == -1) {
            selectedStatus = "";
        } else {
            selectedStatus = chipsStatus[returnedSelectedStatusIndex];
        }

        selectedStatusIndex = returnedSelectedStatusIndex;

        projects_list.clear();
        currentPageNum = 1;
        getProjects(currentPageNum, getFilterMap());
    }

    @Override
    public void selectedCompany(String companyName, String companyId) {
        client_company.setText(companyName);
        selectedCompanyId = companyId;
        reloadData();
    }

    @Override
    public void poNumberSubmitListener(String po_number_text, int projectId, String type) {
        addPoNumber(projectId, po_number_text);
    }
}