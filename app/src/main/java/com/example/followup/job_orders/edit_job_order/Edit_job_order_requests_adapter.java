package com.example.followup.job_orders.edit_job_order;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.followup.R;
import com.example.followup.job_orders.job_order_requests.Job_order_request_item;
import com.example.followup.requests.RequestDetailsActivity;

import java.util.ArrayList;
import java.util.List;


public class Edit_job_order_requests_adapter extends RecyclerView.Adapter<Edit_job_order_requests_adapter.ViewHolder> {

    private final List<Edit_job_order_request_item> items;

    private final Context mContext;

    public Edit_job_order_requests_adapter(Context context, ArrayList<Edit_job_order_request_item> items) {
        this.mContext = context;
        this.items = items;
    }

    @NonNull
    @Override
    public Edit_job_order_requests_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edit_job_order_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Edit_job_order_requests_adapter.ViewHolder holder, int position) {
        holder.request_name.setText(items.get(position).getRequest_name());
        holder.final_cost.setText(items.get(position).getActual_cost());
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
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView request_name;
        final EditText final_cost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            request_name = itemView.findViewById(R.id.request_name);
            final_cost = itemView.findViewById(R.id.final_cost);
        }
    }
}