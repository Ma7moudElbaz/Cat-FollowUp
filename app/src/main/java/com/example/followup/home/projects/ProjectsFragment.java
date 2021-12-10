package com.example.followup.home.projects;

import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.followup.R;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.Webservice;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_projects, container, false);
    }

    FloatingActionButton fab_addProject;
    RecyclerView recyclerView;
    ProgressBar loading;

    ArrayList<Project_item> projects_list;
    Projects_adapter projects_adapter;

    int currentPageNum = 1;
    int lastPageNum;
    boolean mHasReachedBottomOnce = false;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFields(view);
        fab_addProject.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddProjectActivity.class)));

    }

    public void getProjects(int pageNum) {
        loading.setVisibility(View.VISIBLE);

        Webservice.getInstance().getApi().getProjects(UserUtils.getAccessToken(getContext()), pageNum).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    JSONObject responseObject = new JSONObject(response.body().string());
                    JSONArray projectsArray = responseObject.getJSONArray("data");
                    setProjectsList(projectsArray);
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

    public void setProjectsList(JSONArray list) {
        try {
            for (int i = 0; i < list.length(); i++) {
                JSONObject currentObject = list.getJSONObject(i);
                final int id = currentObject.getInt("id");
                final int user_id = currentObject.getInt("user_id");
                final int status = currentObject.getInt("status");
                final String client_company = currentObject.getString("client_company");
                final String project_name = currentObject.getString("project_name");
                final String client_name = currentObject.getString("client_name");
                final String project_country = currentObject.getString("project_country");
                final String project_timeline = currentObject.getString("project_timeline");
                final String created_at = currentObject.getString("created_at");
                final String created_by = currentObject.getJSONObject("user").getString("name");

                projects_list.add(new Project_item(id,user_id,status,client_company,project_name,client_name,project_country,project_timeline,created_at,created_by));

            }

            projects_adapter.notifyDataSetChanged();
            mHasReachedBottomOnce = false;
            currentPageNum++;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initFields(View view){
        fab_addProject = view.findViewById(R.id.fab_add_project);
        loading = view.findViewById(R.id.loading);
        recyclerView = view.findViewById(R.id.recycler_view);
        projects_list = new ArrayList<>();
        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        projects_adapter = new Projects_adapter(getContext(), projects_list);
        recyclerView.setAdapter(projects_adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && !mHasReachedBottomOnce) {
                    mHasReachedBottomOnce = true;

                    if (currentPageNum <= lastPageNum)
                        getProjects(currentPageNum);

                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getProjects(currentPageNum);
    }
}