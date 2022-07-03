package com.example.followup.requests.list;

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
import com.example.followup.requests.RequestsActivity;
import com.example.followup.requests.list.adapters.Print_adapter;
import com.example.followup.requests.list.models.Print_item;
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

public class Print_requests_list extends Fragment {
    RecyclerView recyclerView;
    ProgressBar loading;

    ArrayList<Print_item> print_list;
    Print_adapter print_adapter;

    int currentPageNum = 1;
    int lastPageNum;
    boolean mHasReachedBottomOnce = false;

    int projectId;
    RequestsActivity activity;

    WebserviceContext ws;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_print_requests_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFields(view);

    }

    public void getRequests(int selectedTab, int pageNum, Map<String, String> filterMap) {
        loading.setVisibility(View.VISIBLE);

        ws.getApi().getRequests(UserUtils.getAccessToken(getContext()), projectId, (selectedTab + 1), pageNum, filterMap).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    JSONObject responseObject = new JSONObject(response.body().string());
                    JSONArray requestsArray = responseObject.getJSONArray("data");
                    setPrintList(requestsArray);
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


    public void setPrintList(JSONArray list) {
        try {
            for (int i = 0; i < list.length(); i++) {


                JSONObject currentObject = list.getJSONObject(i);
                final int id = currentObject.getInt("id");
                final int type_id = currentObject.getInt("type_id");
                final int created_by_id = currentObject.getInt("created_by_id");
                final int status_code = currentObject.getInt("status");
                final String cost_status = currentObject.getString("status_cost");
                int cost_status_code = 1;
                if (!cost_status.equals("null")){
                    cost_status_code = Integer.parseInt(cost_status);
                }
                final String quantity = currentObject.getString("quantity");
                final String status_message = currentObject.getString("status_message");
                final String item_name = currentObject.getString("item_name");
                final String description = currentObject.getString("description");
                final String delivery_address = currentObject.getString("delivery_address");
                final String note = currentObject.getString("note");
                final String pages = currentObject.getString("pages");
                final String paper_weight = currentObject.getString("paper_weight");
                final String print_type = currentObject.getString("print_type");
                final String colors = currentObject.getString("color");
                final String lamination = currentObject.getString("lamination");
                final String binding = currentObject.getString("binding");
                final String di_cut = currentObject.getString("di_cut");
                final String designer_name = currentObject.getString("designer_name");
                final String created_by_name = currentObject.getJSONObject("created_by_name").getString("name");

                final boolean have_action = currentObject.getBoolean("have_action");
                ArrayList<Attach_item> attach_files = new ArrayList<>();

                int project_creator_id = currentObject.getInt("project_creator_id");
                final String assigned_to = currentObject.getString("project_assign_id");
                int project_assign_id = 0;
                if (!assigned_to.equals("null")) {
                    project_assign_id = Integer.parseInt(assigned_to);
                }

                print_list.add(new Print_item(id, type_id, created_by_id, project_creator_id, project_assign_id, status_code, cost_status_code, quantity, status_message,
                        item_name, description, delivery_address, note, pages, paper_weight, print_type, colors,
                        lamination, binding, di_cut, designer_name, created_by_name, attach_files, have_action));

            }

            print_adapter.notifyDataSetChanged();
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
        recyclerView = view.findViewById(R.id.recycler_view);
        print_list = new ArrayList<>();
        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        print_adapter = new Print_adapter(getContext(), print_list);
        recyclerView.setAdapter(print_adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && !mHasReachedBottomOnce) {
                    mHasReachedBottomOnce = true;

                    if (currentPageNum <= lastPageNum)
                        getRequests(1, currentPageNum,activity.getFilterMap());

                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        print_list.clear();
        currentPageNum = 1;
        getRequests(1, currentPageNum,activity.getFilterMap());
    }
}