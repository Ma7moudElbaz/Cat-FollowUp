package com.example.followup.job_orders.supplier;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.followup.R;

import java.util.ArrayList;
import java.util.List;


public class Suppliers_adapter_with_callback extends RecyclerView.Adapter<Suppliers_adapter_with_callback.ViewHolder> {

    private final List<Supplier_item> items;

    private final Context mContext;
    private final AdapterCallback mAdapterCallback;

    public Suppliers_adapter_with_callback(Context context, Fragment fragment, ArrayList<Supplier_item> items) {
        try {
            this.mAdapterCallback = ((AdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement AdapterCallback.");
        }
        this.mContext = context;
        this.items = items;
    }

    @NonNull
    @Override
    public Suppliers_adapter_with_callback.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_supplier, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Suppliers_adapter_with_callback.ViewHolder holder, final int position) {
        holder.supplier_name.setText(items.get(position).getName());

        holder.parent_layout.setOnClickListener(v -> mAdapterCallback.adapterCallback(
                items.get(position).getName(), items.get(position).getId()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView supplier_name;
        final RelativeLayout parent_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            supplier_name = itemView.findViewById(R.id.supplier_name);
            parent_layout = itemView.findViewById(R.id.parent_layout);
        }
    }

    public interface AdapterCallback {
        void adapterCallback(String supplierName, String supplierId);
    }
}