package com.example.followup.home.all_job_orders;

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.followup.R;
import com.example.followup.bottomsheets.BottomSheet_choose_filter_job_orders_fragment;
import com.example.followup.home.HomeActivity;
import com.example.followup.job_orders.list.Job_order_item;
import com.example.followup.job_orders.list.Job_orders_adapter;
import com.example.followup.utils.UserType;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.WebserviceContext;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.mindorks.editdrawabletext.EditDrawableText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobOrdersFragment extends Fragment implements BottomSheet_choose_filter_job_orders_fragment.FilterListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_job_orders, container, false);
    }

    public void showFilterSheet() {
        BottomSheet_choose_filter_job_orders_fragment langBottomSheet =
                new BottomSheet_choose_filter_job_orders_fragment(JobOrdersFragment.this, selectedStatusIndex);
        langBottomSheet.show(getParentFragmentManager(), "jo_filter");
    }

    public static void hideKeyboardFragment(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private ProgressDialog dialog;
    RecyclerView recyclerView;
    EditDrawableText search;
    ImageView filterBtn;

    ExtendedFloatingActionButton btn_ceo_reminder, btn_sales_reminder;

    ArrayList<Job_order_item> job_order_list;
    Job_orders_adapter job_order_adapter;

    int currentPageNum = 1;
    int lastPageNum;
    boolean mHasReachedBottomOnce = false;

    int selectedStatusIndex = 0;
    String selectedStatus = "";
    final String[] chipsStatus = new String[]{"", "1", "2", "3", "5", "6", "8", "9", "7", "15", "14"};


    SwipeRefreshLayout swipe_refresh;

    WebserviceContext ws;

    HomeActivity activity;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFields(view);
        activity = (HomeActivity) getActivity();
        selectedStatusIndex = activity.getJoSelectedStatusIndex();
        selectedStatus = chipsStatus[selectedStatusIndex];

        activity.setJoSelectedStatusIndex(0);

        filterBtn.setOnClickListener(v -> showFilterSheet());

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

        btn_ceo_reminder.setOnClickListener(v -> sendCeoReminder());
        btn_sales_reminder.setOnClickListener(v -> sendSalesReminder());
        swipe_refresh.setOnRefreshListener(this::reloadData);
        reloadData();
    }

    public void getJobOrders(int pageNum, Map<String, String> filterMap) {
        swipe_refresh.setRefreshing(true);

        ws.getApi().getAllJobOrders(UserUtils.getAccessToken(getContext()), pageNum, filterMap).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        JSONObject responseObject = new JSONObject(response.body().string());
                        JSONArray jobOrdersArray = responseObject.getJSONArray("data");
                        setJobOrdersList(jobOrdersArray);
                        JSONObject metaObject = responseObject.getJSONObject("meta");
                        lastPageNum = metaObject.getInt("last_page");
                    }

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
    public void setJobOrdersList(JSONArray list) {
        try {
            for (int i = 0; i < list.length(); i++) {

                JSONObject currentObject = list.getJSONObject(i);
                final int id = currentObject.getInt("id");
                final int project_id = currentObject.getInt("project_id");
                final int country_id = currentObject.getInt("country_id");
                final int status_code = currentObject.getInt("status");
                final String status_message = currentObject.getString("status_message");
                final String name = currentObject.getString("name");
                final String po_number = currentObject.getString("po_number");

                final boolean have_action = currentObject.getBoolean("have_action");

                job_order_list.add(new Job_order_item(id, project_id, country_id, status_code, status_message,
                        name, po_number, have_action));

            }

            job_order_adapter.notifyDataSetChanged();
            mHasReachedBottomOnce = false;
            currentPageNum++;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendCeoReminder() {
        Map<String, String> map = new HashMap<>();
        map.put("job_order_id", "");

        dialog.show();
        ws.getApi().sendJobOrderReminder(UserUtils.getAccessToken(getContext()), map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Sent successfully", Toast.LENGTH_LONG).show();
                    } else {
                        assert response.errorBody() != null;
                        Toast.makeText(getContext(), response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    public void sendSalesReminder() {
        Map<String, String> map = new HashMap<>();
        map.put("job_order_id", "");
        map.put("sales", "1");

        dialog.show();
        ws.getApi().sendJobOrderReminder(UserUtils.getAccessToken(getContext()), map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Sent successfully", Toast.LENGTH_LONG).show();
                    } else {
                        assert response.errorBody() != null;
                        Toast.makeText(getContext(), response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void initFields(View view) {

        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Please, Wait...");
        dialog.setCancelable(false);

        ws = new WebserviceContext(getActivity());
        search = view.findViewById(R.id.search);
        filterBtn = view.findViewById(R.id.filter_btn);
        recyclerView = view.findViewById(R.id.recycler_view);
        btn_ceo_reminder = view.findViewById(R.id.btn_ceo_reminder);
        btn_sales_reminder = view.findViewById(R.id.btn_sales_reminder);

        swipe_refresh = view.findViewById(R.id.swipe_refresh);
        job_order_list = new ArrayList<>();

        String loggedInUser = UserType.getUserType(UserUtils.getParentId(getContext()), UserUtils.getChildId(getContext()), UserUtils.getCountryId(getContext()));

        if (loggedInUser.equals("hesham") || loggedInUser.equals("nagat") || loggedInUser.equals("hany") || loggedInUser.equals("speranza")) {
            btn_ceo_reminder.setVisibility(View.VISIBLE);
            btn_sales_reminder.setVisibility(View.VISIBLE);
        } else {
            btn_ceo_reminder.setVisibility(View.GONE);
            btn_sales_reminder.setVisibility(View.GONE);
        }
        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        job_order_adapter = new Job_orders_adapter(getContext(), job_order_list);
        recyclerView.setAdapter(job_order_adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && !mHasReachedBottomOnce) {
                    mHasReachedBottomOnce = true;

                    if (currentPageNum <= lastPageNum)
                        getJobOrders(currentPageNum, getFilterMap());

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
        job_order_list.clear();
        currentPageNum = 1;
        getJobOrders(currentPageNum, getFilterMap());
    }

    public Map<String, String> getFilterMap() {
        Map<String, String> map = new HashMap<>();
        map.put("status", selectedStatus);
        map.put("search", search.getText().toString());

        return map;
    }

    @Override
    public void applyFilterListener(int returnedSelectedStatusIndex) {
        if (returnedSelectedStatusIndex == -1) {
            selectedStatus = "";
        } else {
            selectedStatus = chipsStatus[returnedSelectedStatusIndex];
        }

        selectedStatusIndex = returnedSelectedStatusIndex;

        job_order_list.clear();
        currentPageNum = 1;
        getJobOrders(currentPageNum, getFilterMap());
    }
}