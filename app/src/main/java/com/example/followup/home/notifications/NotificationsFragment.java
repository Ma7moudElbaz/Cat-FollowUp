package com.example.followup.home.notifications;

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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.followup.R;
import com.example.followup.home.HomeActivity;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.WebserviceContext;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    ArrayList<Notification_item> notifications_list;
    RecyclerView notifications_recycler;
    Notification_adapter notifications_adapter;

    ProgressBar loading;
    int currentPageNum = 1;
    int lastPageNum;
    boolean mHasReachedBottomOnce = false;
    HomeActivity activity;

    SwipeRefreshLayout swipe_refresh;


    WebserviceContext ws;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFields(view);

        swipe_refresh.setOnRefreshListener(() -> {
            swipe_refresh.setRefreshing(false);
            onResume();
        });
    }

    private void initFields(View view) {

        ws = new WebserviceContext(getActivity());
        activity = (HomeActivity) getActivity();

        activity.resetBadge();

        loading = view.findViewById(R.id.loading);
        swipe_refresh = view.findViewById(R.id.swipe_refresh);

        notifications_recycler = view.findViewById(R.id.notifications_recycler);
        notifications_list = new ArrayList<>();


        initNotificaitonsRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();
        notifications_list.clear();
        currentPageNum = 1;
        loadNotificationsData(currentPageNum);
    }

    public void loadNotificationsData(int pageNum) {
        loading.setVisibility(View.VISIBLE);

        ws.getApi().getNotifications(UserUtils.getAccessToken(getContext()), pageNum).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    JSONObject responseObject = new JSONObject(response.body().string());
                    JSONArray notificationsArray = responseObject.getJSONArray("data");
                    setNotifications(notificationsArray);
                    JSONObject metaObject = responseObject.getJSONObject("meta");
                    lastPageNum = metaObject.getInt("last_page");
//                    readNotification();
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

    public void setNotifications(JSONArray list) {
        try {
            for (int i = 0; i < list.length(); i++) {
                JSONObject currentObject = list.getJSONObject(i);
                final String notification_id = currentObject.getString("id");
                final String read_at = currentObject.getString("read_at");
                final String created_at = currentObject.getString("created_at");
                final String updated_at = currentObject.getString("updated_at");
                final JSONObject data = currentObject.getJSONObject("data");
                final String from = data.getString("from");
                final String to = data.getString("to");
                final String subject = data.getString("subject");
                final String message = data.getString("message");
                final String action_type = data.getString("action_type");
                final int action_id = data.getInt("action_id");
                notifications_list.add(new Notification_item(action_id, notification_id, from, to, subject, message, action_type, read_at, created_at, updated_at));

            }

            notifications_adapter.notifyDataSetChanged();
            mHasReachedBottomOnce = false;
            currentPageNum++;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initNotificaitonsRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        notifications_recycler.setLayoutManager(layoutManager);
        notifications_adapter = new Notification_adapter(getContext(), notifications_list);
        notifications_recycler.setAdapter(notifications_adapter);

        notifications_recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && !mHasReachedBottomOnce) {
                    mHasReachedBottomOnce = true;

                    if (currentPageNum <= lastPageNum)
                        loadNotificationsData(currentPageNum);

                }
            }
        });

    }

    public void readNotification() {
        Map<String, String> map = new HashMap<>();
        ws.getApi().readNotification(UserUtils.getAccessToken(getContext()), map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d("Error Throw", t.toString());
                Log.d("commit Test Throw", t.toString());
                Log.d("Call", t.toString());
                Toast.makeText(getContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}