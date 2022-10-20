package com.example.followup.job_orders.edit_job_order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.followup.R;

import java.util.ArrayList;
import java.util.List;


public class Edit_job_order_requests_adapter extends RecyclerView.Adapter<Edit_job_order_requests_adapter.ViewHolder> {

    private final List<Edit_job_order_request_item> items;

    private final AdapterCallback mAdapterCallback;


    public Edit_job_order_requests_adapter(Context context, ArrayList<Edit_job_order_request_item> items) {
        this.mAdapterCallback = ((AdapterCallback) context);
        this.items = items;
    }

    @NonNull
    @Override
    public Edit_job_order_requests_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edit_job_order_request, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Edit_job_order_requests_adapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.request_name.setText(items.get(position).getRequest_name());
        holder.cost_type.setText(items.get(position).getCost_type() + " Cost");
        holder.final_cost.setText(items.get(position).getActual_cost());
        holder.quantity.setText(items.get(position).getQuantity());

        holder.delete.setOnClickListener(view -> mAdapterCallback.adapterCallback(items.get(position).getRequest_id(), ""));

        holder.final_cost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                items.get(position).setActual_cost(holder.final_cost.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        holder.quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                items.get(position).setQuantity(holder.quantity.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView request_name, cost_type;
        final ImageView delete;
        final EditText final_cost, quantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            request_name = itemView.findViewById(R.id.request_name);
            delete = itemView.findViewById(R.id.delete);
            cost_type = itemView.findViewById(R.id.cost_type);
            final_cost = itemView.findViewById(R.id.final_cost);
            quantity = itemView.findViewById(R.id.quantity);
        }
    }

    public interface AdapterCallback {
        void adapterCallback(int job_order_request_id, String reason);
    }
}