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
import com.example.followup.home.Attach_item;
import com.example.followup.requests.RequestsActivity;
import com.example.followup.requests.list.adapters.Photography_adapter;
import com.example.followup.requests.list.models.Photography_item;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.Webservice;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Photography_requests_fragment extends Fragment {
    RecyclerView recyclerView;
    ProgressBar loading;

    ArrayList<Photography_item> photography_list;
    Photography_adapter photography_adapter;

    int currentPageNum = 1;
    int lastPageNum;
    boolean mHasReachedBottomOnce = false;

    int projectId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photography_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFields(view);
    }

    public void getRequests(int selectedTab, int pageNum) {
        loading.setVisibility(View.VISIBLE);

        Webservice.getInstance().getApi().getRequests(UserUtils.getAccessToken(getContext()), projectId, (selectedTab + 1), pageNum).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    JSONObject responseObject = new JSONObject(response.body().string());
                    JSONArray requestsArray = responseObject.getJSONArray("data");
                    setPhotographyList(requestsArray);
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


    public void setPhotographyList(JSONArray list) {
        try {
            for (int i = 0; i < list.length(); i++) {


                JSONObject currentObject = list.getJSONObject(i);
                final int id = currentObject.getInt("id");
                final int type_id = currentObject.getInt("type_id");
                final int created_by_id = currentObject.getInt("created_by_id");
                final int status_code = currentObject.getInt("status");
                final String status_message = currentObject.getString("status_message");
                final String item_name = currentObject.getString("item_name");
                final String description = currentObject.getString("description");
                final String delivery_address = currentObject.getString("delivery_address");
                final String note = currentObject.getString("note");
                final String country = currentObject.getString("country");
                final String location = currentObject.getString("location");
                final String days = currentObject.getString("days");
                final String project_type = currentObject.getString("project_type");
                final String camera_type = currentObject.getString("camera_type");
                final String numbers_cameras = currentObject.getString("number_camera");
                final String lighting = currentObject.getString("lighting");
                final String chroma = currentObject.getString("chroma");
                final String props = currentObject.getString("props");
                final String created_by_name = currentObject.getString("created_by_name");

                ArrayList<Attach_item> attach_files = new ArrayList<>();

                photography_list.add(new Photography_item(id, type_id, created_by_id, status_code, status_message,
                        item_name, description, delivery_address, note, country, location, days, project_type, camera_type, numbers_cameras,
                        lighting, chroma, props, created_by_name, attach_files));

            }

            photography_adapter.notifyDataSetChanged();
            mHasReachedBottomOnce = false;
            currentPageNum++;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initFields(View view) {
        RequestsActivity activity = (RequestsActivity) getActivity();
        projectId = activity.getProjectId();

        loading = view.findViewById(R.id.loading);
        recyclerView = view.findViewById(R.id.recycler_view);
        photography_list = new ArrayList<>();
        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        photography_adapter = new Photography_adapter(getContext(), photography_list);
        recyclerView.setAdapter(photography_adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && !mHasReachedBottomOnce) {
                    mHasReachedBottomOnce = true;

                    if (currentPageNum <= lastPageNum)
                        getRequests(0, currentPageNum);

                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        photography_list.clear();
        currentPageNum = 1;
        getRequests(3, currentPageNum);
    }
}