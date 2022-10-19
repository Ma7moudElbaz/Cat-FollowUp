package com.example.followup.bottomsheets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.followup.R;
import com.example.followup.home.projects.Companies_adapter_with_callback;
import com.example.followup.home.projects.Company_item;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.WebserviceContext;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomSheet_projects extends BottomSheetDialogFragment implements Companies_adapter_with_callback.AdapterCallback {


    private final SelectedProjectsListener selectedProjectListener;

    public BottomSheet_projects(Fragment fragment) {
        this.selectedProjectListener = ((SelectedProjectsListener) fragment);
    }


    public static void hideKeyboardFragment(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_projects, container, false);
    }


    int currentPageNum = 1;
    int lastPageNum;
    boolean mHasReachedBottomOnce = false;
    Companies_adapter_with_callback projects_adapter;
    ArrayList<Company_item> projects_list;

    ImageView closeButton;
    RecyclerView recyclerView;
    ProgressBar loading;
    WebserviceContext ws;
    EditText search, company_name;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFields(view);


        closeButton.setOnClickListener(v -> dismiss());


        search.setOnEditorActionListener((v, actionId, event) -> {

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                projects_list.clear();
                currentPageNum = 1;
                getProjects(currentPageNum);
//                hideKeyboardFragment(getContext(), view);
                return true;
            }
            return false;
        });

    }

    private void initFields(View view) {
        closeButton = view.findViewById(R.id.img_close);
        loading = view.findViewById(R.id.loading);
        recyclerView = view.findViewById(R.id.recycler);
        search = view.findViewById(R.id.search);
        projects_list = new ArrayList<>();
        ws = new WebserviceContext(getActivity());
        initRecyclerView();
        getProjects(currentPageNum);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void adapterCallback(String companyName, String companyId) {
        sendBackResult(companyName, companyId);
        dismiss();
    }


    public interface SelectedProjectsListener {
        void selectedProject(String selectedProjectName, String selectedProjectId);
    }

    public void sendBackResult(String selectedProjectName, String selectedProjectId) {
        selectedProjectListener.selectedProject(selectedProjectName, selectedProjectId);
        dismiss();
    }


    public void getProjects(int pageNum) {
        loading.setVisibility(View.VISIBLE);
        Map<String, String> map = new HashMap<>();
        map.put("search", search.getText().toString());

        ws.getApi().getAllProjects(UserUtils.getAccessToken(getContext()), pageNum, map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    JSONObject responseObject = new JSONObject(response.body().string());
                    JSONArray companiesArray = responseObject.getJSONArray("data");
                    setProjectsList(companiesArray);
//                    JSONObject metaObject = responseObject.getJSONObject("meta");
                    lastPageNum = responseObject.getInt("last_page");

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
    public void setProjectsList(JSONArray list) {
        if (currentPageNum == 1) {
            projects_list.add(new Company_item("", "All Projects"));
        }
        try {
            for (int i = 0; i < list.length(); i++) {
                JSONObject currentObject = list.getJSONObject(i);
                final String id = currentObject.getString("id");
                final String name = currentObject.getString("project_name");

                projects_list.add(new Company_item(id, name));

            }

            projects_adapter.notifyDataSetChanged();
            mHasReachedBottomOnce = false;
            currentPageNum++;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        projects_adapter = new Companies_adapter_with_callback(getContext(), this, projects_list);
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
}

