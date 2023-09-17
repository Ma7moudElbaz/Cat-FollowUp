package com.example.followup.admin.statistics.users;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.followup.R;
import com.example.followup.home.HomeActivity;
import com.example.followup.job_orders.list.Job_order_item;
import com.example.followup.job_orders.list.Job_orders_adapter;
import com.example.followup.utils.UserType;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.WebserviceContext;
import com.mindorks.editdrawabletext.EditDrawableText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Statistics_users extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics_users, container, false);
    }

    private ProgressDialog dialog;
    RecyclerView recyclerView;
    ProgressBar loading;
    EditDrawableText search;
    TextView total_users;
    ArrayList<Statistics_user_item> statistics_user_list;
    Statistics_users_adapter statistics_users_adapter;
    int currentPageNum = 1;
    int lastPageNum;
    boolean mHasReachedBottomOnce = false;
    WebserviceContext ws;
    HomeActivity activity;

    public static void hideKeyboardFragment(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initFields(view);
        reloadData();

        search.setDrawableClickListener(drawablePosition -> {
            reloadData();
            hideKeyboardFragment(requireContext(), view);
        });

        search.setOnEditorActionListener((v, actionId, event) -> {

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                reloadData();
                hideKeyboardFragment(requireContext(), v);
                return true;
            }
            return false;
        });
    }

    private void initFields(View view) {

        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Please, Wait...");
        dialog.setCancelable(false);

        ws = new WebserviceContext(getActivity());
        recyclerView = view.findViewById(R.id.recycler_view);
        total_users = view.findViewById(R.id.total_users);
        loading = view.findViewById(R.id.loading);
        search = view.findViewById(R.id.search);

        statistics_user_list = new ArrayList<>();

        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        statistics_users_adapter = new Statistics_users_adapter(getContext(), statistics_user_list);
        recyclerView.setAdapter(statistics_users_adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && !mHasReachedBottomOnce) {
                    mHasReachedBottomOnce = true;

                    if (currentPageNum <= lastPageNum)
                        getUsers(currentPageNum, getFilterMap());

                }
            }
        });
    }

    private void reloadData() {
        statistics_user_list.clear();
        currentPageNum = 1;
        getUsers(currentPageNum, getFilterMap());
    }

    public Map<String, String> getFilterMap() {
        Map<String, String> map = new HashMap<>();
        map.put("per_page", "20");
        map.put("search", search.getText().toString());

        return map;
    }

    public void getUsers(int pageNum, Map<String, String> filterMap) {
        loading.setVisibility(View.VISIBLE);
        ws.getApi().getStatisticsUsers(UserUtils.getAccessToken(getContext()), pageNum, filterMap).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        JSONObject responseObject = new JSONObject(response.body().string());
                        JSONObject responseDataObject = responseObject.getJSONObject("result");
                        JSONArray jobOrdersArray = responseDataObject.getJSONArray("data");
                        setUsersList(jobOrdersArray);
                        JSONObject metaObject = responseDataObject.getJSONObject("meta");
                        lastPageNum = metaObject.getInt("last_page");
                        int totalUsers = responseObject.getInt("users_total");
                        String total_users_count = "Total user : "+String.valueOf(totalUsers);
                        total_users.setText(total_users_count);
                    }

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

    @SuppressLint("NotifyDataSetChanged")
    public void setUsersList(JSONArray list) {
        try {
            for (int i = 0; i < list.length(); i++) {

                JSONObject currentObject = list.getJSONObject(i);
                final int id = currentObject.getInt("id");
                final String name = currentObject.getString("name");
                final int projects_no = currentObject.getInt("projects_count");
                final int requests_no = currentObject.getJSONObject("requests").getInt("count");


                statistics_user_list.add(new Statistics_user_item(id, name, projects_no, requests_no));

            }

            statistics_users_adapter.notifyDataSetChanged();
            mHasReachedBottomOnce = false;
            currentPageNum++;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}