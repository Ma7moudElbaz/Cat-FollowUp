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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
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

public class BottomSheet_companies_from_activity extends BottomSheetDialogFragment implements Companies_adapter_with_callback.AdapterCallback {


    private final BottomSheet_companies_from_activity.SelectedCompanyListener selectedCompanyListener;

    public BottomSheet_companies_from_activity(Activity activity) {
        this.selectedCompanyListener = ((BottomSheet_companies_from_activity.SelectedCompanyListener) activity);
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
        return inflater.inflate(R.layout.bottom_sheet_companies, container, false);
    }


    int currentPageNum = 1;
    int lastPageNum;
    boolean mHasReachedBottomOnce = false;
    Companies_adapter_with_callback companies_adapter;
    ArrayList<Company_item> companies_list;

    ImageView closeButton;
    RecyclerView recyclerView;
    ProgressBar loading;
    WebserviceContext ws;
    EditText search, company_name;
    Button add_company, add_new_company;
    RelativeLayout add_company_layout;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFields(view);

        add_new_company.setOnClickListener(v -> {
            add_company_layout.setVisibility(View.VISIBLE);
            add_new_company.setVisibility(View.GONE);
        });

        closeButton.setOnClickListener(v -> dismiss());

        add_company.setOnClickListener(v -> {
            if (company_name.length() != 0) {
                addCompany();
            } else {
                company_name.setError("This is required field");
            }
        });

        search.setOnEditorActionListener((v, actionId, event) -> {

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                companies_list.clear();
                currentPageNum = 1;
                getCompanies(currentPageNum);
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
        company_name = view.findViewById(R.id.company_name);
        add_company = view.findViewById(R.id.add_company);
        add_new_company = view.findViewById(R.id.add_new_company);
        add_company_layout = view.findViewById(R.id.add_company_layout);
        companies_list = new ArrayList<>();
        ws = new WebserviceContext(getActivity());
        initRecyclerView();
        getCompanies(currentPageNum);
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


    public interface SelectedCompanyListener {
        void selectedCompany(String selectedCompanyName, String selectedCompanyId);
    }

    public void sendBackResult(String selectedCompanyName, String selectedCompanyId) {
        selectedCompanyListener.selectedCompany(selectedCompanyName, selectedCompanyId);
        dismiss();
    }


    public void getCompanies(int pageNum) {
        loading.setVisibility(View.VISIBLE);
        Map<String, String> map = new HashMap<>();
        map.put("search", search.getText().toString());

        ws.getApi().getCompanies(UserUtils.getAccessToken(getContext()), pageNum, map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    JSONObject responseObject = new JSONObject(response.body().string());
                    JSONArray companiesArray = responseObject.getJSONArray("data");
                    setCompaniesList(companiesArray);
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

    public void addCompany() {
        loading.setVisibility(View.VISIBLE);
        Map<String, String> map = new HashMap<>();
        map.put("name", company_name.getText().toString());

        ws.getApi().AddCompany(UserUtils.getAccessToken(getContext()), map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    if (response.isSuccessful()) {
                        company_name.setText("");
                        Toast.makeText(getContext(), "Company Added Successfully and waiting admin approval", Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject responseObject = new JSONObject(response.body().string());
                        Toast.makeText(getContext(), responseObject.getString("error"), Toast.LENGTH_SHORT).show();
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
    public void setCompaniesList(JSONArray list) {
        try {
            for (int i = 0; i < list.length(); i++) {
                JSONObject currentObject = list.getJSONObject(i);
                final String id = currentObject.getString("id");
                final String name = currentObject.getString("name");

                companies_list.add(new Company_item(id, name));

            }

            companies_adapter.notifyDataSetChanged();
            mHasReachedBottomOnce = false;
            currentPageNum++;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        companies_adapter = new Companies_adapter_with_callback(this, companies_list);
        recyclerView.setAdapter(companies_adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && !mHasReachedBottomOnce) {
                    mHasReachedBottomOnce = true;

                    if (currentPageNum <= lastPageNum)
                        getCompanies(currentPageNum);

                }
            }
        });
    }
}
