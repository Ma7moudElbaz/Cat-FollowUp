package com.example.followup.bottomsheets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.followup.R;
import com.example.followup.job_orders.supplier.Supplier_item;
import com.example.followup.job_orders.supplier.Suppliers_adapter_with_callback;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.WebserviceContext;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomSheet_suppliers extends BottomSheetDialogFragment implements Suppliers_adapter_with_callback.AdapterCallback {


    private final SelectedSupplierListener selectedSupplierListener;
    private final int project_id;

    public BottomSheet_suppliers(Activity activity, int project_id) {
        this.selectedSupplierListener = ((SelectedSupplierListener) activity);
        this.project_id = project_id;
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
        return inflater.inflate(R.layout.bottom_sheet_suppliers, container, false);
    }


    Suppliers_adapter_with_callback suppliers_adapter;
    ArrayList<Supplier_item> suppliers_list;

    ImageView closeButton;
    RecyclerView recyclerView;
    ProgressBar loading;
    WebserviceContext ws;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFields(view);

        closeButton.setOnClickListener(v -> dismiss());

    }

    private void initFields(View view) {
        closeButton = view.findViewById(R.id.img_close);
        loading = view.findViewById(R.id.loading);
        recyclerView = view.findViewById(R.id.recycler);
        suppliers_list = new ArrayList<>();
        ws = new WebserviceContext(getActivity());
        initRecyclerView();
        getSuppliers();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void adapterCallback(String supplierName, String supplierId) {
        sendBackResult(supplierName, supplierId);
        dismiss();
    }

    public interface SelectedSupplierListener {
        void selectedSupplier(String selectedCompanyName, String selectedCompanyId);
    }

    public void sendBackResult(String selectedSupplierName, String selectedSupplierId) {
        selectedSupplierListener.selectedSupplier(selectedSupplierName, selectedSupplierId);
        dismiss();
    }


    public void getSuppliers() {
        loading.setVisibility(View.VISIBLE);

        ws.getApi().getSuppliers(UserUtils.getAccessToken(getContext()), project_id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    assert response.body() != null;
                    JSONObject responseObject = new JSONObject(response.body().string());
                    JSONArray suppliersArray = responseObject.getJSONArray("data");
                    setSuppliersList(suppliersArray);
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
    public void setSuppliersList(JSONArray list) {
        try {
            for (int i = 0; i < list.length(); i++) {
                String supplier_name = list.getString(i);

                suppliers_list.add(new Supplier_item("0", supplier_name));

            }

            suppliers_adapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        suppliers_adapter = new Suppliers_adapter_with_callback(getContext(), this, suppliers_list);
        recyclerView.setAdapter(suppliers_adapter);

    }
}

