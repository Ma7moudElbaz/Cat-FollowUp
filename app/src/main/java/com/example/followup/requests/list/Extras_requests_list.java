package com.example.followup.requests.list;

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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.followup.R;
import com.example.followup.requests.RequestsActivity;
import com.example.followup.requests.add.AddExtrasActivity;
import com.example.followup.requests.list.adapters.Extras_adapter;
import com.example.followup.requests.list.adapters.Photography_adapter;
import com.example.followup.requests.list.models.Extras_item;
import com.example.followup.requests.list.models.Photography_item;
import com.example.followup.requests.view.attaches.Attach_item;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.WebserviceContext;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Extras_requests_list extends Fragment {
    RecyclerView recyclerView;
    ProgressBar loading;

    ArrayList<Extras_item> extras_list;
    Extras_adapter extras_adapter;

    int currentPageNum = 1;
    int lastPageNum;
    boolean mHasReachedBottomOnce = false;

    int projectId;
    RequestsActivity activity;
    WebserviceContext ws;

    Button addExtrasRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_extras_requests_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFields(view);
        addExtrasRequest.setOnClickListener(view1 -> {
            Intent i = new Intent(getActivity(), AddExtrasActivity.class);
            startActivity(i);
        });
    }


    public void getRequests(int selectedTab, int pageNum, Map<String, String> filterMap) {
        loading.setVisibility(View.VISIBLE);

        ws.getApi().getRequests(UserUtils.getAccessToken(getContext()), projectId, (selectedTab + 1), pageNum, filterMap).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    JSONObject responseObject = new JSONObject(response.body().string());
                    JSONArray requestsArray = responseObject.getJSONArray("data");
                    setExtrasList(requestsArray);
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


    public void setExtrasList(JSONArray list) {
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
                final String status_message = currentObject.getString("status_message");
                final String item_name = currentObject.getString("item_name");
                final String description = currentObject.getString("description");
                final String delivery_address = currentObject.getString("delivery_address");
                final String created_by_name = currentObject.getJSONObject("created_by_name").getString("name");

                final boolean have_action = currentObject.getBoolean("have_action");

                int project_creator_id = currentObject.getInt("project_creator_id");
                final String assigned_to = currentObject.getString("project_assign_id");
                int project_assign_id = 0;
                if (!assigned_to.equals("null")) {
                    project_assign_id = Integer.parseInt(assigned_to);
                }

                extras_list.add(new Extras_item(id, type_id, created_by_id, project_creator_id, project_assign_id, status_code, cost_status_code, item_name, description, delivery_address, status_message, created_by_name, have_action));

            }

            extras_adapter.notifyDataSetChanged();
            mHasReachedBottomOnce = false;
            currentPageNum++;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initFields(View view) {
        ws = new WebserviceContext(getActivity());
        activity = (RequestsActivity) getActivity();
        projectId = activity.getProjectId();

        loading = view.findViewById(R.id.loading);
        addExtrasRequest = view.findViewById(R.id.add_extras_request);
        recyclerView = view.findViewById(R.id.recycler_view);
        extras_list = new ArrayList<>();
        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        extras_adapter = new Extras_adapter(getContext(), extras_list);
        recyclerView.setAdapter(extras_adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && !mHasReachedBottomOnce) {
                    mHasReachedBottomOnce = true;

                    if (currentPageNum <= lastPageNum)
                        getRequests(3, currentPageNum, activity.getFilterMap());

                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        extras_list.clear();
        currentPageNum = 1;
        getRequests(4, currentPageNum, activity.getFilterMap());
    }
}