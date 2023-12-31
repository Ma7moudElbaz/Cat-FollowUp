package com.example.followup.home.notifications;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.adroitandroid.chipcloud.ChipCloud;
import com.adroitandroid.chipcloud.ChipListener;
import com.example.followup.R;
import com.example.followup.home.HomeActivity;
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

    int currentPageNum = 1;
    int lastPageNum;
    boolean mHasReachedBottomOnce = false;
    HomeActivity activity;

    SwipeRefreshLayout swipe_refresh;


    WebserviceContext ws;

    EditDrawableText search;

    String[] chipsText = new String[]{"All", "Unread", "Action required", "Done"};
    ChipCloud statusChip;
    Spinner notifications_no;
    String haveActionStr = "";
    String isUnreadStr = "";


    public static void hideKeyboardFragment(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFields(view);
        swipe_refresh.setOnRefreshListener(this::reloadData);

        search.setDrawableClickListener(drawablePosition -> {
            reloadData();
            hideKeyboardFragment(requireContext(), view);
        });

        notifications_no.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reloadData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        statusChip.setChipListener(new ChipListener() {
            @Override
            public void chipSelected(int index) {
                switch (index) {
                    case 0:
                        haveActionStr = "";
                        isUnreadStr = "";
                        break;
                    case 1:
                        haveActionStr = "";
                        isUnreadStr = "yes";
                        break;
                    case 2:
                        haveActionStr = "true";
                        isUnreadStr = "";
                        break;
                    case 3:
                        haveActionStr = "false";
                        isUnreadStr = "";
                        break;
                }
                reloadData();
            }

            @Override
            public void chipDeselected(int index) {

            }
        });


        search.setOnEditorActionListener((v, actionId, event) -> {

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                reloadData();
                hideKeyboardFragment(requireContext(), view);
                return true;
            }
            return false;
        });

    }

    private void initFields(View view) {
        ws = new WebserviceContext(getActivity());
        activity = (HomeActivity) getActivity();
        swipe_refresh = view.findViewById(R.id.swipe_refresh);
        search = view.findViewById(R.id.search);
        notifications_recycler = view.findViewById(R.id.notifications_recycler);
        notifications_list = new ArrayList<>();
        notifications_no = view.findViewById(R.id.notifications_no);
        String loggedInUser = UserType.getUserType(UserUtils.getParentId(getContext()), UserUtils.getChildId(getContext()), UserUtils.getCountryId(getContext()));

        if (loggedInUser.equals("ceo")) {
            notifications_no.setVisibility(View.VISIBLE);
        }else {
            reloadData();
        }

        statusChip = view.findViewById(R.id.status_chip);
        statusChip.addChips(chipsText);
        statusChip.setSelectedChip(0);

        initNotificationsRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();
//        reloadData();
    }

    private void reloadData() {
        notifications_list.clear();
        currentPageNum = 1;
        loadNotificationsData(currentPageNum, getFilterMap());
    }

    public void loadNotificationsData(int pageNum, Map<String, String> filterMap) {
        swipe_refresh.setRefreshing(true);

        ws.getApi().getNotifications(UserUtils.getAccessToken(getContext()), pageNum, filterMap).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    assert response.body() != null;
                    JSONObject responseObject = new JSONObject(response.body().string());
                    JSONArray notificationsArray = responseObject.getJSONArray("data");
                    setNotifications(notificationsArray);
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
                boolean needAction = false;
                if (data.has("action")) {
                    needAction = data.getBoolean("action");
                }
                notifications_list.add(new Notification_item(action_id, notification_id, from, to, subject, message, action_type, read_at, created_at, updated_at, needAction));

            }

            notifications_adapter.notifyDataSetChanged();
            mHasReachedBottomOnce = false;
            currentPageNum++;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initNotificationsRecyclerView() {
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
                        loadNotificationsData(currentPageNum, getFilterMap());

                }
            }
        });

    }

    private Map<String, String> getFilterMap() {

        Map<String, String> map = new HashMap<>();
        map.put("per_page", notifications_no.getSelectedItem().toString());
        map.put("search", search.getText().toString());
        map.put("have_action", haveActionStr);
        map.put("is_unread", isUnreadStr);

        return map;
    }

}